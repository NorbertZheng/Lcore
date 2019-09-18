package com.njy.project.simulator.cp0;
import java.util.Arrays;

import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cp0.timer.Timer;


public class CP0 {
	final private int []regFile = new int[32];
	final private boolean []readOnly = new boolean[32];//set readonly , cannot write through WriteReg
	private int regWrite = -1;
	private Timer timer;
	//final private int TLBPDO[] = new int[1 << 20];
	final private int TLBPTE[] = new int[1 << 20];
	final private boolean TLBValid[] = new boolean[1 << 20];
	final private byte priFunc[] = new byte[128 * 16];
	private boolean allmask = true;
	private int mask = 0xffffffff;
	public CP0(Bus bus)
	{
		this.bus = bus;
		readOnly[CP0Register.SR] = true;
		readOnly[CP0Register.EAR] = true;
		regFile[CP0Register.EHBR] = 0xff000000;
		initFunc();
		timer = new Timer(this);
	}
	
	private void initFunc()
	{
		int mo, level;
		int pdelow4, ptelow4;
		for(int i = 0; i < 2048; i++)
		{
			pdelow4 = i >> 7;
			ptelow4 = (i >> 3) & 0xf;
			mo = (i >> 1) & 0x3;
			level = i & 0x1;
			if((pdelow4&0x1) == 0)
			{
				priFunc[i] = ExceptionCode.NoPgTableEntry;
			}
			else if (((pdelow4 >> 1)&0x1) < level) 
			{
				priFunc[i] = ExceptionCode.IllegalVisit;
			}
			else if (((pdelow4 >> 2)&0x1) == 0 && mo == MemOperation.WRITE)
			{
				priFunc[i] = ExceptionCode.IllegalWrite;
			}
			else if (((pdelow4 >> 3)&0x1) == 0 && mo == MemOperation.FETCH)
			{
				priFunc[i] = ExceptionCode.IllegalExec;
			}
			else if((ptelow4&0x1) == 0)
			{
				priFunc[i] = ExceptionCode.NoPgTableEntry;
			}
			else if (((ptelow4 >> 1)&0x1) < level) 
			{
				priFunc[i] = ExceptionCode.IllegalVisit;
			}
			else if (((ptelow4 >> 2)&0x1) == 0 && mo == MemOperation.WRITE)
			{
				priFunc[i] = ExceptionCode.IllegalWrite;
			}
			else if (((ptelow4 >> 3)&0x1) == 0 && mo == MemOperation.FETCH)
			{
				priFunc[i] = ExceptionCode.IllegalExec;
			}
		}
	}
	
	protected Bus bus;
	
	public void Reset()
	{
		for(int i=0; i< 32; i++)
			regFile[i] = 0;
		
		timer.stop();
	}
	
	
	
	public void StartTimer()
	{
		timer.start();
	}
	
	final public int ReadReg(int index)
	{
		return regFile[index];
	}
	
	final public void WriteReg(int index, int data)
	{
		if(!readOnly[index]) 
		{
			switch (index) {
			case CP0Register.ICR:
				synchronized (regFile)
				{
					regFile[index] &= ~data; 
				}
				regWrite = index;
				return;
			case CP0Register.PDBR:
				clearTLB();
			default:
				regFile[index] = data;	
				regWrite = index;
				return;
			}
		}
		
	}
	
	synchronized final public void writeICR(int value)
	{
		regFile[CP0Register.ICR] = value;
	}
	
	final public void SetGE()
	{
		regFile[CP0Register.IER] |= 0x80000000;
	}
	
	final public void ResetGE()
	{
		regFile[CP0Register.IER] &= 0x7fffffff;
	}
	
	final public void IRCheck() throws InterruptException //check interrupt
	{
		int ICR = regFile[CP0Register.ICR];
		int IER = regFile[CP0Register.IER];
		boolean ge = (regFile[CP0Register.IER]>>>31) != 0;
		boolean ir = ((0x7fffffff & ICR & IER) != 0) && ge;
		if( ir ) 
		{
			regFile[CP0Register.SR] &= 0x0fffffff;
			regFile[CP0Register.SR] |= 1 << 30; 
			throw new InterruptException("outer interrupt");
		}
	}
	
	final public void SetEX(int excode) throws InterruptException
	{
		regFile[CP0Register.SR] &= 0x0fff07ff;
		regFile[CP0Register.SR] |=  (excode << 11) | ( 1 << 31 );
		throw new InterruptException(ExceptionCode.ExceptionDescription[excode]);
		//throw new InterruptException("");
	}
	
	final public void SetSC(int sccode) throws InterruptException
	{
		regFile[CP0Register.SR] &= 0x0ffff801;
		regFile[CP0Register.SR] |= (sccode << 1) | ( 1 << 29 );
		throw new InterruptException("SYSCALL" + sccode);
	}
	
	
	final public void SetRS()
	{
		regFile[CP0Register.SR] &= 0x0fffffff;
		regFile[CP0Register.SR] |= 1 << 28;
	}
	
	public void SetIRC(int i)//set interrupt
	{
		if(i<0 || i>=31 || !allmask)
			return;
		synchronized (regFile)
		{
			regFile[CP0Register.ICR] |= (1 << i) & mask;
		}
		
		//System.out.println("setirc");
	}
	
