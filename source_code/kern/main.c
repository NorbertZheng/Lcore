#include "../lib/ulib.h"

extern void pause();
extern unsigned int fork();
extern unsigned int printf(unsigned char *fmt, ...);

unsigned int main()
{
	unsigned int r = fork();
	unsigned int s;
	
	printf("%x", r);

	if (-1 == r) {
		printf("failed!\n");
	} else if (r) {
		// printf("Hello\n");
		while (1) {
			printf("0");
		}
	} else {
		s = fork();
		// printf("%x", s);
		if (s > 0) {
			while (1) {
				printf("1");
			}
		} else if (s == -1) {
			printf("error!\n");
		} else {
			while (1) {
				printf("2");
			}
		}
	}
	pause();
}

void pause()
{
	while (1);
}

