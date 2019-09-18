package com.njy.project.simulator.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.board.BoardCtrl;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.UIInterface;
import com.njy.project.simulator.ui.dialog.BuildSDFileDialog;
import com.njy.project.simulator.ui.dialog.RamLoadAddressDialog;
import com.njy.project.simulator.ui.inspector.InspectorFrame;
import com.njy.project.simulator.ui.run.RunFrame;
import com.njy.project.simulator.util.FileChooserUtil;

public class MainMenuBar extends JMenuBar implements UIInterface{
	private DataController simulatorData;
	private UIController uiController;
	public MainMenuBar()
	{
		simulatorData = DataController.getInstance();
		uiController = UIController.getInstance();
		uiController.AddUI(this);
		TheActionListener theActionListener = new TheActionListener();
		////////////////////////File Menu/////////////////////////////////
		fileMenu = new JMenu(" File ");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);
		openROMMenuItem = new JMenuItem("Load ROM", KeyEvent.VK_P);
		KeyStroke openRomXKeyStroke = KeyStroke.getKeyStroke("control O");
		openROMMenuItem.setAccelerator(openRomXKeyStroke);
		openROMMenuItem.addActionListener(theActionListener);
		fileMenu.add(openROMMenuItem);
		
		openRAMMenuItem = new JMenuItem("Load RAM", KeyEvent.VK_O);
		KeyStroke openRamXKeyStroke = KeyStroke.getKeyStroke("control shift O");
		openRAMMenuItem.setAccelerator(openRamXKeyStroke);
		openRAMMenuItem.addActionListener(theActionListener);
		fileMenu.add(openRAMMenuItem);
		
		saveFileMenuItem = new JMenuItem("Save File", KeyEvent.VK_S);
		KeyStroke saveFileXKeyStroke = KeyStroke.getKeyStroke("control S");
		saveFileMenuItem.setAccelerator(saveFileXKeyStroke);
		saveFileMenuItem.addActionListener(theActionListener);
		fileMenu.add(saveFileMenuItem);
		
		saveDataMenuItem = new JMenuItem("Save Data", KeyEvent.VK_D);
		KeyStroke saveDataXKeyStroke = KeyStroke.getKeyStroke("control shift S");
		saveDataMenuItem.setAccelerator(saveDataXKeyStroke);
		saveDataMenuItem.addActionListener(theActionListener);
		fileMenu.add(saveDataMenuItem);
		
		JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		exitMenuItem.addActionListener(theActionListener);
		fileMenu.add(exitMenuItem);
		
        ////////////////////////Edit Menu/////////////////////////////////
		editMenu  = new JMenu(" Edit ");
		editMenu.setMnemonic(KeyEvent.VK_E);
		add(editMenu);
		JMenuItem cutMenuItem = new JMenuItem("Cut", KeyEvent.VK_T);
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control X");
		cutMenuItem.setAccelerator(ctrlXKeyStroke);
		cutMenuItem.addActionListener(theActionListener);
		editMenu.add(cutMenuItem); 
		
		JMenuItem copyMenuItem = new JMenuItem("Copy", KeyEvent.VK_C);
		KeyStroke ctrlCKeyStroke = KeyStroke.getKeyStroke("control C");
		copyMenuItem.setAccelerator(ctrlCKeyStroke);
		copyMenuItem.addActionListener(theActionListener);
		editMenu.add(copyMenuItem);
		
		editMenu.addSeparator();   
		JMenuItem findMenuItem = new JMenuItem("Find", KeyEvent.VK_F);
		KeyStroke f3KeyStroke = KeyStroke.getKeyStroke("F3");
		findMenuItem.setAccelerator(f3KeyStroke);
		findMenuItem.addActionListener(theActionListener);
		editMenu.add(findMenuItem); 
		
		JMenuItem preferrenceMenuItem = new JMenuItem("Preferrence", KeyEvent.VK_P);
		preferrenceMenuItem.addActionListener(theActionListener);
		editMenu.add(preferrenceMenuItem);
		
        ////////////////////////Run Menu/////////////////////////////////
		runMenu = new JMenu(" Run ");
		runMenu.setMnemonic(KeyEvent.VK_R);
		add(runMenu);
		runMenuItem = new JMenuItem("Run", KeyEvent.VK_R);
		KeyStroke runXKeyStroke = KeyStroke.getKeyStroke("control F11");
		runMenuItem.setAccelerator(runXKeyStroke);
		runMenuItem.addActionListener(theActionListener);
		runMenu.add(runMenuItem);
		
		debugMenuItem = new JMenuItem("Debug", KeyEvent.VK_D);
		KeyStroke debugXKeyStroke = KeyStroke.getKeyStroke("F11");
		debugMenuItem.setAccelerator(debugXKeyStroke);
		debugMenuItem.addActionListener(theActionListener);
		runMenu.add(debugMenuItem);
		
