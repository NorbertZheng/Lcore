package com.njy.project.simulator.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class UIGridBagConstraints extends GridBagConstraints
{
	public UIGridBagConstraints(int gridx, int gridy)
	{
		this.gridx=gridx;
		this.gridy=gridy;
	}
	
	public UIGridBagConstraints(int gridx,int gridy, int gridwidth, int gridheight)
	{
		this.gridx=gridx;
		this.gridy=gridy;
		this.gridwidth=gridwidth;
		this.gridheight=gridheight;
	}
	
	public UIGridBagConstraints setAnchor(int anchor)
	{
		this.anchor=anchor;
		return this;
	}
	
	public UIGridBagConstraints setFill(int fill)
	{
		this.fill=fill;
		return this;
	}
	
	public UIGridBagConstraints setWeight(double weightx, double weighty)
	{
		this.weightx=weightx;
		this.weighty=weighty;
		return this;
	}
	
	public UIGridBagConstraints setInsets(int distance)
	{
		this.insets = new Insets(distance, distance, distance, distance);
		return this;
	}
	
	public UIGridBagConstraints setInsets(int top, int left, int bottom, int right)
	{
		this.insets=new Insets(top, left, bottom, right);
		return this;		
	}
	
	public UIGridBagConstraints setIpad(int ipadx, int ipady) 
	{
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}
}