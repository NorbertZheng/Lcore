#ifndef QS_CPU_H
#define QS_CPU_H

#include "qs_config.h"
#include "qs_macro.h"

#define _IS_M2M 0
#define _IS_MASTER 0 // 在IS_M2M为1的情况下才有效

extern int _cursorX;
extern int _cursorY;

extern int *led_base;
extern int *ps2_base;
extern int *rs232_base;
extern int *rs232_busy;
extern int *rs232_slave_base;
extern int *rs232_slave_busy;
extern int *graphics_base;
extern int *font_base;
extern int *text_base;
extern int (*_intrEntr)(int);
extern int (*_sysMain)();
extern char ID;

#endif // QS_CPU_H

