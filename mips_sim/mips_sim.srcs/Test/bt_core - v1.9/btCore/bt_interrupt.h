#ifndef BT_INTERRUPT_H
#define BT_INTERRUPT_H

#include "bt_reg_macro.h"
#include "qs_io.h"
#include "graphics.h"
#include "bt_main.h"

#define _DOS_VECTOR   0x21
#define _BIOS_VECTOR  0x10
#define _EXIT_VECTOR  0x22

int _interruptMain(int intVector);

void _dosInterruptMain();
void _biosInterruptMain();
//static void exit2Dos();
void _putPixelQuick(int x, int y, int color);
int _getBiosColor(int al);

#endif
