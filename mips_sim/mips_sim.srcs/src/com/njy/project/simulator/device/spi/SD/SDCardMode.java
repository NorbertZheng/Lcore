package com.njy.project.simulator.device.spi.SD;

public class SDCardMode
{
	final public static int CMDRECV = 0; 
	final public static int CMDREPL = 1; 
	final public static int SINGLEREADREPL = 2; 
	final public static int MULTIREADREPL = 3; 
	final public static int SINGLEWRITEREPL = 4; 
	final public static int MULTIWRITEREPL = 5; 
	final public static int SINGLEDATSEND = 6; 
	final public static int MULTIDATSEND = 7; 
	final public static int SINGLEDATRECV = 8; 
	final public static int MULTIDATRECV = 9; 
	final public static int SINGLEDATARESPONSE = 10; 
	final public static int MULTIDATARESPONSE = 11; 
	final public static int IMMRESPONSE = 12; 
	
	final public static int IDLE = 0;
	final public static int READY = 1;
	final public static int READING = 2;
	final public static int WRITEING = 3;
	final public static int ERASING = 4;
	final public static int ERROR = 5;
	
	//read error code
	final public static int READERROR = 1;
	final public static int CCERROR = 2;
	final public static int CARDECCERROR = 4;
	final public static int OUTOFRANGE = 8;
	
	//response token
	final public static int DATACCEPTED = 0x05;
	final public static int CRCERROR = 0x0B;
	final public static int WRITEERROR = 0x0D;
}
