package com.njy.project.simulator.ui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

public class PreferrenceDialog extends JDialog
{
	public PreferrenceDialog()
	{
		// TODO �Զ���ɵĹ��캯����
		setTitle("Preferrence");
		setModal(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(width, height);
		setLocation((dim.width - width) >> 1, (dim.height - height) >> 1);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		initGenPanel();
		initSysPanel();
		initSimPanel();
		initAdvPanel();
		
		jTabbedPane.add("General", genJPanel);
		jTabbedPane.add("System", sysJPanel);
		jTabbedPane.add("Simulator", simJPanel);
		jTabbedPane.add("Advanced", advJPanel);
		
		setLayout(new BorderLayout());
		add(jTabbedPane, BorderLayout.CENTER);
		
		setVisible(true);
		
	}
	
	private void initGenPanel()
	{
		genJPanel.setLayout(new GridLayout(4,2));
		genJPanel.add(new JLabel("Default Rom File"));
		genJPanel.add(new JButton("Select"));
	}
	
	private void initSysPanel()
	{
		sysJPanel.setLayout(new GridLayout(4,2));
		sysJPanel.add(new JLabel("RAM SIZE"));
		sysJPanel.add(new JComboBox<>());
		sysJPanel.add(new JLabel("Default Rom File"));
		sysJPanel.add(new JButton("Select"));
		
	}
	
	private void initSimPanel()
	{
		simJPanel.setLayout(new GridLayout(10,4,5,5));
		simJPanel.setBorder(new CompoundBorder(new EtchedBorder(), new LineBorder(simJPanel.getBackground(), 10)));
		simJPanel.add(new JLabel("Disable Delay Slot(Need Reboot)"));
		simJPanel.add(new JCheckBox());
		simJPanel.add(new JLabel("MMU"));
		simJPanel.add(new JComboBox<>());
		simJPanel.add(new JLabel("Initial PC"));
		simJPanel.add(new JCheckBox());
		simJPanel.add(new JLabel("Print Exceptions"));
		simJPanel.add(new JCheckBox());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());
		simJPanel.add(new JLabel());

	}
	
	private void initAdvPanel()
	{
		advJPanel.setLayout(new GridLayout(4,2));
	}
	
	private JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP) ;
	private JPanel genJPanel = new JPanel();
	private JPanel sysJPanel = new JPanel();
	private JPanel simJPanel = new JPanel();
	private JPanel advJPanel = new JPanel();
	private final int width = 400;
	private final int height = 300;
}
