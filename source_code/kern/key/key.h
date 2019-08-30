#ifndef _LCORE_KEY_H
#define _LCORE_KEY_H

#define KEYBUFF_SIZE			1024

extern void init_keyboard();
extern void keyb_handler(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc);
extern void get_scancode(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc);
extern void get_ch(unsigned char *buf);

#endif

