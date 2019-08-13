#include "../../tool/tool.h"
#include "vga.h"
#include "print.h"

static char *imap = "0123456789ABCDEF";

unsigned int printk(char *fmt, ...)
{
	unsigned int argint;
	char argch;
	char *argstr;
	char *pfmt;
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
					index += print_char(argch);
					break;
				case 's':
				case 'S':
					argstr = va_arg(vp, char *);
					index += print_str(argstr);
					break;
				case 'b':
				case 'B':
					argint = va_arg(vp, unsigned int);
					index += print_binary(argint);
					break;
				case 'x':
				case 'X':
					argint = va_arg(vp, unsigned int);
					index += print_hex(argint);
					break;
				case '%':
					index += print_char('%');
					break;
				default:
					break;
			}
			++pfmt;
		} else {
			index += print_char(*pfmt);
			++pfmt;
		}
	}
	va_end(vp);
	return index;
}

unsigned int print_char(char ch)
{
	put_char(ch, COLOR_BLACK, COLOR_WHITE, VGA_ROW_CONSOLE);
	return 1;
}

unsigned int print_str(char *s)
{
	unsigned int res = 0;
	
	while (*s) {
		print_char(*s);
		++s;
		++res;
	}
	return res;
}

unsigned int print_binary(unsigned int i)
{
	unsigned int res = 0;

	if (!i) {
		print_str("0b0");
		return 1;
	}
	res = print_binary(i >> 1) + 1;
	print_char((char) (imap[i % 2]));
	return res;
}

unsigned int print_hex(unsigned int i)
{
	unsigned int res = 0;
	
	if (!i) {
		print_str("0x0");
		return 1;
	}
	res = print_hex(i >> 4) + 1;
	print_char((char) (imap[i % 16]));
	return res;
}

