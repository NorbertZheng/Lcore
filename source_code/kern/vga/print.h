#ifndef _LCORE_PRINT_H
#define _LCORE_PRINT_H

#define PUT_CH(ch, bg, fg)			put_char(ch, bg, fg, VGA_ROW_CONSOLE)

extern unsigned char *imap;
extern unsigned int printk(unsigned char *fmt, ...);
extern unsigned int print_char(unsigned char ch);
extern unsigned int print_str(unsigned char *s);
extern unsigned int print_binary(unsigned int i);
extern unsigned int print_hex(unsigned int i);

#endif
