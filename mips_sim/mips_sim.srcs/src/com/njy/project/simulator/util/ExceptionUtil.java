package com.njy.project.simulator.util;

public class ExceptionUtil {
	public static int EXCEPTION_NONE = 0;
	public static int EXCEPTION_RESET = 1;
	public static int EXCEPTION_BUSERR = 2;
	public static int EXCEPTION_DPF = 3;
	public static int EXCEPTION_IPF = 4;
	public static int EXCEPTION_TICK = 5;
	public static int EXCEPTION_ALIGN = 6;
	public static int EXCEPTION_ILLEGAL = 7;
	public static int EXCEPTION_INT = 8;
	public static int EXCEPTION_DTLBMISS = 9;
	public static int EXCEPTION_ITLBMISS = 10;
	public static int EXCEPTION_RANGE = 11;
	public static int EXCEPTION_SYSCALL = 12;
	public static int EXCEPTION_FLOAT = 13;
	public static int EXCEPTION_TRAP = 14;
	public static int EXCEPTION_UNUSED = 15;

	public static int EXC_BIT_SHIFT = 2; // Exception shift in C0 status
											// register
	public static int EXC_BITS_CLEAR = 0xFFFFFF83; // The last 8 bits are 1
													// 00000 11

	public static int IR_BIT_SHIFT = 31; // IR bit in C0 status register
	public static int IR_BIT_CLEAR = 0xEFFFFFFF; // Used to clear IR bit
	public static int IR_BIT_SET = 0x80000000; // Used to set IR bit

	public static int EXC_ENTR = 0x00000038; // Exception handler entrance point
}
