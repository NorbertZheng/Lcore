#ifndef _LCORE_VGA_H
#define _LCORE_VGA_H

#include "../lock/lock.h"

#define VGA_MAX_ROW			30
#define VGA_MAX_COL			80

#define VGA_TAB_LEN			4

/*
 * top of the screen(28lines) is used to log input/output on the console
 * bottom of the screen(2lines) is used to display system realtime info
 */
#define VGA_ROW_SYSTEM		2
#define VGA_ROW_CONSOLE		(VGA_MAX_ROW - VGA_ROW_SYSTEM)

struct vga_attr {
	unsigned int cursor_row;
	unsigned int cursor_col;
	unsigned short *vga_buffer;
	unsigned int *io_vga_ctrl;
	unsigned int *io_vga_buff;
	unsigned int *io_vga_cursor;
	unsigned int *io_vga_flash;
	struct lock_t vga_lock;
};

extern struct vga_attr vga;
extern void init_vga();
extern void clean_screen(unsigned int scope);
extern void set_cursor(unsigned short row, unsigned short col);
extern void scroll_screen(unsigned int scope);
extern void put_char_ex(unsigned int ch, unsigned int bg, unsigned int fg, unsigned int row, unsigned int col, unsigned int scope);
extern void put_char(unsigned int ch, unsigned int bg, unsigned int fg, unsigned int scope);

#endif

