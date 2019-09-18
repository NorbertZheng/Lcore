package com.njy.project.simulator.ui.main.inspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class InspectorTextTableBase extends JTable
{
	public InspectorTextTableBase()
	{
		// TODO �Զ���ɵĹ��캯����
		setModel(new DefaultTableModel(row, column));
		setShowGrid(false);
		setBackground(normalBG);
		getTableHeader().setFont(plainFont);
		getTableHeader().setReorderingAllowed(false);   //���������ƶ�   
		getTableHeader().setResizingAllowed(false);   //�����������
		int w = Toolkit.getDefaultToolkit().getFontMetrics(pcFont).getWidths()[Font.BOLD | Font.ITALIC];
		addMouseMotionListener(rowCellRender);
		addMouseListener(rowCellRender);
		initFontAndColor();
		TableColumnModel   cm   =   getColumnModel();     //������ģ��

		cm.getColumn(0).setCellRenderer(myLabelRenderer);
		cm.getColumn(0).setMaxWidth(columnWitdhs[0] * w);
		cm.getColumn(0).setMinWidth(columnWitdhs[0] * w);
		for(int i = 1; i< 7; i++)
		{
			TableColumn   column  = cm.getColumn(i);//�õ���i���ж���   
			if(i!= 6)
			{
				column.setMaxWidth(columnWitdhs[i] * w);
				column.setMinWidth(columnWitdhs[i] * w);
			}
			column.setCellRenderer(rowCellRender);
		}
		setColumnSelectionAllowed(true);
		
		setDataIndex();
		
	}
	
	public int getYAndSelcet(int pos)
	{
		setColumnSelectionInterval(0, getColumnCount()-1);
		setRowSelectionInterval(pos, pos);
		return getY(pos);
	}
	
	public int getY(int pos)
	{
		return getRowHeight() * pos;
	}
	
	public void setPC(int pos)
	{
		if(breakDisposer != null && breakDisposer.isBreakPoint(pos))
			setValueAt(bkinstPtrLabel, pos, 0);
		else
			setValueAt(instPtrLabel, pos, 0);
		rowFonts[pos] = pcFont;
		rowColors[pos] = pcForeColor;
		rowBGColors[pos] = pcBG;
	}
	
	public void setPCBreak(int pos)
	{
		setValueAt(bkinstPtrLabel, pos, 0);
		rowFonts[pos] = pcFont;
		rowColors[pos] = pcForeColor;
		rowBGColors[pos] = pcBG;
	}
	
	public void setBreak(int pos)
	{
		setValueAt(brkPtrLabel, pos, 0);
		rowFonts[pos] = breakFont;
		rowColors[pos] = breakForeColor;
		rowBGColors[pos]= breakBG;
	}
	
	public void loadBreak()
	{
		if(breakDisposer!=null)
			breakDisposer.loadBreak();
	}
	
	public void setChanged(int pos)
	{
		rowFonts[pos] = changedFont;
		rowColors[pos] = changedForeColor;
		rowBGColors[pos]= changedBG;
	}
	
	public void resetLine(int pos)
	{
		setValueAt(null, pos, 0);
		rowFonts[pos] = plainFont;
		rowColors[pos] = plainForeColor;
		rowBGColors[pos]= normalBG;
	}
	
	private void setDataIndex()
	{
		for(int i = 0; i< 4; i++)
			dataIndex[i] = getColumn("0"+i).getModelIndex();
	}
	
	private void initFontAndColor()
	{
		for(int i = 0; i < 1024; i++)
		{
			rowColors[i] = plainForeColor;
			rowBGColors[i] = normalBG;
			rowFonts[i] = plainFont;
		}
	}
	
	public void reverseData()
	{
		
		for(int i = 0; i < 4 ;i++)
		{
			columnModel.getColumn(dataIndex[i]).setHeaderValue("0" + (3-i));
		}
		
		setDataIndex();
	}
	
	public int  findData(int startRow, int startCol, String s)
	{
		System.out.println(startRow + " " + startCol + " " + s);
		int col = getColumnCount();
		int row = getRowCount();
		for(int j = startCol < 2 ? 2 : startCol; j < col; j++)
		{
			int f = ((String)getValueAt(startRow, j)).indexOf(s);
			System.out.println(f);
			if(f != -1)
			{
				setRowSelectionInterval(startRow, startRow);
				setColumnSelectionInterval(j, j);
				return getY(startRow);
			}
		}
		
		for(int i = startRow + 1 ; i < row; i++)
		{
			for(int j = 2; j < col; j++)
			{
				int f = ((String)getValueAt(i, j)).indexOf(s);
				System.out.println(f);
				if(f != -1)
				{
					setRowSelectionInterval(i, i);
					setColumnSelectionInterval(j, j);
					return getY(i);
				}
			}
		}
		
		return -1;
	}
	
	public void setBreakDisposer(InspectorBreakDisposerInterface breakDisposer) 
	{
		this.breakDisposer = breakDisposer;
	}
	
	public void setDumper(InspectorDumperInterface dumper)
	{
		this.dumper = dumper;
	}
	
	public void insertData(int address, byte b0, byte b1, byte b2, byte b3)
	{
		// TODO �Զ���ɵķ������
		int pos = (address & 0xfff) >>> 2;
		resetLine(pos);
		setValueAt(String.format("%08X", address), pos, 1);
		setValueAt(String.format("%02X", b0), pos, dataIndex[0]);
		setValueAt(String.format("%02X", b1), pos, dataIndex[1]);
		setValueAt(String.format("%02X", b2), pos, dataIndex[2]);
		setValueAt(String.format("%02X", b3), pos, dataIndex[3]);
		
		if(dumper != null)
			setValueAt(dumper.dump(address, b0, b1, b2, b3), pos, 6);
	}
	
	@Override
	public boolean isCellEditable(int arg0, int arg1)
	{
		// TODO �Զ���ɵķ������
		return false;
	}
	
	public class MyRowCellRender extends DefaultTableCellRenderer implements MouseMotionListener,MouseListener {
		private int rowHover = -1;
		private boolean in = false;
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// TODO Auto-generated method stub
			setBackground(rowBGColors[row]);
			if(in && row == rowHover) 
				setBackground(hoverBG);
			Component component =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			setHorizontalAlignment(columnAligns[column]);
			setFont(rowFonts[row]);
			setForeground(rowColors[row]);
			return component;
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			int tmp = InspectorTextTableBase.this.rowAtPoint(e.getPoint());
			if(tmp!=rowHover)
			{
				rowHover = tmp;
				InspectorTextTableBase.this.repaint();
			}
			
			//InspectorTextTableBase.this.repaint();
		}
		@Override
		public void mouseClicked(MouseEvent arg0)
		{
			// TODO �Զ���ɵķ������
			
			if(InspectorTextTableBase.this.columnAtPoint(arg0.getPoint()) == 0)
			{
				if(arg0.getClickCount() == 2 && arg0.getButton() == MouseEvent.BUTTON1)
				{
					if(breakDisposer != null) breakDisposer.toggleBreak(InspectorTextTableBase.this.rowAtPoint(arg0.getPoint()));
				}
				else if(arg0.getClickCount() == 1 && arg0.getButton() == MouseEvent.BUTTON1)
				{
					InspectorTextTableBase.this.setRowSelectionInterval(InspectorTextTableBase.this.rowAtPoint(arg0.getPoint()),
							InspectorTextTableBase.this.rowAtPoint(arg0.getPoint()));
					InspectorTextTableBase.this.setColumnSelectionInterval(0, InspectorTextTableBase.this.getColumnCount() - 1);
				}
			}
		}
		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			// TODO �Զ���ɵķ������
			in = true;
			
		}
		@Override
		public void mouseExited(MouseEvent arg0)
		{
			// TODO �Զ���ɵķ������
			in = false;
			InspectorTextTableBase.this.repaint();
		}
		@Override
		public void mousePressed(MouseEvent arg0)
		{
			// TODO �Զ���ɵķ������
			
		}
		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			// TODO �Զ���ɵķ������
			
		}


	}
	
	public class MyLabelRenderer extends JLabel implements TableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object arg1, boolean arg2, boolean arg3, int arg4, int arg5)
		{
			// TODO �Զ���ɵķ������
			setIcon((ImageIcon)arg1);
			return this;
		}
		
	}
	
	final private int dataIndex[] = new int[4];
	final private int columnAligns[]={JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.LEFT};
	final private int columnWitdhs[] = {2,10,4,4,4,4,10};
	final private String[] column={"","Address","00","01","02","03","Dump"};
	final private Object[][] row=new Object[1024][7];
	final private Font plainFont = new Font("Consolas", Font.PLAIN, 12);
	final private Font pcFont = new Font("Consolas", Font.BOLD | Font.ITALIC, 12);
	final private Font breakFont = new Font("Consolas", Font.BOLD | Font.ITALIC, 12);
	final private Font changedFont = new Font("Consolas", Font.BOLD | Font.ITALIC, 12);
	final private Color plainForeColor = Color.DARK_GRAY;
	final private Color pcForeColor = Color.RED;
	final private Color breakForeColor = Color.BLUE;
	final private Color changedForeColor = Color.RED;
	final private Color normalBG = UIManager.getColor("Label.background");
	final private Color hoverBG = Color.WHITE;
	final private Color breakBG = Color.YELLOW;
	final private Color pcBG = Color.LIGHT_GRAY;
	final private Color changedBG = Color.LIGHT_GRAY;
	
	
	private MyRowCellRender rowCellRender = new MyRowCellRender();
	private Color[] rowColors = new Color[1024];
	private Color[] rowBGColors = new Color[1024];
	private Font[] rowFonts = new Font[1024];
	
	private ImageIcon instPtrLabel = new ImageIcon("./ui/icon_active/inst_ptr.png");
	private ImageIcon brkPtrLabel =  new ImageIcon("./ui/icon_active/brk_ptr.png");
	private ImageIcon bkinstPtrLabel = new ImageIcon("./ui/icon_active/bkinst_ptr.png");
	private MyLabelRenderer myLabelRenderer = new MyLabelRenderer();
	private InspectorBreakDisposerInterface breakDisposer = null;
	private InspectorDumperInterface dumper = null;
	
}
