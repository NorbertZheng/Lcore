#ifndef _LCORE_PRINT_H
#define _LCORE_PRINT_H

extern unsigned char *imap;

extern unsigned int printk(char *fmt, ...);
extern unsigned int print_char(char ch);
extern unsigned int print_str(char *s);
extern unsigned int print_binary(unsigned int i);
extern unsigned int print_hex(unsigned int i);

#endif
