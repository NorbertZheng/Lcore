package com.njy.project.simulator.cp0.timer;
import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cp0.CP0Register;
import com.njy.project.simulator.cp0.InterruptCode;

public class Timer implements Runnable
{
	private volatile int time;//��ֹ���Ż�
	private CP0 cp0;
	private boolean running = true;

	public Timer(CP0 cp0)
	{
		// TODO Auto-generated constructor stub
		this.cp0 = cp0;
	}

	public void run()
	{
		// TODO Auto-generated method stub
		//System.out.println("timer");
		try
		{
			while (running)
			{
				// System.gc();
				time = (cp0.ReadReg(CP0Register.TIR) & 0x0fff);
				if (time > 0)
				{
					//System.out.println(time);
					Thread.sleep(time);
					//System.out.println("wait over");
					cp0.SetIRC(InterruptCode.Timer);
				}
			}
		} 
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		running = false;
	}
	
	public void start()
	{
		running = true;
		new Thread(Timer.this).start();
	}

}
