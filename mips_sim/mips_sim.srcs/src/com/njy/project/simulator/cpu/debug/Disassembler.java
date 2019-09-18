package com.njy.project.simulator.cpu.debug;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.njy.project.simulator.cpu.FunctionCode;
import com.njy.project.simulator.cpu.OpCode;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

public class Disassembler {
	public static String[] RegMap = new String[32];
	public static String[] OpMap = new String[64];
	public static String[] FnMap = new String[64];
	public static String[] CP0RegMap = new String[32];
	
	public static void main(String args[]) throws FileNotFoundException {
		DataInputStream dataInputStream = new DataInputStream(new FileInputStream("./test.bin"));
		int i = 0;
		try {
			while(true)
			{	
				i = dataInputStream.readInt();
				System.out.println(singleInstruction(i, 0));
			}
		
		} catch (IOException e) {
			// TODO �Զ���ɵ� catch ��
			System.out.println("EOF");
		}
	}

	public static String singleInstruction(int instruction, int pc)
	{
		int op = (instruction >> 26) & 0x3f;
		int rs = (instruction >> 21) & 0x1f;
		int rt = (instruction >> 16) & 0x1f;
		int rd = (instruction >> 11) & 0x1f;
		int code = (instruction >> 6) & 0x3f; 
		int shamt = (instruction >> 6) & 0x1f;
		int func = (instruction >> 0) & 0x3f;
		int unsignedImme = instruction & 0xffff;
		int signedImme = (unsignedImme << 16) >> 16;
		int addr = ((instruction & 0x3ffffff) << 2) | (pc & 0xf0000000);
		
		switch (op) 
		{
		case OpCode.RTYPE:
			switch (func) 
			{
			case FunctionCode.ADD:
			case FunctionCode.ADDU:
			case FunctionCode.SUB:
			case FunctionCode.SUBU:
			case FunctionCode.AND:
			case FunctionCode.XOR:
			case FunctionCode.NOR:
			case FunctionCode.SLT:
			case FunctionCode.SLTU:
			case FunctionCode.MOVZ:
			case FunctionCode.MOVN:
			case FunctionCode.SLLV:
			case FunctionCode.SRAV:
			{
				return FnMap[func] + " " + RegMap[rd] + ", " + RegMap[rs] + ", " + RegMap[rt];
			}
			case FunctionCode.OR:
			{
				if(rt == 0)
					return "MOVE " + RegMap[rd] + ", "  + RegMap[rs];
				return "OR " + RegMap[rd] + ", " + RegMap[rs] + ", " + RegMap[rt];
			}
			case FunctionCode.SLL:
			{
				if(instruction == 0) return "NOP";
				return "SLL " + RegMap[rd] + ", " + RegMap[rt] + ", " + shamt; 
			}
			case FunctionCode.SRL:
			{
				if(rs == 0)
					return "SRL " + RegMap[rd] + ", " + RegMap[rt] + ", " + shamt; 
				else 
				{
					return "ROTR " + RegMap[rd] + ", " + RegMap[rt] + ", " + shamt;  
				}
			}
			case FunctionCode.SRA:
			{
				return "SRA " + RegMap[rd] + ", " + RegMap[rt] + ", " + shamt;
			}
			
			case FunctionCode.SRLV:
			{
				if(shamt == 0)
					return "SRLV" + " " + RegMap[rd] + ", " + RegMap[rs] + ", " + RegMap[rt];
				else 
				{
					return "ROTRV" + " " + RegMap[rd] + ", " + RegMap[rs] + ", " + RegMap[rt];
				}
			}
			case FunctionCode.JR:
				// jr rs
			{
				return "JR " + RegMap[rs]; 
			}
			case FunctionCode.JALR:
				// jalr rd, rs
			{
				return "JALR " + RegMap[rd] + ", " + RegMap[rs]; 
			}
			case FunctionCode.SYSCALL:
			{
				return "SYSCALL " + code;
			}
			default:
				break;
			}
			break;
		// j type
		case OpCode.J:
		case OpCode.JAL:
		{
			return OpMap[op] + " " + String.format("%08X", addr);
		}
		// i type
			// addi rt, rs, signedImme
		case OpCode.ANDI:
		case OpCode.ORI:
		case OpCode.XORI:
		case OpCode.SLTI:
		case OpCode.SLTIU:
		{
			return OpMap[op] + " " + RegMap[rt] + ", " + RegMap[rs] + ", " + signedImme;
		}
		case OpCode.LUI:{
			return OpMap[op] + " " + RegMap[rt] + ", " + signedImme;
		}
		case OpCode.ADDI:
		case OpCode.ADDIU:
		{
			if(rs == 0)
				return "LI " + RegMap[rt] + ", " + signedImme;
			return OpMap[op] + " " + RegMap[rt] + ", " + RegMap[rs] + ", " + signedImme;
		}
		case OpCode.SW:
			// sw rt, signedImme(rs)
		case OpCode.SB:
		case OpCode.SH:
		case OpCode.LW:
		case OpCode.LB:
		case OpCode.LBU:
		case OpCode.LH:
		case OpCode.LHU:
		{
			return OpMap[op] + " " + RegMap[rt] + ", " + signedImme + "(" + RegMap[rs] + ") " ;
		}
		case OpCode.BNE:
			// bne rs, rt, label
		
		{
			return OpMap[op] + " " + RegMap[rs] + ", " + RegMap[rt] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
		}
		case OpCode.BLEZ:
			// blez rs, offset
		case OpCode.BGTZ:
			// bgtz rs, offset
		{
			return OpMap[op] + " " + RegMap[rs] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
		}
		case OpCode.BEQ:
			// beq rs, rt, label
		{
			if(rs == 0 && rt == 0)
				return String.format("B %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));;
			return OpMap[op] + " " + RegMap[rs] + ", " + RegMap[rt] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
		}
		case OpCode.BZ:
			switch (rt) {
			case 0x0://bltz
			{
				return "BLTZ " + RegMap[rs] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
			}
			case 0x1: // bgez
			{
				return "BGEZ " + RegMap[rs] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
			}
			case 0x10://bltzal
			{
				return "BLTZAL " + RegMap[rs] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
			}
			case 0x11://bgezal
			{
				return "BGEZAL " + RegMap[rs] + String.format(", %d(0X%X)", signedImme, pc + 4 + (signedImme << 2));
			}
			default:
				break;
			}
			break;
		case OpCode.COP0: // Coprocessor 0
		{
			if(rs == 0)
			{
				// mfc0 rt, rd
				return "MFC0 " + RegMap[rt] + ", " + RegMap[rd];
			}
			else if(rs == 0x04)
			{
				// mtc0 rt, rd
				return "MTC0 " + RegMap[rt] + ", " + RegMap[rd];
			}
			else if(func == 0x18 && (rs >>> 4) == 1)//ERET
			{
				return "ERET";
			}
			break;
		}
		case OpCode.CACHE:
		{
			return "CACHE";
		}
		default:
			break;
		}
		
		return "";
	}
	
	static {                       
		RegMap[0]  =  "$ZERO";
		RegMap[1]  =    "$AT"; 
		RegMap[2]  =    "$V0"; 
		RegMap[3]  =    "$V1"; 
		RegMap[4]  =    "$A0"; 
		RegMap[5]  =    "$A1"; 
		RegMap[6]  =    "$A2"; 
		RegMap[7]  =    "$A3"; 
		RegMap[8]  =    "$T0"; 
		RegMap[9]  =    "$T1"; 
		RegMap[10] =    "$T2"; 
		RegMap[11] =    "$T3"; 
		RegMap[12] =    "$T4"; 
		RegMap[13] =    "$T5"; 
		RegMap[14] =    "$T6"; 
		RegMap[15] =    "$T7"; 
		RegMap[16] =    "$S0"; 
		RegMap[17] =    "$S1"; 
		RegMap[18] =    "$S2"; 
		RegMap[19] =    "$S3"; 
		RegMap[20] =    "$S4"; 
		RegMap[21] =    "$S5"; 
		RegMap[22] =    "$S6"; 
		RegMap[23] =    "$S7"; 
		RegMap[24] =    "$T8"; 
		RegMap[25] =    "$T9"; 
		RegMap[26] =    "$K0"; 
		RegMap[27] =    "$K1"; 
		RegMap[28] =    "$GP"; 
		RegMap[29] =    "$SP"; 
		RegMap[30] =    "$FP"; 
		RegMap[31] =    "$RA"; 
		
		
		OpMap[0x04] = "BEQ"  ;     
		OpMap[0x05] = "BNE"  ;     
		OpMap[0x06] = "BLEZ";   
		OpMap[0x07] = "BGTZ";   
		OpMap[0x08] = "ADDI" ;    
		OpMap[0x09] = "ADDIU";   
		OpMap[0x0a] = "SLTI" ;    
		OpMap[0x0b] = "SLTIU";   
		OpMap[0x0c] = "ANDI" ;    
		OpMap[0x0d] = "ORI"  ;     
		OpMap[0x0e] = "XORI" ;    
		OpMap[0x0f] = "LUI"  ; 
		
		OpMap[0x20] = "LB"   ;      
		OpMap[0x21] = "LH"   ;      
		OpMap[0x23] = "LW"   ;      
		OpMap[0x24] = "LBU"  ;     
		OpMap[0x25] = "LHU"  ;     
		OpMap[0x28] = "SB"   ;      
		OpMap[0x29] = "SH"   ;      
		OpMap[0x2b] = "SW"   ;          
		OpMap[0x02] = "J"    ;       
		OpMap[0x03] = "JAL"  ;     
		OpMap[0x2f] = "CACHE"; 
		
		FnMap[0x00] = "SLL"     ;     
		FnMap[0x02] = "SRL"     ;     
		FnMap[0x03] = "SRA"     ;     
		FnMap[0x04] = "SLLV"    ;    
		FnMap[0x06] = "SRLV"    ;    
		FnMap[0x07] = "SRAV"    ;    
		FnMap[0x08] = "JR"      ;      
		FnMap[0x09] = "JALR"    ;    
		FnMap[0x20] = "ADD"     ;     
		FnMap[0x21] = "ADDU"    ;    
		FnMap[0x22] = "SUB"     ;     
		FnMap[0x23] = "SUBU"    ;    
		FnMap[0x24] = "AND"     ;     
		FnMap[0x25] = "OR"      ;      
		FnMap[0x26] = "XOR"     ;     
		FnMap[0x27] = "NOR"     ;     
		FnMap[0x2a] = "SLT"     ;     
		FnMap[0x2b] = "SLTU"    ;    
		FnMap[0x0A] = "MOVZ"    ;    
		FnMap[0x0B] = "MOVN"    ;	
		FnMap[0x0C] = "SYSCALL" ; 
		
		CP0RegMap[0] = "$SR"   ;
		CP0RegMap[1] = "$EAR"  ;
		CP0RegMap[2] = "$EPCR" ;
		CP0RegMap[3] = "$EHBR"  ;
		CP0RegMap[4] = "$IER"  ;
		CP0RegMap[5] = "$ICR"  ;
		CP0RegMap[6] = "$PDBR" ;
		CP0RegMap[7] = "$TIR"  ;
		CP0RegMap[8] = "$WDR"  ;
	}              
	
}
	
