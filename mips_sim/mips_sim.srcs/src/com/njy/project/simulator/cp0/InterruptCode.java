package com.njy.project.simulator.cp0;

public class InterruptCode {
	public static final int Reserved = 0;
	public static final int Timer = 0;
	public static final int VGA = 1;
	public static final int BtnSW = 2;
	public static final int Keyboard = 3;
	public static final int Mouse = 4;
	public static final int SPI = 5;
	public static final int UART= 6;
	public static final int ParallelPort = 7;
	public static final int USB= 8;
	public static final int NET= 9;
	
	public static String[] InterruptDescription = 
		{
			"Timer",    
			"VGA",    
			"Btn&SW",        
			"Keyboard",      
			"Mouse",  
			"SPI",     
		};
}
