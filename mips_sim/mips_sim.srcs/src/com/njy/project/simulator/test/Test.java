package com.njy.project.simulator.test;

import java.io.FileWriter;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.util.Util;


public class Test {
	public static void main(String[] args) {
		int data = 0xff;
		System.out.println((data << 24) >> 24);
		System.out.println(Util.signExt(data, AccessMode.BYTE, 1));
		System.out.println(Util.signExt(data, AccessMode.HALFWORD, 1));
		System.out
				.println(Integer.toHexString(Util.saveHighBits(0x12345678, 4)));
		System.out
				.println(Integer.toHexString(Util.saveHighBits(0x12345678, 8)));
		System.out
.println(Integer.toHexString(Util
				.saveHighBits(0x12345678, 16)));
		System.out
				.println(Integer.toHexString(Util.delHighBits(0x12345678, 16)));
		System.out
				.println(Integer.toHexString(Util.saveLowBits(0x12345678, 16)));
		System.out
				.println(Integer.toHexString(Util.delLowBits(0x12345678, 16)));
		System.out.println(Integer.toHexString(Util.delLowBits(0, 2)));

		int a = -2147483648;
		int b = 67108864;
		System.out.println("111");
		System.out.println(Util.compareIntIgnoreSign(a, b));
		System.out.println(Util.compareIntIgnoreSign(-1, 3));

		int x = -2147483648, y = 67108864;
		System.out.println(((x ^ y) >=0 ? x - y : -x) >>> 31);
		System.out.println(0x3fffffff - 0x80000000);
		int count = 1;
		long timeStart = System.currentTimeMillis();
		while (count != 0) {
			count++;
		}
		String string = "hjghghklazxcvbnm7865esxdfhc vb;pijolm, xcrtdgfv b;nm;npiyhoynb v8ctfuvi ;nmlk ,k][";
		System.out.println("l:" + string.length());
		byte buffer[] = new byte[512];
		for(int i = 0; i < 512; i++)
		{
			buffer[i] = 0x02;
		}
		System.out.println(String.format("%04X",Util.crc16_ccitt(buffer, 0, 512)));
		long timeStop = System.currentTimeMillis();
		System.out.println((timeStop - timeStart) + "ms");
		
	}
}
