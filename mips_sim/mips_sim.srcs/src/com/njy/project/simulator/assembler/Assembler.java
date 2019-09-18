package com.njy.project.simulator.assembler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Assembler
{
	private Vector<InstAnalyzer> iVector = new Vector<>();
	private int lineCount;
	private int  progCount;
	private BufferedReader bufferedReader;
	private boolean finished = false;
	
	public Assembler(BufferedReader bufferedReader) throws IOException
	{
		this.bufferedReader = bufferedReader;
	}
	
	private boolean prepare() throws IOException, AssembleException
	{
		lineCount = 0;
		progCount = 0;
		String string;
		int cmd = 0;
		while((string = bufferedReader.readLine())!=null)	
		{
			////delete the commat
			string = string.trim();
			int i;
			if((i = string.indexOf(';')) != -1)
			{
				string = string.substring(0, i);
			}
			///////////////////////////////////////////
			lineCount ++;
			if(string.startsWith(".data"))
				cmd = 1;
			else if(string.startsWith(".text"))
				cmd = 0;
			else if(string.startsWith(".") && cmd == 1){
				parseData(string);
				continue;
			}else{
				
				
			}
				
			InstAnalyzer instAnalyzer = new InstAnalyzer(string,  lineCount, progCount);
			////////eliminate empty line//////////////// 
			if(!instAnalyzer.instructionSplit())
				continue;
			
			/////////////check label////////////////
			if(instAnalyzer.LabelGet()) //normal label with instruction or no label
			{
				iVector.add(instAnalyzer);
				progCount ++;
			}
			else//label as a whole line
				AnalyzerHelper.LabelMap.put(instAnalyzer.getLabel(),  progCount);
			
		}
		
		return true;
	}
	
	public int parseData(String s) throws AssembleException{
		String[] ss = s.split("\\s|,");
		switch (ss[0])
		{
		case ".data":
			return 1;
		case ".byte":
		case ".half":
		case ".word":
		case ".asciiz":
		case ".space":
			break;
		case ".text":
			return 0;
		default:
			throw new AssembleException("unrecognized command", lineCount);
		}
		return 0;
	}
	
	public void Assemble()
	{
		try
		{
			prepare();
			
			for(InstAnalyzer ia : iVector)
			{
				ia.analyze();
			}
			
			finished = true;
		}
		catch (IOException e)
		{
			// TODO: handle exception
			System.out.println("IO Error:" + e.getMessage());
		}
		catch (AssembleException e) {
			// TODO: handle exception
			System.out.println("Assemble error at line " + e.getLineNo() + ":" + e.getMessage());
			finished = false;
		}
	}
	
	public void WriteToFile(String filename) throws IOException
	{
		File file = new File(filename);
		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
		
		for(InstAnalyzer ia : iVector)
		{
			dataOutputStream.writeInt(ia.getInstruction());
		}
		
		dataOutputStream.flush();
		dataOutputStream.close();
		
	}
	
	public void Print()
	{
		for(InstAnalyzer ia : iVector)
		{
			ia.Printinstruction();
		}
	}
	
	
	public static void main(String args[]) throws IOException
	{
		File file = new File("./test.asm");
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		Assembler assembler = new Assembler(bufferedReader);
		assembler.Assemble();
		
		if(assembler.isFinished())
		{
			assembler.Print();
			assembler.WriteToFile("test.bin");
		}
		bufferedReader.close();
		
	}

	public boolean isFinished()
	{
		return finished;
	}
}
