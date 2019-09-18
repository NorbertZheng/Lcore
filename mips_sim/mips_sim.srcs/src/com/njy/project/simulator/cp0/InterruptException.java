package com.njy.project.simulator.cp0;

public class InterruptException extends Exception 
{

	public InterruptException(){}
	
	public InterruptException(String msg)
	{
		super(msg);
		//System.out.println(msg);
		//System.exit(0);
	}
}
