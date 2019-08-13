#ifndef _LCORE_TIME_H
#define _LCORE_TIME_H

#define _CLOCK_INTERVAL			500

extern struct intr_work info_work;

extern void time_handler();
extern void init_time(unsigned int interval);
extern void flush_systime();

#endif

