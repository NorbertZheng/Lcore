package com.njy.project.simulator.assembler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import com.njy.project.simulator.cpu.FunctionCode;
import com.njy.project.simulator.cpu.OpCode;
import com.njy.project.simulator.util.Util;

public class InstAnalyzer
{
	private String instString;	
	private int instruction = 0x00000000;
	private String label;
	private boolean isLabelled = false;
	private int lineNo;
	private int progNo;
	private String errorMessageString = null;
	
	public InstAnalyzer(String string, int ln, int pn)
	{
		this.instString = string.toUpperCase().trim();
		this.lineNo = ln;
		this.progNo = pn;
	}
	
	private Vector<String> vector = new Vector<>();
	
	public boolean instructionSplit()
	{
		if(instString.equals("")) return false;// empty
		
/////////////////////////////split string//////////////////
		
		String[] splitStrings = instString.split("\\s|,");
		for(String s : splitStrings)
		{
			if(!s.equals(""))
			{
				vector.add(s);
				//System.out.println(s);
			}
		}
		
		return true;
	}
	
	public boolean LabelGet() throws AssembleException
	{
		
		////////////////////check label///////////////////////////
		String tString = vector.get(0);
		
		if(tString.endsWith(":"))
		{
			isLabelled = true;
			label = tString.substring(0, tString.length() - 1);
			if(vector.size() == 1) return false;
			if(label.equals("")) 
			{
				throw new AssembleException("empty label", lineNo) ;
			}
			
			if(AnalyzerHelper.LabelMap.get(label) != null)
			{
				throw new AssembleException("duplicated label", lineNo) ;
			}
			
			AnalyzerHelper.LabelMap.put(label, progNo);
			vector.remove(0);
			return true;
		}
		
		return true;
	}
	
