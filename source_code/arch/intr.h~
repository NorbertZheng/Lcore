#ifndef _LCORE_INTR_H
#define _LCORE_INTR_H

#include "../kern/kern.h"

/*
 * interrupt
 */
#define _INTR_ENTRY				0x00000000
#define _INTR_FLAG				0x40000000
#define _INTR_MAX				31
#define _INTR_CLOCK				0x00000001
#define _INTR_KEYB				0x00000008
#define _INTR_SPI				0x00000020
#define _INTR_GLOBAL			0x80000000

extern unsigned char _exint_handler;
extern unsigned char _end_ex;
extern struct intr_block interrupt[_INTR_MAX];

extern void init_exint();
extern void do_interrupt(unsigned int status, unsigned int errArg, unsigned int errPc, unsigned int *regs);
extern void enable_intr(unsigned int val);
extern void disable_intr(unsigned int val);
extern unsigned int register_handler(intr_fn handler, unsigned int intr);
extern void unregister_handler(unsigned int intr);
extern unsigned int register_work(struct list_head *work_node, unsigned int intr);
extern void unregister_work(struct list_head *work_node);
extern unsigned int get_timer();
extern void set_timer(unsigned int interval);
extern unsigned int get_ier();
extern void set_ier(unsigned int val);
extern unsigned int get_icr();
extern void clean_icr(unsigned int val);

#endif

