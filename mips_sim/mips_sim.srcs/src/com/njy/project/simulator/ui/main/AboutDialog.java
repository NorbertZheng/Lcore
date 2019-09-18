package com.njy.project.simulator.ui.main;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import com.njy.project.simulator.data.DataController;


public class AboutDialog extends JDialog
{
	public AboutDialog()
	{
		// TODO �Զ���ɵĹ��캯����
		super();
		setTitle("About MIPS SIMULATOR");
		setModal(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(width, height);
		setLocation((dim.width - width) >> 1, (dim.height - height) >> 1);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel jPanel = new JPanel();
		JTextArea jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		jTextArea.setText("MIPS SIMULATOR\nVersion " + DataController.version +"\nContact : koney2006@live.cn");
		jTextArea.setBackground(jPanel.getBackground());
		jPanel.add(jTextArea);
		jPanel.setBorder(new EtchedBorder());
		
		add(jPanel);
		
		setVisible(true);
		
		
	}
	
	private final int width = 360;
	private final int height = 120;
	
	
}
