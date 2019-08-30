#ifndef _LCORE_USERLIB_H
#define _LCORE_USERLIB_H

#define COLOR_BLACK			0
#define COLOR_RED			4
#define COLOR_GREEN			2
#define COLOR_YELLOW		6
#define COLOR_BLUE			1
#define COLOR_MAGENTA		5
#define COLOR_CYAN			3
#define COLOR_WHITE			7
#define NULL				0

typedef unsigned char * va_list;
#define _INTSIZEOF(n)		((sizeof(n) + sizeof(unsigned int) - 1) & ~(sizeof(unsigned int) - 1))
#define va_start(ap, v)		(ap = (va_list) &v + _INTSIZEOF(v))
#define va_arg(ap, t)		(*(t *) ((ap += _INTSIZEOF(t)) - _INTSIZEOF(t)))
#define va_end(ap)			(ap = (va_list) 0)

#define SYS_FORK			1
#define SYS_PUTCH			2

extern void enter_syscall3(unsigned int arg1, unsigned int arg2, unsigned int arg3, unsigned int no);
extern void enter_syscall2(unsigned int arg1, unsigned int arg2, unsigned int no);
extern void enter_syscall1(unsigned int arg1, unsigned int no);
extern void enter_syscall0(unsigned int no);

extern void api_putch(unsigned int ch, unsigned int bg, unsigned int fg);
extern unsigned int fork();
extern unsigned int printf(unsigned char *fmt, ...);

#endif

