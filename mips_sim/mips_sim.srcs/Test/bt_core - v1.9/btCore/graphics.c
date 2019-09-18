#include "graphics.h"

/**
 * 图形函数
 * @author 孙龙
 * @date 2012-03-04
 */

/**
 * 原型：int  getpixel(int x, int y);
 * 功能：返回屏幕上指定点的状态
 * 说明：：(x,y)为屏幕上点的坐标，如果点为清除状态返回零，否则返回非零值
 */
int getpixel(int x, int y) {
    int tx, index, rt, wp; 			//index the byte to be drawn, wp : word postion
    int *vgaBase = graphics_base;
    tx = x >> 1; 				// tx 为 （x，y）所在的字
    index = x % 2; 			// index 为（x，y）所在的字中的位置
    wp = tx + (y << 8) + (y << 6);  // 偏移量为一行的字数
    rt = vgaBase[wp];
    switch(index) {
    case 0:
        rt >>= 16;
        break;
    case 1:
        break;
    default:
        rt = 0;
    }
    rt &= 0x0000ffff;
    return rt;
}

/**
 * 原型：void putpixel(int x, int y, int color);
 * 功能：在屏幕的指定位置上画点
 * 说明: (x,y)为屏幕上点坐标，指定颜色 color(5, 5, 6)
 */
void putpixel(int x, int y, int color) {
    int tx, index, temp, wp;
    int *vgaBase = graphics_base;
    tx = x >> 1;
    index = x % 2;
    wp = tx + (y << 8) + (y << 6);
    temp = vgaBase[wp];
    color &= 0xffff;
    switch(index) {
    case 0 :
        color <<= 16;
        temp &= 0x0000ffff;
        break;
    case 1 :
        temp &= 0xffff0000;
        break;
    default:
        temp = 0xffffffff;
        break;
    }
    vgaBase[wp] = color | temp;
}

/**
 * 原型：void rectangle(int left, int top, int right, int bottom, int color);
 * 功能：在屏幕上画一矩形边框
 * 说明: (left,top)指定左上角坐标，(right,bottom)指定右下角坐标，指定边框颜色 color
 */
void rectangle(int left, int top, int right, int bottom, int color) {
    int x, y;
    for (x = left, y = top; x <= right; x++)
        putpixel(x, y, color);
    for (x = left, y = bottom; x <= right; x++)
        putpixel(x, y, color);
    for (x = left, y = top; y <= bottom; y++)
        putpixel(x, y, color);
    for (x = right,y = top; y <= bottom; y++)
        putpixel(x, y, color);

}

/**
 * 原型：void rectangle(int left, int top, int right, int bottom, int color);
 * 功能：在屏幕上画一矩并将其填充
 * 说明: (left,top)指定左上角坐标，(right,bottom)指定右下角坐标，指定填充颜色 color
 */
void rectfilled(int left, int top, int right, int bottom, int color) {
    int x, y;
    for (x = left; x <= right; x++)
        for (y = top; y <= bottom; y++)
            putpixel(x, y, color);
}

/**
 * 原型：void circle(int x, int y, int radius, int color);
 * 功能：画圆框
 * 说明：x 和 y 分别为圆心的横纵坐标，radius 为半径，color 为圆边框的颜色
 */
void circle(int x, int y, int radius, int color) {
    int tx, ty, p;
    tx = 0;
    ty = radius;
    p = 3 - (radius << 1);
    while (tx <= ty) {
        putpixel(x + ty, y - tx, color);		// 0,1a
        putpixel(x + tx, y - ty, color);    // 1,1b
        putpixel(x - tx, y - ty, color);		// 2,2b
        putpixel(x - ty, y - tx, color);		// 3,2a
        putpixel(x - ty, y + tx, color);		// 4,3a
        putpixel(x - tx, y + ty, color);    // 5,3b
        putpixel(x + tx, y + ty, color);    // 6,4b
        putpixel(x + ty, y + tx, color);		// 7,4a
        if (p < 0)
            p = p + (tx << 2) + 6;
        else {
            p = p + ((tx - ty) << 2) + 10;
            ty -= 1;
        }
        tx += 1;
    }
}

/**
 * 原型：void circlefilled(int x, int y, int radius, int color)
 * 功能：画圆，并填充
 * 说明：x 和 y 分别为圆心的横纵坐标，radius 为半径，color 为圆边框以及填充的的颜色
 * 			 该函数还有待改进
 */
