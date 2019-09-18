package com.njy.project.simulator.test;

public class SPIBufferTest
{
	private byte buffer[];
	private int head;
	private int tail;
	public boolean isFull = false;
	public boolean isEmpty = true;
	public SPIBufferTest()
	{
		// TODO �Զ���ɵĹ��캯����
		buffer = new byte[0x8];
		head = tail = 0;
	}
	
	final public int read()
	{
		int data = buffer[tail++];
		tail &= 0x7;
		isEmpty = head == tail;
		isFull = false;
		
		return data;
	}
	
	final public void  write(int data)
	{
		buffer[head ++] = (byte)data;
		head &= 0x7;
		
		isFull = head == tail;
		isEmpty = false;
	}
	
	
	public static void main(String args[])
	{
		System.out.println(Integer.parseInt("ff000000", 16));
		SPIBufferTest spiBufferTest = new SPIBufferTest();
		
		byte a[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		System.out.println(spiBufferTest.isEmpty);
		for(int i = 0;i<4;i++)
		{
			
			spiBufferTest.write(a[i]);
			System.out.print("write " + a[i] + " ");
			System.out.print("Full:" + spiBufferTest.isFull + " ");
			System.out.println("Empty:" + spiBufferTest.isEmpty);
		}
		for(int i = 0;i<4;i++)
		{
			System.out.print("read:" +spiBufferTest.read() + " ");
			System.out.print("Full:" + spiBufferTest.isFull + " ");
			System.out.println("Empty:" + spiBufferTest.isEmpty);
		}
		
		
		for(int i = 4;i<12;i++)
		{
			spiBufferTest.write(a[i]);
			System.out.print("write " + a[i] + " ");
			System.out.print("Full:" + spiBufferTest.isFull + " ");
			System.out.println("Empty:" + spiBufferTest.isEmpty);
		}
		
		for(int i = 0;i<8;i++)
		{
			System.out.print("read:" +spiBufferTest.read() + " ");
			System.out.print("Full:" + spiBufferTest.isFull + " ");
			System.out.println("Empty:" + spiBufferTest.isEmpty);
		}
;
	}
	
}