	public boolean analyze() throws AssembleException
	{
		
		////////////////analyze start////////////////////////////////
		Integer opcpde, fncode, rd, rs, rt;
		int shamt;
		
		if((opcpde = AnalyzerHelper.OpMap.get(vector.get(0))) == null)
		{
			throw new AssembleException("unkown instruction", lineNo) ;
		}
		fncode = AnalyzerHelper.FnMap.get(vector.get(0));
		instruction |= opcpde << 26;
		
		switch (opcpde)
		{
		case OpCode.RTYPE:
			switch (fncode) 
			{
			case FunctionCode.ADD:
			case FunctionCode.ADDU:
			case FunctionCode.SUB:
			case FunctionCode.SUBU:
			case FunctionCode.AND:
			case FunctionCode.OR:
			case FunctionCode.XOR:
			case FunctionCode.NOR:
			case FunctionCode.SLT:
			case FunctionCode.SLTU:
			case FunctionCode.SLLV:
			case FunctionCode.SRLV:
			case FunctionCode.SRAV:
			case FunctionCode.MOVZ:
			case FunctionCode.MOVN:
			{
				if(vector.size() != 4)
				{
					throw new AssembleException("incorrect instruction format", lineNo) ;
				}
				
				if((rd = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
				{
					throw new AssembleException("unkown register", lineNo) ;
				}
				
				if((rs = AnalyzerHelper.RegMap.get(vector.get(2))) == null)
				{
					throw new AssembleException("unkown register", lineNo) ;
				}
				
				if((rt = AnalyzerHelper.RegMap.get(vector.get(3))) == null)
				{
					throw new AssembleException("unkown register", lineNo) ;
				}
				
				instruction |= fncode |(rd << 11) | (rt << 16) | (rs << 21);
				if(vector.get(0) == "ROTRV") instruction |= 1<<6;
				
				break;
			}
			case FunctionCode.SLL:
			case FunctionCode.SRL:
			case FunctionCode.SRA:
			{
				if(vector.size() != 4)
				{
					throw new AssembleException("incorrect instruction format", lineNo) ;
				}
				
				if((rd = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
				{
					throw new AssembleException("unkown register", lineNo) ;
				}
				
				
				if((rt = AnalyzerHelper.RegMap.get(vector.get(2))) == null)
				{
					throw new AssembleException("unkown register", lineNo) ;
				}
				
				try
				{
					shamt = Util.parseStringToInt(vector.get(3));
				}
				catch (NumberFormatException e)
				{
					// TODO: handle exception
					throw new AssembleException("incorrect number format", lineNo) ;
				}
				
				instruction |= fncode |(rd << 11) | (rt << 16) | (shamt << 6);
				if(vector.get(0) == "ROTR") instruction |= 1<<21;
				break;
			}
			
			case FunctionCode.JR:
				// jr rs
			{
				if(vector.size() != 2)
				{
					throw new AssembleException("incorrect instruction format", lineNo) ;
				}
				
				if((rs = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
				{
					throw new AssembleException("unkown register", lineNo) ;
				}
				
				instruction |= fncode |(rs << 21);
				
				break;
			}
			case FunctionCode.JALR:
				// jalr rd, rs
			{

				if(vector.size() != 3 && vector.size() != 2)
				{
					throw new AssembleException("incorrect instruction format", lineNo) ;
				}
				
				
				if(vector.size() == 3)
				{
					if((rd = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
					{
						throw new AssembleException("unkown register", lineNo) ;
					}
					
					if((rs = AnalyzerHelper.RegMap.get(vector.get(2))) == null)
					{
						throw new AssembleException("unkown register", lineNo) ;
					}
				}
				else 
				{
					rd = 31;
					if((rs = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
					{
						throw new AssembleException("unkown register", lineNo) ;
					}
				}	
				
				instruction |= fncode | (rd << 11) | (rs << 21);
				
				break;
			}
			
			case FunctionCode.SYSCALL:
			{
				if(vector.size() != 1)
				{
					throw new AssembleException("incorrect instruction format", lineNo) ;
				}
				try
				{
					int code = Util.parseStringToInt(vector.get(1));
					instruction |= fncode | (code << 6);
				}
				catch (NumberFormatException e)
				{
					// TODO: handle exception
					throw new AssembleException("incorrect number format", lineNo) ;
				}
				
				
				break;
			}
			default:
			{
				throw new AssembleException("unknown instruction", lineNo) ;
			}
			}
			break;
		// j type
		case OpCode.J:
		case OpCode.JAL:
		{
			if(vector.size() != 1)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			Integer addr;
			if((addr = AnalyzerHelper.LabelMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unknown label", lineNo) ;
			}
			instruction |= addr & 0x03ffffff;

			break;
		}
		// i type
		case OpCode.ADDI:
		case OpCode.ADDIU:
		case OpCode.ANDI:
		case OpCode.ORI:
		case OpCode.XORI:
		case OpCode.SLTI:
		case OpCode.SLTIU:
		{
			if(vector.size() != 4)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			if((rt = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			
			if((rs = AnalyzerHelper.RegMap.get(vector.get(2))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			int imme;
			try
			{
				imme = Util.parseStringToInt(vector.get(3));
			}
			catch (NumberFormatException e)
			{
				// TODO: handle exception
				throw new AssembleException("incorrect number format", lineNo) ;
			}
			
			instruction |= (imme & 0x0000ffff) |(rt << 16) | (rs << 21);
			break;
		}
		case OpCode.LUI:
		{
			if(vector.size() != 3)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			if((rt = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			int imme;
			try
			{
				imme = Util.parseStringToInt(vector.get(3));
			}
			catch (NumberFormatException e)
			{
				// TODO: handle exception
				throw new AssembleException("incorrect number format", lineNo) ;
			}
			
			instruction |= (imme & 0x0000ffff) | (rt << 16);
			break;
		}
		case OpCode.SW:
		case OpCode.SB:
		case OpCode.SH:
		case OpCode.LW:
		case OpCode.LB:
		case OpCode.LBU:
		case OpCode.LH:
		case OpCode.LHU:
		{
			if(vector.size() != 3)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			if((rt = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			String[] s = parseLSI(vector.get(2));
			if(s == null)
			{
				// TODO: handle exception
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			int imme;
			try
			{
				imme = Util.parseStringToInt(s[0]);
			}
			catch (NumberFormatException e)
			{
				// TODO: handle exception
				throw new AssembleException("incorrect number format", lineNo) ;
			}
			
			if((rs = AnalyzerHelper.RegMap.get(s[1])) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			instruction |= (imme & 0x0000ffff) |(rt << 16) | (rs << 21);
			
			break;
		}
		case OpCode.BEQ:
		case OpCode.BNE:
		{
			if(vector.size() != 4)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			if((rs = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			if((rt = AnalyzerHelper.RegMap.get(vector.get(2))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}

			Integer offset;
			if((offset = AnalyzerHelper.LabelMap.get(vector.get(3))) == null)
			{
				throw new AssembleException("unknown label", lineNo) ;
			}
			
			offset -= progNo + 1;
			
			instruction |= (offset & 0x0000ffff) |(rt << 16) | (rs << 21);
			break;
		}
		case OpCode.BLEZ:
		case OpCode.BGTZ:
		{
			if(vector.size() != 4)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			if((rs = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			Integer offset;
			if((offset = AnalyzerHelper.LabelMap.get(vector.get(2))) == null)
			{
				throw new AssembleException("unknown label", lineNo) ;
			}

			offset -= progNo + 1;
			
			instruction |= (offset & 0x0000ffff) | (rs << 21);
			break;
		}
		case OpCode.BZ:
		{
			if(vector.size() != 3)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			if((rt = AnalyzerHelper.BZMap.get(vector.get(0))) == null)
			{
				throw new AssembleException("unknown instruction", lineNo) ;
			}
			
			if((rs = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			Integer offset;
			if((offset = AnalyzerHelper.LabelMap.get(vector.get(2))) == null)
			{
				throw new AssembleException("unknown label", lineNo) ;
			}
			
			offset -= progNo + 1;
			
			instruction |= (offset & 0x0000ffff) |(rt << 16) | (rs << 21);
			
			break;
		}
		case OpCode.COP0: // Coprocessor 0
		{
			if(vector.size() != 3  && vector.size() != 2)
			{
				throw new AssembleException("incorrect instruction format", lineNo) ;
			}
			
			
			if((rs = AnalyzerHelper.CP0Map.get(vector.get(0))) == null)
			{
				throw new AssembleException("unknown instruction", lineNo) ;
			}
			
			if((rt = AnalyzerHelper.RegMap.get(vector.get(1))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			if((rd = AnalyzerHelper.RegMap.get(vector.get(2))) == null)
			{
				throw new AssembleException("unkown register", lineNo) ;
			}
			
			instruction |= (rd << 11) | (rt << 16) | (rs << 21);
			
			break;
			
		}
		case OpCode.CACHE:
		{
			
		}
		default:
		{
			throw new AssembleException("unkown instruction ", lineNo) ;
		}
		}
		
		
		return true;
	}
	
	private String[] parseLSI(String s)
	{
		System.out.println(s);
		String[] tmp = s.split("\\(");
		if(tmp.length != 2) return null;
		if(!tmp[1].endsWith(")")) return null;
		tmp[1] = tmp[1].substring(0, tmp[1].length()-1);
		
		return tmp;
	}
	
	public static void main(String []args) throws IOException
	{
		String string;
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		string = bReader.readLine();
		
		AnalyzerHelper.LabelMap.put("LABEL1", 10);
		
		InstAnalyzer iAnalyzer = new InstAnalyzer(string, 1, 0);
		iAnalyzer.instructionSplit();
		try
		{
			iAnalyzer.LabelGet();
			iAnalyzer.analyze();
			iAnalyzer.Printinstruction();
		}
		catch (AssembleException e)
		{
			// TODO: handle exception
			System.out.println("Error at line " + e.getLineNo() + ":" + e.getMessage());
		}

	}
	
	public void Printinstruction()
	{
		System.out.printf("%08x\n",instruction);
	}

	public int getInstruction()
	{
		return instruction;
	}

	public boolean isLabelled()
	{
		return isLabelled;
	}

	public String getErrorMessageString()
	{
		return errorMessageString;
	}

	public String getLabel()
	{
		return label;
	}
	
}
