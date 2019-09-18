package com.njy.project.simulator.ui.inspector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import org.omg.CORBA.COMM_FAILURE;

import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cpu.MIPSCPU;
import com.njy.project.simulator.cpu.debug.Disassembler;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.UIInterface;
import com.njy.project.simulator.util.Util;
import com.sun.istack.internal.FinalArrayList;
import com.sun.jndi.url.corbaname.corbanameURLContextFactory;


public class InspectorFrame extends JFrame implements UIInterface
{
	public InspectorFrame(int x, int y)
	{
		this();
		setLocation(x, y);
	}
	
	public InspectorFrame()
	{
		super("Data Inspector");
		dataController = DataController.getInstance();
		UIController.getInstance().AddUI(this);
		//dataController.LoadMemory(null);
		
		initDataPanel();
		initFuncPanel();
		initRegPanel();
		setLayout(new BorderLayout());
		add(dataPanel, BorderLayout.CENTER);
		JPanel rPanel = new JPanel();
		rPanel.add(regPanel);
		rPanel.add(cp0RegPanel);
		//add(regPanel, BorderLayout.CENTER);
		//add(cp0RegPanel, BorderLayout.EAST);
		add(rPanel, BorderLayout.EAST);
		add(functionPanel, BorderLayout.SOUTH);
		//setResizable(false);
		pack();
		refresh(-1);
		setVisible(true);
		addKeyListener(new KeyListener()
		{
			
			@Override
			public void keyTyped(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				if(arg0.getKeyCode() == KeyEvent.VK_F5)
				{
					refresh(-1);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				
			}
		});
		addWindowListener(new WindowListener()
		{
			
			@Override
			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent e)
			{
				// TODO Auto-generated method stub
				UIController.getInstance().RemoveUI(InspectorFrame.this);
			}
			
			@Override
			public void windowActivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frameCount ++;
	}
	
	private void refresh(int line)
	{
		// TODO �Զ���ɵķ������
		int v = line < 0? dataPanel.getVerticalScrollBar().getValue()
				: line * fontSizePx;
		RefreshRamData();
		RefreshRegData();
		setScrollBar(v);
		if(dispMode == 1)
			loadbreak();
	}
	
	@Override
	public void Refresh()
	{
		// TODO Auto-generated method stub
		
		if(dataController.isStepSynPCAddr() && dataController.getStatus() >= 2)
		{
			if(dispMode == 1)
			{
				SetToInst(-1);
			}
			else 
			{
				refresh(-1);
			}
		}
	}

	private DataController dataController;
	////////////////////////////////params////////////////////////////////////////////////////
	private int startAddress = 0;
	private int dispMode = 0;
	
	////////////////////////////////for the font used///////////////////////////////////////////
	private void setAttr()
	{
		StyleConstants.setFontFamily(attData, "Consolas");
		StyleConstants.setFontSize(attData, fontSizePound);
		StyleConstants.setForeground(attData, Color.BLUE);
		
		StyleConstants.setFontFamily(attPC, "Consolas");
		StyleConstants.setFontSize(attPC, fontSizePound);
		StyleConstants.setBold(attPC, true);
		StyleConstants.setItalic(attPC, true);
		StyleConstants.setForeground(attPC, Color.RED);
		regFont = new Font("Consolas", Font.PLAIN, fontSizePound);
		regChangedFont = new Font("Consolas", Font.BOLD | Font.ITALIC, fontSizePound);
		
		fontSizePx = Toolkit.getDefaultToolkit().getFontMetrics(regFont).getHeight();
		fontSizePcPX = Toolkit.getDefaultToolkit().getFontMetrics(regChangedFont).getHeight();
		leading = Toolkit.getDefaultToolkit().getFontMetrics(regFont).getLeading();
		System.out.println(fontSizePx);
		System.out.println(fontSizePcPX);
		
	}

	
	private SimpleAttributeSet attData = new SimpleAttributeSet();
	private SimpleAttributeSet attPC = new SimpleAttributeSet();
	private SimpleAttributeSet setUsed;
	private Font regFont = new Font("Consolas", Font.PLAIN, 12);
	private Font regChangedFont = new Font("Consolas", Font.BOLD | Font.ITALIC, 12);
	private int fontSizePound = 12;
	private int fontSizePx;
	private int fontSizePcPX;
	private int leading = 0;
	
	////////////////////////////////////for function panel///////////////////////////////////////
	
	private void initFuncPanel()
	{
		TheActionListener theActionListener = new TheActionListener();
		functionPanel = new JPanel();
		functionPanel.setLayout(new FlowLayout());
		
		displayMode = new JComboBox<>(dispStrings);
		displayMode.setActionCommand("DisplayMode");
		displayMode.addActionListener(theActionListener);
		functionPanel.add(pageIndex);
		functionPanel.add(startAddr);
		functionPanel.add(pageNum);
		functionPanel.add(displayMode);
		functionPanel.add(refreshButton);
		refreshButton.setActionCommand("Refresh");
		refreshButton.addActionListener(theActionListener);
		pcButton.setActionCommand("PC");
		pcButton.addActionListener(theActionListener);
		functionPanel.add(pcButton);
		functionPanel.add(new JLabel("Jump To"));
		functionPanel.add(jumpAddr);
		
		startAddr.addKeyListener(new KeyListener()
		{
			
			@Override
			public void keyTyped(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					try
					{
						startAddress = Integer.parseInt(startAddr.getText(), 16) << 12;
						System.out.printf("%08X\n", startAddress);
					}
					catch (Exception e)
					{
						// TODO: handle exception
						e.printStackTrace();
					}
					refresh(-1);
				}
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				
			}
		});
		
		jumpAddr.addKeyListener(new KeyListener()
		{
			
			@Override
			public void keyTyped(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int addr = Util.parseStringToHex(jumpAddr.getText());
					SetToInst(addr);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				// TODO �Զ���ɵķ������
				
			}
		});
	}
	
	
	private class TheActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//System.out.println(event.getActionCommand());
			switch (event.getActionCommand()) {
			case "Refresh":
				try
				{
					startAddress = Integer.parseInt(startAddr.getText(), 16) << 12;
					System.out.printf("%08X\n", startAddress);
				}
				catch (Exception e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				refresh(-1);
				break;
			case "DisplayMode":
				dispMode = ((JComboBox)event.getSource()).getSelectedIndex();
				refresh(-1);
				break;
			case "PC":
				SetToInst(-1);
				break;
			default:
				break;
			}
		}
	}
	
