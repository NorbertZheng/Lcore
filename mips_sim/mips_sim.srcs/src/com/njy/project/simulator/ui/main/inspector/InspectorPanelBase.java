package com.njy.project.simulator.ui.main.inspector;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.util.Util;

public class InspectorPanelBase extends JPanel
{
	public InspectorPanelBase()
	{
		// TODO 锟皆讹拷锟斤拷锟缴的癸拷锟届函锟斤拷锟斤拷锟�
		inspectorTextTableBase = new InspectorTextTableBase();
		inspectorTextTableBase.addKeyListener(innerKeyListener);
		dataController = DataController.getInstance();
		setLayout(new BorderLayout());
		jScrollPane = new JScrollPane(inspectorTextTableBase);
		add(innerToolBar, BorderLayout.NORTH);
		add(jScrollPane, BorderLayout.CENTER);
	}
	
	public boolean RefreshData()
	{
		if(dispMode == 0)
		{
			physicalAddress = startAddress;
		}
		else
		{
			try
			{
				physicalAddress = this.dataController.getCp0().MMU(startAddress);
			}
			catch(Exception e)
			{
				return false;
			}
		}
		
		if((physicalAddress >>> 24) == 0xff && (physicalAddress >>> 16) != 0XFFFF)
		{
			if(this.dispMode == 0)
			{
				innerToolBar.setPrevPageEnabled(physicalAddress != 0xff000000);
				innerToolBar.setNextPageEnabled((physicalAddress + 0x1000 ) < 0XFFFF0000);
			}
			dataController.getBus().getRom().readBlock(physicalAddress, 4096, dataBuffer);
		}
		else if(Util.compareIntIgnoreSign(physicalAddress , dataController.GetRamSize()) < 0)
		{
			if(this.dispMode == 0)
			{
				innerToolBar.setPrevPageEnabled(physicalAddress != 0);
				innerToolBar.setNextPageEnabled(Util.compareIntIgnoreSign(physicalAddress + 0x1000 , dataController.GetRamSize())< 0);
			}
			dataController.getBus().getMainMemory().readBlock(physicalAddress, 4096, dataBuffer);
		}
		else
			return false;

		int adr, data;
		for(int i = 0; i < 1024; i++)
		{
			adr = startAddress +(i<<2);
			data = dataBuffer[i];
			inspectorTextTableBase.insertData(adr, (byte)(data&0xff), (byte)((data >> 8)&0xff), (byte)((data >> 16)&0xff), (byte)(data >> 24));
		}
		return true;
	}
	
	public void gotoPage(int addr)
	{
		startAddress = addr;
		innerToolBar.setPageAddr(addr);
		RefreshData();
	}
	
	public void gotoAddr(int addr)
	{
		System.out.println(addr);
		startAddress = addr & 0xfffff000;
		innerToolBar.setPageAddr(addr);
		innerToolBar.setAddr(addr);
		if(RefreshData())
		{
			int v = inspectorTextTableBase.getYAndSelcet((addr & 0xfff)>>>2);
			int h = jScrollPane.getHeight() >> 2;
			System.out.println(h);
			jScrollPane.getVerticalScrollBar().setValue( (v / h) * h);
		}
	}
	
	public void findData()
	{
		int rowStart = inspectorTextTableBase.getSelectedRow();
		int colStart = inspectorTextTableBase.getSelectedColumn();
		if(rowStart < 0)
			rowStart = colStart = 0;
		else
		{
			colStart ++;
			if(colStart == 7)
			{
				colStart = 2;
				rowStart ++;
			}
		}
		int v = inspectorTextTableBase.findData(rowStart, colStart, innerToolBar.getKeyWord());
		if(v>= 0)
		{
			int h = jScrollPane.getHeight() >> 2;
			jScrollPane.getVerticalScrollBar().setValue( (v / h) * h);
		}
	}
	
	public void refreshPage()
	{
		int v = jScrollPane.getVerticalScrollBar().getValue();
		gotoPage(startAddress);
		jScrollPane.getVerticalScrollBar().setValue(v);	
	}
	
