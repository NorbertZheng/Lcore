package com.njy.project.simulator.device.keyboard;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.AbstractDevice;

public class DeviceKeyboard{
	private AbstractDevice abstractDevice = null;
	public DeviceKeyboard() {	
//		super(0xFFFF0000+(InterruptCode.Keyboard << 8), 256);	
//		index = InterruptCode.Keyboard;
		abstractDevice = (AbstractDevice)DataController.getInstance().getBus().getDevice(InterruptCode.Keyboard);
	}
	
	final public void WriteKeyCode(int c)
	{
		System.out.println(String.format("keycode:%02X", c));
		abstractDevice.writeRawAddr(0xc, c, AccessMode.WORD);
	}
}
