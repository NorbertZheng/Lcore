#include "global.h"

int _cursorX = 0;
int _cursorY = 0;

int *led_base = (int *)0x70000000;
int *ps2_base = (int *)0x90000000;
int *rs232_base = (int *)0x20000000;
int *rs232_busy = (int *)0x20000004;
int *rs232_slave_base = (int *) 0x60000000;
int *rs232_slave_busy = (int *) 0x60000004;
int *graphics_base = (int *)0x50000000;
int *font_base = (int *)0x000C12C0;
int *text_base = (int *)0x000C0000;
int (*_intrEntr)(int) = NULL;
int (*_sysMain)() = NULL;
char ID = 1;
