package com.njy.project.simulator.device.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BoardDispCtrl extends JPanel
{
	private final int verticalWidth = 8;
	private final int verticalHeight = 40;
	private final int horizonWidth = 32;
	private final int horizonHeight = 8;
	private final int dist = 4;
	private final int pointR = 4;
	
	private final int marginLeft = 8;
	private final int marginTop = 8;
	
	private final int unitWidth = 80;
	private final int unitHeight = 100;
	
	private int dataBuffer[] = new int[32];//LED Segment Displays ,seven segment and one point, from right to left, 0 to 31
	
	private int unitMap[][] =
	{
		{  5, 4,17,16, 0,12,24,28 },
		{  7, 6,19,18, 1,13,25,29 },
		{  9, 8,21,20, 2,14,26,30 },
		{ 11,10,23,22, 3,15,27,31 },
	};
	
	private int numMap[][] =
	{
		{1,1,1,1,1,0,1},//0
		{0,1,0,1,0,0,0},//1
		{0,1,1,0,1,1,1},//2
		{0,1,0,1,1,1,1},//3
		{1,1,0,1,0,1,0},//4
		{1,0,0,1,1,1,1},//5
		{1,0,1,1,1,1,1},//6
		{0,1,0,1,1,0,0},//7
		{1,1,1,1,1,1,1},//8
		{1,1,0,1,1,1,1},//9
		{1,1,1,1,1,1,0},//A
		{1,0,1,1,0,1,1},//b
		{1,0,1,0,1,0,1},//C
		{0,1,1,1,0,1,1},//d
		{1,0,1,0,1,1,1},//E
		{1,0,1,0,1,1,0},//F
	};
	
	private int dataRectMap[][] = new int[32][4];;
	
	private Color colorMap[] = {
			Color.DARK_GRAY, Color.RED
	};
	
	public void SetData(int data)
	{
		for(int i =0; i< 32; i++)
		{
			dataBuffer[i] = (data >>> i) & 0x01;
		}
		repaint();
	}
	
	public void SetNum(int num, int count)
	{
		num &= 0x0f;
		for(int i =0 ; i < 7; i++)
		{
			dataBuffer[unitMap[count][i]] = numMap[num][i];
		}
		repaint();
	}
	
	public void SetAllNum(int num, int mask)
	{
		int dispNum = 0;
		int maskNum = 0;
		for(int i =0 ; i < 4; i++)
		{
			dispNum = (num >>> (i << 2)) & 0xf;
			maskNum = (mask >>> i) & 0x01;
			for(int j =0 ;j <7 ; j++)
			{
				dataBuffer[unitMap[i][j]] = numMap[dispNum][j] & maskNum;
			}
			
		}
		repaint();
	}
	
	public void SetAllDot(int d)
	{
		dataBuffer[31] = d >>> 3;
		dataBuffer[30] = (d >>> 2) & 1;
		dataBuffer[29] = (d >>> 1) & 1;
		dataBuffer[28] = d & 1;
	}
	
	public BoardDispCtrl()
	{
		InitDataMap();
		setPreferredSize(new Dimension(unitWidth * 4 + dist * 2, unitHeight));
		setBackground(Color.BLACK);
		repaint();
	}
	
	private void InitDataMap()
	{
		int j = 0;
		int stepX = dist * 2 + horizonWidth + verticalWidth;
		int stepY = dist + verticalHeight;
		for(; j < 4;j++)
		{
			dataRectMap[unitMap[3][j]][0] = stepX * (j & 0x01) + marginLeft;
			dataRectMap[unitMap[3][j]][1] = stepY * (j >> 1) + marginTop;
			dataRectMap[unitMap[3][j]][2] = verticalWidth;
			dataRectMap[unitMap[3][j]][3] = verticalHeight;
		}
		
		stepY = verticalHeight - ( (horizonHeight - dist) >> 1 );
		for(; j < 7; j++)
		{
			dataRectMap[unitMap[3][j]][0] = verticalWidth + dist + marginLeft;
			dataRectMap[unitMap[3][j]][1] = stepY * (j - 4) + marginTop;
			dataRectMap[unitMap[3][j]][2] = horizonWidth;
			dataRectMap[unitMap[3][j]][3] = horizonHeight;
		}
		
		dataRectMap[unitMap[3][j]][0] = 2 * verticalWidth + 4 * dist + horizonWidth + marginLeft;
		dataRectMap[unitMap[3][j]][1] = 2 * verticalHeight  + marginTop;
		dataRectMap[unitMap[3][j]][2] = pointR * 2;
		dataRectMap[unitMap[3][j]][3] = pointR * 2;
		
		for(int i = 0 ; i < 3; i++)
		{
			int base = (3 - i) * unitWidth;
			for(int j1 = 0; j1 < 8;j1++)
			{
				dataRectMap[unitMap[i][j1]][0] = base + dataRectMap[unitMap[3][j1]][0];
				dataRectMap[unitMap[i][j1]][1] = dataRectMap[unitMap[3][j1]][1];
				dataRectMap[unitMap[i][j1]][2] = dataRectMap[unitMap[3][j1]][2];
				dataRectMap[unitMap[i][j1]][3] = dataRectMap[unitMap[3][j1]][3];
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		// TODO �Զ���ɵķ������
		super.paintComponent(g);
		for(int i=0 ;i < 32; i++)
		{
			g.setColor(colorMap[dataBuffer[i]]);
			g.fillRect(dataRectMap[i][0], dataRectMap[i][1], dataRectMap[i][2], dataRectMap[i][3]);
		}
		//for(int i = 0; i < 4; i++)
			//PaintOne(g, i);
	}
	
	
	
	private void PaintOne(Graphics g, int count)
	{
		for(int i = 0; i < 8; i++)
		{
			g.setColor(colorMap[dataBuffer[unitMap[count][i]]]);
			g.fillRect(dataRectMap[unitMap[count][i]][0], dataRectMap[unitMap[count][i]][1], dataRectMap[unitMap[count][i]][2], dataRectMap[unitMap[count][i]][3]);
		}
	}
	
	public void Test()
	{
		Random random = new Random(System.currentTimeMillis());
		for(int i = 0; i<32; i++)
		{
			dataBuffer[i] = random.nextInt(2);
		}
		repaint();
	}
	
	public static void main(String args[]) throws InterruptedException
	{
		JFrame jFrame = new JFrame("test");
		BoardDispCtrl boardDispCtrl = new BoardDispCtrl();
		jFrame.add(boardDispCtrl);
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int i = 0;
		while(true)
		{
			Thread.sleep(1000);
			boardDispCtrl.SetData(0xff00ff00);
			//boardDispCtrl.SetAllDot((i++) & 0xf);
		}
	}

}
