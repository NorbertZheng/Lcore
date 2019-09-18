package com.njy.project.simulator.device.board;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BoardLEDCtrl extends JPanel
{
	private ImageIcon[] imageIcon = new ImageIcon[2];
	private LED[] led = new LED[8];
	public BoardLEDCtrl()
	{
		// TODO �Զ���ɵĹ��캯����
		imageIcon[0] = new ImageIcon("./ui/icon_inactive/LED_off.png");
		imageIcon[1] = new ImageIcon("./ui/icon_active/LED_on.png");
		this.setLayout(new BorderLayout());
		JPanel panelUp = new JPanel();
		JPanel panelDown = new JPanel();
		panelUp.setLayout(new GridLayout(1, 8));
		panelDown.setLayout(new GridLayout(1, 8));
		for(int i = 0; i < 8 ;i++)
		{
			led[7 - i] = new LED();
			panelUp.add(led[7 - i]);
		}
		
		for(int i = 0; i < 8 ;i++)
		{
			panelDown.add(new JLabel("LED_" + (7-i), JLabel.CENTER));
		}
		
		this.add(panelUp, BorderLayout.NORTH);
		this.add(panelDown, BorderLayout.SOUTH);
	}
	
	public void SetData(int d)
	{
		for(int i= 0; i< 8; i++)
		{
			led[i].SetState((d >>> i) & 1);
		}
	}
	
	private class LED extends JLabel
	{
		private int  state = 0;
		public LED()
		{
			// TODO �Զ���ɵĹ��캯����
			setIcon(imageIcon[state]);
		}
		
		public void SetState(int s)
		{
			setIcon(imageIcon[state = s]);
		}
	}
	
	public static void main(String args[]) throws InterruptedException
	{
		JFrame jFrame = new JFrame("test");
		BoardLEDCtrl boardDispCtrl = new BoardLEDCtrl();
		jFrame.add(boardDispCtrl);
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int i = 0;
		while(true)
		{
			Thread.sleep(1000);
			boardDispCtrl.SetData(i++);
			//boardDispCtrl.SetAllDot((i++) & 0xf);
		}
	}
}
