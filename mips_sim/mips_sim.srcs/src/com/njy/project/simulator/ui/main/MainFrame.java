package com.njy.project.simulator.ui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import com.njy.project.simulator.cpu.CPUStatus;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.keyboard.KeyboardCtrl;
import com.njy.project.simulator.device.vga.VGACtrl;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.UIInterface;
import com.njy.project.simulator.ui.main.inspector.MainInspector;
import com.njy.project.simulator.ui.run.RunFrame;

public class MainFrame extends JFrame implements UIInterface{
	
	public static void  main(String args[]) throws IOException
	{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				// TODO �Զ���ɵķ������
				MainFrame.getInstance();
				
			}
		
		});
		

	}
	
	private MainFrame()
	{
		// TODO �Զ���ɵĹ��캯����
		super("MIPS Simulator");
		UIController uiController = UIController.getInstance();
		uiController.AddUI(this);
		mainConsole = MainConsole.getInstance();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setJMenuBar(simulatorMenu);
		setLayout(new BorderLayout());
		add(simulatorToolBar, BorderLayout.NORTH);
		add(mainInspector, BorderLayout.CENTER);
		add(mainConsole, BorderLayout.SOUTH);
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getWidth()) >> 1, (dim.height - getHeight()) >> 1);
		addWindowListener(new WindowListener()
		{
			
			@Override
			public void windowOpened(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void windowIconified(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				DataController.getInstance().saveConfig();
			}
			
			@Override
			public void windowClosed(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void windowActivated(WindowEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
		});
		setVisible(true);
		uiController.RefreshAll();
		//new Thread(new Refresher()).start();
		//vgaCtrl.requestFocusInWindow();
		//
	}
	
	@Override
	public void Refresh() {
		if(DataController.getInstance().getStatus() == CPUStatus.DEBUGPAUSED)
		{
			this.requestFocus();
		}
		
		setTitle("MIPS SIMULATOR - " + CPUStatus.statusDescription[DataController.getInstance().getStatus()]);
	}
	
	public static MainFrame getInstance()
	{
		if(instance == null)
			instance = new MainFrame();
		
		return instance;
	}
	
	private static MainFrame instance= null;
	private MainMenuBar simulatorMenu = new MainMenuBar();
	private MainToolBar simulatorToolBar = new MainToolBar();
	private MainConsole mainConsole;
	private MainInspector mainInspector = new MainInspector();

	
}
