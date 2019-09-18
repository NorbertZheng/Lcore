#include "qs_cpu.h"

//////////////////////////////////////// DEVICE
// LED
int *led_base = (int *)0x70000000;

// PS2 KEYBOARD
int *ps2_base = (int *)0x90000000;

// VGA
int *graphics_base = (int *)0x50000000;
// CURSOR
int _cursorX = 0;
int _cursorY = 0;

// RS232 MASTER
int *rs232_base = (int *)0x20000000;
int *rs232_busy = (int *)0x20000004;
// RS232 SLAVE
int *rs232_slave_base = (int *) 0x60000000;
int *rs232_slave_busy = (int *) 0x60000004;

//////////////////////////////////////// FONT RELATED
// FONT LIBRARY BASE
int *font_base = (int *)0x000C12C0;
// FONT RAM BASE
int *text_base = (int *)0x000C0000;


//////////////////////////////////////// INTERRUPT ENTRANCE
int (*_intrEntr)(int) = NULL;
int (*_sysMain)() = NULL;
char ID = 1;
