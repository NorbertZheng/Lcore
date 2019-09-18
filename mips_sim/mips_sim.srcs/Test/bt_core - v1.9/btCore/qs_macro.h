#ifndef MACRO_H
#define MACRO_H

// color
#define COLOR_SIZE 16
#define COLOR_WHITE 0xFFFF
#define COLOR_BLACK 0x0000
#define COLOR_RED   0xF800
#define COLOR_GREEN 0x07E0
#define COLOR_BLUE  0x001F
#define COLOR_BROWN 0xFC00
#define COLOR_PURPLE 0x9009
#define COLOR_YELLOW 0xFFE0
#define COLOR_PINK	0xF81F
#define COLOR_BKG COLOR_BLACK
#define COLOR_FRG COLOR_WHITE

// vga
#define RESOLUTION_X 640
#define RESOLUTION_Y 480
#define VRAM_SIZE (RESOLUTION_X * RESOLUTION_Y) / (32 / COLOR_SIZE)	// the size of vram: word

// font
#define ASCII_8_16 1

#define ENTER 10
#define BKSP 8

#define NULL 0
#define EOF -1

#endif
