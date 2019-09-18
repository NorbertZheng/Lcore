package com.njy.project.simulator.file;

import java.io.FileWriter;
import java.io.IOException;

import com.njy.project.simulator.file.bin.BinFileReader;
import com.njy.project.simulator.file.coe.COEFileReader;
import com.njy.project.simulator.file.coe.COEReadException;


public class FileFactory
{
	private FileFactory(){}
	
	private static FileFactory instance = null;
	
	public static FileFactory getInstance(){
		if(instance == null){
			instance = new FileFactory();
		}
		return instance;
	}
	
	public FileInterface getFile(String path) throws IOException, COEReadException
	{
		if(path.endsWith(".coe"))
			return new COEFileReader(path, 8);
		else 
			return new BinFileReader(path);
	}
	
	public void saveFile(FileInterface file) throws IOException, COEReadException
	{
		saveFileAs(file, file.getPath());
	}
	
	public void saveFileAs(FileInterface file, String path) throws IOException, COEReadException
	{
		FileWriter fileWriter = new FileWriter(path);
		int b = 0;
		while((b = file.readByte()) != -1){
			fileWriter.write(b);
		}
		fileWriter.flush();
		fileWriter.close();
	}
	
	public void saveDataAs(int[] data, String path) throws IOException, COEReadException
	{
		FileWriter fileWriter = new FileWriter(path);
		for(int i = 0; i< data.length; i++){
			fileWriter.write(data[i] & 0xff);
			fileWriter.write((data[i] >> 8) & 0xff);
			fileWriter.write((data[i] >> 16) & 0xff);
			fileWriter.write((data[i] >> 24) & 0xff);
		}
		fileWriter.flush();
		fileWriter.close();
	}
}
