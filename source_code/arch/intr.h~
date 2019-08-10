#ifndef _LCORE_INTR_H
#define _LCORE_INTR_H

/*
 * interrupt
 */
#define _INTR_ENRTY				0x00000000

#define _INTR_CLOCK				0x80000001
#define _INTR_KEYB				0x80000008
#define _INTR_SPI				0x80000020
#define _INTR_GLOBAL			0x80000029

extern unsigned int _exint_handler;
extern unsigned int _end_ex;

extern void init_exint();

extern void enable_global();
extern void disable_global();
extern void enable_intr();
extern void disable_intr();

#endif

