package com.njy.project.simulator.device;

import java.util.Arrays;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.util.Util;

public class AbstractDevice implements Device {
	public AbstractDevice(int base, int size) {
		// TODO Auto-generated constructor stub
		initial(base, size );
	}
	
	public AbstractDevice(int base, int index, int shift)
	{
		// TODO �Զ����ɵĹ��캯�����
		initial( base+ (index << shift), 1 << shift);
		this.index = index;
	}

	@Override
	public void initial(int base, int size) {
		// TODO Auto-generated method stub
		this.ramBase = base;
		if ((base < 0) && (base + size > 0)) {
			size = 0xffffffff - base + 1;
		}
		this.ramSize = size;
		ram = new int[ramSize >>> 2];
		baseMask = size - 1;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		Arrays.fill(ram, 0);
	}

	@Override
	final public boolean isValidAddr(int address, int mode) {
		// TODO Auto-generated method stub
		return Util.compareIntIgnoreSign(ramBase, address) <= 0 
				&& Util.compareIntIgnoreSign(address + mode - 1, ramBase + ramSize -1) <= 0;
	}

	@Override
	final public boolean isValidAddr(int address) {
		// TODO Auto-generated method stub
		return isValidAddr(address, AccessMode.WORD);
	}

	@Override
	final public int read(int address, int mode, boolean signed) {
		//address -= ramBase;
		//for byte array
		address = transAddr(address);
		
		switch(mode)
		{
		case AccessMode.BYTE:
		{
			int data = 0;
			int offset = address & 0x3;
			data = ram[address >>> 2] << ((~offset) << 3);
			return signed? data >> 24: data >>> 24;
		}
		case AccessMode.HALFWORD:
		{
			int data = 0;
			int offset = (address & 0x3)>>>1;
			data = ram[address >>> 2] << ((~offset) << 4);
			return signed? data >> 16: data >>> 16;
		}
		case AccessMode.WORD:
			return ram[address >>> 2];
		}	
		return 0;
	};

	@Override
	final public int read(int address) {
		// TODO Auto-generated method stub
		return ram[transAddr(address) >>> 2];
	}

	@Override
	synchronized final public void write(int address, int data, int mode) {
		// TODO Auto-generated method stub
		//address -= ramBase;
		//System.out.println(String.format("%08X %08X", address, data));
		address = transAddr(address);
		//for byte array		
		switch(mode)
		{
		case AccessMode.BYTE:
		{
			int addr = address >>> 2;
//			int offset = address & 0x3;
//			switch (offset) {
//			case 0:
//				ram[addr] &= 0xffffff00;
//				ram[addr] |= data&0xff;
//				return;
//			case 1:
//				ram[addr] &= 0xffff00ff;
//				ram[addr] |= (data&0xff) << 8;
//				return;
//			case 2:
//				ram[addr] &= 0xff00ffff;
//				ram[addr] |= (data&0xff) << 16;
//				return;
//			case 3:
//				ram[addr] &= 0x00ffffff;
//				ram[addr] |= data << 24;
//				return;
//			}
			int offset = (address & 0x3) << 3;
			ram[addr] &= ~(0xff << offset);
			ram[addr] |= ((data&0xff) << offset);
			return;
		}
		case AccessMode.HALFWORD:
		{
			int addr = address >>> 2;
			int offset = ((address & 0x3)) << 3;
			ram[addr] &= ~((0xffff) << offset);
			ram[addr] |= (data & 0xffff) << offset;
//			int offset = address & 0x3;
//			if(offset == 0)
//			{
//				ram[addr] &= 0xffff0000;
//				ram[addr] |= data&0xffff;
//				return;
//			}
//			ram[addr] &= 0xffff;
//			ram[addr] |= data << 16;
			return;
		}
		case AccessMode.WORD:
		{
			ram[address >>> 2] = data;
			return;
		}
		}	
	}

	@Override
	final public void write(int address, int data) {
		// TODO Auto-generated method stub
		write(address, data, AccessMode.WORD);
	}
	
	@Override
	public int getLastWrite() {
		// TODO Auto-generated method stub
		lastWriteRead = true;
		return lastWrite;
	}
	
	public boolean lastWriteRead()
	{
		return lastWriteRead ;
	}
	
	final public int readRawAddr(int address, int mode, boolean signed)
	{

		switch(mode)
		{
		case AccessMode.BYTE:
		{
			int data = 0;
			int offset = address & 0x3;
			data = ram[address >>> 2] << ((~offset) << 3);
			return signed? data >> 24: data >>> 24;
		}
		case AccessMode.HALFWORD:
		{
			int data = 0;
			int offset = (address & 0x3)>>>1;
			data = ram[address >>> 2] << ((~offset) << 4);
			return signed? data >> 16: data >>> 16;
		}
		case AccessMode.WORD:
			return ram[address >>> 2];
		}	
		return 0;
	}
	
	final public void writeRawAddr(int address, int data, int mode)
	{
		switch(mode)
		{
		case AccessMode.BYTE:
		{
			int addr = address >>> 2;
			int offset = (address & 0x3) << 3;
			ram[addr] &= ~(0xff << offset);
			ram[addr] |= ((data&0xff) << offset);
			return;
		}
		case AccessMode.HALFWORD:
		{
			int addr = address >>> 2;
			int offset = ((address & 0x3)) << 3;
			ram[addr] &= ~((0xffff) << offset);
			ram[addr] |= (data & 0xffff) << offset;
			return;
		}
		case AccessMode.WORD:
		{
			ram[address >>> 2] = data;
			return;
		}
		}	
	}
	
	final public int[] getData()
	{
		return ram;
	}
	
	@Override
	final public int getindex()
	{
		// TODO �Զ����ɵķ������
		return index;
	}
	
	private int transAddr(int addr) {
		// TODO Auto-generated method stub
		return addr & baseMask;
	}

	private int lastWrite = 0;
	private boolean lastWriteRead = false;
	protected int index;
	protected int ramBase;
	protected int ramSize;
	protected int baseMask = 0;
	protected int[] ram;

}
