package com.njy.project.simulator.device.spi.SD;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JProgressBar;

public class SDBuilder implements Runnable
{
	private long size;
	private String filePath;
	private long datCount = 0;
	private boolean running;
	private int state;
	private File file;
	private JProgressBar jProgressBar = null;
	
	public SDBuilder(long size,  String filePath) throws IOException
	{
		this.size = size;
		this.filePath = filePath;
		file = new File(filePath);
		
	}
	
	final public double getProgress()
	{
		if(size != 0)
			return datCount * 1.0 / size;
		
		return 0;
	}
	
	public void setProgress()
	{
		if(jProgressBar != null) 
		{
			jProgressBar.setValue((int)(getProgress() * 100));
		}
	}
	
	public void startBuilding()
	{
		running = true;
		state = 1;
		new Thread(this).start();
	}
	
	@Override
	public void run()
	{
		
		FileWriter fileWriter ;
		try
		{
			fileWriter = new FileWriter(file);
			for(datCount= 0; datCount < size && running; datCount++)
			{
				fileWriter.write(0);
			}
			fileWriter.flush();
			fileWriter.close();
		}
		catch (IOException e)
		{
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
			state = -1;
			return;
		}
		
		
		running = false;
		state = 0;
		
		if(datCount < size)
		{
			if(file.exists())
				file.delete();
		}
	}

	public void stop()
	{
		running = false;
		state = 0;
	}
	
	public int getState()
	{
		return state;
	}

	public void setjProgressBar(JProgressBar jProgressBar)
	{
		this.jProgressBar = jProgressBar;
	}

	public boolean isRunning()
	{
		return running;
	}

}
