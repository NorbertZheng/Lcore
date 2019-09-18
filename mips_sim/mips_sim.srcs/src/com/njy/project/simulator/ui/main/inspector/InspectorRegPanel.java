package com.njy.project.simulator.ui.main.inspector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cpu.MIPSCPU;
import com.njy.project.simulator.cpu.debug.Disassembler;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.ui.UIInterface;

public class InspectorRegPanel extends JPanel
{
	public InspectorRegPanel()
	{
		// TODO �Զ���ɵĹ��캯����
		dataController = DataController.getInstance();
		initRegPanel();
		setLayout(new BorderLayout());
		add(regPanel, BorderLayout.CENTER);
		add(cp0RegPanel, BorderLayout.EAST);
	}

	public void Refresh()
	{
		// TODO �Զ���ɵķ������
		RefreshRegData();
	}
	
	private void initRegPanel()
	{
		MIPSCPU cpu = dataController.getMips_CPU();
		CP0 cp0 = dataController.getCp0();
		regPanel = new JPanel();
		//regPanel.setPreferredSize(new Dimension(160, 640));
		//regPanel.setLayout(new GridLayout(32, 2));
		regPanel.setBorder(new TitledBorder("GPR"));
		JPanel regLabelPanel = new JPanel();
		JPanel regTextPanel = new JPanel();
		regLabelPanel.setLayout(new GridLayout(32, 1));
		regTextPanel.setLayout(new GridLayout(32, 1));
		
		for(int i =0 ; i< 32; i++)
		{
			regLabel[i] = new JLabel(Disassembler.RegMap[i] + " = ");
			regLabel[i].setFont(regFont);
			regTextField[i] = new JTextField(String.format("%08X", cpu.ReadReg(i)));
			regTextField[i] .setBorder(BorderFactory.createEmptyBorder());
			regTextField[i].setEditable(false);
			regTextField[i].setFont(regFont);
			regLabelPanel.add(regLabel[i]);
			regTextPanel.add(regTextField[i]);
		}
		regPanel.setLayout(new BorderLayout());
		regPanel.add(regLabelPanel, BorderLayout.WEST);
		regPanel.add(regTextPanel, BorderLayout.EAST);
		/////////////////////////////////////////////////////////////
		cp0RegPanel = new JPanel();
		cp0RegPanel.setPreferredSize(new Dimension(160, 640));
		cp0RegPanel.setLayout(new GridLayout(32, 2));
		cp0RegPanel.setBorder(new TitledBorder("CP0"));
		JPanel cp0RegLabelPanel = new JPanel();
		JPanel cp0RegTextPanel = new JPanel();
		cp0RegLabelPanel.setLayout(new GridLayout(32, 1));
		cp0RegTextPanel.setLayout(new GridLayout(32, 1));
		
		for(int i=0; i< 9 ; i++)
		{
			cp0RegLabel[i] = new JLabel(Disassembler.CP0RegMap[i] + " = ");
			cp0RegLabel[i].setFont(regFont);
			cp0RegTextField[i] = new JTextField(String.format("%08X", cp0.ReadReg(i)));
			cp0RegTextField[i].setBorder(BorderFactory.createEmptyBorder());
			cp0RegTextField[i].setEditable(false);
			cp0RegTextField[i].setFont(regFont);
			cp0RegLabelPanel.add(cp0RegLabel[i]);
			cp0RegTextPanel.add(cp0RegTextField[i]);
		}
		
		cp0RegPanel.setLayout(new BorderLayout());
		cp0RegPanel.add(cp0RegLabelPanel, BorderLayout.WEST);
		cp0RegPanel.add(cp0RegTextPanel, BorderLayout.EAST);
	}
	
	public void RefreshRegData()
	{
		MIPSCPU cpu = dataController.getMips_CPU();
		CP0 cp0 = dataController.getCp0();
		int regWrite = cpu.getRegWrite();
		int cp0RegWrite = cp0.getRegWrite();
		for(int i =0 ; i< 32; i++)
		{
			if(i == regWrite && i!= 0)
			{
				regLabel[i].setFont(regChangedFont);
				regLabel[i].setForeground(Color.RED);
				regTextField[i].setText(String.format("%08X", cpu.ReadReg(i)));
				regTextField[i].setFont(regChangedFont);
				regTextField[i].setForeground(Color.RED);
				continue;
			}
			
			regLabel[i].setFont(regFont);
			regLabel[i].setForeground(Color.BLACK);
			regTextField[i].setText(String.format("%08X", cpu.ReadReg(i)));
			regTextField[i].setFont(regFont);
			regTextField[i].setForeground(Color.BLACK);
		}
		
		for(int i=0; i< 9 ; i++)
		{
			if(i == cp0RegWrite && i >= 0)
			{
				cp0RegLabel[i].setFont(regChangedFont);
				cp0RegLabel[i].setForeground(Color.RED);
				cp0RegTextField[i].setText(String.format("%08X", cp0.ReadReg(i)));
				cp0RegTextField[i].setFont(regChangedFont);
				cp0RegTextField[i].setForeground(Color.RED);
				continue;
			}
			
			cp0RegLabel[i].setFont(regFont);
			cp0RegLabel[i].setForeground(Color.BLACK);
			cp0RegTextField[i].setText(String.format("%08X", cp0.ReadReg(i)));
			cp0RegTextField[i].setFont(regFont);
			cp0RegTextField[i].setForeground(Color.BLACK);
		}
		repaint();
	}
	
	private DataController dataController;
	private JPanel regPanel;
	private JPanel cp0RegPanel;
	private JLabel regLabel[] = new JLabel[32];
	private JTextField regTextField[] = new JTextField[32];
	private JLabel cp0RegLabel[] = new JLabel[32];
	private JTextField cp0RegTextField[] = new JTextField[32];
	
	private Font regFont = new Font("Consolas", Font.PLAIN, 12);
	private Font regChangedFont = new Font("Consolas", Font.BOLD | Font.ITALIC, 12);
}
