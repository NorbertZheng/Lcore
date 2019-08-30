#include "../../arch/arch.h"
#include "../../arch/intr.h"
#include "../../tool/tool.h"
#include "../list.h"
#include "../kern.h"
#include "../vga/vga.h"
#include "../vga/print.h"
#include "time.h"

#define WELCOME_COL			0
#define VERSION_COL			35
#define TIME_COL			72

unsigned int time_index;
static unsigned int sys_time_total;
static unsigned int sys_time_hours_x;
static unsigned int sys_time_hours_y;
static unsigned int sys_time_minutes_x;
static unsigned int sys_time_minutes_y;
static unsigned int sys_time_seconds_x;
static unsigned int sys_time_seconds_y;
static unsigned int sys_time_msecs;

static int ver_wel_end;
static short *flush_pos;
static char flush_msg[VGA_MAX_COL];
static short flush_msg_len;
static int flush_direct;	// 0 to right; 1 to left

struct intr_work info_work;

void timer_handler(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	struct list_head *pos;
	struct intr_work *entry;
	
	list_for_each(pos, &(interrupt[time_index].head)) {
		entry = list_entry(pos, struct intr_work, node);
		entry->work(regs, status, errArg, errPc);
	}
}

static void echo_delimiter()
{
	unsigned int col;
	
	for (col = 0; col < VGA_MAX_COL; ++col) {
		put_char_ex('_', COLOR_BLACK, COLOR_RED, VGA_ROW_CONSOLE, col, VGA_MAX_ROW);
	}
}

static void build_welcome_version()
{
	unsigned char *wc = "Welcome! Lcore-v1.0!";
	unsigned int col;

	flush_msg_len = 0;
	for (col = 0; wc[col] != 0; ++col) {
		flush_msg[col] = wc[col];
		++flush_msg_len;
	}
	flush_msg[col] = 0;
}

static void flush_ver_wel()
{
	short *tmp = flush_pos;
	int col, index;

	for (col = 0; col <= (TIME_COL-3); ++col)
		tmp[col] = 0;

	if (!flush_direct) {
		if (ver_wel_end <= (TIME_COL-3)) {
			col = ver_wel_end;
			index = flush_msg_len-1;
		} else {
			col = TIME_COL-3;
			index = flush_msg_len-(ver_wel_end-(TIME_COL-3))-1;
		}
		++ver_wel_end;
		if ((ver_wel_end-(TIME_COL-3)) == flush_msg_len)
			flush_direct = 1;
	} else {
		if (ver_wel_end > (TIME_COL-3)) {
			col = TIME_COL-3;
			index = flush_msg_len-(ver_wel_end-(TIME_COL-3))-1;
		} else {
			col = ver_wel_end;
			index = flush_msg_len-1;
		}
		--ver_wel_end;
		if (ver_wel_end == -1)
			flush_direct = 0;
	}

	for (; (col >= 0) && (index >= 0); ) {
		tmp[col--] = (((COLOR_BLACK<<4)|COLOR_RED)<<8)|flush_msg[index--];
	}

}

static void echo_welcome()
{
	unsigned char *wc = "Welcome! Guys~";
	unsigned int col;

	for (col = WELCOME_COL; wc[col - WELCOME_COL]; ++col) {
		put_char_ex(wc[col - WELCOME_COL], COLOR_BLACK, COLOR_RED, VGA_ROW_CONSOLE + 1, col, VGA_MAX_ROW);
	}
}

static void echo_version()
{
	unsigned char *v = "Lcore V1.0";
	unsigned int col;

	for (col = VERSION_COL; v[col - VERSION_COL]; ++col) {
		put_char_ex(v[col - VERSION_COL], COLOR_BLACK, COLOR_RED, VGA_ROW_CONSOLE + 1, col, VGA_MAX_ROW);
	}
}

static void echo_time()
{
	unsigned char t[16];
	unsigned int col;
	t[0] = imap[sys_time_hours_x];
	t[1] = imap[sys_time_hours_y];
	t[2] = ':';
	t[3] = imap[sys_time_minutes_x];
	t[4] = imap[sys_time_minutes_y];
	t[5] = ':';
	t[6] = imap[sys_time_seconds_x];
	t[7] = imap[sys_time_seconds_y];
	t[8] = 0;

	for (col = TIME_COL; t[col - TIME_COL]; ++col) {
		put_char_ex(t[col - TIME_COL], COLOR_BLACK, COLOR_RED, VGA_ROW_CONSOLE + 1, col, VGA_MAX_ROW);
	}
}

void init_systime()
{
	struct vga_attr *vga_pointer = &vga;

	sys_time_total = 0;
	sys_time_hours_x = 0;
	sys_time_hours_y = 0;
	sys_time_minutes_x = 0;
	sys_time_minutes_y = 0;
	sys_time_seconds_x = 0;
	sys_time_seconds_y = 0;
	sys_time_msecs = 0;

	ver_wel_end = 0;
	flush_direct = 0;
	flush_pos = vga_pointer->vga_buffer + multiply(VGA_ROW_CONSOLE + 1, VGA_MAX_COL);

	echo_delimiter();
	build_welcome_version();
	// echo_welcome();
	// echo_version();
	echo_time();
}

void init_time(unsigned int interval)
{
	struct intr_work *info_work_pointer = &info_work;

	time_index = highest_set(_INTR_CLOCK);
	
	if (register_handler(timer_handler, time_index))
		return;

	info_work_pointer->work = flush_systime;
	INIT_LIST_HEAD(&(info_work_pointer->node));
	if (register_work(&(info_work_pointer->node), time_index))
		return;
	
	set_timer(interval);
	init_systime();

	printk("Setup Timer ok :\n");
	printk("\tregister TIMER's handler at %x\n", timer_handler);
	printk("\tregister first work(%x)\n", &info_work);
	printk("\tset timer %x ms\n", interval);
}

void flush_systime(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	++sys_time_total;
	sys_time_msecs += _CLOCK_INTERVAL;

	flush_ver_wel();
	
	while (sys_time_msecs >= 1000) {
		++sys_time_seconds_y;
		if (sys_time_seconds_y == 10) {
			sys_time_seconds_y = 0;
			++sys_time_seconds_x;
			if (sys_time_seconds_x == 6) {
				sys_time_seconds_x = 0;
				++sys_time_minutes_y;
				if (sys_time_minutes_y == 10) {
					sys_time_minutes_y = 0;
					++sys_time_minutes_x;
					if (sys_time_minutes_x == 6) {
						sys_time_minutes_x = 0;
						++sys_time_hours_y;
						if (sys_time_hours_y == 10) {
							sys_time_hours_y = 0;
							++sys_time_hours_x;
						}
					}
				}
			}
		}
		sys_time_msecs -= 1000;
		echo_time();
	}
}

