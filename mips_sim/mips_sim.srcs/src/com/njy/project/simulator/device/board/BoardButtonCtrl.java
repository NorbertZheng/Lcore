package com.njy.project.simulator.device.board;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;

public class BoardButtonCtrl extends JPanel
{
	private JButton BL, BR, BU, BD, BS;
	private DeviceBoard deviceBoard = null;
	public BoardButtonCtrl()
	{
		// TODO �Զ���ɵĹ��캯����
		Dimension dimension = new Dimension(30,30);
		
		BL = new JButton();
		BL.setActionCommand("12");
		BL.setIcon(new ImageIcon("./ui/icon_active/BL.png"));
		BL.setPressedIcon(new ImageIcon("./ui/icon_active/BLp.png"));
		BL.addMouseListener(new TheActionListener(12));
		BL.setPreferredSize(dimension);
		
		BR = new JButton();
		BR.setActionCommand("11");
		BR.setIcon(new ImageIcon("./ui/icon_active/BR.png"));
		BR.setPressedIcon(new ImageIcon("./ui/icon_active/BRp.png"));
		BR.addMouseListener(new TheActionListener(11));
		BR.setPreferredSize(dimension);
		
		BU = new JButton();
		BU.setActionCommand("10");
		BU.setIcon(new ImageIcon("./ui/icon_active/BU.png"));
		BU.setPressedIcon(new ImageIcon("./ui/icon_active/BUp.png"));
		BU.addMouseListener(new TheActionListener(10));
		BU.setPreferredSize(dimension);
		
		BD = new JButton();
		BD.setActionCommand("9");
		BD.setIcon(new ImageIcon("./ui/icon_active/BD.png"));
		BD.setPressedIcon(new ImageIcon("./ui/icon_active/BDp.png"));
		BD.addMouseListener(new TheActionListener(9));
		BD.setPreferredSize(dimension);
		
		BS = new JButton();
		BS.setActionCommand("8");
		BS.setIcon(new ImageIcon("./ui/icon_active/BS.png"));
		BS.setPressedIcon(new ImageIcon("./ui/icon_active/BSp.png"));
		BS.addMouseListener(new TheActionListener(8));
		BS.setPreferredSize(dimension);
		
		this.setLayout(new GridLayout(3, 3, 5, 5));
		setPreferredSize(new Dimension(100,100));
		this.add(new JLabel());this.add(BU);this.add(new JLabel());
		this.add(BL);            this.add(BS);this.add(BR);
		this.add(new JLabel());this.add(BD);this.add(new JLabel());
	}
	
	
	public void setDeviceBoard(DeviceBoard deviceBoard)
	{
		this.deviceBoard = deviceBoard;
	}

	private class TheActionListener implements MouseListener {
		int idx;
		public TheActionListener(int idx)
		{
			// TODO �Զ���ɵĹ��캯����
			this.idx = idx;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			// TODO �Զ���ɵķ������
			
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// TODO �Զ���ɵķ������
			
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO �Զ���ɵķ������
			
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			// TODO �Զ���ɵķ������
			
			if(e.getButton() == MouseEvent.BUTTON1 & deviceBoard != null)
			{
				deviceBoard.SetBtn(idx);
				DataController.getInstance().getCp0().SetIRC(InterruptCode.BtnSW);
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			// TODO �Զ���ɵķ������
			if(e.getButton() == MouseEvent.BUTTON1 & deviceBoard != null)
			{
				deviceBoard.ResetBtn(idx);
				DataController.getInstance().getCp0().SetIRC(InterruptCode.BtnSW);
			}
		}
	}
	
	
	
	public static void main(String args[])
	{
		JFrame jFrame = new JFrame("test");
		jFrame.add(new BoardButtonCtrl());
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
}
