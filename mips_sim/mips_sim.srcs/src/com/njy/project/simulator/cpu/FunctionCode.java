package com.njy.project.simulator.cpu;


public class FunctionCode {
	public static final int SLL = 0x00; 
	public static final int SRL = 0x02; 
	public static final int SRA = 0x03; 
	public static final int ROTR = 0x02;
	public static final int SLLV = 0x04; 
	public static final int SRLV = 0x06; 
	public static final int SRAV = 0x07; 
	public static final int ROTRV = 0x06;
	public static final int JR = 0x08; 
	public static final int JALR = 0x09; 
	public static final int ADD = 0x20; 
	public static final int ADDU = 0x21; 
	public static final int SUB = 0x22;
	public static final int SUBU = 0x23; 
	public static final int AND = 0x24; 
	public static final int OR = 0x25; 
	public static final int XOR = 0x26; 
	public static final int NOR = 0x27; 
	public static final int SLT = 0x2a; 
	public static final int SLTU = 0x2b;
	public static final int MOVZ = 0x0A;
	public static final int MOVN = 0x0B;	
	public static final int SYSCALL = 0x0C;
	public static final int BREAK = 0x3f;
}
