package com.njy.project.simulator.device.board;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;

public class BoardSwitchCtrl extends JPanel
{
	private Switch[] sw = new Switch[8];
	private ImageIcon[] imageIcon = new ImageIcon[2];
	private DeviceBoard deviceBoard = null;
	public BoardSwitchCtrl()
	{
		// TODO �Զ���ɵĹ��캯����
		TheActionListener actionListener = new TheActionListener();
		imageIcon[0] = new ImageIcon("./ui/icon_active/off.png");
		imageIcon[1] = new ImageIcon("./ui/icon_active/on.png");
		this.setLayout(new BorderLayout());
		JPanel panelUp = new JPanel();
		JPanel panelDown = new JPanel();
		panelUp.setLayout(new GridLayout(1, 8));
		panelDown.setLayout(new GridLayout(1, 8));
		for(int i = 0; i < 8 ;i++)
		{
			int idx= 7- i;
			sw[idx] = new Switch();
			sw[idx].setActionCommand(""+idx);
			sw[idx].addActionListener(actionListener);
			sw[idx].setPreferredSize(new Dimension(50,80));
			panelUp.add(sw[idx]);
		}
		
		for(int i = 0; i < 8 ;i++)
		{
			panelDown.add(new JLabel("SW_" + (7-i), JLabel.CENTER));
		}
		
		this.add(panelUp, BorderLayout.NORTH);
		this.add(panelDown, BorderLayout.SOUTH);
	}
	
	public void setDeviceBoard(DeviceBoard deviceBoard)
	{
		this.deviceBoard = deviceBoard;
	}
	
	private class TheActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//System.out.println(event.getActionCommand());
			int idx = Integer.parseInt(event.getActionCommand());
			
			if(deviceBoard != null)
			{
				deviceBoard.SetSwitch(sw[idx].ShiftState());
				DataController.getInstance().getCp0().SetIRC(InterruptCode.BtnSW);
			}
		}
	}
	
	private class Switch extends JButton
	{
		private int state = 0;
		public Switch()
		{
			setIcon(imageIcon[state]);
		}
		
		public int ShiftState()
		{
			if(state == 0) 
			{
				setIcon(imageIcon[state = 1]);		
			}
			else 
			{
				setIcon(imageIcon[state = 0]);
			}
			return state;
		}
		
		public int getState()
		{
			return state;
		}
	}
	
	public static void main(String args[])
	{
		JFrame jFrame = new JFrame("test");
		jFrame.add(new BoardSwitchCtrl());
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}




