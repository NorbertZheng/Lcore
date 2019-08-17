#ifndef _LCORE_TOOL_H
#define _LCORE_TOOL_H

#define COLOR_BLACK			0
#define COLOR_RED			4
#define COLOR_GREEN			2
#define COLOR_YELLOW		6
#define COLOR_BLUE			1
#define COLOR_MAGENTA		5
#define COLOR_CYAN			3
#define COLOR_WHITE			7

typedef char * va_list;
#define _INTSIZEOF(n)		((sizeof(n) + sizeof(unsigned int) - 1) & ~(sizeof(unsigned int) - 1))
#define va_start(ap, v)		(ap = (va_list) &v + _INTSIZEOF(v))
#define va_arg(ap, t)		(*(t *)((ap += _INTSIZEOF(t)) - _INTSIZEOF(t)))
#define va_end(ap)			(ap = (va_list) 0)

extern void *memcpy(void *dest, void *src, unsigned int len);
extern void *memset(void *dest, unsigned int ch, unsigned int n);
extern unsigned int mkint(unsigned short high, unsigned short low);
extern unsigned short mkshort(char high, char low);
extern unsigned int multiply(unsigned int a, unsigned int b);
extern unsigned int division(unsigned int n, unsigned int div);
extern unsigned int highest_set(unsigned int n);
extern unsigned int lowest_set(unsigned int n);
extern unsigned int is_bound(unsigned int val, unsigned int bound);

#endif

