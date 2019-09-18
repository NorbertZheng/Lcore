#ifndef BT_MAIN_H
#define BT_MAIN_H

#include "qs_io.h"
#include "qs_debug.h"
#include "bt_x86_opcode_macro.h"
#include "bt_reg_macro.h"
#include "bt_decode.h"
#include "bt_generate.h"
#include "bt_branch.h"


#define _MIPS_TYPE 0
#define _X86_TYPE 1
#define REGISTER_NUM 17
//extern int _x86Register[REGISTER_NUM];
extern unsigned int *_x86Register;
extern int _x86Terminal;
extern int _x86Segment;

void btMain();
void _saveRegisters(int type);
void _saveMipsRegister();
void _saveX86Register();
void _loadRegisters(int type);
void _contextSwitch(int type);
void _initialX86Registers();
void printMips();
void printX86();
void _contextSwitchTest();
int runX86();
void _addExitCode(char *baseAddr, int *length);
void _branchHandleAsm();
int _branchHandlePre(int flag, int type, int spc, int cpc);

void _testBranch();

void _saveX86Registers();
void _saveMipsRegisters();
void _loadX86Registers();
void _loadMipsRegisters();

void _saveMipsSp();
void _loadMipsSp();

extern int _mipsSp;

#endif
