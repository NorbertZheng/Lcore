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

static unsigned int time_index;
static unsigned int sys_time_total;
static unsigned int sys_time_hours_x;
static unsigned int sys_time_hours_y;
static unsigned int sys_time_minutes_x;
static unsigned int sys_time_minutes_y;
static unsigned int sys_time_seconds_x;
static unsigned int sys_time_seconds_y;
static unsigned int sys_time_msecs;

struct intr_work info_work;

void timer_handler()
{
	struct list_head *pos;
	struct intr_work *entry;
	
	list_for_each(pos, &(interrupt[time_index].head)) {
		entry = list_entry(pos, struct intr_work, node);
		entry->work();
	}
}

static void echo_delimiter()
{
	unsigned int col;
	
	for (col = 0; col < VGA_MAX_COL; ++col) {
		put_char_ex('_', COLOR_BLACK, COLOR_RED, VGA_ROW_CONSOLE, col, VGA_MAX_ROW);
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
	sys_time_total = 0;
	sys_time_hours_x = 0;
	sys_time_hours_y = 0;
	sys_time_minutes_x = 0;
	sys_time_minutes_y = 0;
	sys_time_seconds_x = 0;
	sys_time_seconds_y = 0;
	sys_time_msecs = 0;

	echo_delimiter();
	echo_welcome();
	echo_version();
	echo_time();
}

void init_time(unsigned int interval)
{
	time_index = highest_set(_INTR_CLOCK);
	
	if (register_handler(timer_handler, time_index))
		return;

	info_work.work = flush_systime;
	INIT_LIST_HEAD(&info_work.node);
	if (register_work(&(info_work.node), time_index))
		return;
	
	set_timer(interval);
	init_systime();

	printk("Setup Timer ok :\n");
	printk("\tregister TIMER's handler at %x\n", timer_handler);
	printk("\tregister first work(%x)\n", &info_work);
	printk("\tset timer %x ms\n", interval);
}

void flush_systime()
{
	++sys_time_total;
	sys_time_msecs += _CLOCK_INTERVAL;
	
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

