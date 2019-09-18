package com.njy.project.simulator.device.spi.SD;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.njy.project.simulator.util.Util;

public class SDCard implements Runnable
{
	private RandomAccessFile randomAccessFile;
	private String filePath = null;
	private int blockSize = 512;
	private long capcity = Util.oneG  * 4;
	
	private int addr;
	private int eraseEndAddr;
	private int state = 0;
	private int error_code = 0;
	private byte []buffer = null;
	private boolean isInit = false;
	
	final public static byte START_TOKEN = (byte)0xfe;
	final public static byte WRITE_START_TOKEN = (byte)0xfc;
	final public static byte WRITE_STOP_TOKEN = (byte)0xfd;
	
	public SDCard()
	{
	}
	
	public SDCard(String filePath)
	{
		setFile(filePath);
	}
	
	public boolean setFile(String filePath)
	{
		if(isInit) return false;
		try
		{
			randomAccessFile = new RandomAccessFile(filePath, "rw");
			this.capcity = randomAccessFile.length();
		}
		catch (IOException e)
		{
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
			return false;
		}
		
		this.filePath = filePath;
		isInit = true;
		return true;
	}
	
	final public boolean isValidAddr(int addr)
	{
		return (((long)addr & 0xffffffff) << 9) < capcity;
	}
	
	final public void reset()
	{
		error_code = 0;
		state = 0;
		addr = 0;
		eraseEndAddr = 0;
	}
	
	final public int setAddress(int addr)
	{
		this.addr = addr;
		try
		{
			randomAccessFile.seek(((long)addr & 0xffffffffl) << 9);
		}
		catch (IOException e)
		{
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	final public boolean nextBlock()
	{
		addr ++;
		try
		{
			randomAccessFile.seek(((long)addr) << 9);
		}
		catch (IOException e)
		{
			// TODO �Զ���ɵ� catch ��
			e.printStackTrace();
			state = SDCardMode.ERROR;
			error_code = SDCardMode.READERROR;
			return false;
		}
		return true;
	}
	
	
	final public void readBlock(byte[] buffer)
	{
		this.buffer = buffer;
		state = SDCardMode.READING;
		new Thread(this).start();
	}
	
	final public void writeBlock(byte[] buffer)
	{
		try
		{
			short crc = Util.crc16_ccitt(buffer, 1, 512);
			if(buffer[513] != (byte)(crc >>> 8) ||buffer[514] != (byte)crc)
			{
				state = SDCardMode.ERROR;
				error_code = SDCardMode.CRCERROR;
				return;
			}
			randomAccessFile.write(buffer, 1, 512);
			state = SDCardMode.READY;
			return;
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			switch (state)
			{
			case SDCardMode.READING:
				error_code = SDCardMode.READERROR;
				break;
			case SDCardMode.WRITEING:
				error_code = SDCardMode.WRITEERROR;
				break;
			default:
				break;
			}
			state = SDCardMode.ERROR;
		}
		
	}



	@Override
	public void run()
	{
		// TODO �Զ���ɵķ������
		try
		{
			switch (state)
			{
			case SDCardMode.READING:
			{
				buffer[0] = START_TOKEN;
				randomAccessFile.read(buffer, 1, 512);
				short crc = Util.crc16_ccitt(buffer, 1, 512);
				buffer[513] = (byte)(crc >>> 8);
				buffer[514] = (byte)crc;
				state = SDCardMode.READY;;
				break;
			}
			case SDCardMode.WRITEING:
			{
				short crc = Util.crc16_ccitt(buffer, 1, 512);
				if(buffer[513] != (byte)(crc >>> 8)||buffer[514] != (byte)crc)
				{
					state = SDCardMode.ERROR;
					error_code = SDCardMode.CRCERROR;
					return;
				}
				randomAccessFile.write(buffer, 1, 512);
				state = SDCardMode.READY;
				return;
			}
			default:
				break;
			}
		}
		catch (IOException e) 
		{
			// TODO: handle exception
			
			switch (state)
			{
			case SDCardMode.READING:
				error_code = SDCardMode.READERROR;
				break;
			case SDCardMode.WRITEING:
				error_code = SDCardMode.WRITEERROR;
				break;
			default:
				break;
			}
			state = SDCardMode.ERROR;
		}
		
		
	}
	
	public void finishTrans()
	{
		state = SDCardMode.IDLE;
	}

	public int getState()
	{
		return state;
	}

	public int getErrorCode()
	{
		return error_code;
	}
	
	
	
}
