package com.njy.project.simulator.device.spi;

import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;

public class SPICtrl implements Runnable
{
	private SPISlave[] spiSlaves = new SPISlave[16];
	private DeviceSPI deviceSPI;
	private int selector = 0;
	private SPISlave selectedSlave = null;
	private boolean enabled = true;
	private CP0 cp0;
	public SPICtrl(CP0 cp0, Bus bus)
	{
		// TODO �Զ���ɵĹ��캯����
		this.cp0 = cp0;
		deviceSPI = new DeviceSPI();
		bus.mountDevice(deviceSPI);//force mount
		start();
	}
	
	public void addSlave(SPISlave spiSlave, int index)
	{
		spiSlaves[index] = spiSlave;
	}
	
	private SPISlave selectSlave()
	{
		for(int i = 0; i < 16; i++)
		{
			if(((selector >>> i) & 1) != 0)
				return spiSlaves[i];
		}
		
		return null;
	}
	
	public void disable()
	{
		enabled = false;
	}
	
	public void start()
	{
		enabled = true;
		new Thread(this).start();
	}
	
	@Override
	public void run()
	{
		// TODO �Զ���ɵķ������
		while(enabled)
		{
			SPIrunning();
		}
		
	}
	
	private void SPIrunning()
	{
		int selectorNow ;
		while(deviceSPI.isEnabled() && enabled)
		{
			//System.out.println("in!");
			if(deviceSPI.transBufferEmpty()) 
			{
				if(deviceSPI.getTR())
				{
					//cp0.SetIRC(InterruptCode.SPI);
				}
				deviceSPI.setTR(0);
				deviceSPI.setTE(1);
				continue;
			}
			deviceSPI.setTR(1);
			deviceSPI.setTE(0);
			selectorNow = deviceSPI.getSelectedSlave();
			if(selectorNow != selector)
			{
				selector = selectorNow;
				selectedSlave = selectSlave();
			}
			
			if(selectedSlave == null) continue;
			synchronized (deviceSPI)
			{
				selectedSlave.transmitData(deviceSPI);
			}
			
		}
	}
	
	
	
	
	public static void main(String args[])
	{
		//new SPICtrl();
	}
	
}
