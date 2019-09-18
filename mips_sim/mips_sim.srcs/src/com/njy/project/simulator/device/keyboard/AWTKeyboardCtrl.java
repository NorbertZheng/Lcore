package com.njy.project.simulator.device.keyboard;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

public class AWTKeyboardCtrl implements AWTEventListener
{

	@Override
	public void eventDispatched(AWTEvent arg0)
	{
		// TODO �Զ���ɵķ������
		switch (((KeyEvent)arg0).getID())
		{
		case KeyEvent.KEY_PRESSED:
			
			break;
		case KeyEvent.KEY_RELEASED:
			
			break;
		default:
			break;
		}
	}

	private byte[][] scanCodeMake = 
		{
			{},//00
			{},{},{},{},{},{},{},//01-07
			{0x66},//08 backspace
			{0x0D},//09 tab
			{0x5A},//10 enter
			{},//
			{},//12 win
			{},//13 
			{},{},//14,15
			{0x12},//16 shift
			{0x14},//17 ctrl
			{0x11},//18 alt
			{},//19 pause
			{0x58},//20 caps_lock
			{},{},{},{},{},{},//21-26
			{0x76},//27 esc
			{},{},{},{},
			{0x29},//32 space
			{(byte)0xE0,0x7D},//33 page_up
			{(byte)0xE0,0x7A},//34 page_down
			{(byte)0xE0,0x69},//35 end
			{(byte)0xE0,0x6C},//36 home
			{(byte)0xE0,0x6B},//37 left
			{(byte)0xE0,0x75},//38 up
			{(byte)0xE0,0x74},//39 right
			{(byte)0xE0,0x72},//40 down
			{},
			{},
			{},
			{0x41},//44 ,<
			{0x4E},//45 -_
			{0x49},//46 .>
			{0x4A},//47 /?
			{0x45},
			{0x16},
			{0x1E},
			{0x26},
			{0x25},
			{0x2E},
			{0x36},
			{0x3D},
			{0x3E},
			{0x46},//48 - 57,  0 - 9
			{},
			{0x4C},//59 ;:
			{},
			{0x55},//61 =+
			{},
			{},
			{},//58-64
			{0x1C},
			{0x32},
			{0x21},
			{0x23},
			{0x24},
			{0x2B},
			{0x34},
			{0x33},
			{0x43},
			{0x3B},
			{0x42},
			{0x4B},
			{0x3A},
			{0x31},
			{0x44},
			{0x4D},
			{0x15},
			{0x2D},
			{0x1B},
			{0x2C},
			{0x3C},
			{0x2A},
			{0x1D},
			{0x22},
			{0x35},
			{0x1A},//65 - 90, a-z
			{0x54},//91 [{
			{0x5D},//92 \|
			{0x5B},//93 ]}
			{},
			{},
			{0x70},
			{0x69},
			{0x72},
			{0x7A},
			{0x6B},
			{0x73},
			{0x74},
			{0x6C},
			{0x75},
			{0x7D},//96-105 num 0 - 9
			{0x7C},//106 kp_*
			{0x79},//107 kp_+
			{},//108 kp_sep
			{0x7B},//109 kp_-
			{0x71},//110 kp_.
			{(byte)0xE0,0x4A},//111 kp_/
			{0x05},
			{0x06},
			{0x04},
			{0x0C},
			{0x03}, 
			{0x0B},
			{(byte)0x83},
			{0x0A},
			{0x01},
			{0x09}, 
			{0x78},
			{0x07},//112 - 123 F1-F12
			{},{},//124 125
			{},
			{(byte)0xE0, 0x71},//127 delete
			{},{},{},
			{},{},{},{},{}, {},{},{},{},{}, 
			{},{},{},
			{0x77},//144 numlock
			{},
			{},{},{},{},{}, 
			{},{},{},{},
			{(byte)0xE0,0x70},//155 insert
			{},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{} ,{},{},{},{},{},
			{},{},{},{},{},//-190 
			{},
			{0x0E},//192 `~
			{},{},{},
			{},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{} ,
			{},
			{0x52},//222 '"
			{},{},{},
		};
		
