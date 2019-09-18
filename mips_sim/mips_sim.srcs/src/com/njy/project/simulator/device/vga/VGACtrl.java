package com.njy.project.simulator.device.vga;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JPanel;

import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.data.DataController;
import com.njy.project.simulator.device.MainMemory;

public class VGACtrl extends JPanel implements Runnable{
	DeviceVGA deviceVGA;
	MainMemory mainMemory;
	private final String defaultFontPath = "./vgaChar";
	private boolean disabled = false;
	public VGACtrl()
	{
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		mainMemory = DataController.getInstance().getBus().getMainMemory();
		deviceVGA = new DeviceVGA();
		initFontBuffer(defaultFontPath);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(Color.BLACK);
		xCountText = WIDTH / 8 ;
		yCountText = HEIGHT / 16;
		for(int i = 0; i < 256; i++)
		{
			byte data = (byte)i;
			colorTable[i] = new Color(((data & 0x3) << 6) | ((data & 0x1C) << 11) | ((data & 0xE0) << 16));
		}
		new Thread(this).start();
	}
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	int VRAMSIZE[] = {
			(WIDTH / 8) * (HEIGHT / 16) * 2, WIDTH * HEIGHT
	};
	
	int VGAMode;
	int VGARes = 0;
	private int[] dataBuffer =  new int[(WIDTH * HEIGHT)>>>2];
	private byte[] fontBuffer = new byte[256 * 16 * 8];
	
	private Color[] colorTable = new Color[256];
	/**
	 * 娑擄拷閲滈崓蹇曠2娑擃亜鐡ч懞鍌︾礉閸楋拷6娴ｅ稄绱濋崚鍡楀焼娑撶療閿涳拷閿涘閿涳拷閿涘閿涳拷閿涳拷
	 */
	@Override
	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		VGARes = deviceVGA.GetVGARes();
		if(VGARes == 0 || disabled)
		{
			//g.setColor(Color.BLACK);
			//g.fillRect(0, 0, WIDTH, HEIGHT);
			return;
		}
		
