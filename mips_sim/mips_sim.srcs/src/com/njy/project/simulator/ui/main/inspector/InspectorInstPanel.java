package com.njy.project.simulator.ui.main.inspector;

import java.util.ArrayList;

import javax.swing.border.TitledBorder;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.njy.project.simulator.cpu.MIPSCPU;
import com.njy.project.simulator.cpu.debug.BreakPoint;
import com.njy.project.simulator.cpu.debug.Disassembler;
import com.njy.project.simulator.data.DataController;
import com.sun.jndi.cosnaming.IiopUrl.Address;

public class InspectorInstPanel extends InspectorPanelBase
{
	public InspectorInstPanel() 
	{
		// TODO Auto-generated constructor stub
		inspectorTextTableBase.setDumper(new InspectorDumperInterface()
		{
			
			@Override
			public String dump(int addr, byte b0, byte b1, byte b2, byte b3)
			{
				// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
				return Disassembler.singleInstruction((b0&0xff)|((b1&0xff) << 8)|((b2&0xff) << 16)|((b3&0xff) << 24), addr);
			}
		});
		setBorder(new TitledBorder("Instruction"));
		inspectorTextTableBase.reverseData();
		inspectorTextTableBase.setBreakDisposer(new MyBreakDisposer());
		dispMode = 1;
		gotoPage(0);
	}
	
	@Override
	public boolean RefreshData() {
		// TODO Auto-generated method stub
		boolean res = super.RefreshData();
		inspectorTextTableBase.loadBreak();
		pc = dataController.getMips_CPU().getPcNext();
		
		if((pc & 0xfffff000) == startAddress)
			inspectorTextTableBase.setPC((pc & 0xfff) >>> 2);
		
		return res;
	}
	
	public void gotoPC(int addr) {
		// TODO Auto-generated method stub
		startAddress = addr & 0xfffff000;
		innerToolBar.setPageAddr(addr);
		innerToolBar.setAddr(addr);
		if(RefreshData())
		{
			int v = inspectorTextTableBase.getY((addr & 0xfff)>>>2);
			int h = jScrollPane.getHeight() >> 2;
			System.out.println(h);
			jScrollPane.getVerticalScrollBar().setValue( (v / h) * h);
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
			if(cpu.checkBreakPoint(addr))
			{
				cpu.removeBreakPoint(addr);
				if(addr == pc)
					inspectorTextTableBase.setPC(pos);
				else
					inspectorTextTableBase.resetLine(pos);
				
				return;
			}
			
			cpu.addBreakPoint(addr);
			if(addr == pc)
			{
				inspectorTextTableBase.setPC(pos);
				return;
			}
			
			inspectorTextTableBase.setBreak(pos);
		}

		@Override
		public void loadBreak() 
		{
			// TODO Auto-generated method stub
			ArrayList<Long> breakList = cpu.getBreak(physicalAddress);
			int addr, data;
			for(Long l: breakList)
			{
				addr = (int)(l >>> 32);
				data = (int)l.longValue();
				inspectorTextTableBase.insertData(startAddress + (addr & 0xfff), (byte)(data&0xff), (byte)((data >> 8)&0xff), (byte)((data >> 16)&0xff), (byte)(data >> 24));
				inspectorTextTableBase.setBreak((addr & 0xfff) >>> 2);
			}
		}
		
		@Override
		public boolean isBreakPoint(int pos) {
			// TODO Auto-generated method stub
			return cpu.checkBreakPoint(physicalAddress + (pos << 2));
		}
		
	}
	
	private int pc = 0;
}