		private byte[][] scanCodeBreak = 
			{
				{},//00
				{},{},{},{},{},{},{},//01-07
				{(byte)0xF0,0x66},//08 backspace
				{(byte)0xF0,0x0D},//09 tab
				{(byte)0xF0,0x5A},//10 enter
				{},//
				{},//12 win
				{},//13 
				{},{},//14,15
				{(byte)0xF0,0x12},//16 shift
				{(byte)0xF0,0x14},//17 ctrl
				{(byte)0xF0,0x11},//18 alt
				{},//19 pause
				{(byte)0xF0,0x58},//20 caps_lock
				{},{},{},{},{},{},//21-26
				{(byte)0xF0,0x76},//27 esc
				{},{},{},{},
				{(byte)0xF0,0x29},//32 space
				{(byte)0xE0,(byte)0xF0,0x7D},//33 page_up
				{(byte)0xE0,(byte)0xF0,0x7A},//34 page_down
				{(byte)0xE0,(byte)0xF0,0x69},//35 end
				{(byte)0xE0,(byte)0xF0,0x6C},//36 home
				{(byte)0xE0,(byte)0xF0,0x6B},//37 left
				{(byte)0xE0,(byte)0xF0,0x75},//38 up
				{(byte)0xE0,(byte)0xF0,0x74},//39 right
				{(byte)0xE0,(byte)0xF0,0x72},//40 down
				{},
				{},
				{},
				{(byte)0xF0,0x41},//44 ,<
				{(byte)0xF0,0x4E},//45 -_
				{(byte)0xF0,0x49},//46 .>
				{(byte)0xF0,0x4A},//47 /?
				{(byte)0xF0,0x45},
				{(byte)0xF0,0x16},
				{(byte)0xF0,0x1E},
				{(byte)0xF0,0x26},
				{(byte)0xF0,0x25},
				{(byte)0xF0,0x2E},
				{(byte)0xF0,0x36},
				{(byte)0xF0,0x3D},
				{(byte)0xF0,0x3E},
				{(byte)0xF0,0x46},//48 - 57,  0 - 9
				{},
				{(byte)0xF0,0x4C},//59 ;:
				{},
				{(byte)0xF0,0x55},//61 =+
				{},
				{},
				{},//58-64
				{(byte)0xF0,0x1C},
				{(byte)0xF0,0x32},
				{(byte)0xF0,0x21},
				{(byte)0xF0,0x23},
				{(byte)0xF0,0x24},
				{(byte)0xF0,0x2B},
				{(byte)0xF0,0x34},
				{(byte)0xF0,0x33},
				{(byte)0xF0,0x43},
				{(byte)0xF0,0x3B},
				{(byte)0xF0,0x42},
				{(byte)0xF0,0x4B},
				{(byte)0xF0,0x3A},
				{(byte)0xF0,0x31},
				{(byte)0xF0,0x44},
				{(byte)0xF0,0x4D},
				{(byte)0xF0,0x15},
				{(byte)0xF0,0x2D},
				{(byte)0xF0,0x1B},
				{(byte)0xF0,0x2C},
				{(byte)0xF0,0x3C},
				{(byte)0xF0,0x2A},
				{(byte)0xF0,0x1D},
				{(byte)0xF0,0x22},
				{(byte)0xF0,0x35},
				{(byte)0xF0,0x1A},//65 - 90, a-z
				{(byte)0xF0,0x54},//91 [{
				{(byte)0xF0,0x5D},//92 \|
				{(byte)0xF0,0x5B},//93 ]}
				{},
				{},
				{(byte)0xF0,0x70},
				{(byte)0xF0,0x69},
				{(byte)0xF0,0x72},
				{(byte)0xF0,0x7A},
				{(byte)0xF0,0x6B},
				{(byte)0xF0,0x73},
				{(byte)0xF0,0x74},
				{(byte)0xF0,0x6C},
				{(byte)0xF0,0x75},
				{(byte)0xF0,0x7D},//96-105 num 0 - 9
				{(byte)0xF0,0x7C},//106 kp_*
				{(byte)0xF0,0x79},//107 kp_+
				{},//108 kp_sep
				{(byte)0xF0,0x7B},//109 kp_-
				{(byte)0xF0,0x71},//110 kp_.
				{(byte)0xE0,(byte)0xF0,0x4A},//111 kp_/
				{(byte)0xF0,0x05},
				{(byte)0xF0,0x06},
				{(byte)0xF0,0x04},
				{(byte)0xF0,0x0C},
				{(byte)0xF0,0x03}, 
				{(byte)0xF0,0x0B},
				{(byte)0xF0,(byte)0x83},
				{(byte)0xF0,0x0A},
				{(byte)0xF0,0x01},
				{(byte)0xF0,0x09}, 
				{(byte)0xF0,0x78},
				{(byte)0xF0,0x07},//112 - 123 F1-F12
				{},{},//124 125
				{},
				{(byte)0xF0,(byte)0xE0, 0x71},//127 delete
				{},{},{},
				{},{},{},{},{}, {},{},{},{},{}, 
				{},{},{},
				{(byte)0xF0,0x77},//144 numlock
				{},
				{},{},{},{},{}, 
				{},{},{},{},
				{(byte)0xE0,(byte)0xF0,0x70},//155 insert
				{},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{} ,{},{},{},{},{},
				{},{},{},{},{},//-190 
				{},
				{(byte)0xF0,0x0E},//192 `~
				{},{},{},
				{},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{}, {},{},{},{},{} ,
				{},
				{(byte)0xF0,0x52},//222 '"
				{},{},{},
			};
}
