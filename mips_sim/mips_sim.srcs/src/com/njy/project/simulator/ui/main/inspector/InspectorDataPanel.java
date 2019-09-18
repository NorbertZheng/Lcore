package com.njy.project.simulator.ui.main.inspector;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cpu.MIPSCPU;
import com.njy.project.simulator.data.DataController;


public class InspectorDataPanel extends InspectorPanelBase
{
	public InspectorDataPanel()
	{
		// TODO �Զ���ɵĹ��캯����
		inspectorTextTableBase.setDumper(dataDumper);
		inspectorTextTableBase.setBreakDisposer(new MyBreakDisposer());
		jTableHeader = inspectorTextTableBase.getTableHeader();
		inspectorTextTableBase.getColumnModel().getColumn(6).setHeaderValue(modeDisp[dataDumper.getDumpMode()]);
		jTableHeader.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getButton() == MouseEvent.BUTTON1)
				{
					int col = jTableHeader.columnAtPoint(e.getPoint());
					if(col == 6)
					{
						inspectorTextTableBase.getColumnModel().getColumn(col).setHeaderValue(modeDisp[dataDumper.changeDumpMode()]);
						jTableHeader.repaint();
						refreshPage();
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		setBorder(new TitledBorder("Data"));
		
		gotoPage(0);
	}
	
	@Override
	public boolean RefreshData()
	{
		// TODO �Զ���ɵķ������
		if((startAddress >>> 16) == 0xffff)
		{
			Bus bus = dataController.getBus();
			int adr, data;
			for(int i = 0; i < 1024; i++)
			{
				adr = startAddress +(i<<2);
				data = bus.read(adr);
				inspectorTextTableBase.insertData(adr, (byte)(data&0xff), (byte)((data >> 8)&0xff), (byte)((data >> 16)&0xff), (byte)(data >> 24));
			}
			return true;

		}
		return super.RefreshData();
	}
	
	private class DataDumper implements InspectorDumperInterface
	{
		private int dumpMode = 0;
		@Override
		public String dump(int addr, byte b0, byte b1, byte b2, byte b3) {
			// TODO Auto-generated method stub
			switch (dumpMode) {
			case 0:
				return String.format("%c%c%c%c", b0 >= 0? b0:b0+256, b1 >= 0? b1:b1+256, b2 >= 0? b2:b2+256, b3 >= 0? b3:b3+256);
			case 1:
				return String.format("%3d %3d %3d %3d", b0 & 0xff, b1 & 0xff, b2 & 0xff, b3 & 0xff);
			case 2:
				return String.format("%11d", (b0 & 0xff) | ((b1 & 0xff) << 8) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 24));
			case 3:
				return String.format(" %10d", ((long)((b0 & 0xff) | ((b1 & 0xff) << 8) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 24))) & 0xffffffffl);
			default:
				break;
			}
			return null;
		}
		public int getDumpMode() {
			return dumpMode;
		}
		public void setDumpMode(int dumpMode) {
			this.dumpMode = dumpMode;
		}
		
		public int changeDumpMode()
		{
			if(dumpMode++ == 3)
				dumpMode = 0;
			
			return dumpMode;
		}
		
	}
	
	
	public class MyBreakDisposer implements InspectorBreakDisposerInterface
	{
		private MIPSCPU cpu = null;
		public MyBreakDisposer() 
		{
			// TODO Auto-generated constructor stub
			cpu = DataController.getInstance().getMips_CPU();
		}
		@Override
		public void toggleBreak(int pos) 
		{
			// TODO Auto-generated method stub
			
			int addr = physicalAddress + (pos << 2);
			System.out.printf("break point addr:%08X\n",addr);
			if(cpu.checkDataBreakPoint(addr))
			{
				cpu.removeDataBreakPoint(addr);
				inspectorTextTableBase.resetLine(pos);
				return;
			}
			
			cpu.addDataBreakPoint(addr);
			inspectorTextTableBase.setBreak(pos);
		}

		@Override
		public void loadBreak() 
		{
			// TODO Auto-generated method stub
			ArrayList<Integer> breakList = cpu.getDataBreak(physicalAddress);
			for(Integer l: breakList)
			{
				inspectorTextTableBase.setBreak((l & 0xfff) >>> 2);
			}
		}
		
		@Override
		public boolean isBreakPoint(int pos) {
			// TODO Auto-generated method stub
			return cpu.checkDataBreakPoint(physicalAddress + (pos << 2));
		}
		
	}
	
	private DataDumper dataDumper = new DataDumper();
	private JTableHeader jTableHeader;
	private String[] modeDisp = {
		"Dump(char)", "Dump(byte)","Dump(int)","Dump(uint)",
	};
}
