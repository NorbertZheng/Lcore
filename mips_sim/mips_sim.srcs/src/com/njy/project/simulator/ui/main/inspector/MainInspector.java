package com.njy.project.simulator.ui.main.inspector;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.njy.project.simulator.cpu.CPUStatus;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.ui.UIController;
import com.njy.project.simulator.ui.UIInterface;

public class MainInspector extends JPanel implements UIInterface
{
	public MainInspector()
	{
		// TODO �Զ���ɵĹ��캯����
		UIController.getInstance().AddUI(this);
		setLayout(new BorderLayout());
		add(inspectorDataPanel, BorderLayout.WEST);
		add(inspectorInstPanel,BorderLayout.CENTER);
		add(inspectorRegPanel, BorderLayout.EAST);
	}
	
	
	@Override
	public void Refresh() {
		// TODO Auto-generated method stub
		DataController dataController = DataController.getInstance();
		switch (DataController.getInstance().getStatus()) {
		case CPUStatus.STOPPED:
			if(dataController.isRamFileLoaded() || dataController.isRomFileLoaded())
			{
				inspectorInstPanel.gotoPC(dataController.getMips_CPU().getPcNext());
				inspectorInstPanel.requestFocusInWindow();
			}
		case CPUStatus.RUNING:
		case CPUStatus.PAUSED:
		case CPUStatus.DEBUG:
			break;
		case CPUStatus.DEBUGPAUSED:
			inspectorInstPanel.gotoPC(dataController.getMips_CPU().getPcNext());
			inspectorInstPanel.requestFocusInWindow();
			break;
		default:
			break;
		}
		
		inspectorDataPanel.refreshPage();
		inspectorRegPanel.Refresh();
		
	}
	
	private InspectorDataPanel inspectorDataPanel = new InspectorDataPanel();
	private InspectorInstPanel inspectorInstPanel = new InspectorInstPanel();
	private InspectorRegPanel inspectorRegPanel = new InspectorRegPanel();
}
