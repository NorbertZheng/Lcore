package com.njy.project.simulator.ui.main;

import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import sun.java2d.loops.MaskBlit;

import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.UIInterface;
import com.njy.project.simulator.ui.run.RunFrame;
import com.njy.project.simulator.util.Util;

public class MainToolBar<JCheckbox> extends JToolBar implements UIInterface
{
	private DataController simulatorData;
	private UIController uiController;
	public MainToolBar()
	{
		simulatorData = DataController.getInstance();
		uiController = UIController.getInstance();
		uiController.AddUI(this);
		ActionListener actionListener = new TheActionListener();
		setRollover(true);
		for(int i = 0; i < toolbarCommand.length; i++)
		{
			toobarButtons[i] = new JButton();
			toobarButtons[i].setIcon(iconActiveMap[i]);
			toobarButtons[i].setDisabledIcon(iconInActiveMap[i]);
			toobarButtons[i].setActionCommand(toolbarCommand[i]);
			toobarButtons[i].addActionListener(actionListener);
			toobarButtons[i].setEnabled(stateShift[simulatorData.getStatus()][i]);
			add(toobarButtons[i]);
		}
		addSeparator();
		showRun.setActionCommand("ShowRun");
		showRun.setEnabled(false);
		showRun.addActionListener(actionListener);
		add(showRun);
		
		clear.setActionCommand("Clear");
		clear.addActionListener(actionListener);
		add(clear);
		
		reload.setActionCommand("Reload");
		reload.addActionListener(actionListener);
		add(reload);
		
		addSeparator();
		add(pcField);
		pcField.setMaximumSize(pcField.getPreferredSize());
		pcField.setToolTipText("Enter the PC address which the CPU will start from.");
		pcField.setText(String.format("%08X", DataController.getInstance().getPcAddr()));
		pcField.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusLost(FocusEvent e){}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				pcField.selectAll();	
			}
		});
		
		pcField.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent arg0){}
			@Override
			public void keyReleased(KeyEvent arg0){}
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					System.out.println("setpc");
					setPC();
					Refresh();
					uiController.RefreshAllExcept(MainToolBar.this);
				}
			}
		});
		setPC.setActionCommand("SetPC");
		setPC.addActionListener(actionListener);
		add(setPC);
		allmask.setActionCommand("Mask");
		allmask.addActionListener(actionListener);
		add(allmask);
		for(int i = 0; i < InterruptCode.InterruptDescription.length; i++){
			imask[i] = new JCheckBox(InterruptCode.InterruptDescription[i], true);
			imask[i].setActionCommand("Mask");
			imask[i].addActionListener(actionListener);
			add(imask[i]);
		}

	}
	
	@Override
	public void Refresh() {
		// TODO �Զ���ɵķ������
		for(int i = 0; i < toolbarCommand.length; i++)
		{
			toobarButtons[i].setEnabled(stateShift[DataController.getInstance().getStatus()][i]);
		}
		
		showRun.setEnabled(RunFrame.getInstance()==null && DataController.getInstance().getStatus() != 0);
	}
	
	private class TheActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//System.out.println(event.getActionCommand());
			switch (event.getActionCommand()) {
			case "Run":
				if(!uiController.ContainsUI("RunFrame"))
					RunFrame.createFrame().setVisible(true);
				while(RunFrame.getInstance()==null);
				simulatorData.Start();
				break;
			case "Pause":
				simulatorData.Pause();
				break;
			case "Resume":
				simulatorData.Resume();
				break;
			case "Stop":
				simulatorData.Stop();
				RunFrame.destroyFrame();
				break;
			case "Debug":
				if(!uiController.ContainsUI("RunFrame"))
					RunFrame.createFrame().setVisible(true);
				simulatorData.Debug();
				break;
			case "Step":
				simulatorData.Step();
				break;
			case "ShowRun":
				if(!uiController.ContainsUI("RunFrame"))
					RunFrame.createFrame().setVisible(true);
				break;
			case "Clear":
			{
				int res = JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Confirm Clear?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(res == JOptionPane.NO_OPTION) return;
				simulatorData.Clear();
				break;
			}
			case "Reload":
			{
				int res = JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Confirm Reload?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(res == JOptionPane.NO_OPTION) return;
				simulatorData.Reload();
				break;
			}
			case "SetPC":
				setPC();
				break;
			case "Mask":
			{
				int msk = 0xffffffff;
				simulatorData.setAllmask(allmask.isSelected());
				for(int i = 0; i < InterruptCode.InterruptDescription.length; i++){
					if(!imask[i].isSelected()){
						msk &= ~(1<<i);
					}
				}
				simulatorData.setMask(msk);
				return;
			}
			default:
				break;
			}
			Refresh();
			uiController.RefreshAllExcept(MainToolBar.this);
		}
	}
	
	private void setPC()
	{
		try
		{
			int pc = Util.parseStringToHex(pcField.getText());
			if(!simulatorData.getBus().isValidAddress(pc))
			{
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Not a valid address!", "Error", JOptionPane.ERROR_MESSAGE);
				pcField.setText(String.format("%08X", simulatorData.getPcAddr()));
				return;
			}
			pc &= 0xfffffffc;
			simulatorData.setPcAddr(pc);
			if(simulatorData.getStatus() == 0)
				simulatorData.getMips_CPU().setPc(pc);
			pcField.setText(String.format("%08X", pc));
		}
		catch (NumberFormatException e)
		{
			// TODO: handle exception
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Not a correct hex number!", "Error", JOptionPane.ERROR_MESSAGE);
			pcField.setText(String.format("%08X", simulatorData.getPcAddr()));
		}
	}
	
	
	private JCheckBox allmask = new JCheckBox("All", true);
	private JCheckBox imask[] = new JCheckBox[31];
	private JTextField pcField = new JTextField(10);
	private JButton setPC = new JButton(new ImageIcon("./ui/icon_active/setpc.png"));
	private JButton toobarButtons[] = new JButton[6];
	private JButton showRun = new JButton(new ImageIcon("./ui/icon_active/showrun.png"));
	private JButton clear = new JButton(new ImageIcon("./ui/icon_active/clear.png"));
	private JButton reload = new JButton(new ImageIcon("./ui/icon_active/reload.png"));
	private boolean stateShift[][] = 
	{
		{true, false, false, false, true, false},//stopped
		{false, true, false, true, false, false},//running
		{false, false, true, true, true, true},//pause
		{false, true, false, true, false, false},//debug
		{true, false, true, true, false, true},//debugpaused
	};
	
	private static String toolbarCommand[] = {
		"Run", "Pause", "Resume", "Stop", "Debug","Step"
	};
	private static ImageIcon[] iconActiveMap = new ImageIcon[6];
	private static ImageIcon[] iconInActiveMap = new ImageIcon[6];
	static {
		for(int i =0; i< toolbarCommand.length; i++)
		{
			iconActiveMap[i] =  new ImageIcon("./ui/icon_active/" + toolbarCommand[i] + ".png");
			iconInActiveMap[i] = new ImageIcon("./ui/icon_inactive/" + toolbarCommand[i] + ".png");
		}
	
	}
}
