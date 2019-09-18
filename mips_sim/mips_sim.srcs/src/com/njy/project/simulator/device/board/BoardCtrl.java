package com.njy.project.simulator.device.board;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.Device;

public class BoardCtrl extends JFrame implements Runnable
{
	private DeviceBoard deviceBoard;
	
	private BoardButtonCtrl boardButtonCtrl;
	private BoardSwitchCtrl boardSwitchCtrl;
	private BoardLEDCtrl boardLEDCtrl;
	private BoardDispCtrl boardDispCtrl;
	
	private JPanel northPanel;
	private JPanel southPanel;
	private JPanel functionPanel;
	private JComboBox displayMode;
	private String []dispStrings ={"Data", "Graphic"};
	private int dispMode = 0;
	
	public BoardCtrl(int x , int y)
	{
		this();
		setLocation(x, y);
	}
	
	public BoardCtrl()
	{
		// TODO �Զ���ɵĹ��캯����
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		deviceBoard = new DeviceBoard();
		
		boardButtonCtrl = new BoardButtonCtrl();
		boardSwitchCtrl = new BoardSwitchCtrl();
		boardLEDCtrl = new BoardLEDCtrl();
		boardDispCtrl = new BoardDispCtrl();
		
		boardButtonCtrl.setDeviceBoard(deviceBoard);
		boardSwitchCtrl.setDeviceBoard(deviceBoard);
		
		
		northPanel = new JPanel();
		northPanel.add(boardDispCtrl);
		northPanel.add(boardButtonCtrl);
		
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(boardLEDCtrl, BorderLayout.CENTER);
		southPanel.add(boardSwitchCtrl, BorderLayout.SOUTH);
		
		initFunctionPanel();
		this.setLayout(new BorderLayout());
		add(functionPanel, BorderLayout.NORTH);
		add(northPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		new Thread(this).start();
	}
	
	private void initFunctionPanel()
	{
		functionPanel = new JPanel();
		
		displayMode = new JComboBox<>(dispStrings);
		displayMode.setActionCommand("DisplayMode");
		
		displayMode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispMode = ((JComboBox)e.getSource()).getSelectedIndex();
			}
		});
		
		functionPanel.add(new JLabel("Segment Display Mode:"));
		functionPanel.add(displayMode);
		
		
	}
	
	private int data0;
	private int data1;
	private void refreshDisplay()
	{
		data0 = deviceBoard.ReadData(0);
		data1 = deviceBoard.ReadData(1);
		boardLEDCtrl.SetData(data0 & 0xff);
		if(dispMode == 0)
		{	
			boardDispCtrl.SetAllDot((data0 >>> 8) & 0x0f);
			boardDispCtrl.SetAllNum(data0 >>> 16, (data0 >>> 12) & 0x0f);
		}
		else
		{
			boardDispCtrl.SetData(data1);
		}
	}
	
	@Override
	public void run()
	{
		// TODO �Զ���ɵķ������
		while(true)
		{
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				// TODO �Զ���ɵ� catch ��
				e.printStackTrace();
			}
			
			refreshDisplay();
		}
	}
	
	public static void main(String args[])
	{
		BoardCtrl boardCtrl = new BoardCtrl();
	}

	
}