void circlefilled(int x, int y, int radius, int color) {
    int i;
    for(i = 1; i < radius; i++)
        circle(x, y, i, color);
}

/**
 * 原型：void line(int x1,int y1,int x2,int y2, int color);
 * 功能：在屏幕上画直线
 * 说明: (x1,y1)为起点坐标，(x2,y2)为终点坐标
 */
void line(int x1,int y1,int x2,int y2, int color) {
    int dx, dy, x, y, p, const1, const2, inc, tmp;
    dx = x2 - x1;
    dy = y2 - y1;
    if ((dx > 0 && dy > 0) || (dx < 0 && dy < 0) || (dx == 0) || (dy == 0))
        inc = 1;
    else
        inc = -1;
    if (abs(dx) > abs(dy)) {
        if (dx < 0) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
            tmp = y1;
            y1 = y2;
            y2 = tmp;
            dx = -dx;
            dy = -dy;
        }
        p = (dy << 1) - dx;
        const1 = dy << 1;
        const2 = (dy - dx) << 1;
        x = x1;
        y = y1;
        putpixel(x, y, color);
        while (x < x2) {
            x++;
            if (p < 0)
                p += const1;
            else {
                y += inc;
                p += const2;
            }
            putpixel(x, y, color);
        }
    } else {
        if (dy < 0) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
            tmp = y1;
            y1 = y2;
            y2 = tmp;
            dx = -dx;
            dy = -dy;
        }
        p = (dx << 1)-dy;
        const1 = dx << 1;
        const2 = (dx - dy) << 1;
        x = x1;
        y = y1;
        putpixel(x, y, color);
        while (y < y2) {
            y++;
            if (p < 0)
                p += const1;
            else {
                x += inc;
                p += const2;
            }
            putpixel(x, y, color);
        }
    }
}

/**
 * 原型：void printnum(int x, int y, int num, int color);
 * 功能：打印给定的数字，调试时使用
 * 说明: (x,y)为起点坐标，num 为要打印的数字，指定字体颜色 font_color 背景色bkg_color
 *       目前该函数只支持 0=<num<1000,可根据需要自己编写
 */
void printnum(int x, int y, int num, int font_color, int bkg_color) {
    int i;
    int tw;

    tw=num;
    i=0;
    while(tw>=100) {
        tw-=100;
        i++;
    }
    if(i!=0)
        printchar(x,y,i+'0',font_color,bkg_color);
    i=0;
    while(tw>=10) {
        tw-=10;
        i++;
    }
    if(num>99) {
        printchar(x+16,y,i+'0',font_color,bkg_color);
    } else {
        if(i!=0)
            printchar(x+16,y,i+'0',font_color,bkg_color);
    }
    printchar(x+32,y,tw+'0',font_color,bkg_color);
}

/**
 * 原型：void printword(int x, int y, int word, int color);
 * 功能：打印给定的字，调试时使用
 * 说明: (x,y)为起点坐标，word 为要打印的字即4个字节，先打印高字节，指定颜色 color
 */
void printword(int x, int y, int word, int font_color,int bkg_color) {
    int i;
    int j,tw;
    for (i=28,j=0; i>=0; i-=4,j+=8) {
        tw = word >> i;
        tw &= 0x0f;
        if (tw>9)
            tw = tw+'a'-10;
        else
            tw += '0';
        printchar(x+j,y,tw,font_color,bkg_color);
    }
}

/**
 * 原型：void printchar(int x, int y, int ch, int color);
 * 功能：打印给定的字符
 * 说明: (x,y)为起点坐标，ch 为要打印的字符的ASCII码，
 *       指定字体颜色 font_color,背景颜色
 */
void printchar(int x, int y, int ch, int font_color, int bkg_color) {
    int i,j,k=0,sc,tx; //sc 右移位数
    int* fontbase = font_base;
    fontbase = fontbase+(ch<<2);
    k=0;
    sc=0;
    tx=0;
    for(i=0; i<4; i++)
        for(j=0; j<32; j++) {
            sc++;
            if(sc==8) {
                y++;
                sc=0;
            }
            if(((fontbase[i]>>(31-j))&0x1) ==1)
                putpixel(tx+x,y,font_color);
            else
                putpixel(tx+x,y,bkg_color);
            if(tx==7)
                tx=0;
            else
                tx++;
        }
}



