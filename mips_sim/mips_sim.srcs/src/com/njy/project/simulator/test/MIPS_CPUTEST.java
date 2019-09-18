package com.njy.project.simulator.test;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cpu.MIPSCPU;
import com.njy.project.simulator.device.MainMemory;
import com.njy.project.simulator.util.Util;

public class MIPS_CPUTEST {
	public static void main(String[] args) throws InterruptedException ,IOException{
		MainMemory mainMemory = new MainMemory(0, Util.oneM);
		Bus bus = new Bus(mainMemory);
		CP0 cp0 = new CP0(bus);
		initialMem(bus, "./test.bin", 0);
		MIPSCPU cpu = new MIPSCPU(bus, cp0);
		cpu.setPc(0);
		cpu.Start();
	}

	private static void initialMem(Bus bus, String filePath, int address) {
		try {
			DataInputStream  inBin = new DataInputStream(new FileInputStream(filePath));
			int data;
			while (true) {
				try {
					data = inBin.readInt();
				} catch (EOFException e) {
					// TODO: handle exception
					inBin.close();
					break;
				}
				bus.write(address, data, AccessMode.WORD);
				address+=4;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