	final public void DisableMMU()
	{
		regFile[CP0Register.PDBR] &= 0xfffffffe;
	}
	
	public void clearTLB()
	{
		Arrays.fill(TLBValid, false);
	}
	
	final public int MMU(int va, int mo, int am) throws InterruptException
	{
		//check unaligned instruction or data
		
		if((va & (am -1)) != 0)
		{
			regFile[CP0Register.EAR] = va;
			switch(mo)
			{
			case MemOperation.FETCH:
				SetEX(ExceptionCode.UnAlignedPCAddr);
			case MemOperation.READ:
			case MemOperation.WRITE:
				SetEX(ExceptionCode.UnAlignedDataAddr);
			}
		}
		
		////////////////////////////////////////////////////////////////////////////////////
		
		if((regFile[CP0Register.PDBR] & 0x01) == 0) return va;//not enable mmu
		int PHign20 = va >>> 12;
		if(TLBValid[PHign20])
		{
			int PTE = TLBPTE[PHign20];
			int exceptionCode = priFunc[((PTE&0xff)<<3)|(mo << 1)|GetLevel()];
			if(exceptionCode!=0)
			{
				regFile[CP0Register.EAR] = va;
				SetEX(exceptionCode);
			}		
			return (PTE & 0xfffff000)|(va & 0xfff);
		}
		
		
		//int PDO = va >>> 22;//page directory offset
		int PDE = bus.read(( regFile[CP0Register.PDBR] & 0xfffff000 ) | ((va >>> 22) << 2));
		//check if the entry is valid
		int molevel = (mo << 1) | GetLevel();
		int exceptionCode = priFunc[((PDE&0xf)<<7)|(0xf << 3)|molevel];
		if(exceptionCode!=0)
		{
			regFile[CP0Register.EAR] = va;
			SetEX(exceptionCode);
		}
		
		//int PTO = PHign20 & 0x000003ff;
		//int PTB = PDE & 0xfffff000;// get page table base address
		int PTE = bus.read((PDE & 0xfffff000) | ((PHign20 & 0x3ff) << 2));	
		
		exceptionCode = priFunc[(0xf << 7)|((PTE&0xf)<<3)|molevel];
		if(exceptionCode!=0)
		{
			regFile[CP0Register.EAR] = va;
			SetEX(exceptionCode);
		}
		
		//System.out.println(String.format("read page %08X", PHign20) + String.format( " %08X", PTE)  + String.format(" %08X", PDE));
		if(PHign20 == 0x7ffff )
			System.out.println(String.format( "%08X", PTE)  + String.format(" %08X", PDE));
		TLBPTE[PHign20] = (PTE & 0xffffff0f) | ((PDE & 0xf) << 4);
		TLBValid[PHign20] = true;
		
		return (PTE & 0xfffff000) | (va & 0x00000fff);
	}
	
	final public int MMU(int virtualPageAddr) throws InterruptException
	{
		if((regFile[CP0Register.PDBR] & 0x01) == 0) return virtualPageAddr;//not enable mmu

//		if(TLBValid[PHign20])
//		{
//			int PTE = TLBPTE[PHign20];
//			if((PTE & 0x1) == 0)
//				throw new InterruptException();	
//			return (PTE & 0xfffff000)|(virtualPageAddr & 0xfff);
//		}
//		
		
		//int PDO = va >>> 22;//page directory offset
		int PDE = bus.read(( regFile[CP0Register.PDBR] & 0xfffff000 ) | ((virtualPageAddr >>> 22) << 2));
		//check if the entry is valid
		if((PDE & 0x1) == 0)
			throw new InterruptException();
		
		//int PTO = PHign20 & 0x000003ff;
		//int PTB = PDE & 0xfffff000;// get page table base address
		int PHign20 = virtualPageAddr >>> 12;
		int PTE = bus.read((PDE & 0xfffff000) | ((PHign20 & 0x3ff) << 2));	
		if((PTE & 0x1) == 0)
			throw new InterruptException();	
		
		//TLBPTE[PHign20] = (PTE & 0xffffff0f) | ((PDE & 0xf) << 4);
		//TLBValid[PHign20] = true;
		
		return (PTE & 0xfffff000) | (virtualPageAddr & 0x00000fff);
	}
	
	final public void SavePC(int PC)
	{
		regFile[CP0Register.EPCR] = (PC & 0xfffffffc) | (regFile[CP0Register.SR] & 0x01);
		regFile[CP0Register.SR] &= 0xfffffffe; //set to kernel mode
		regWrite = CP0Register.EPCR;
	}
	
	
	final public void DoEret()
	{
		regFile[CP0Register.SR] = (0x0ffffffe  & regFile[CP0Register.SR]) | (0x00000001 & regFile[CP0Register.EPCR]); //
		regWrite = CP0Register.SR;
		SetGE();
	}
	
	final public int GetLevel()
	{
		return regFile[CP0Register.SR] & 0x01 ; 
	}

	final public int getRegWrite()
	{
		return regWrite;
	}

	public boolean isAllmask()
	{
		return allmask;
	}

	public void setAllmask(boolean allmask)
	{
		this.allmask = allmask;
	}

	public int getMask()
	{
		return mask;
	}

	public void setMask(int mask)
	{
		this.mask = mask;
	}
}


