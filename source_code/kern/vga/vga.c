#include "../../tool/tool.h"
#include "../../arch/arch.h"
#include "print.h"
#include "vga.h"

unsigned int cursor_row;
unsigned int cursor_col;
unsigned short *vga_buffer;

static unsigned int *io_vga_ctrl;
static unsigned int *io_vga_buff;
static unsigned int *io_vga_cursor;
static unsigned int *io_vga_flash;

void init_vga(unsigned int buffer)
{
	cursor_row = 0;
	cursor_col = 0;
	vga_buffer = (unsigned short *) buffer;

	io_vga_ctrl = (unsigned int *) _IO_VGA_CTRL;
	io_vga_buff = (unsigned int *) _IO_VGA_BUFF;
	io_vga_cursor = (unsigned int *) _IO_VGA_CURS;
	io_vga_flash = (unsigned int *) _IO_VGA_FLASH;

	clean_screen(VGA_MAX_ROW);
	*io_vga_flash = mkint(0x8000, 1000);		// flash cursor every 1000ms
	*io_vga_buff = buffer;
	*io_vga_ctrl = mkint(0x4000, 1);			// text mode, enable flash cursor

	printk("Setup vga ok:\n");
	printk("\tusing TEXT mode\n");
	printk("\tenable hardware cursor\n");
	printk("\tbuffer start at %x\n", *io_vga_buff);
}

void clean_screen(unsigned int scope)
{
	unsigned int r, c;
	unsigned short *buffer = vga_buffer;
	unsigned short val = mkshort((COLOR_BLACK << 4) | COLOR_BLACK, 0);
	
	for (r = 0; r < scope; r++) {
		for (c = 0; c < VGA_MAX_COL; c++) {
			*buffer = val;
			++buffer;
		}
	}
	set_cursor(0, 0);
}

void set_cursor(unsigned short row, unsigned short col)
{
	cursor_row = row;
	cursor_col = col;
	*io_vga_cursor = mkint(cursor_row, cursor_col);
}

/*
 * scroll up 1 line
 */
void scroll_screen(unsigned int scope)
{
	unsigned int r, c_len;
	
	if (!cursor_row)
		return;
	
	c_len = multiply(sizeof(short), VGA_MAX_COL);
	for (r = 0; r < (scope - 1); ++r) {
		memcpy(vga_buffer + multiply(r, c_len), vga_buffer + multiply(r + 1, c_len), c_len);
	}
	memset(vga_buffer + multiply(r, c_len), 0, c_len);
	set_cursor(cursor_row - 1, cursor_col);
}

/*
 * put a char to the screen
 */
void put_char_ex(unsigned int ch, unsigned int bg, unsigned int fg, unsigned int row, unsigned int col, unsigned int scope)
{
	unsigned int val = mkshort((bg << 4) | fg, ch);
	unsigned short *position;
	
	if (row >= scope)
		row = scope - 1;
	if (col >= VGA_MAX_COL)
		col = VGA_MAX_COL - 1;

	position = vga_buffer + multiply(row, VGA_MAX_COL) + col;
	*position = val;
}

void put_char(unsigned int ch, unsigned int bg, unsigned int fg, unsigned int scope)
{
	unsigned short val = mkshort((bg << 4) | fg, ch);
	unsigned short *position;

	if (ch == '\n') {
		cursor_col = 0;
		++cursor_row;
		if (cursor_row == scope) {
			scroll_screen(scope);
		} else {
			set_cursor(cursor_row, cursor_col);
		}
		return;
	}

	if (ch == '\t') {
		cursor_col += VGA_TAB_LEN;
		if (cursor_col >= VGA_MAX_COL) {
			cursor_col = 0;
			++cursor_row;
		}
		if (cursor_row == scope) {
			scroll_screen(scope);
		} else {
			set_cursor(cursor_row, cursor_col);
		}
		return;
	}

	position = vga_buffer + multiply(cursor_row & 0x0000ffff, VGA_MAX_COL) + cursor_col;
	*position = val;
	++cursor_col;
	if (cursor_col == VGA_MAX_COL) {
		cursor_col = 0;
		++cursor_row;
	}
	if (cursor_row == scope) {
		scroll_screen(scope);
	} else {
		set_cursor(cursor_row, cursor_col);
	}
}
	
