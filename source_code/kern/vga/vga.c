#include "../../tool/tool.h"
#include "../../arch/arch.h"
#include "../mm/bootmm.h"
#include "print.h"
#include "vga.h"

struct vga_attr vga;

void init_vga()
{
	unsigned char *area;
	struct vga_attr *vga_pointer = &vga;

	area = bootmm_alloc_pages(VGA_MAX_COL * VGA_MAX_ROW * sizeof(unsigned short), _MM_VGABUFF, 0x10000);
	if (!area)
		die();

	vga_pointer->cursor_row = 0;
	vga_pointer->cursor_col = 0;
	vga_pointer->vga_buffer = (unsigned short *) area;
	vga_pointer->io_vga_ctrl = (unsigned int *) _IO_VGA_CTRL;
	vga_pointer->io_vga_buff = (unsigned int *) _IO_VGA_BUFF;
	vga_pointer->io_vga_cursor = (unsigned int *) _IO_VGA_CURS;
	vga_pointer->io_vga_flash = (unsigned int *) _IO_VGA_FLASH;

	init_lock(&(vga_pointer->vga_lock));
	clean_screen(VGA_MAX_ROW);
	*(vga_pointer->io_vga_flash) = mkint(0x8000, 1000);		// flash cursor every 1000ms
	*(vga_pointer->io_vga_buff) = (unsigned int) area;
	*(vga_pointer->io_vga_ctrl) = mkint(0x4000, 1);			// text mode, enable flash cursor

	printk("Setup vga ok:\n");
	printk("\tusing TEXT mode\n");
	printk("\tenable hardware cursor\n");
	printk("\tbuffer start at %x\n", *(vga_pointer->io_vga_buff));
}

void clean_screen(unsigned int scope)
{
	unsigned int r, c;
	struct vga_attr *vga_pointer = &vga;
	unsigned short *buffer = vga_pointer->vga_buffer;
	unsigned short val = mkshort((COLOR_BLACK << 4) | COLOR_BLACK, 0);
	
	for (r = 0; r < scope; ++r) {
		for (c = 0; c < VGA_MAX_COL; ++c) {
			*buffer = val;
			++buffer;
		}
	}
	set_cursor(0, 0);
}

void set_cursor(unsigned short row, unsigned short col)
{
	struct vga_attr *vga_pointer = &vga;

	vga_pointer->cursor_row = (row & 0x0000ffff);
	vga_pointer->cursor_col = (col & 0x0000ffff);
	*(vga_pointer->io_vga_cursor) = mkint(vga_pointer->cursor_row, vga_pointer->cursor_col);
}

/*
 * scroll up 1 line
 */
void scroll_screen(unsigned int scope)
{
	unsigned int r, c_len;
	struct vga_attr *vga_pointer = &vga;	

	if (!(vga_pointer->cursor_row))
		return;
	
	c_len = multiply(sizeof(unsigned short), VGA_MAX_COL);
	for (r = 0; r < (scope - 1); ++r) {
		memcpy(vga_pointer->vga_buffer + multiply(r, VGA_MAX_COL), vga_pointer->vga_buffer + multiply(r + 1, VGA_MAX_COL), c_len);
	}
	memset(vga_pointer->vga_buffer + multiply(r, VGA_MAX_COL), 0, c_len);
	set_cursor(vga_pointer->cursor_row - 1, vga_pointer->cursor_col);
}

/*
 * put a char to the screen
 */
void put_char_ex(unsigned int ch, unsigned int bg, unsigned int fg, unsigned int row, unsigned int col, unsigned int scope)
{
	unsigned int val = mkshort((bg << 4) | fg, ch);
	unsigned short *position;
	struct vga_attr *vga_pointer = &vga;
	
	if (row >= scope)
		row = scope - 1;
	if (col >= VGA_MAX_COL)
		col = VGA_MAX_COL - 1;

	lockup(&(vga_pointer->vga_lock));
	position = vga_pointer->vga_buffer + multiply(row, VGA_MAX_COL) + col;
	*position = val;
	unlock(&(vga_pointer->vga_lock));
}

void put_char(unsigned int ch, unsigned int bg, unsigned int fg, unsigned int scope)
{
	unsigned short val = mkshort((bg << 4) | fg, ch);
	unsigned short *position;
	struct vga_attr *vga_pointer = &vga;

	lockup(&(vga_pointer->vga_lock));
	if (ch == '\n') {
		vga_pointer->cursor_col = 0;
		++(vga_pointer->cursor_row);
		if (vga_pointer->cursor_row == scope) {
			scroll_screen(scope);
		} else {
			set_cursor(vga_pointer->cursor_row, vga_pointer->cursor_col);
		}
		goto out;
	}

	if (ch == '\t') {
		vga_pointer->cursor_col += VGA_TAB_LEN;
		if (vga_pointer->cursor_col >= VGA_MAX_COL) {
			vga_pointer->cursor_col = 0;
			++(vga_pointer->cursor_row);
		}
		if (vga_pointer->cursor_row == scope) {
			scroll_screen(scope);
		} else {
			set_cursor(vga_pointer->cursor_row, vga_pointer->cursor_col);
		}
		goto out;
	}

	position = vga_pointer->vga_buffer + multiply(vga_pointer->cursor_row & 0x0000ffff, VGA_MAX_COL) + vga_pointer->cursor_col;
	*position = val;
	++(vga_pointer->cursor_col);
	if (vga_pointer->cursor_col == VGA_MAX_COL) {
		vga_pointer->cursor_col = 0;
		++(vga_pointer->cursor_row);
	}
	if (vga_pointer->cursor_row == scope) {
		scroll_screen(scope);
	} else {
		set_cursor(vga_pointer->cursor_row, vga_pointer->cursor_col);
	}
out:
	unlock(&(vga_pointer->vga_lock));
}
	
