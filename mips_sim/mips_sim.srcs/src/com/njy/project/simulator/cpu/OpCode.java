package com.njy.project.simulator.cpu;

public class OpCode {
	public static final int RTYPE = 0x00;
	// I type
	public static final int BZ = 0x01; 
	public static final int BEQ = 0x04; 
	public static final int BNE = 0x05; 
	public static final int BLEZ = 0x06; 
	public static final int BGTZ = 0x07; 
	public static final int ADDI = 0x08;
	public static final int ADDIU = 0x09; 
	public static final int SLTI = 0x0a; 
	public static final int SLTIU = 0x0b; 
	public static final int ANDI = 0x0c; 
	public static final int ORI = 0x0d; 
	public static final int XORI = 0x0e; 
	public static final int LUI = 0x0f; 
	public static final int COP0 = 0x10; 
	public static final int LB = 0x20; 
	public static final int LH = 0x21; 
	public static final int LW = 0x23; 
	public static final int LBU = 0x24; 
	public static final int LHU = 0x25; 
	public static final int SB = 0x28; 
	public static final int SH = 0x29; 
	public static final int SW = 0x2b;
	// J type
	public static final int J = 0x02; 
	public static final int JAL = 0x03;
	public static final int CACHE = 0x2f;
}
