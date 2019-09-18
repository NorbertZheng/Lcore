package com.njy.project.simulator.cpu;

public class CPUStatus {
	public static final int STOPPED = 0x00;
	public static final int RUNING = 0x01;
	public static final int PAUSED = 0x02;
	public static final int DEBUG = 0x03;
	public static final int DEBUGPAUSED = 0x04;
	
	public static String []statusDescription = {
			"Stopped","Running", "Paused", "Debuging", "Debuging Paused"	
		};
}
