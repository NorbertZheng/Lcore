package com.njy.project.simulator.device.vga;
import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.AbstractDevice;

public class DeviceVGA{
	private AbstractDevice abstractDevice = null;
	private int[] ram;
	private boolean cursorChangeRead = false;
	public DeviceVGA() {	
		abstractDevice = (AbstractDevice)DataController.getInstance().getBus().getDevice(InterruptCode.VGA);
		ram = abstractDevice.getData();
//		super(0xFFFF0000+(InterruptCode.VGA << 8), 256);	
//		index = InterruptCode.VGA;
		//ram[0x03] |= 1 << 7;//
		//ram[0] = 1;abstractDevice
		//write(0xFFFF0000+(InterruptCode.VGA << 8)+ 0x0c, 200, AccessMode.HALFWORD);
		//write(0xFFFF0000+(InterruptCode.VGA << 8)+ 0x08, (15 << 16)|40, AccessMode.WORD);
	}
	
	final public int GetVGAMode() 
	{
		return (ram[0] >>> 24)>>>7;
	}
	
	final public boolean IsHWCursorEnabled()
	{
		return (((ram[0] >>> 24)>>>6) & 0x01)==1;
	}
	
	final public int GetVGARes() 
	{
		return ram[0] & 0x0f;
	}
	
	final public int GetVRAM_ADDR() 
	{
//		int data = 0;
//		for (int i = 0; i < 4; i++) {
//			data |= (ram[0x04 + i] & 0xff) << ( i << 3);
//		}	
		
		return ram[0x1];
	}
	 
	final public int GetCVPos() 
	{
//		int data = 0;
//		for (int i = 0; i < 2; i++) {
//			data |= (ram[0x0A + i] & 0xff) << ( i << 3);
//		}	
		return ram[0x02] >>> 16;
	}
	
	
	final public int GetCHPos() 
	{
//		int data = 0;
//		for (int i = 0; i < 2; i++) {
//			data |= (ram[0x08 + i] & 0xff) << ( i << 3);
//		}	
		return ram[0x02] & 0xffff;
	}
	
	final public int GetCFlashTime()
	{
		return ram[0x03] & 0xffff;
	}
	
	final public int IsForceLighted()
	{
		return (ram[0x03] >>> 16) >>> 7;
	}
	
	final public boolean IsCursorChanged()
	{
		return (((abstractDevice.getLastWrite() & 0xff) >>> 2) == 2) && (!abstractDevice.lastWriteRead());

	}
	
}
