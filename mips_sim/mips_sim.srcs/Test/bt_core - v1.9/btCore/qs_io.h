#ifndef QS_IO_H
#define QS_IO_H

#include "my_arg.h"
#include "global.h"
#include "util.h"
#include "bt_interrupt.h"

// leds related
void _lightLeds(int leds);

// vga releted
void drawPixel(int x, int y, int color);
void vgaSingleColor(int color);
void testVGA();

//////////////////////////////////////// RS232 RELATED
int rs232ReadByteNonBlock();
void rs232SendByteSlaveWithAddr(char b);
int rs232Check();
char rs232ReceiveByte();
void rs232SendByte(char b);
int rs232ReceiveWord();
void rs232SendWord(int word);
void rs232Test();
void rs232SendWordToSlave(int word);
void rs232SendByteToSlave(char b);
int rs232CheckSlave();
int rs232ReceiveWordWithId();
char rs232ReceiveByteWithId();
void rs232SendWordSlave(int word);
void rs232SendByteSlave(char b);
void rs232SendWordSlave1(int word);
void rs232SendByteSlave1(char b);

int getWordFromBuffer(char *buffer, int *begin, int *end);
char getByteFromBuffer(char *buffer, int *begin, int *end);
void receivePackage(char *buffer, int *length);
void receivePackageNew(char *buffer, int *end);
int getWordFromBytes(char *buffer);
void signal(int data);
int receiveAck();

// keyboard related
char getchar();
int checkkey();
void putchar(int ascii);
void putcharWithFore(int ascii, int foregroundColor);
void putcharWithForeBack(int ascii, int foregroundColor, int backgroundColor);
void putcharWithColor(int asciiWithColor);
void putchar1(char ascii);
void adaptCursorForFont(int ascii);

// cursor related
void setCursor(int x, int y);
void resetCursor();
void addCursorForFontX(int delX);
void clear();
void newline();
void printString(char *message);
void printMessage(int *message);
void singleBackground(int color);
void printNum(int num);
void printf(char *fmt, ...);

#endif