	private String []dispStrings ={"Data", "Inst"};
	private JLabel pageIndex = new JLabel("Page Index 0 <=");
	private JTextField startAddr = new JTextField(10);
	private JLabel pageNum = new JLabel("<= 0X" + Integer.toHexString((DataController.getInstance().GetRamSize() >>> 12) - 1));
	//private JTextField addCount = new JTextField(10);
	private JButton refreshButton = new JButton("Refresh");
	private JButton pcButton = new JButton("Show PC");
	private JTextField jumpAddr = new JTextField(10);
	private JComboBox displayMode;//associated with dispMode
	private JPanel functionPanel;
	
	
	
	/////////////////////////////////////for data panel//////////////////////////////////////////
	
	private void initDataPanel()
	{	
		textpane = new JTextPane();
		dataJPanel = new JPanel();
		ptrJPanel = new JPanel();
		
		ptrJPanel.setLayout(null);
		ptrJPanel.add(instPtrLabel);
		ptrJPanel.add(bkinstPtrLabel);
		ptrJPanel.setPreferredSize(new Dimension(15, 0));
		ptrJPanel.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				// TODO �Զ���ɵķ������
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO �Զ���ɵķ������
				if(!ptrJPanel.isEnabled() || dispMode == 0) return;
				if(e.getClickCount() == 2)
				{
					int y = e.getPoint().y;
					int addr = y / fontSizePx;
					toggleBreak(addr);
					
				}
			}
		});
		
		instPtrLabel.setVisible(false);
		instPtrLabel.setBounds(0, 0, 15, 15);
		
		
		//textpane.setPreferredSize(new Dimension(440,0));
		textpane.setEditable(false);
		textpane.setAutoscrolls(false);
		textpane.setBackground(dataJPanel.getBackground());
		document = textpane.getDocument();
		//////////////////////////////////////
		setAttr();
		insetsTop = textpane.getInsets().top;
		//dataJPanel.setPreferredSize(new Dimension(455, fontSizePx * 1025));
		dataJPanel.setLayout(new BorderLayout());
		dataJPanel.add(ptrJPanel, BorderLayout.WEST);
		dataJPanel.add(textpane, BorderLayout.CENTER);
		
		dataPanel = new JScrollPane(dataJPanel);
		dataPanel.setBorder(new CompoundBorder(new TitledBorder("Memory Data"), new EtchedBorder()));
		dataPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//dataPanel.setPreferredSize(new Dimension(480,640));
		dataPanel.setAutoscrolls(false);
		ptrJPanel.setEnabled(false);
	}	
	
	public void RefreshRamData()
	{
		if((startAddress >>> 24) == 0xff && (startAddress >>> 16) != 0XFFFF)
			dataController.getBus().getRom().readBlock(startAddress, 4096, dataBuffer);
		else if(Util.compareIntIgnoreSign(startAddress , dataController.GetRamSize()) < 0)
			dataController.getBus().getMainMemory().readBlock(startAddress, 4096, dataBuffer);
		else 
			return;
		
		boolean isPC;
		int adr;
		int data;
		int pc = dataController.getMips_CPU().getPcNext();
		try
		{
			Clear();
			if(dispMode == 0)
			{
				for(int i = 0; i < 1024;)
				{
					adr = startAddress + (i << 2);
					isPC = pc == adr;
					setUsed = attData;
					if(isPC) setUsed = attPC;
					insertAddr(adr);
					data = dataBuffer[i];
					insertData((byte)(data & 0xff));
					insertData((byte)((data>>>8) & 0xff));
					insertData((byte)((data>>>16) & 0xff));
					insertData((byte)(data>>>24));
					insertDataChar((byte)(data & 0xff));
					insertDataChar((byte)((data>>>8) & 0xff));
					insertDataChar((byte)((data>>>16) & 0xff));
					insertDataChar((byte)(data>>>24));
					insertString("\n");
					i+=4;	
				}
			}
			else 
			{
				for(int i = 0; i < 1024;)
				{
					adr = startAddress + (i << 2);
					isPC = pc == adr;
					setUsed = attData;
					if(isPC) setUsed = attPC;
					insertAddr(adr);
					data = dataBuffer[i];
					insertData((byte)(data>>>24));
					insertData((byte)((data>>>16) & 0xff));
					insertData((byte)((data>>>8) & 0xff));
					insertData((byte)(data & 0xff));
					
					insertString("    " + Disassembler.singleInstruction(data, adr));
					insertString("\n");
					i+=4;
				}	
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		ptrJPanel.setEnabled(true);
		ptrJPanel.setPreferredSize(new Dimension(15, fontSizePx * 1025));
		//textpane.setPreferredSize(new Dimension(440,fontSizePx * 1025));

	}
	
	private void toggleBreak(int i)
	{
		if(i >= 1024) return;
		if(brkPtrLabels[i] != null)
		{
			brkPtrLabels[i].setVisible(false);
			brkPtrLabels[i].setEnabled(false);
			brkPtrLabels[i] = null;
			dataController.getMips_CPU().removeBreakPoint(startAddress + (i << 2));
		}
		else
		{
			brkPtrLabels[i] = new JLabel(new ImageIcon("./ui/icon_active/brk_ptr.png"));
			ptrJPanel.add(brkPtrLabels[i], null);
			brkPtrLabels[i].setBounds(0, i * fontSizePx, 15, fontSizePx + insetsTop + leading);
			brkPtrLabels[i].setVisible(true);
			dataController.getMips_CPU().addBreakPoint(startAddress + (i << 2));
		}
		
	}
	
	private void loadbreak()
	{
		MIPSCPU cpu = dataController.getMips_CPU();
		for(int i = 0; i< 1024; i++)
		{
			if(cpu.checkBreakPoint(startAddress + (i << 2) ))
			{
				brkPtrLabels[i] = new JLabel(new ImageIcon("./ui/icon_active/brk_ptr.png"));
				ptrJPanel.add(brkPtrLabels[i], null);
				brkPtrLabels[i].setBounds(0, i * fontSizePx, 15, fontSizePx + insetsTop + leading);
				brkPtrLabels[i].setVisible(true);
			}
		}
	}
	
	public void SetToInst(int addr)
	{
		int pc = addr < 0? dataController.getMips_CPU().getPcNext() : addr & 0xfffffffc;
		int pos = ( pc & 0xfff ) >>> 2;
		startAddress = pc & 0xfffff000;
		startAddr.setText(String.format("%05X", startAddress >>> 12));
		refresh(pos);
		System.out.println(pos);
		if(pos >= 1 && brkPtrLabels[pos - 1]!=null )
		{
			brkPtrLabels[pos-1].setVisible(true);
		}
		
		if(brkPtrLabels[pos]!=null)
		{
			brkPtrLabels[pos].setVisible(false);
			instPtrLabel.setVisible(false);
			bkinstPtrLabel.setVisible(true);
			bkinstPtrLabel.setBounds(0, pos * fontSizePx, 15, fontSizePcPX + insetsTop + leading);
		}
		else 
		{
			bkinstPtrLabel.setVisible(false);
			instPtrLabel.setVisible(true);
			instPtrLabel.setBounds(0, pos * fontSizePx, 15, fontSizePcPX + insetsTop + leading);
		}
	}
	
	private void setScrollBar(final int value)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			   public void run() { 
				   dataPanel.getVerticalScrollBar().setValue(value);
			   }
		});
	}
	
	private void setInstPtr()
	{
		instPtrLabel.setVisible(true);
		int pc = dataController.getMips_CPU().getPcNext();
		if((pc & 0xfffff000) == startAddress)
		{
			int pos = (pc - startAddress) >>> 2;
			instPtrLabel.setLocation(0, pos * fontSizePx);
		}
	}
	
	final public void insertData(byte b) throws BadLocationException
	{
		document.insertString(document.getLength(), String.format("%02X", b)+ " ", setUsed);
	}
	
	final public void insertDataChar(byte b)  throws BadLocationException
	{
		int c;
		if(b=='\n') c = ' ';
		else 
			c = b >= 0? b:(b+256);
		
		document.insertString(document.getLength(), String.format("%c", c), setUsed);	
	}
	
	final public void insertAddr(int addr)  throws BadLocationException
	{
		document.insertString(document.getLength(), String.format("%08X", addr) + "  ", setUsed);
	}
	
	final public void insertString(String s)    throws BadLocationException
	{
		document.insertString(document.getLength(), s, setUsed);
	}
	
	final public void Clear() throws BadLocationException
	{
		document.remove(0, document.getLength());
	}
	
	final public void noteBreak(int pos)
	{
		//int i = document.getText(0, document.getLength()).
	}
	
	private JScrollPane dataPanel;
	private JTextPane textpane;
	private JPanel dataJPanel;
	private JPanel ptrJPanel;
	private JLabel instPtrLabel = new JLabel(new ImageIcon("./ui/icon_active/inst_ptr.png"));
	private JLabel []brkPtrLabels = new JLabel[1024];
	private JLabel bkinstPtrLabel = new JLabel(new ImageIcon("./ui/icon_active/bkinst_ptr.png"));
	private int dataBuffer[] = new int[1024];
	private Document document;
	private static int frameCount = 0;
	private int insetsTop = 0;
	
	//private boolean loadBreak = false;
	///////////////////////////////////for reg panel//////////////////////////////////////////////
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
		//Font font = new Font("Consolas", Font.BOLD, 12);
		for(int i =0 ; i< 32; i++)
		{
			regLabel[i] = new JLabel(Disassembler.RegMap[i] + " = ");
			regLabel[i].setFont(regFont);
			regTextField[i] = new JTextField(String.format("%08X", cpu.ReadReg(i)));
			regTextField[i].setEditable(false);
			regTextField[i].setFont(regFont);
			regLabelPanel.add(regLabel[i]);
			regTextPanel.add(regTextField[i]);
			//regPanel.add(regLabel[i]);
			//regPanel.add(regTextField[i]);
		}
		regPanel.setLayout(new BorderLayout());
		regPanel.add(regLabelPanel, BorderLayout.WEST);
		regPanel.add(regTextPanel, BorderLayout.EAST);
		/////////////////////////////////////////////////////////////
		cp0RegPanel = new JPanel();
		//cp0RegPanel.setPreferredSize(new Dimension(160, 640));
		//cp0RegPanel.setLayout(new GridLayout(32, 2));
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
			cp0RegTextField[i].setEditable(false);
			cp0RegTextField[i].setFont(regFont);
			cp0RegLabelPanel.add(cp0RegLabel[i]);
			cp0RegTextPanel.add(cp0RegTextField[i]);
			//cp0RegPanel.add(cp0RegLabel[i]);
			//cp0RegPanel.add(cp0RegTextField[i]);
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
				return;
			
			}
			regLabel[i].setFont(regFont);
			regLabel[i].setForeground(Color.BLACK);
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
				return;
			
			}
			cp0RegLabel[i].setFont(regFont);
			cp0RegLabel[i].setForeground(Color.BLACK);
			cp0RegTextField[i].setFont(regFont);
			cp0RegTextField[i].setForeground(Color.BLACK);
		}
	}
	
	
	private JPanel regPanel;
	private JPanel cp0RegPanel;
	private JLabel regLabel[] = new JLabel[32];
	private JTextField regTextField[] = new JTextField[32];
	private JLabel cp0RegLabel[] = new JLabel[32];
	private JTextField cp0RegTextField[] = new JTextField[32];
	

}
