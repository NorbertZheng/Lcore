package com.njy.project.simulator.cp0;

public class ExceptionCode {
	public static final int InvPCAdddr = -1;
	public static final int InvDataAddr = -2;
	public static final int UnAlignedPCAddr = 1;
	public static final int UnAlignedDataAddr = 2;
	public static final int UnDefinedInst = 3;
	public static final int IllegalInst = 4;
	public static final int NoPgTableEntry = 5;
	public static final int IllegalVisit= 6;
	public static final int IllegalExec= 7;
	public static final int IllegalWrite = 8;
	public static final int ArithmaticOverflow= 9;
	public static final int ZeroDivisor = 10;
	
	public static String[] ExceptionDescription = 
	{
		"",
		"UnAlignedPCAddr",    
		"UnAlignedDataAddr",    
		"UnDefinedInst",        
		"IllegalInst",      
		"NoPgTableEntry",  
		"IllegalVisit",
		"IllegalExec",          
		"IllegalWrite",           
		"ArithmaticOverflow",   
		"ZeroDivisor",         
	};
}
