#include "tool.h"

void *memcpy(void *dest, void *src, unsigned int len)
{
	unsigned char *deststr = dest;
	unsigned char *srcstr = src;
	while (len--) {
		*deststr = *srcstr;
		++deststr;
		++srcstr;
	}
	return dest;
}

void *memset(void *dest, unsigned int ch, unsigned int n)
{
	unsigned char *deststr = dest;
	while (n--) {
		*deststr = ch;
		++deststr;
	}
	return dest;
}

