package com.njy.project.simulator.device.board;


import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.AbstractDevice;

public class DeviceBoard
{
	private AbstractDevice abstractDevice = null;
	
	public DeviceBoard()
	{
		// TODO �Զ���ɵĹ��캯����
		abstractDevice = (AbstractDevice)DataController.getInstance().getBus().getDevice(InterruptCode.BtnSW);
		//super(0xffff0000 + (InterruptCode.BtnSW << 8), 0x100);
		//index = InterruptCode.BtnSW;
	}
	
	final public int ReadData(int mode)
	{
		//int data = 0;
		int base = 0x04 + (mode << 2);
		int [] ram = abstractDevice.getData();
//		for(int i = 0; i < 4; i++)
//		{
//			data |= (ram[base + i] & 0xff ) << (i << 3);
//		}
		
		return ram[base];
	}
	
	public void SetSwitch(int bit)
	{
		int[] ram = abstractDevice.getData();
		ram[0] |= 1 << bit; 
	}
	
	public void ResetSwitch(int bit)
	{
		int[] ram = abstractDevice.getData();
		ram[0] &= ~(1 << bit); 
	}
	
	public void SetBtn(int bit)
	{
		int[] ram = abstractDevice.getData();
		ram[1] |= 1 << (bit - 8);
	}
	
	public void ResetBtn(int bit)
	{
		int[] ram = abstractDevice.getData();
		ram[1] &= ~(1 << (bit - 8));
	}
}
