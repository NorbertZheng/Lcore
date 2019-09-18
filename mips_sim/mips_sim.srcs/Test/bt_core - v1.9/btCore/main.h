#ifndef MAIN_H
#define MAIN_H

#include "global.h"
#include "my_arg.h"
#include "qs_io.h"
#include "graphics.h"
#include "bt_main.h"
#include "qs_debug.h"
#include "bt_jlut.h"

#define COMMAND_BUFFER_SIZE 20
int command_buffer[COMMAND_BUFFER_SIZE];
int user_func_name[20];
char responseBuffer[2400];

void backspace();
void welcome();
void clear_part(int, int);
void wait_enter();
void putcharWithColor(int ascii);
// cursor operations in graphics mode
void reset_cursor_G();
// cursor operations in text mode
void reset_cursor_T();
void set_cursor_T(int, int);
void add_cursor_T();
void newline_T();
void newline_T_rs232();
void print_prompt();
// command
int get_command();
void send_command();
void process_command();
int process_command1();
void process_command_slave();
int check_system();
void debug();

void draw_progress_bar();
void setCursorForNonAscii(int y, int x);
int shellMain();
void _systemMain();
void _initialSystem();
void sendBootCommand();

int (*user_func)();

void testFlag();
int isExe(int type);
void print_prompt_T();
int waitSignal();
void process_command_master();
void send_command_master();
void process_command_master1();
int canRunType(int type);
int isToSlave(int type, int id);

#endif
