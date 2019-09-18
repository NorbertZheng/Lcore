package com.njy.project.simulator.cpu;

import java.util.ArrayList;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cp0.*;
import com.njy.project.simulator.cpu.OpCode;
import com.njy.project.simulator.cpu.debug.BreakPoint;
import com.njy.project.simulator.cpu.debug.DataBreakPoint;
import com.njy.project.simulator.ui.UIController;

public class MIPSCPU implements Runnable{
	
	final private int []regFile = new int[32];//GPR
	private Integer status = 0;
	protected Bus bus;
	private CP0 cp0;
	private int pc = 0;
	private int pcPlus4 = 0;
	private int pcNext = 0;
	private int pcInit = 0;
	private int regWrite = 0;
	private boolean isJmpOrBranch = false;
	private boolean isDelaySlot = true;
	private long startTime = 0;
	private long consumingTime = 0;
	private int speedCount = 0;
	private int speedMask = 0xffff;
	private boolean testMode = false;
	private boolean stopFlag = false;
	private  BreakPoint breakPoint;
	private DataBreakPoint dataBreakPoint;
	final public CP0 getCp0() {
		return cp0;
	}
	
	public MIPSCPU(Bus bus, CP0 cp0) {
		// TODO Auto-generated constructor stub	
		this.bus = bus;
		this.cp0 = cp0;
		breakPoint = new BreakPoint(bus);
		dataBreakPoint = new DataBreakPoint();
		Reset();
	}

	public void Reset() {
		pc = pcNext = pcInit;
		for(int i = 0; i<32 ;i++)
		{
			regFile[i] = 0;
		}
		status = 0;
		cp0.Reset();
		System.gc();
	}

	//cpu control
	public void run()
	{
		speedCount = 0;
		startTime = System.nanoTime();
		while (!stopFlag) 
		{
			runCPU();	
			consumingTime = System.nanoTime() - startTime;
			speedCount ++;
			if(speedCount > speedMask)
			{
				speedCount = 0;
				startTime = System.nanoTime();
			}
		}
		
		consumingTime = 0;
	}
	
	String threadCtrlObject = "";
	
	
	public void addBreakPoint(int addr)
	{
		//breakPoints[instCount >>> 5] |= 1 << (instCount & 0x1f); 
		
		if(status == CPUStatus.DEBUG)
		{
			breakPoint.AddAndApplyBreakPoint(addr);
			return;
		}
		
		breakPoint.AddBreakPoint(addr);
	}
	
	public void addDataBreakPoint(int addr){
		dataBreakPoint.addBreak(addr);
	}
	
	public ArrayList<Long> getBreak(int addr)
	{
		return breakPoint.GetOnePageBreak(addr);
	}
	
	public ArrayList<Integer> getDataBreak(int addr)
	{
		return dataBreakPoint.getOnePageBreak(addr);
	}
	
	public boolean checkBreakPoint(int addr)
	{
		return breakPoint.IsContainBreakPoint(addr);
		//return ((breakPoints[instCount >>> 5] >>> (instCount & 0x1f)) & 0x1);
	}
	
	public boolean checkDataBreakPoint(int addr){
		return dataBreakPoint.isBreak(addr);
	}
	
	public void removeBreakPoint(int addr)
	{
		breakPoint.RemoveBreakPoint(addr);
		//breakPoints[instCount >>> 5] &= ~(1 << (instCount & 0x1f)); 
	}
	
	public void removeDataBreakPoint(int addr){
		dataBreakPoint.removeBreak(addr);
	}
	
	private void doBreak()
	{
		System.out.printf("pause %08X, %08X\n",pc, pcNext);
		System.out.println(isJmpOrBranch);
		pcPlus4 = pc;
		breakPoint.setBreaked(true);
		Pause();
		UIController.getInstance().RefreshAll();
	}
	
