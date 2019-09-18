package com.njy.project.simulator.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import sun.net.www.content.image.jpeg;

import com.njy.project.simulator.data.ConfigParam;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.spi.SD.SDBuilder;
import com.njy.project.simulator.device.spi.SD.SDCard;
import com.njy.project.simulator.device.spi.SD.SlaveSDCard;
import com.njy.project.simulator.ui.main.MainFrame;
import com.njy.project.simulator.util.FileChooserUtil;
import com.njy.project.simulator.util.Util;
import com.sun.corba.se.spi.orb.StringPair;



public class BuildSDFileDialog extends JDialog
{
	public BuildSDFileDialog(JFrame parent)
	{
		// TODO �Զ���ɵĹ��캯����
		
		super(parent, true);
		setTitle("Build SD Card File");
		setLayout(new GridLayout(6, 1, 3, 3));
		innerActionListener inActionListener = new innerActionListener();
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		p1.add(dirLabel);
		p1.add(chooseDir);
		chooseDir.setActionCommand("Dir");
		chooseDir.addActionListener(inActionListener);
		add(p1);
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		p2.add(new JLabel("SD File Name:"));
		p2.add(sdFileName);
		add(p2);
		
		JPanel p3 = new JPanel(); 
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		p3.add(new JLabel("SD File Size:"));
		sdFileSize = new JComboBox<>(SDSize);
		p3.add(sdFileSize);
		add(p3);
		
		JPanel p4 = new JPanel(); 
		p4.setLayout(new FlowLayout(FlowLayout.LEFT));
		p4.add(new JLabel("SPI Slave ID:"));
		spiSlave = new JComboBox<>(spiSlaveNum);
		p4.add(spiSlave);
		add(p4);
		
		
		JPanel p5 = new JPanel(); 
		p5.setLayout(new FlowLayout(FlowLayout.LEFT));
		p5.add(setAsDefault);
		add(p5);
		
		
		JPanel p6 = new JPanel(); 
		p6.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButton.setActionCommand("OK");
		cancelButton.setActionCommand("Cancel");
		
		okButton.addActionListener(inActionListener);
		cancelButton.addActionListener(inActionListener);
		p6.add(okButton);
		p6.add(cancelButton);
		add(p6);
		
		pack();
		setResizable(false);
		setLocation(parent.getX()+((parent.getWidth() - getWidth()) >> 1), parent.getY() + ((parent.getHeight() - getHeight()) >> 1));
		setVisible(true);
	}
	
	private class innerActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO �Զ���ɵķ������
			switch (e.getActionCommand())
			{
			case "Dir":
				FileChooserUtil fileChooserUtil = new FileChooserUtil(BuildSDFileDialog.this);
				File f = fileChooserUtil.getDir();
				if(f != null)
				{
					sdFileDir = f.getPath();
					if(sdFileDir.length() > 33)
						dirLabel.setText(sdFileDir.substring(0, 30)+"...");
					else
						dirLabel.setText(sdFileDir);
					dirLabel.setToolTipText(sdFileDir);
				}
				break;
			case "OK":
				String string = sdFileDir + "\\" + sdFileName.getText();
				long l = ConfigParam.sdSizeMap.get(sdFileSize.getSelectedItem());
				System.out.println(string + " " +l);
				new InnerProgressBar(BuildSDFileDialog.this, l,string);
				DataController.getInstance().getSpiCtrl().addSlave(new SlaveSDCard(new SDCard(string)), (Integer)spiSlave.getSelectedItem());
				break;
			case "Cancel":
				BuildSDFileDialog.this.setVisible(false);
				BuildSDFileDialog.this.dispose();
				break;
			default:
				break;
			}
		}
		
	}
	
	private class InnerProgressBar extends JDialog implements Runnable
	{
		public InnerProgressBar(JDialog parent, long l, String string) 
		{
			// TODO �Զ���ɵĹ��캯����
			super(parent, true);
			setUndecorated(true);
			
			JPanel jPanel = new JPanel();
			
			jPanel.setBorder(new CompoundBorder(new EtchedBorder(), new TitledBorder("Building SD Card File...")));
			jPanel.setLayout(new BorderLayout());
			jProgressBar = new JProgressBar(0, 100);
			jProgressBar.setPreferredSize(new Dimension(500, 25));
			jProgressBar.setStringPainted(true);
			jProgressBar.setBorderPainted(true);
			
			//jProgressBar.setForeground(Color.blue);
			//jProgressBar.setValue(50);
			//jPanel.add(new JLabel("Building SD Card File..."), BorderLayout.NORTH);
			jPanel.add(jProgressBar, BorderLayout.CENTER);
			JButton canceButton = new JButton("Cancel");
			canceButton.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					// TODO �Զ���ɵķ������
					int res = JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Confirm stopping buiding? ", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(res == JOptionPane.CANCEL_OPTION) return;
					sdBuilder.stop();
					InnerProgressBar.this.setVisible(false);
					InnerProgressBar.this.dispose();
				}
			});
			
			JPanel j = new JPanel();
			j.setLayout(new FlowLayout(FlowLayout.CENTER));
			j.add(canceButton);
			jPanel.add(j, BorderLayout.SOUTH);
			
			add(jPanel);
			pack();
			setResizable(false);
			setLocation(parent.getX()+((parent.getWidth() - getWidth()) >> 1), parent.getY() + ((parent.getHeight() - getHeight())>>1));
			
			System.out.println("sdasrfetgfdsfsd");
			try
			{
				this.sdBuilder = new SDBuilder(l, string);
			}
			catch (IOException e)
			{
				// TODO: handle exception
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Cannot start Buiding", "Error", JOptionPane.ERROR_MESSAGE);
			}
			System.out.println("dsdasdasdasdasdasd");
			new Thread(this).start();
			
			setVisible(true);
		}
		
		private SDBuilder sdBuilder;
		private JProgressBar jProgressBar;

		@Override
		public void run()
		{
			// TODO �Զ���ɵķ������
			System.out.println("daskldhklasjfkhsf");
			sdBuilder.setjProgressBar(jProgressBar);
			sdBuilder.startBuilding();
			System.out.println("start");
			while(sdBuilder.isRunning())
			{
				//System.out.println("Building....");
				sdBuilder.setProgress();
			}
			
			if(sdBuilder.getState() != 0)
			{
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Build SD File failed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			this.setVisible(false);
			this.dispose();
		}
	}
	
	private JLabel dirLabel = new JLabel("   Directory:");
	private JButton chooseDir = new JButton("...");
	private JButton okButton = new JButton("  OK  ");
	private JButton cancelButton = new JButton("Cancel");
	private String sdFileDir = null;
	private JTextField sdFileName  = new JTextField(26);
	private JComboBox<String> sdFileSize ;
	private JComboBox<Integer> spiSlave ;
	private JCheckBox setAsDefault = new JCheckBox("Set as default SD File");
	
	private String[] SDSize = 
	{
			"256M",
			"512M",
			"1G"  ,
			"2G"  ,
			"4G"  ,
			"8G"  ,
			"16G" ,
			"32G" ,
	};
	
	private Integer[] spiSlaveNum = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
}
