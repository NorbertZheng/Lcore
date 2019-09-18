package com.njy.project.simulator.file.coe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.file.FileInterface;
import com.njy.project.simulator.util.Util;

public class COEFileReader implements FileInterface
{
	private int radix = 16;
	private BufferedReader bufferedReader = null;
	private String path;
	private byte[] data;
	private int widthMode= 1;//8,16,32
	private int count = 0;
	private StringBuilder stringBuilder = new StringBuilder();
	public COEFileReader(String path, int width) throws IOException,COEReadException
	{
		switch (width)
		{
		case 16:
			widthMode = AccessMode.HALFWORD;
			break;
		case 32:
			widthMode = AccessMode.WORD;
			break;
		default:
			widthMode = AccessMode.BYTE;
			break;
		}
		this.path = path;
		init();
		if(bufferedReader!=null) 
			bufferedReader.close();
	}
	
	public COEFileReader(byte[] data, int radix)
	{
		this.radix = radix;
		this.data = new byte[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}
	
	public COEFileReader(int[] data, int radix)
	{
		this.radix = radix;
		int l = data.length << 2;
		this.data = new byte[l];
		for(int i = 0; i < data.length; i++)
		{
			l = i << 2;
			this.data[l  ] = (byte)data[i ];
			this.data[l+1] = (byte)(data[i ] >> 8) ;
			this.data[l+2] = (byte)(data[i ] >>16) ;
			this.data[l+3] = (byte)(data[i ] >>24) ;
		}
	}
	
	public void writeCoe(String path, int radix, int width) throws IOException
	{
		FileWriter fileWriter = new FileWriter(path);
		fileWriter.write("memory_initialization_radix=" + radix + ";\n");
		fileWriter.write("memory_initialization_vector=\n");
		int cnt = AccessMode.BYTE;
		switch (width)
		{
		case 16:
			cnt = AccessMode.HALFWORD;
			break;
		case 32:
			cnt = AccessMode.WORD;
			break;
		default:
			break;
		}
		
		for(int i = 0; i < cnt&& i < data.length; i++)
			fileWriter.write(Util.numFormat(data[i], radix));
		for(int i = cnt; i < data.length;)
		{
			fileWriter.write(",");
			fileWriter.write("\n");
			int j = i+cnt;
			for(; i < j && i < data.length; i++)
				fileWriter.write(Util.numFormat(data[i], radix));
		}
		fileWriter.write(";");
		fileWriter.flush();
		fileWriter.close();
	}
	
	private void init() throws IOException,COEReadException
	{
		//fileReader = new FileReader(filepath);
		//////////get radix///////////////////////////
		bufferedReader = new BufferedReader(new FileReader(path));
		String line = bufferedReader.readLine();
		int cmt = 0;
		while(line != null)
		{
			if((cmt = line.indexOf(';'))!= -1)
			{
				line = line.substring(0, cmt);
			}
			stringBuilder.append(line);
			stringBuilder.append("\n");
			line = bufferedReader.readLine();
		}
		
		System.out.println(stringBuilder.toString());
		Pattern pattern = Pattern.compile("\\s*memory_initialization_radix\\s*=\\s*([0-9]+)\\s*memory_initialization_vector\\s*=(.*)", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(stringBuilder);
		if(!matcher.matches())
			throw new COEReadException("Format Error!");
		
		switch (matcher.group(1))
		{
		case "2":
			radix = 2;
			break;
		case "16":
			radix = 16;
			break;
		default:
			throw new COEReadException("Format Error!");
		}
		String[] dataString = matcher.group(2).split(",");
		if(dataString == null) 
			throw new COEReadException("Format Error!");
		data = new byte[dataString.length * this.widthMode];
		int tmp;
		try
		{
			for(int i = 0; i < data.length;)
			{
				tmp=Integer.parseInt(dataString[i].trim(), radix);
				for(int j = i + widthMode;i < j && i < dataString.length; i++)
				{
					data[i] = (byte)tmp;
					tmp >>>= 8;
				}
			}
			
		}
		catch (NumberFormatException e)
		{
			// TODO: handle exception
			throw new COEReadException("Format Error!");
		}
		
	}
	
	@Override
	public int readByte()
	{
		if(count >= data.length)
		{
			return -1;
		}
		return data[count++];
		
	}
	
	@Override
	public void close() throws IOException
	{
		
	}
	

	@Override
	public String getPath()
	{
		// TODO 自动生成的方法存根
		return path;
	}

	
	public static void main(String args[])
	{
		try
		{
			COEFileReader coeFileReader = new COEFileReader("./vgaChar.coe",8);
			int data = coeFileReader.readByte();
			while(data != -1)
			{
				System.out.println((byte)data);
				data = coeFileReader.readByte();
			}
			
			coeFileReader.writeCoe("./test.coe", 16, 32);
		}
		catch (IOException | COEReadException e)
		{
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
		}
	}
	
}
