package com.njy.project.simulator.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.main.MainFrame;
import com.njy.project.simulator.util.Util;

public class RamLoadAddressDialog extends JDialog
{
	private  String path;
	private JTextField jTextField = new JTextField(10);
	private JButton okButton = new JButton("  OK  ");
	private JButton cancelButton = new JButton("Cancel");
	public RamLoadAddressDialog(JFrame parent, String path)
	{
		super(parent, true);
		this.path = path;
		setTitle("Set Ram Load Address");
		setLayout(new FlowLayout());
		add(new JLabel("Ram load address"));
		add(jTextField);
		jTextField.setText(String.format("%08X", DataController.getInstance().getBus().getMainMemory().getLoadAddr()));
		add(okButton);
		add(cancelButton);
		
		InnerActionListener innerActionListener = new InnerActionListener();
		okButton.addActionListener(innerActionListener);
		cancelButton.addActionListener(innerActionListener);
		
		okButton.setActionCommand("OK");
		cancelButton.setActionCommand("Cancel");

		pack();
		
		setLocation(parent.getX()+((parent.getWidth() - getWidth()) >> 1), parent.getY() + ((parent.getHeight() - getHeight())) >> 1);
		setVisible(true);
		
	}
	
	private class InnerActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO �Զ���ɵķ������
			switch (e.getActionCommand())
			{
			case "OK":
				try
				{
					int addr = Util.parseStringToHex(jTextField.getText());
					if(!DataController.getInstance().getBus().isValidPCAddress(addr))
					{
						JOptionPane.showMessageDialog(MainFrame.getInstance(), "Not a valid address!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					DataController.getInstance().getBus().getMainMemory().setLoadAddr(addr);
				}
				catch (NumberFormatException e2)
				{
					// TODO: handle exception
					JOptionPane.showMessageDialog(MainFrame.getInstance(), "Not a correct hex number!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				DataController.getInstance().LoadRam(path);
				UIController.getInstance().RefreshAll();
				
				break;
			case "Cancel":
				break;

			default:
				break;
			}
			RamLoadAddressDialog.this.setVisible(false);
			RamLoadAddressDialog.this.dispose();
		}
		
	}
}