	protected class InspectorInnerToolBar extends JToolBar
	{
		public InspectorInnerToolBar()
		{
			// TODO 锟皆讹拷锟斤拷锟缴的癸拷锟届函锟斤拷锟斤拷锟�
			FocusListener focusListener = new FocusListener()
			{
				
				@Override
				public void focusLost(FocusEvent arg0)
				{
					// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
					
				}
				
				@Override
				public void focusGained(FocusEvent arg0)
				{
					// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
					((JTextField)arg0.getSource()).selectAll();
				}
			};
			
			setRollover(true);
			
			
			gotoAddrButton.setToolTipText("Go to address");
			gotoPageButton.setToolTipText("Go to page");
			gotoAddrButton.setActionCommand("GoToAddr");
			gotoPageButton.setActionCommand("GoToPage");
			gotoAddrButton.addActionListener(innerActionListener);
			gotoPageButton.addActionListener(innerActionListener);
			
			gotoAddrField.setMaximumSize(gotoAddrField.getPreferredSize());
			gotoPageField.setMaximumSize(gotoPageField.getPreferredSize());
			gotoAddrField.addKeyListener(innerKeyListener);
			gotoPageField.addKeyListener(innerKeyListener);
			gotoAddrField.addFocusListener(focusListener);
			gotoPageField.addFocusListener(focusListener);
			
			gotoAddrField.setName("AddrField");
			gotoPageField.setName("PageField");
			
			add(gotoAddrField);
			add(gotoAddrButton);
			add(gotoPageField);
			add(gotoPageButton);
			
			
			findDataField.setMaximumSize(findDataField.getPreferredSize());
			findDataButton.setActionCommand("FindData");
			findDataButton.addActionListener(innerActionListener);
			findDataField.addKeyListener(innerKeyListener);
			findDataField.setName("DataField");
			add(findDataField);
			add(findDataButton);
			

			addSeparator();
			prevPage.setActionCommand("PrevPage");
			refreshPage.setActionCommand("RefreshPage");
			nextPage.setActionCommand("NextPage");
			prevPage.addActionListener(innerActionListener);
			refreshPage.addActionListener(innerActionListener);
			nextPage.addActionListener(innerActionListener);
			add(prevPage);
			add(refreshPage);
			add(nextPage);
		}
		
		public int getPageAddr() throws NumberFormatException
		{
			return Util.parseStringToHex(gotoPageField.getText()) << 12;
		}
		
		public int getAddr() throws NumberFormatException
		{
			return Util.parseStringToHex(gotoAddrField.getText());
		}
		
		public void setPageAddr(int addr)
		{
			addr >>>= 12;
			gotoPageField.setText(String.format("%05X", addr));
		}
		
		public void setAddr(int addr)
		{
			gotoAddrField.setText(String.format("%08X", addr));
		}
		
		public String getKeyWord()
		{
			return findDataField.getText();
		}
		
		public void setKeyWord(String k)
		{
			findDataField.setText(k);
		}
		
		public void setNextPageEnabled(boolean b)
		{
			nextPage.setEnabled(b);
		}
		
		public void setPrevPageEnabled(boolean b)
		{
			prevPage.setEnabled(b);
		}
		
		private JButton gotoPageButton = new JButton(new ImageIcon("./ui/icon_active/gotopage.png"));
		private JButton gotoAddrButton = new JButton(new ImageIcon("./ui/icon_active/gotoaddr.png"));
		private JButton findDataButton = new JButton(new ImageIcon("./ui/icon_active/find.png"));
		private JButton nextPage = new JButton(new ImageIcon("./ui/icon_active/next.png"));
		private JButton prevPage = new JButton(new ImageIcon("./ui/icon_active/prev.png"));
		private JButton refreshPage = new JButton(new ImageIcon("./ui/icon_active/refresh.png"));
		private JTextField gotoPageField = new JTextField(6);
		private JTextField gotoAddrField = new JTextField(9);
		private JTextField findDataField = new JTextField(12);
	}
	
	protected class InnerActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
			try
			{
				switch (arg0.getActionCommand())
				{
				case "GoToAddr":
					gotoAddr(innerToolBar.getAddr());;
					break;
				case "GoToPage":
					gotoPage(innerToolBar.getPageAddr());
					break;
				case "PrevPage":
					gotoPage(startAddress - 0x1000);	
					break;
				case "NextPage":
					gotoPage(startAddress + 0x1000);
					break;
				case "RefreshPage":
					refreshPage();
					break;
				case "FindData":
					findData();
					break;
				default:
					break;
				}
			}
			catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected class InnerKeyListener implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent arg0)
		{
			// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
			
		}

		@Override
		public void keyReleased(KeyEvent arg0)
		{
			// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
			try
			{
				switch (arg0.getKeyCode())
				{
				case KeyEvent.VK_ENTER:
					switch (((JTextField)arg0.getSource()).getName()) {
					case "AddrField":
						gotoAddr(innerToolBar.getAddr());
						break;
					case "PageField":
						gotoPage(innerToolBar.getPageAddr());
						break;
					case "DataField":
						findData();
						break;
					default:
						break;
					}
					break;
						
				case KeyEvent.VK_F5:
					refreshPage();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					gotoPage(startAddress + 0x1000);
					break;
				case KeyEvent.VK_PAGE_UP:
					gotoPage(startAddress - 0x1000);
					break;
				default:
					break;
				}
			}
			catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0)
		{
			// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
			
			
		}
		
	}
	
	protected InnerKeyListener innerKeyListener = new InnerKeyListener();
	protected InnerActionListener innerActionListener = new InnerActionListener();
	protected InspectorInnerToolBar innerToolBar = new InspectorInnerToolBar();
	protected JScrollPane jScrollPane;
	protected DataController dataController;
	protected int startAddress = 0;
	protected int dispMode = 0;
	protected int physicalAddress = 0;
	protected InspectorTextTableBase inspectorTextTableBase;
	private int dataBuffer[] = new int[1024];
}