	public void Pause()
	{
		synchronized (status) {
			switch (status)
			{
			case CPUStatus.RUNING:
				status = CPUStatus.PAUSED;
				break;
			case CPUStatus.DEBUG:
				status = CPUStatus.DEBUGPAUSED;
				breakPoint.RestoreBreakPoint();
				break;
			default:
				break;
			}
				
		}
		stopFlag = true;
	}
	
	public void Stop()
	{
		boolean restoreFlag = false;
		synchronized (status) {
			restoreFlag = status == CPUStatus.DEBUG;
			status = CPUStatus.STOPPED;
		}
		if(restoreFlag)
			breakPoint.RestoreBreakPoint();
		stopFlag = true;
		
		Reset();
	}
	
	public void Resume()
	{
		switch (status)
		{
		case CPUStatus.PAUSED:
			Start();
			break;
		case CPUStatus.DEBUGPAUSED:
			if(breakPoint.isBreaked())
			{
				Step();//run one single
				breakPoint.setBreaked(false);
			}
			Debug();
			break;
		default:
			break;
		}
	}
	
	public void Step()
	{
		synchronized (status) {
			switch (status) {
			case CPUStatus.PAUSED:
				runCPU();
				break;
			case CPUStatus.DEBUGPAUSED:
				runCPU();
				break;
			default:
				break;
			}

		}
		
	}
	
	public void Debug()
	{
		synchronized (status) {
			breakPoint.ApplyBreakPoint();
			status = CPUStatus.DEBUG;
		}
		stopFlag = false;
		cpuThread = new Thread(this);
		cpuThread.start();
	}
	
	
	
	private Thread cpuThread = null;
	public void Start()
	{
		synchronized (status) {
			status = CPUStatus.RUNING;
		}
		stopFlag = false;
		cpuThread = new Thread(this);
		cpuThread.start();
	}
	
	private void runCPU()
	{
		try 
		{
			if(isJmpOrBranch && isDelaySlot)
			{
				int pcTemp = pcNext;
				pc = pcPlus4;
				singleInstruction();
				pcNext = pcTemp;
				return;
			}
			
			pc = pcNext;
			singleInstruction();
			
		} 
		catch (InterruptException e) 
		{
			// TODO: handle exception
			pcNext = cp0.ReadReg(CP0Register.EHBR);
			cp0.ResetGE();
			if(isJmpOrBranch && isDelaySlot)
			{
				cp0.SavePC(pc - 4);
			}
			else 
			{
				cp0.SavePC(pc);
			}
		}	
		
	}
	
