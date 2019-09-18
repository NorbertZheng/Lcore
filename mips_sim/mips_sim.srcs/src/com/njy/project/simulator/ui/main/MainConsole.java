package com.njy.project.simulator.ui.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

import com.njy.project.simulator.data.DataController;


public class MainConsole extends JPanel
{
	private static MainConsole instance = null;
	
	public static MainConsole getInstance()
	{
		if(instance == null)
			instance = new MainConsole();
		
		return instance;
	}
	private MainConsole()
	{
		// TODO �Զ���ɵĹ��캯����
		setPreferredSize(new Dimension(640, 160));
		setBorder(new TitledBorder("Console"));
		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		jTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		jTextArea.setBackground(Color.WHITE);
		document = jTextArea.getDocument();
		setLayout(new GridLayout(1,1));
		add(new JScrollPane(jTextArea));
		WelcomMessage();
	}
	
	public void InsertMessage(String s)
	{
		jTextArea.insert(">" + s + "\n", document.getLength());
	}
	
	public void WelcomMessage()
	{
		jTextArea.insert(">MIPS SIMULATOR " + DataController.version + "\n", document.getLength());
		jTextArea.insert(">Welcom!\n", document.getLength());
	}
	
	private Document document;
	private JTextArea jTextArea;

}