		//////////////////////Window////////////////////////////////////
		
		windowMenu = new JMenu(" Window ");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		add(windowMenu);
//		dataInspectorItem = new JMenuItem("Data Inspector", KeyEvent.VK_D);
//		dataInspectorItem.addActionListener(theActionListener);
//		windowMenu.add(dataInspectorItem);
		
		boardIOItem = new JMenuItem("Board IO", KeyEvent.VK_B);
		boardIOItem.addActionListener(theActionListener);
		windowMenu.add(boardIOItem);
		
		buildSDItem = new JMenuItem("SD File Builder", KeyEvent.VK_S);
		buildSDItem.addActionListener(theActionListener);
		windowMenu.add(buildSDItem);
		///////////////////////HELP////////////////////////////////
		helpMenu = new JMenu(" Help ");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);
		
		JMenuItem aboutMenuItem = new JMenuItem("About MIPS SIMULATOR", KeyEvent.VK_A);
		aboutMenuItem.addActionListener(theActionListener);
		helpMenu.add(aboutMenuItem);
	}
	
	private class TheActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			switch (event.getActionCommand().trim()) {
			case "Load ROM":
			{
				fileChooser = new FileChooserUtil("./TestBin", MainMenuBar.this);
				String s = null;
				try {
					s = fileChooser.getFile().getPath();
					System.out.println(s);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (s != null && !s.equals("")) {
					simulatorData.LoadRom(s);
				}
				break;
			}
			case "Load RAM":
			{
				fileChooser = new FileChooserUtil("./TestBin", MainMenuBar.this);
				String s = null;
				try {
					s = fileChooser.getFile().getPath();
					System.out.println(s);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (s != null && !s.equals("")) {
					new RamLoadAddressDialog(MainFrame.getInstance(), s);
				}
				break;
			}
			case "Exit":
				System.exit(0);
			case "Run":
			{
				if(!uiController.ContainsUI("RunFrame"))
					RunFrame.createFrame().setVisible(true);
				while(RunFrame.getInstance()==null);
				simulatorData.Start();
				break;
			}
			case "Debug":
			{
				if(!uiController.ContainsUI("RunFrame"))
					RunFrame.createFrame().setVisible(true);
				simulatorData.Debug();
				break;
			}
			case "Preferrence":
			{
				//new PreferrenceDialog();
				break;
			}
			case "Data Inspector":
			{
						// TODO �Զ���ɵķ������
				MainFrame mainFrame = MainFrame.getInstance();
				new InspectorFrame(mainFrame.getX() + mainFrame.getWidth(), mainFrame.getY());
				
				break;
			}
			case "Board IO":
			{
				MainFrame mainFrame = MainFrame.getInstance();
				new BoardCtrl(mainFrame.getX() + mainFrame.getWidth(), mainFrame.getY());
				break;
			}
			case "SD File Builder":
			{
				new BuildSDFileDialog(MainFrame.getInstance());
				break;
			}
			case "About MIPS SIMULATOR":
			{
				new AboutDialog();
				break;
			}
			default:
				break;
			}
			Refresh();
			uiController.RefreshAllExcept(MainMenuBar.this);
		}
	}
	
	public void SetRunMenuItem(boolean b)
	{
		runMenuItem.setEnabled(b);
	}
	
	public void SetDebugMenuItem(boolean b)
	{
		debugMenuItem.setEnabled(b);
	}
	
	private static String menubarCommand[] = {
		"LoadROM","LoadRAM", "Run", "Debug"
	};
	
	private boolean stateShift[][] = 
		{
			{true,  true,  true,  true,},//stopped
			{false, false, false, false,},//running
			{false, false, false, true,},//paused
			{false, false, false, false,},//debug
			{false, false, true,  false,},//debugpaused
		};
	
	@Override
	public void Refresh() {
		// TODO �Զ���ɵķ������
		openROMMenuItem.setEnabled(stateShift[simulatorData.getStatus()][0]);
		openRAMMenuItem.setEnabled(stateShift[simulatorData.getStatus()][1]);
		runMenuItem.setEnabled(stateShift[simulatorData.getStatus()][2]);
		debugMenuItem.setEnabled(stateShift[simulatorData.getStatus()][3]);
	}
	
	private FileChooserUtil fileChooser;
	JMenuItem openROMMenuItem;
	JMenuItem openRAMMenuItem;
	JMenuItem saveFileMenuItem;
	JMenuItem saveDataMenuItem;
	JMenuItem runMenuItem;
	JMenuItem debugMenuItem;
	JMenuItem dataInspectorItem;
	JMenuItem boardIOItem;
	JMenuItem buildSDItem;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu windowMenu;
	private JMenu runMenu;
	private JMenu helpMenu;

}
