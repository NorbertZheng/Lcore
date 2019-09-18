package com.njy.project.simulator.test;

import java.io.IOException;

import javax.swing.JFrame;

import com.njy.project.simulator.device.vga.VGACtrl;

public class VGATest extends JFrame{
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private VGACtrl vga;
	
	public VGATest() throws IOException
	{
		// TODO Auto-generated constructor stub
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		vga = new VGACtrl();
		setSize(WIDTH, HEIGHT);
		add(vga);
		pack();
		//vga.initTest();
	}
	
	
	public void Test()
	{
		vga.initTestGraphic();
		
	}
	
	public void Refresh()
	{
		vga.repaint();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		final VGATest vgaTestFrame = new VGATest();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO �Զ���ɵķ������
				while(true)
				{
					try {
						Thread.sleep(3000);
						vgaTestFrame.Test();
					} catch (InterruptedException e) {
						// TODO �Զ���ɵ� catch ��
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();
		
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO �Զ���ɵķ������
				while(true)
				{
					try {
						Thread.sleep(16);
						vgaTestFrame.Refresh();
					} catch (InterruptedException e) {
						// TODO �Զ���ɵ� catch ��
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();*/
	}

}

