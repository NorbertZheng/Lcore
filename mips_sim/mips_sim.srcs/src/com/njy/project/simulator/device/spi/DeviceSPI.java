package com.njy.project.simulator.device.spi;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cp0.InterruptCode;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.Device;

public class DeviceSPI implements Device, SPIMaster
{
	public DeviceSPI()
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鐧告嫹閿熷眾鍑介敓鏂ゆ嫹閿熸枻鎷烽敓锟�
		index = InterruptCode.SPI;
		ramBase = 0xffff0000 + (InterruptCode.SPI << 8);
		baseMask = 256 - 1;
		SetTXLeft(bufferSize);
	}
	
	@Override
	final public boolean isValidAddr(int address)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		return false;
	}
	@Override
	final public boolean isValidAddr(int address, int mode)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		return false;
	}
	@Override
	final public int read(int address)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		return read(address, AccessMode.WORD, false);
	}
	@Override
	final public int read(int address, int mode, boolean signed)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		address &= baseMask;
		//for byte array
		
		if(address == 0x0c)
		{
			return readRXBuff();
		}
		
		int data = 0;
		switch(mode)
		{
		case AccessMode.BYTE:
			data |= ram[address] << 24;
			return signed? data >>> 24: data >> 24;
		case AccessMode.HALFWORD:
			data |= (ram[address ++] & 0xff) << 16;
			data |= ram[address] << 24;
			return signed? data >>> 16: data >> 16;
		case AccessMode.WORD:
			data |= (ram[address ++] & 0xff);
			data |= (ram[address ++] & 0xff) << 8;
			data |= (ram[address ++] & 0xff) << 16;
			data |= ram[address] << 24;
		}
		
		return data;
	}
	@Override
	public void write(int address, int data)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		write(address, data, AccessMode.WORD);
	}
	@Override
	public void write(int address, int data, int mode)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		address &= baseMask;
		//for byte array
		
		switch (address >>> 2)
		{
		case 0:
			return;
		case 2:
			ram[0] = 0;
			ram[1] = 0;
			ram[2] = 0;
			ram[3] = 0;
			ram[4] = 0;
			ram[5] = 0;
			ram[6] = 0;
			ram[7] = 0;
			SetTXLeft(bufferSize);
			break;
		case 3:
			System.out.println("data write: " + String.format("%02X", (byte)data));
			writeTXBuff(data);
			return;
		default:
			break;
		}
		
		switch(mode)
		{
		case AccessMode.BYTE:
		{
			ram[address] = (byte)data;
			return;
		}
		case AccessMode.HALFWORD:
		{
			ram[address++] = (byte)data;
			ram[address  ] = (byte)(data >>> 8);
			return;
		}
		case AccessMode.WORD:
		{
			ram[address++] = (byte)data;
			ram[address++] = (byte)(data >>> 8);
			ram[address++] = (byte)(data >>> 16);
			ram[address  ] = (byte)(data >>> 24);
			return;
		}
		}	
	}
	@Override
	public void initial(int base, int size)
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		
	}
	@Override
	public void reset()
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		
	}
	@Override
	final public int getindex()
	{
		// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
		return index;
	}
	
	@Override
	public int getLastWrite() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	////for cpu
	final public int readRXBuff()
	{
		synchronized (this)
		{
			if(rx_left == 0)
			{
				setUF(1);
				DataController.getInstance().getCp0().SetIRC(InterruptCode.SPI);
				return 0;
			}
			
			int data = rxBuffer.read();
			SetRXLeft(rx_left - 1);
			SetTXLeft(tx_left + 1);
			System.out.println("data read " + String.format("%02X", (byte)data) + " txleft:" + tx_left + " rxleft:" + rx_left);
			return data;
		}
		
	}
	
	final public void writeTXBuff(int data)
	{
		synchronized (this)
		{
			if(tx_left == 0)
			{
				setOF(1);
				DataController.getInstance().getCp0().SetIRC(InterruptCode.SPI);
				return;
			}
			
				
			txBuffer.write(data);
			SetTXLeft(tx_left - 1);
			
			if(tx_left == 0)
				setBF(1);
		}
	}
	
	
	////for controller
	@Override
	final public int transmitData(int data)
	{
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		//if(txBuffer.getLeft() == 0) return -1;
		rxBuffer.write(data);
		SetRXLeft(rx_left + 1);
		return txBuffer.read();
	}
	
	@Override
	final public void SlaveToSPI(int data)
	{
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		//if(txBuffer.getLeft() == 0) return -1;
		rxBuffer.write(data);
		SetRXLeft(rx_left + 1);
	}
	
	@Override
	final public int SPIToSlave()
	{
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		//if(txBuffer.getLeft() == 0) return -1;
		return txBuffer.read();
	}
	
	final public boolean transBufferEmpty()
	{
		return txBuffer.isEmpty;
	}
	
	////////////////////////////
	
	final public int getSelectedSlave()
	{
		return (ram[0x0a] & 0xff) |((ram[0x0b] & 0xff) << 8);
	}
	
	final public boolean getOF()
	{
		return ((ram[0] >>>3) & 1) != 0;
	}
	
	final public void setOF(int data)
	{
		ram[0] |= data << 3;
	}
	
	final public boolean getUF()
	{
		return ((ram[0] >>>2) & 1)!=0;
	}
	
	final public void setUF(int data)
	{
		ram[0] |= data << 2;
	}
	
	final public boolean getBF()
	{
		return ((ram[0] >>>1) & 1) != 0;
	}
	
	final public void setBF(int data)
	{
		ram[0] |= data << 1;
	}
	
	final public boolean getTE()
	{
		return (ram[0] & 1) != 0;
	}
	
	final public void setTE(int data)
	{
		ram[0] |= data;
	}
	
	final public int getRXLeft()
	{
		return rx_left;
	}
	
	final public int getTXLeft()
	{
		return tx_left;
	}
	
	final public void SetRXLeft(int data)
	{
		rx_left = data;
		ram[0x06] = (byte)(data & 0xff);
		ram[0x07] = (byte)((data >> 8) & 0xff);
	}
	
	final public void SetTXLeft(int data)
	{
		tx_left = data;
		ram[0x04] = (byte)(data & 0xff);
		ram[0x05] = (byte)((data >> 8) & 0xff);
	}
	
	final public void setTR(int data)
	{
		ram[0x03] = (byte)((data & 0x1) << 7);
	}
	
	final public boolean getTR()
	{
		return (ram[0x03]>>>7) != 0;
	}
	
	final public boolean isEnabled()
	{
		return (ram[0x8] & 1) != 0;
	}
	
	public class SPIInnerBuffer
	{
		private byte buffer[];
		private int head;
		private int tail;
		private int size;
		private int mask;
		public boolean isFull = false;
		public boolean isEmpty = true;
		public SPIInnerBuffer(int size)
		{
			// TODO 閿熺殕璁规嫹閿熸枻鎷烽敓缂寸殑鐧告嫹閿熷眾鍑介敓鏂ゆ嫹閿熸枻鎷烽敓锟�
			this.size = size;
			this.mask = size - 1;
			buffer = new byte[size];
			head = tail = 0;
		}
		
		final public int read()
		{
			int data = buffer[tail++];
			tail &= mask;
			isEmpty = head == tail;
			//isFull = false;
			return data;
		}
		
		final public void  write(int data)
		{
			buffer[head ++] = (byte)data;
			head &= mask;
			//isFull = head == tail;
			isEmpty = false;
			
		}
		
		final public int getLeft()
		{
			if(isEmpty) return 0;
			
			return head <= tail? head + size - tail : head - tail;
		}
		
	}
	

	public static void main(String args[])
	{
		
	}
	
	
	private int index;
	private int ramBase;
	private int baseMask;
	private final int ramSize = 256;
	private byte[] ram = new byte[256];
	private int bufferSize = 256;
	private int rx_left = 0;
	private int tx_left = 0;
	private SPIInnerBuffer txBuffer = new SPIInnerBuffer(bufferSize);
	private SPIInnerBuffer rxBuffer = new SPIInnerBuffer(bufferSize);


	

	
}