	private void singleInstruction() throws InterruptException
	{
		cp0.IRCheck();//check outer interrupt
		
		int instruction = bus.read(cp0.MMU(pc, MemOperation.FETCH,AccessMode.WORD));//fetch instruction
		pcPlus4 = pc + 4;
		
		
		int op = (instruction >> 26) & 0x3f;
		switch (op) 
		{
		case OpCode.RTYPE:
		{
			int func = (instruction >> 0) & 0x3f;
			switch (func) 
			{
			case FunctionCode.ADD:
			case FunctionCode.ADDU:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rs] + regFile[rt]);
				break;
			}
			case FunctionCode.SUB:
			case FunctionCode.SUBU:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rs] - regFile[rt]);
				break;
			}
			case FunctionCode.AND:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rs] & regFile[rt]);
				break;
			}
			case FunctionCode.OR:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rs] | regFile[rt]);
				break;
			}
			case FunctionCode.XOR:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rs] ^ regFile[rt]);
				break;
			}
			case FunctionCode.NOR:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, ~(regFile[rs] | regFile[rt]));
				break;
			}
			case FunctionCode.SLT:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, (regFile[rs] < regFile[rt])? 1 : 0);//0x80000000 will make (regFile[rs] - regFile[rt]) >>> 1 wrong
				break;
			}
			case FunctionCode.SLTU:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, compareAsUnsignedInt(regFile[rs] , regFile[rt]));
				break;
			}
			case FunctionCode.SLL:
			{
				int shamt = (instruction >> 6) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rt] << shamt);
				break;
			}
			case FunctionCode.SRL:
			{
				int shamt = (instruction >> 6) & 0x1f;
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				if(rs == 0)
					writeReg(rd, regFile[rt] >>> shamt);
				else 
				{
					writeReg(rd, (regFile[rt] >>> shamt) | (regFile[rt] << (32 - shamt))) ;  
				}
				break;
			}
			case FunctionCode.SRA:
			{
				int shamt = (instruction >> 6) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rt] >> shamt);
				break;
			}
			case FunctionCode.SLLV:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rt] << regFile[rs]);
				break;
			}
			case FunctionCode.SRLV:
			{
				int shamt = (instruction >> 6) & 0x1f;
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				if(shamt == 0)
					writeReg(rd, regFile[rt] >>> regFile[rs]);
				else 
				{
					int rot = regFile[rs] & 0x0000001f;
					writeReg(rd, (regFile[rt] >>> rot) | (regFile[rt] << (32 - rot))) ;  
				}
				break;
			}
			case FunctionCode.SRAV:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				writeReg(rd, regFile[rt] >> regFile[rs]);
				break;
			}
			case FunctionCode.JR:
				// jr rs
			{
				int rs = (instruction >> 21) & 0x1f;
				isJmpOrBranch = true;
				pcNext = regFile[rs];
				return;
			}
			case FunctionCode.JALR:
				// jalr rd, rs
			{
				int rs = (instruction >> 21) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				isJmpOrBranch = true;
				pcNext = regFile[rs];
				writeReg(rd, isDelaySlot ? (pcPlus4 + 4) : pcPlus4);
				return;
			}
			case FunctionCode.MOVZ:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				if(regFile[rt] == 0) writeReg(rd, regFile[rs]);
				break;
			}
			case FunctionCode.MOVN:
			{
				int rs = (instruction >> 21) & 0x1f;
				int rt = (instruction >> 16) & 0x1f;
				int rd = (instruction >> 11) & 0x1f;
				if(regFile[rt] != 0) writeReg(rd, regFile[rs]);
				break;
			}
			case FunctionCode.SYSCALL:
			{
				int code = (instruction >> 6) & 0x3ff; 
				pc = pcPlus4;
				cp0.SetSC(code);
			}
			case FunctionCode.BREAK:
			{
				if(status < CPUStatus.DEBUG) cp0.SetEX(ExceptionCode.UnDefinedInst);
				doBreak();
				return;
			}
			default:
			{
				cp0.SetEX(ExceptionCode.UnDefinedInst);
			}
			}
			break;
		}
		// j type
		case OpCode.J:
		{
			int addr = ((instruction & 0x3ffffff) << 2) | (pc & 0xf0000000);
			isJmpOrBranch = true;
			pcNext = addr;
			return;
		}
		case OpCode.JAL:
		{
			int addr = ((instruction & 0x3ffffff) << 2) | (pc & 0xf0000000);
			isJmpOrBranch = true;
			pcNext = addr;
			writeReg(31, isDelaySlot ? (pcPlus4 + 4) : pcPlus4);
			return;
		}
		// i type
		case OpCode.ADDI:
		case OpCode.ADDIU:
			// addi rt, rs, signedImme
		{
			
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, regFile[rs] + signedImme);
			break;
		}
		case OpCode.ANDI:
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int unsignedImme = instruction & 0xffff;
			writeReg(rt, regFile[rs] & unsignedImme);
			break;
		}
		case OpCode.ORI:
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int unsignedImme = instruction & 0xffff;
			writeReg(rt, regFile[rs] | unsignedImme);
			break;
		}
		case OpCode.XORI:
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int unsignedImme = instruction & 0xffff;
			writeReg(rt, regFile[rs] ^ unsignedImme);
			break;
		}
		case OpCode.LUI:
		{
			int rt = (instruction >> 16) & 0x1f;
			int unsignedImme = instruction & 0xffff;
			writeReg(rt, unsignedImme << 16);
			break;
		}
		case OpCode.SW:
		{
			// sw rt, signedImme(rs)	
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			int paddr = cp0.MMU(regFile[rs] + signedImme, MemOperation.WRITE, AccessMode.WORD);
			if(dataBreakPoint.isBreak(paddr) && status == CPUStatus.DEBUG){
				doBreak();
				return;
			}
			bus.write( paddr, regFile[rt], AccessMode.WORD);
			break;
		}
		case OpCode.SB:
		{
			// sb rt, signedImme(rs)
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			int paddr = cp0.MMU(regFile[rs] + signedImme, MemOperation.WRITE, AccessMode.BYTE);
			if(dataBreakPoint.isBreak(paddr) && status == CPUStatus.DEBUG){
				doBreak();
				return;
			}
			bus.write(paddr , regFile[rt], AccessMode.BYTE);
			break;
		}
		case OpCode.SH:
			// sh rt, signedImme(rs)
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			int paddr = cp0.MMU(regFile[rs] + signedImme, MemOperation.WRITE, AccessMode.HALFWORD);
			if(dataBreakPoint.isBreak(paddr) && status == CPUStatus.DEBUG){
				doBreak();
				return;
			}
			bus.write( paddr, regFile[rt], AccessMode.HALFWORD);
			break;
		}
		case OpCode.LW:
			// lw rt, signedImme(rs)
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, bus.read(cp0.MMU(regFile[rs] + signedImme, MemOperation.READ , AccessMode.WORD), AccessMode.WORD, false));
			break;
		}
		case OpCode.LB:
			// lbu rt, offset(rs)
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, bus.read(cp0.MMU(regFile[rs] + signedImme, MemOperation.READ, AccessMode.BYTE), AccessMode.BYTE, true));
			break;
		}
		case OpCode.LBU:
			// lbu rt, offset(rs)
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, bus.read(cp0.MMU(regFile[rs] + signedImme, MemOperation.READ, AccessMode.BYTE), AccessMode.BYTE, false));
			break;
		}
		case OpCode.LH:
			// lh rt, offset(rs)
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, bus.read(cp0.MMU(regFile[rs] + signedImme, MemOperation.READ, AccessMode.HALFWORD), AccessMode.HALFWORD, true));
			break;
		}
		case OpCode.LHU:
			// lhu rt, offset(rs)
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, bus.read(cp0.MMU(regFile[rs] + signedImme, MemOperation.READ, AccessMode.HALFWORD), AccessMode.HALFWORD, false));
			break;
		}
		case OpCode.SLTI:
			// slti rt, rs, signedImme
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, (regFile[rs] < signedImme) ? 1 : 0);
			break;
		}
		case OpCode.SLTIU:
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			writeReg(rt, compareAsUnsignedInt(regFile[rs] , signedImme));
			break;
		}
		case OpCode.BEQ:
			// beq rs, rt, label
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			isJmpOrBranch = true;
			if (regFile[rs] == regFile[rt])
				pcNext = pcPlus4 + (signedImme << 2);
			else
				pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;//if delay slot, we should take the pc+8 as the next instruction
			return;
		}
		case OpCode.BNE:
			// bne rs, rt, label
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			isJmpOrBranch = true;
			if (regFile[rs] != regFile[rt])
				pcNext = pcPlus4 + (signedImme << 2);
			else
				pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
			return;
		}
		case OpCode.BLEZ:
			// blez rs, offset
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			if(rt != 0){
				cp0.SetEX(ExceptionCode.UnDefinedInst);
			}
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			isJmpOrBranch = true;
			if (regFile[rs] <= 0) 
			{
				pcNext = pcPlus4 + (signedImme << 2);
				//regFile[31] = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
			}
			else
			{
				pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
			}
			return;
		}
		case OpCode.BGTZ:
			// bgtz rs, offset
		{
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			if(rt != 0){
				cp0.SetEX(ExceptionCode.UnDefinedInst);
			}
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			isJmpOrBranch = true;
			if (regFile[rs] > 0) 
			{
				pcNext = pcPlus4 + (signedImme << 2);
				//regFile[31] = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
			}
			else
			{
				pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
			}
			return;
		}
		case OpCode.BZ:
		{
			isJmpOrBranch = true;
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int signedImme = ((instruction & 0xffff) << 16) >> 16;
			switch (rt) {
			case 0x0://bltz
			{
				if (regFile[rs] < 0)
					pcNext = pcPlus4 + (signedImme << 2);
				else 
					pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
				return;
			}
			case 0x1: // bgez
			{
				if (regFile[rs] >= 0)
					pcNext = pcPlus4 + (signedImme << 2);
				else 
					pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
				return;
			}
			case 0x10://bltzal
			{
				if (regFile[rs] < 0)
				{
					pcNext = pcPlus4 + (signedImme << 2);
					regFile[31] = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
				}
				else 
					pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
				return;
			}
			case 0x11://bgezal
			{
				if (regFile[rs] >= 0)
				{
					pcNext = pcPlus4 + (signedImme << 2);
					regFile[31] = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
				}
				else 
					pcNext = isDelaySlot ? (pcPlus4 + 4) : pcPlus4;
				return;
			}
			default:
				break;
			}
			return;
		}
		case OpCode.COP0: // Coprocessor 0
		{
			if(cp0.GetLevel() == 1)
				cp0.SetEX(ExceptionCode.IllegalInst);
			
			int func = (instruction >> 0) & 0x3f;
			int rs = (instruction >> 21) & 0x1f;
			int rt = (instruction >> 16) & 0x1f;
			int rd = (instruction >> 11) & 0x1f;
			switch (rs) {
			case 0:
				this.writeReg(rt, cp0.ReadReg(rd));
				break;
			case 0x04:
				cp0.WriteReg(rd, regFile[rt]);
				break;
			case 0x10:
				if(func == 0x18)
				{
					cp0.DoEret();
					pcNext = cp0.ReadReg(CP0Register.EPCR) & 0xfffffffc;
					isJmpOrBranch = false;
				}
				return;
			default:
				cp0.SetEX(ExceptionCode.UnDefinedInst);
				break;
			}
			break;
		}
		case OpCode.CACHE:
		{
			if(cp0.GetLevel() == 1)
				cp0.SetEX(ExceptionCode.IllegalInst);
			break;
		}
		default:
		{
			cp0.SetEX(ExceptionCode.UnDefinedInst);
		}
		}
		
		pcNext = pcPlus4;
		isJmpOrBranch = false;
	}
	
	private void writeReg(int index, int data)
	{
		if(index != 0) 
		{
			regFile[index] = data; 
			regWrite = index;
		}
	}
	
	private static int compareAsUnsignedInt(int x, int y)//if x < y return 1, else return 0
	{
		return ((x ^ y) >=0 ? x - y : y) >>> 31;
	}
	
	public int ReadReg(int index)
	{
		return regFile[index];
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
		this.pcNext = pc;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public int getStatus() {
		return status;
	}

	public int getRegWrite()
	{
		return regWrite;
	}

	public long getConsumingTime()
	{
		return consumingTime;
	}

	public void setDelaySlot(boolean isDelaySlot)
	{
		this.isDelaySlot = isDelaySlot;
	}

	public int getPcNext()
	{
		if(status == 0) return pcInit;
		return isJmpOrBranch && isDelaySlot? pcPlus4 : pcNext;
	}

	public int getSpeedCount()
	{
		return speedCount;
	}

	public int getSpeedMask()
	{
		return speedMask;
	}

	public BreakPoint getBreakPoint() {
		return breakPoint;
	}

	public void setPcInit(int pcInit)
	{
		this.pcInit = pcInit;
	}	

}