		VGAMode = deviceVGA.GetVGAMode();
		readData();
		Draw(g);
	}
	
	private void readData()
	{
		mainMemory.readBlock(deviceVGA.GetVRAM_ADDR(), VRAMSIZE[VGAMode] , dataBuffer);
	}
	
	final public void Draw(Graphics g)
	{
		if(VGAMode == 0)
			drawTextMode(g);
		else
			drawGraphicMode(g);
		
	}
	
	private Color color[] = new Color[2];
	
	private Color[] colorMap = {
			 new Color(  0,   0,   0), new Color(  0,   0, 255), 
			 new Color(  0, 255,   0), new Color(  0, 255, 255),
			 new Color(255,   0,   0), new Color(255,   0, 255), 
			 new Color(255, 255,   0), new Color(255, 255, 255) 
	};
	
	
	private double counter = 0;
	private int cFlashTime = 0;
	private int cVPrevious = 0;
	private int cHPrevious = 0;
	private int cVPresent = 0;
	private int cHPresent = 0;
	private Color cursor[] = {Color.WHITE,Color.BLACK};
	
	boolean stateCounter = false;
	////////////////////////text mode/////////////////////////////
	private void drawTextMode(Graphics g)
	{		
		int pos;
		int addr;
		int offset;
		int data;
		for(int i = 0; i < yCountText; i++)
		{
			pos = i * xCountText  << 1 ;
			for(int j = 0; j < xCountText; j++, pos += 2)
			{
				addr = pos >>> 2;
				offset = pos & 0x3;
				data = (dataBuffer[addr] >>> (offset << 3)) & 0xffff; 
				color[0] = colorMap[(data >>> 8) >>> 4];
				color[1] = colorMap[(data >>> 8)  & 0x07];
				drawOneChar(g, j << 3, i << 4, data & 0xff);
			}
		}
		//draw cursor
		if(deviceVGA.IsHWCursorEnabled())
		{
			
			cFlashTime = deviceVGA.GetCFlashTime();
			//System.out.println(cFlashTime)
			if(counter > cFlashTime - 16)
			{
				counter -= cFlashTime;
				stateCounter ^= true;
			}
			
			
			
			//update present cursor position
			cVPresent = deviceVGA.GetCVPos();
			cHPresent = deviceVGA.GetCHPos();
			
			if(((cVPresent!=cVPrevious) || (cHPresent!=cHPrevious)))
			{
				counter = 0 ;
				stateCounter = true;
			}
			
			pos = cVPresent * xCountText  + cHPresent << 1;
			//save present cursor position
			cVPrevious = cVPresent;
			cHPrevious = cHPresent;
			if(pos >= dataBuffer.length) return;
			if(stateCounter || (cFlashTime == 0))
			{
				DrawHWCursor(g, cHPresent << 3, cVPresent << 4, (dataBuffer[pos >>> 2] >>> (pos & 0x3 << 3)) & 0xff);
			}
			
		}
	}
	
	private int xCountText = 0;
	private int yCountText = 0;
	
	
	private void drawOneChar(Graphics g, int xPos, int yPos, int charCode)
	{
		//g.setColor(bgColor);
		for(int i = 0 ; i < 16; i++ )
		{
			for(int j=0; j < 8 ; j++)
			{
				g.setColor(color[fontBuffer[(charCode << 7) + (i << 3) + j]]);
				g.drawLine(xPos+j, yPos+i, xPos+j, yPos+i);
			}
		}
	}
	////////////////////////hardware cursor//////////////////////////
	private void DrawHWCursor(Graphics g, int xPos, int yPos,  int charCode)
	{
		//g.setColor(cursor[0]);
		//g.fillRect(xPos, yPos, 8, 16);
		try
		{
			for(int i = 0 ; i < 16; i++ )
			{
				for(int j=0; j < 8 ; j++)
				{
					g.setColor(cursor[fontBuffer[(charCode << 7) + (i << 3) + j]]);
					g.drawLine(xPos+j, yPos+i, xPos+j, yPos+i);
				}
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			System.out.println(charCode);
			System.exit(0);
		}
		
	}
	
	//////////////////////////graphic mode//////////////////////////////
	private void drawGraphicMode(Graphics g)
	{
		byte data = 0;
		int pos = 0;
		int addr;
		int offset;
		for(int i = 0; i < HEIGHT; i++)
		{
			pos = i * WIDTH;
			for(int j = 0; j < WIDTH; j++)
			{
				addr = pos + j >>> 2;
				offset = pos + j & 0x3;
				g.setColor(colorTable[(dataBuffer[addr] >>> (offset << 3)) & 0xff]);
				g.drawLine(j, i, j, i);
			}
		}
	}
	
	/**
	 * 
	 * @param data
	 *            - low 16 bits are valid
	 * @return
	 */
	private Color generateColor(int data) {
		data &= 0xffff;

		int r = ((data >> 11) & 0x1f) << 3;
		int g = ((data >> 6) & 0x1f) << 3;
		int b = ((data >> 0) & 0x3f) << 2;

		return new Color(r, g, b);
	}
	
	private void initFontBuffer(String path)
	{
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
			String string;
			for(int i=0; i < 128; i++)
			{
				string = bufferedReader.readLine();
				String[] splitStrings = string.split("\\s|,|;");
				for(int j=0; j < 16; j++)
				{
					int a = (Integer.parseInt(splitStrings[j], 16) & 0xff);
					//System.out.printf("%02x ",a);
					for(int k = 0; k < 8; k++)
					{
						fontBuffer[(i << 7) + (j << 3) + k] = (byte) ((a >>> (7 - k)) & 0x01);
						//System.out.printf("%d",fontBuffer[(i << 7) + (j << 3) + k]);
					}
					//System.out.print(" ");							
				}
				//System.out.println();
			}
			
			for(int i=128; i < 256; i++)
			{
				for(int j=0; j < 16; j++)
				{
					for(int k = 0; k < 8; k++)
					{
						fontBuffer[(i << 7) + (j << 3) + k] = 0;
					}							
				}
			}
			
			bufferedReader.close();
		}
		catch (IOException e)
		{
			// TODO: handle exception
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void initTest()
	{
		Random random  = new Random();
		int pos = 0;
		for(int i = 0; i < yCountText; i++)
		{
			pos = i * xCountText << 1;
			for(int j = 0; j < xCountText; j++, pos += 2)
			{
				
				dataBuffer[pos] = (byte)( (random.nextInt(8) << 4) | random.nextInt(8) ); //random color
				dataBuffer[pos + 1] = (byte)(random.nextInt(128));
			}
		}
	}
	
	public void initTestGraphic()
	{
		Random random  = new Random();
		for(int i = 0; i < VRAMSIZE[1]; i++)
		{
			dataBuffer[i] = (byte)random.nextInt();
		}
	}
	
	
	
	public static void main(String args[]) throws IOException
	{
		//VGACtrl vgaCtrl = new VGACtrl("./16_8.coe");
		//vgaCtrl.initTest();
	}

	
	private long frameTime = 16000000;
	@Override
	public void run() {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		long time = System.nanoTime();
		try {
			while(true)
			{
				time = System.nanoTime();
				Thread.sleep(16, 600000);
				repaint();
				frameTime = System.nanoTime() - time;
				//System.out.println(frameTime);
				counter += frameTime / 1e6;
			}
		} catch (Exception e) {
			// TODO 鑷姩鐢熸垚鐨� catch 鍧�
			e.printStackTrace();
		}
		
	}

	public boolean isDisabled() {
		return disabled;
	}

	synchronized public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
