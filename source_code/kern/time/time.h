#ifndef _LCORE_TIME_H
#define _LCORE_TIME_H

#define _CLOCK_INTERVAL			500

extern unsigned int time_index;
extern struct intr_work info_work;

extern void timer_handler(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc);
extern void init_time(unsigned int interval);
extern void flush_systime(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc);

#endif

