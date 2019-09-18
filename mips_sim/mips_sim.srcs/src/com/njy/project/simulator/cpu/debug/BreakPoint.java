package com.njy.project.simulator.cpu.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.device.DeviceRom;
import com.njy.project.simulator.device.MainMemory;

public class BreakPoint {
	private HashMap<Integer, Integer> romBrkPointHashMap = new HashMap<>();
	private HashMap<Integer, Integer> ramBrkPointHashMap = new HashMap<>();
	private MainMemory mainMemory;
	private DeviceRom deviceRom;
	final private int breakInst = 0x0000003f;
	private boolean isBreaked = false;
	public BreakPoint(Bus bus) 
	{
		// TODO �Զ����ɵĹ��캯�����
		mainMemory = bus.getMainMemory();
		deviceRom = bus.getRom();
	}
	
	final public boolean AddBreakPoint(int addr)
	{
		addr &= 0xfffffffc;
		if((addr & 0xff000000) == 0xff000000)
		{
			romBrkPointHashMap.put(addr, deviceRom.read(addr));
		}
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
		{
			ramBrkPointHashMap.put(addr, mainMemory.read(addr));
		}
		else 
			return false;
					
		return true;
	}
	
	final public boolean AddAndApplyBreakPoint(int addr)
	{
		addr &= 0xfffffffc;
		if((addr & 0xff000000) == 0xff000000)
		{
			romBrkPointHashMap.put(addr, deviceRom.read(addr));
			deviceRom.write(addr, breakInst);
		}
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
		{
			ramBrkPointHashMap.put(addr, mainMemory.read(addr));
			mainMemory.write(addr, breakInst);
		}
		else 
			return false;
					
		return true;
	}
	
	final public boolean ApplyBreakPoint(int addr)
	{
		addr &= 0xfffffffc;
		if((addr & 0xff000000) == 0xff000000)
		{
			if(romBrkPointHashMap.containsKey(addr))
				deviceRom.write(addr, breakInst);
		}
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
		{
			if(ramBrkPointHashMap.containsKey(addr))
				mainMemory.write(addr, breakInst);
		}
		else 
			return false;
					
		return true;
	}
	
	
	final public boolean RemoveBreakPoint(int addr)
	{
		if((addr & 0xff000000) == 0xff000000)
		{
			if(romBrkPointHashMap.containsKey(addr))
			{
				deviceRom.write(addr, romBrkPointHashMap.get(addr));
				romBrkPointHashMap.remove(addr);
			}
			
		}
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
		{
			if(ramBrkPointHashMap.containsKey(addr))
			{
				mainMemory.write(addr, ramBrkPointHashMap.get(addr));
				ramBrkPointHashMap.remove(addr);
			}
		}
		else 
			return false;
					
		return true;
	}
	
	final public boolean RestoreBreakPoint(int addr)
	{
		if((addr & 0xff000000) == 0xff000000)
		{
			if(romBrkPointHashMap.containsKey(addr))
				deviceRom.write(addr, romBrkPointHashMap.get(addr));
		}
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
		{
			if(ramBrkPointHashMap.containsKey(addr))
				mainMemory.write(addr, ramBrkPointHashMap.get(addr));
		}
		else 
			return false;
					
		return true;
	}
	
	final public boolean IsContainBreakPoint(int addr)
	{
		if((addr & 0xff000000) == 0xff000000)
		{
			return romBrkPointHashMap.containsKey(addr);
		}
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
		{
			return ramBrkPointHashMap.containsKey(addr);
		}
		
		return false;
	}
	
	final public ArrayList<Long> GetOnePageBreak(int addr)
	{
		ArrayList<Long> res = new ArrayList<>();
		addr &= 0xfffff000;
		Iterator<Map.Entry<Integer, Integer>> iter;
		if((addr & 0xff000000) == 0xff000000)
			iter = romBrkPointHashMap.entrySet().iterator();
		else if (mainMemory.isValidAddr(addr & 0xfffffffc, AccessMode.WORD))
			iter = ramBrkPointHashMap.entrySet().iterator();
		else
			return res;
		
		Map.Entry<Integer, Integer> entry;
		while (iter.hasNext()) 
		{
			entry = iter.next();
			int address = entry.getKey();
			if((address & 0xfffff000) == addr)
			{
				res.add((((long)address) << 32) | ((long)entry.getValue()) & 0xffffffffL);
			}
		}
		
		return res;
	}
	
	final public void ApplyBreakPoint()
	{
		Iterator<Map.Entry<Integer, Integer>> iter = ramBrkPointHashMap.entrySet().iterator();
		Map.Entry<Integer, Integer> entry;
		int addr;
		while (iter.hasNext()) 
		{
			entry = iter.next();
			addr = entry.getKey();
			mainMemory.write(addr, breakInst);
		}
		
		iter = romBrkPointHashMap.entrySet().iterator();
		
		while (iter.hasNext()) 
		{
			entry = iter.next();
			addr = entry.getKey();
			deviceRom.write(addr, breakInst);
		}
	}
	
	final public void RestoreBreakPoint()
	{
		Iterator<Map.Entry<Integer, Integer>> iter = ramBrkPointHashMap.entrySet().iterator();
		Map.Entry<Integer, Integer> entry;
		int addr, inst;
		while (iter.hasNext()) 
		{
			entry = iter.next();
			addr = entry.getKey();
			inst = entry.getValue();
			mainMemory.write(addr, inst);
		}
		
		iter = romBrkPointHashMap.entrySet().iterator();
		
		while (iter.hasNext()) 
		{
			entry = iter.next();
			addr = entry.getKey();
			inst = entry.getValue();
			deviceRom.write(addr, inst);
		}
	}

	public boolean isBreaked() {
		return isBreaked;
	}

	public void setBreaked(boolean isBreaked) {
		this.isBreaked = isBreaked;
	}
	
}
