package com.njy.project.simulator.device.spi.SD;
import com.njy.project.simulator.device.spi.SPIMaster;
import com.njy.project.simulator.device.spi.SPISlave;

public class SlaveSDCard implements SPISlave
{
	public SlaveSDCard()
	{
		// TODO �Զ���ɵĹ��캯����
	}
	
	public SlaveSDCard(SDCard sdCard)
	{
		// TODO �Զ���ɵĹ��캯����
		this.sdCard = sdCard;
	}
	
	
	@Override
	public void transmitData(SPIMaster spiMaster) {
		// TODO Auto-generated method stub
		//System.out.print("transmit ");
		switch (mode)
		{
		case SDCardMode.CMDRECV:
		{
			byte b;
			b = (byte)spiMaster.SPIToSlave();
			if(dataCount == 0 && b == (byte)0xff)
			{
					
				switch (sdCard.getState())
				{
				case SDCardMode.IDLE:
				case SDCardMode.READY:
					spiMaster.SlaveToSPI(0xff);
					return;
				default:
					spiMaster.SlaveToSPI(0);
					return;
				}
			}
				
			spiMaster.SlaveToSPI(0xff);
			//System.out.println("data "+String.format("%02X", b));
			dataBuffer[dataCount++] = b;
			if(dataCount == dataTotal)
			{
				//System.out.println(dataCount);
				dataCount = 0;
				//System.out.println("cmd:" + (dataBuffer[0] & 0x3f));
				//System.exit(0);
				resolveCommand();
			}
			break;
		}
		case SDCardMode.CMDREPL:
		{
			spiMaster.transmitData(dataBuffer[dataCount++]);
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				dataTotal = 6;
				mode = SDCardMode.CMDRECV;
			}
			break;
		}
		case SDCardMode.SINGLEREADREPL:
		{
			spiMaster.transmitData(dataBuffer[dataCount++]);
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				dataTotal = 515;
				sdCard.readBlock(dataBuffer);
				mode = SDCardMode.SINGLEDATSEND;
			}
			break;
		}
		case SDCardMode.SINGLEWRITEREPL:
		{
			spiMaster.transmitData(dataBuffer[dataCount++]);
			
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				dataTotal = 515;
				mode = SDCardMode.SINGLEDATRECV;
			}
			break;
		}
		case SDCardMode.MULTIREADREPL:
		{
			spiMaster.transmitData(dataBuffer[dataCount++]);
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				dataTotal = 515;
				cmdCount = 0;
				mode = SDCardMode.MULTIDATSEND;
			}
			break;
		}
		case SDCardMode.MULTIWRITEREPL:
		{
			spiMaster.transmitData(dataBuffer[dataCount++]);
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				dataTotal = 515;
				mode = SDCardMode.MULTIDATRECV;
			}
			break;
		}
		case SDCardMode.SINGLEDATSEND:
		{
			switch (sdCard.getState())
			{
			case SDCardMode.READING:
				spiMaster.transmitData(0xff);
				break;
			case SDCardMode.ERROR:
				spiMaster.transmitData(sdCard.getErrorCode());
				mode = SDCardMode.CMDRECV;
				break;
			case SDCardMode.READY:
				spiMaster.transmitData(dataBuffer[dataCount++]);
				if(dataCount == dataTotal)
				{
					dataCount = 0;
					dataTotal = 6;
					sdCard.finishTrans();
					System.out.println("single data read finish.................................................................................");
					mode = SDCardMode.CMDRECV;
				}
				break;
			default:
				break;
			}
			break;
		}
		case SDCardMode.MULTIDATSEND:
		{
			
			byte b = (byte)spiMaster.SPIToSlave();
			if(dataCount == 0)
			{
				switch (b)
				{
				case (byte)0xff:
					switch (sdCard.getState())
					{
					case SDCardMode.READING:
						spiMaster.SlaveToSPI(0xff);
						return;
					case SDCardMode.ERROR:
						spiMaster.SlaveToSPI(sdCard.getErrorCode());
						mode = SDCardMode.CMDRECV;
						return;
					case SDCardMode.READY:
						spiMaster.SlaveToSPI(dataBuffer[dataCount++]);
						return;
					case SDCardMode.IDLE:
						sdCard.readBlock(dataBuffer);
						spiMaster.SlaveToSPI(0xff);
						return;
					}
					return;
				case 0x4c:
					cmd[cmdCount++] = b;
					spiMaster.SlaveToSPI(0xff);
					return;
				default:
					if(cmdCount == 0) 
					{
						spiMaster.SlaveToSPI(0xff);
						return;
					}
					cmd[cmdCount++] = b;
					spiMaster.SlaveToSPI(0xff);
					if(cmdCount == 6)
					{
						if(cmd[0] == 0x4c && (cmd[5] & 0x1) == 1)
						{
							sdCard.finishTrans();
							dataTotal = 1;
							dataBuffer[0] = 0;
							mode = SDCardMode.CMDREPL;
							System.out.println("multi data read finish.................................................................................");
						}
						cmdCount = 0;
						dataCount = 0;
						return;
					}
					return;
				}
			}
			
			spiMaster.SlaveToSPI(dataBuffer[dataCount++]);
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				cmdCount = 0;
				sdCard.finishTrans();
			}
			
			break;
		}
		case SDCardMode.SINGLEDATRECV:
		{
			dataBuffer[dataCount] = (byte)spiMaster.transmitData(0Xff);
			if(dataCount==0 && dataBuffer[0] != SDCard.START_TOKEN)
				return;
			dataCount++;
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				sdCard.writeBlock(dataBuffer);
				mode = SDCardMode.SINGLEDATARESPONSE;
				
			}
			break;
		}
		case SDCardMode.MULTIDATRECV:
		{
			dataBuffer[dataCount] = (byte)spiMaster.SPIToSlave();
			if(dataCount==0)
			{
				switch (dataBuffer[0])
				{
				case SDCard.WRITE_START_TOKEN:
					spiMaster.SlaveToSPI(0xff);
					dataCount++;
					break;
				case SDCard.WRITE_STOP_TOKEN:
				{
					spiMaster.SlaveToSPI(0xff);
					sdCard.finishTrans();
					dataTotal = 6;
					mode = SDCardMode.CMDRECV;
					System.out.println("multi data write finish.................................................................................");
					return;
				}	
				case (byte)0xff:
				{
					switch (sdCard.getState())
					{
					case SDCardMode.IDLE:
					case SDCardMode.READY:
						spiMaster.SlaveToSPI(0xff);
						return;
					default:
						spiMaster.SlaveToSPI(0);
						return;
					}
				}
				default:
					return;
				}
			}	
			else
			{
				spiMaster.SlaveToSPI(0xff);
				dataCount++;
			}
				
			if(dataCount == dataTotal)
			{
				dataCount = 0;
				sdCard.writeBlock(dataBuffer);
				mode = SDCardMode.MULTIDATARESPONSE;
			}
			break;
		}
		case SDCardMode.SINGLEDATARESPONSE:
		{
			switch (sdCard.getState())
			{
			case SDCardMode.ERROR:
				spiMaster.transmitData(sdCard.getErrorCode());
				dataTotal = 6;
				mode = SDCardMode.CMDRECV;
				break;
			case SDCardMode.READY:
				spiMaster.transmitData(SDCardMode.DATACCEPTED);
				System.out.println("single data write finish.................................................................................");
				sdCard.finishTrans();
				dataTotal = 6;
				mode = SDCardMode.CMDRECV;
				break;
			default:
				break;
			}
			break;
		}
		case SDCardMode.MULTIDATARESPONSE:
		{
			switch (sdCard.getState())
			{
			case SDCardMode.ERROR:
				spiMaster.transmitData(sdCard.getErrorCode());
				mode = SDCardMode.CMDRECV;
				break;
			case SDCardMode.READY:
				spiMaster.transmitData(SDCardMode.DATACCEPTED);
				System.out.println("multi data one block write finish.................................................................................");
				mode = SDCardMode.MULTIDATRECV;
				break;
			default:
				break;
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void reset()
	{
		mode = 0;
		dataCount = 0;
		cmdCount = 0;
	}
	
	private void resolveCommand()
	{
		if((dataBuffer[0] & 0xc0) != 0x40 || (dataBuffer[5] & 0x1) != 1) return;
		try
		{
			switch (dataBuffer[0] & 0x3f)
			{
			case 0:
				reset();
				dataBuffer[0] = 1;
				dataTotal = 1;
				mode = SDCardMode.CMDREPL;
				return;
			case 8:
				dataBuffer[0] = 1;
				dataTotal = 5;
				mode = SDCardMode.CMDREPL;
				return;
			case 41:
				dataBuffer[0] = 0;
				dataTotal = 1;
				mode = SDCardMode.CMDREPL;
				return;
			case 17:
			{
				System.out.println("single data read");
				int addr = ((dataBuffer[1] & 0xff) << 24) | ((dataBuffer[2] & 0xff) << 16) | ((dataBuffer[3] & 0xff) << 8) | (dataBuffer[4]& 0xff);
				if(sdCard.isValidAddr(addr))
				{
					sdCard.setAddress(addr);
					dataBuffer[0] = 0;
					dataTotal = 1;
					mode = SDCardMode.SINGLEREADREPL;
				}
				else
				{
					dataBuffer[0] = 0x20;
					dataTotal = 1;
					mode = SDCardMode.CMDREPL;
				}
				return;
			}
			case 18:
			{
				System.out.println("multi data read");
				int addr = ((dataBuffer[1] & 0xff) << 24) | ((dataBuffer[2] & 0xff) << 16) | ((dataBuffer[3] & 0xff) << 8) | (dataBuffer[4]& 0xff);
				if(sdCard.isValidAddr(addr))
				{
					sdCard.setAddress(addr);
					dataBuffer[0] = 0;
					dataTotal = 1;
					mode = SDCardMode.MULTIREADREPL;
				}
				else
				{
					dataBuffer[0] = 0x20;
					dataTotal = 1;
					mode = SDCardMode.CMDREPL;
				}
				return;
			}
			case 24:
			{
				System.out.println("single data write");
				int addr = ((dataBuffer[1] & 0xff) << 24) | ((dataBuffer[2] & 0xff) << 16) | ((dataBuffer[3] & 0xff) << 8) | (dataBuffer[4]& 0xff);
				System.out.println("write block:" + addr);
				if(sdCard.isValidAddr(addr))
				{
					sdCard.setAddress(addr);
					dataBuffer[0] = 0;
					dataTotal = 1;
					mode = SDCardMode.SINGLEWRITEREPL;
				}
				else
				{
					dataBuffer[0] = 0x20;
					dataTotal = 1;
					mode = SDCardMode.CMDREPL;
				}
				return;
			}
			case 25:
			{
				System.out.println("multi data write");
				
				int addr = ((dataBuffer[1] & 0xff) << 24) | ((dataBuffer[2] & 0xff) << 16) | ((dataBuffer[3] & 0xff) << 8) | (dataBuffer[4]& 0xff);
				if(sdCard.isValidAddr(addr))
				{
					sdCard.setAddress(addr);
					dataBuffer[0] = 0;
					dataTotal = 1;
					mode = SDCardMode.MULTIWRITEREPL;
				}
				else
				{
					dataBuffer[0] = 0x20;
					dataTotal = 1;
					mode = SDCardMode.CMDREPL;
				}
				return;
			}
			case 32:
			case 33:
			{
				int addr = ((dataBuffer[1] & 0xff) << 24) | ((dataBuffer[2] & 0xff) << 16) | ((dataBuffer[3] & 0xff) << 8) | (dataBuffer[4]& 0xff);
				if(sdCard.isValidAddr(addr))
					dataBuffer[0] = 0;
				else
					dataBuffer[0] = 0x20;
				dataTotal = 1;
				mode = SDCardMode.CMDREPL;
				return;
			}
			case 38:
			{
				dataBuffer[0] = 0;
				dataTotal = 1;
				mode = SDCardMode.CMDREPL;
				return;
			}
			default:
				dataBuffer[0] = 0x04;
				dataTotal = 1;
				mode = SDCardMode.CMDREPL;
				return;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			dataBuffer[0] = 0x40;
			dataTotal = 1; 
			mode = SDCardMode.CMDREPL;
			return;
		}
		
	}
	
	public void setSdCard(SDCard sdCard)
	{
		this.sdCard = sdCard;
	}

	
	private SDCard sdCard;
	private byte[] dataBuffer = new byte[0x10000];
	private byte[] cmd = new byte[6];
	//private boolean transmit = false;
	private int mode = 0;//0: received command;1:command reply; 2: receive data from master ;3 : send data to master
	private int dataCount = 0;
	private int cmdCount = 0;
	private int dataTotal = 6;
	private boolean active = false;
	
}
