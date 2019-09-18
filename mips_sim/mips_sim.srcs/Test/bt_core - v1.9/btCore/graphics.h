#ifndef GRAPHICS_H
#define GRAPHICS_H

#include "global.h"
#include "util.h"

/***************************************************************************/
/* º¯ÊýÉùÃ÷*/
int  abs(int x);
int  checkkey();
int  checkandgetkey(int* key);
void circle(int x, int y, int radius, int color);
void circlefilled(int x, int y, int radius, int color);
int  getpixel(int x, int y);
void line(int x1,int y1,int x2,int y2, int color);
void printnum(int x, int y, int num, int font_color, int bkg_color);
void printword(int x, int y, int word, int font_color,int bkg_color);
void printchar(int x, int y, int ch, int font_color, int bkg_color);
void putpixel(int x, int y, int color);
void rectangle(int left, int top, int right, int bottom, int color);
void rectfilled(int left, int top, int right, int bottom, int color);
/***************************************************************************/

#endif
