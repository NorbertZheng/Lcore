package com.njy.project.simulator.ui.run;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.njy.project.simulator.cpu.CPUStatus;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.keyboard.KeyboardCtrl;
import com.njy.project.simulator.device.vga.VGACtrl;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.UIInterface;
import com.njy.project.simulator.ui.main.MainConsole;
import com.njy.project.simulator.util.Util;

public class RunFrame extends JFrame implements Runnable, UIInterface
{
	public RunFrame() throws Exception
	{
		// TODO �Զ���ɵĹ��캯����
		
		setResizable(false);
		setSize(400, 400);
		dataController = DataController.getInstance();
		if(dataController.isRamFileLoaded())
			this.fileName = Util.getPathFileString(dataController.getRamFile());
		else if(dataController.isRomFileLoaded())
			this.fileName = Util.getPathFileString(dataController.getRomFile());
		else
			throw new Exception("no file loaded");
		UIController.getInstance().AddUI(this);
		setTitle(this.fileName);
		vgaCtrl = new VGACtrl();
		vgaCtrl.setDisabled(false);
		vgaCtrl.addKeyListener(new KeyboardCtrl());
		add(vgaCtrl);
		new Thread(this).start();
		//vgaCtrl.requestFocusInWindow();
		pack();
		setVisible(true);
		//setAlwaysOnTop(true);
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				instance = null;
				UIController.getInstance().RefreshAll();
				UIController.getInstance().RemoveUI(RunFrame.this);
				RunFrame.this.dispose();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static RunFrame createFrame()
	{
		try {
			if(instance == null)
				instance = new RunFrame();
		} catch (Exception e) {
			// TODO: handle exception
			MainConsole.getInstance().InsertMessage("Error:" + e.getMessage());
		}
		return instance;
	}
	
	public static void destroyFrame()
	{
		if(instance!=null)
			instance.dispose();
		instance = null;
	}
	
	public static RunFrame getInstance() 
	{
		return instance;
	}
	
	@Override
	public void run()
	{
		// TODO �Զ���ɵķ������
		while(true)
		{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO �Զ���ɵ� catch ��
				e.printStackTrace();
			}
			long consumeTime =  dataController.getMips_CPU().getConsumingTime();
			if(dataController.isShowCPUSpeed() && (dataController.getStatus() & 0x1) == 1 && consumeTime != 0)
			{
				double freq = 1.0 * dataController.getMips_CPU().getSpeedCount() / consumeTime * 1e3; 
				setTitle(fileName + " - " + CPUStatus.statusDescription[dataController.getStatus()] +  " Speed:" + String.format("%6f", freq) + "MIPS");
			}
			else 
			{
				setTitle(fileName + " - " + CPUStatus.statusDescription[dataController.getStatus()]);
			}
		}
	}
	
	@Override
	public void Refresh()
	{
		// TODO �Զ���ɵķ������
		//setTitle(fileName + " - " + status[dataController.getStatus()]);
	}
	
	private static RunFrame instance = null;
	private VGACtrl vgaCtrl;
	private DataController dataController;
	private String fileName;
}
