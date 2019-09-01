#include "ulib.h"

/*
 * api
 */
unsigned int fork()
{
	unsigned int val;

	enter_syscall0(SYS_FORK);
	asm volatile(
		"move	%0, $v0"
		:"=r"(val)
	);
	// printf("fork : ok\n");

	return val;
}

void api_putch(unsigned int ch, unsigned int bg, unsigned int fg)
{
	enter_syscall3(ch, bg, fg, SYS_PUTCH);
}

/*
 * printf function
 */
unsigned char api_imap[16] = "0123456789ABCDEF";

unsigned int api_print_char(unsigned char ch)
{
	api_putch(ch, COLOR_BLACK, COLOR_WHITE);
	return 1;
}

unsigned int api_print_str(unsigned char *s)
{
	unsigned int res = 0;

	while (*s) {
		api_print_char(*s);
		++res;
		++s;
	}

	return res;
}

unsigned int api_print_binary(unsigned int i)
{
	unsigned int res = 0;

	if (!i) {
		api_print_str("0b0");
		return 1;
	}
	res = api_print_binary(i >> 1) + 1;
	api_print_char((unsigned char) (api_imap[i % 2]));
	return res;
}

unsigned int api_print_hex(unsigned int i)
{
	unsigned int res = 0;
	
	if (!i) {
		api_print_str("0x0");
		return 1;
	}
	res = api_print_hex(i >> 4) + 1;
	api_print_char((unsigned char) (api_imap[i % 16]));
	return res;
}

unsigned int printf(unsigned char *fmt, ...)
{
	unsigned int argint;
	unsigned char argch;
	unsigned char *argstr;
	unsigned char *pfmt;
	unsigned int index;
	va_list vp;

	va_start(vp, fmt);
	pfmt = fmt;
	index = 0;
	while (*pfmt) {
		if (*pfmt == '%') {
			switch (*(++pfmt)) {
				case 'c':
				case 'C':
					argch = va_arg(vp, unsigned int);
					index += api_print_char(argch);
					break;
				case 's':
				case 'S':
					argstr = va_arg(vp, unsigned char *);
					index += api_print_str(argstr);
					break;
				case 'b':
				case 'B':
					argint = va_arg(vp, unsigned int);
					index += api_print_binary(argint);
					break;
				case 'x':
				case 'X':
					argint = va_arg(vp, unsigned int);
					index += api_print_hex(argint);
					break;
				case '%':
					index += api_print_char('%');
					break;
				default:
					break;
			}
			++pfmt;
		} else {
			index += api_print_char(*pfmt);
			++pfmt;
		}
	}
	va_end(vp);
	return index;
}

