package com.njy.project.simulator.device;

import java.io.IOException;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.file.FileInterface;
import com.njy.project.simulator.util.Util;


public class MainMemory extends AbstractDevice {
	private int loadAddr = 0;
	
 	public MainMemory(int base, int size) {
		super(base, size);
		// TODO Auto-generated constructor stub
	}
	
	final public int GetRamSize()
	{
		return ramSize;
	}
	
	final public void readBlock(int addr, int size, int[] buffer) 
	{
		if(Util.compareIntIgnoreSign(addr, ramSize) >= 0)
			return;
		if(addr + size >= ramSize)
			size = ramSize - addr;
		
		System.arraycopy(ram, addr>>>2, buffer, 0, size>>>2);
	}
	
	public void initialRam(FileInterface fileInterface) throws IOException, IndexOutOfBoundsException
	{
		int address = loadAddr ;
		int b;
		while ((b = fileInterface.readByte())!= -1) 
		{
			write(address, b, AccessMode.BYTE);
			address++;
		}
		
		fileInterface.close();

	}

	public void setLoadAddr(int loadAddr) {
		this.loadAddr = loadAddr;
	}

	public int getLoadAddr() {
		return loadAddr;
	}

}
