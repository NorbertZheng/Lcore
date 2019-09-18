package com.njy.project.simulator.assembler;

public class AssembleException extends Exception
{
	private int lineNo;
	
	public AssembleException(String msg, int ln)
	{
		super(msg);
		this.lineNo = ln;
	}

	public int getLineNo()
	{
		return lineNo;
	}
}
