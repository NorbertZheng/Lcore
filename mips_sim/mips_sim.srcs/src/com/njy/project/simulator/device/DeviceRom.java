package com.njy.project.simulator.device;
import java.io.IOException;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.file.FileInterface;
import com.njy.project.simulator.util.Util;

public class DeviceRom extends AbstractDevice{
	
	public  DeviceRom() {
		// TODO �Զ���ɵĹ��캯����
		super(0xFF000000, 16 * Util.oneM - 64 * Util.oneK );
	}
	
	final public int GetBase()
	{
		return ramBase;
	}
	
	public void initialRom(FileInterface fileInterface)  throws IOException, IndexOutOfBoundsException
	{
		int address = this.ramBase;
		int b;
		while ((b = fileInterface.readByte())!= -1) 
		{	
			write(address, b, AccessMode.BYTE);
			address++;
		}
		fileInterface.close();	
	}
	
	final public void readBlock(int addr, int size, int[] buffer) 
	{
		addr-= ramBase;
		if(addr >= ramSize)
			return;
		if(addr + size >= ramSize)
			size = ramSize - addr;
		
		System.arraycopy(ram, addr>>>2, buffer, 0, size >>> 2);
	}

}
