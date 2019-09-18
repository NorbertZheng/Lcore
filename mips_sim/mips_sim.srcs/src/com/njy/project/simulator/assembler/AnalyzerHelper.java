package com.njy.project.simulator.assembler;

import java.util.HashMap;

public class AnalyzerHelper
{
	public static HashMap<String, Integer> RegMap= new HashMap<>();
	public static HashMap<String, Integer> OpMap = new HashMap<>();
	public static HashMap<String, Integer> FnMap = new HashMap<>();
	public static HashMap<String, Integer> LabelMap = new HashMap<>();
	public static HashMap<String, Integer> BZMap = new HashMap<>();
	public static HashMap<String, Integer> CP0Map = new HashMap<>();
	static {
		////////////////////////////////////////////////////
		for(int i = 0; i < 32 ;i++)
		{
			RegMap.put("$"+i, i);
		}
		RegMap.put("$ZERO", 0); 
		RegMap.put("$AT", 1);
		RegMap.put("$V0", 2);
		RegMap.put("$V1", 3);
		RegMap.put("$A0", 4);
		RegMap.put("$A1", 5);
		RegMap.put("$A2", 6);
		RegMap.put("$A3", 7);
		RegMap.put("$T0", 8);
		RegMap.put("$T1", 9);
		RegMap.put("$T2", 10);
		RegMap.put("$T3", 11);
		RegMap.put("$T4", 12);
		RegMap.put("$T5", 13);
		RegMap.put("$T6", 14);
		RegMap.put("$T7", 15);
		RegMap.put("$S0", 16);
		RegMap.put("$S1", 17);
		RegMap.put("$S2", 18);
		RegMap.put("$S3", 19);
		RegMap.put("$S4", 20);
		RegMap.put("$S5", 21);
		RegMap.put("$S6", 22);
		RegMap.put("$S7", 23);
		RegMap.put("$T8", 24);
		RegMap.put("$T9", 25);
		RegMap.put("$K0", 26);
		RegMap.put("$K1", 27);
		RegMap.put("$GP", 28);
		RegMap.put("$SP", 29);
		RegMap.put("$FP", 30);
		RegMap.put("$RA", 31);
		
		////////////////////////////////////////////////
		///////////////////////RTYPE/////////////////////////
		OpMap.put("SLL", 0x00);     
		OpMap.put("SRL", 0x00);     
		OpMap.put("SRA", 0x00);     
		OpMap.put("SLLV", 0x00);    
		OpMap.put("SRLV", 0x00);    
		OpMap.put("SRAV", 0x00); 
		OpMap.put("ROTRV", 0x00); 
		OpMap.put("ROTR", 0x00); 
		OpMap.put("JR", 0x00);      
		OpMap.put("JALR", 0x00);    
		OpMap.put("ADD", 0x00);     
		OpMap.put("ADDU", 0x00);    
		OpMap.put("SUB", 0x00);     
		OpMap.put("SUBU", 0x00);    
		OpMap.put("AND", 0x00);     
		OpMap.put("OR", 0x00);      
		OpMap.put("XOR", 0x00);     
		OpMap.put("NOR", 0x00);     
		OpMap.put("SLT", 0x00);     
		OpMap.put("SLTU", 0x00);    
		OpMap.put("MOVZ", 0x00);    
		OpMap.put("MOVN", 0x00);	
		OpMap.put("SYSCALL", 0x00); 
		OpMap.put("NOP", 0x00); 
		//OpMap.put("BZ",0x01);      
		OpMap.put("BLTZ",0x01); 
		OpMap.put("BGEZ",0x01); 
		OpMap.put("BLTZAL",0x01); 
		OpMap.put("BGEZAL",0x01); 
		
		OpMap.put("BEQ",0x04);     
		OpMap.put("BNE",0x05);     
		OpMap.put("BLEZ",0x06);   
		OpMap.put("BGTZ",0x07);   
		OpMap.put("ADDI",0x08);    
		OpMap.put("ADDIU",0x09);   
		OpMap.put("SLTI",0x0a);    
		OpMap.put("SLTIU",0x0b);   
		OpMap.put("ANDI",0x0c);    
		OpMap.put("ORI",0x0d);     
		OpMap.put("XORI",0x0e);    
		OpMap.put("LUI",0x0f); 
		
		//////////////////COP0//////////////////
		OpMap.put("MFC0",0x10); 
		OpMap.put("MTC0",0x10); 
		OpMap.put("ERET",0x10); 
		
		OpMap.put("LB",0x20);      
		OpMap.put("LH",0x21);      
		OpMap.put("LW",0x23);      
		OpMap.put("LBU",0x24);     
		OpMap.put("LHU",0x25);     
		OpMap.put("SB",0x28);      
		OpMap.put("SH",0x29);      
		OpMap.put("SW",0x2b); 
		         
		OpMap.put("J",0x02);       
		OpMap.put("JAL",0x03);     
		OpMap.put("CACHE",0x2f);   
		
		/////////////////////////////////////////////////
		FnMap.put("SLL", 0x00);     
		FnMap.put("NOP", 0x00);  
		FnMap.put("SRL", 0x02);     
		FnMap.put("SRA", 0x03);     
		FnMap.put("SLLV", 0x04);    
		FnMap.put("SRLV", 0x06);    
		FnMap.put("SRAV", 0x07);    
		FnMap.put("JR", 0x08);      
		FnMap.put("JALR", 0x09);    
		FnMap.put("ADD", 0x20);     
		FnMap.put("ADDU", 0x21);    
		FnMap.put("SUB", 0x22);     
		FnMap.put("SUBU", 0x23);    
		FnMap.put("AND", 0x24);     
		FnMap.put("OR", 0x25);      
		FnMap.put("XOR", 0x26);     
		FnMap.put("NOR", 0x27);     
		FnMap.put("SLT", 0x2a);     
		FnMap.put("SLTU", 0x2b);    
		FnMap.put("MOVZ", 0x0A);    
		FnMap.put("MOVN", 0x0B);	
		FnMap.put("SYSCALL", 0x0C); 
		
		/////////////////////////////////////////
		BZMap.put("BLTZ",0x00); 
		BZMap.put("BGEZ",0x01); 
		BZMap.put("BLTZAL",0x10); 
		BZMap.put("BGEZAL",0x11); 
		
		/////////////////////////////////////////
		
		CP0Map.put("MFC0",0x00); 
		CP0Map.put("MTC0",0x04); 
		CP0Map.put("ERET",0x10); 
		
	}
}
