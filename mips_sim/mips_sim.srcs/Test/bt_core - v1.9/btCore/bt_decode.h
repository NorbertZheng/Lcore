#ifndef BT_DECODE_H
#define BT_DECODE_H

#include "bt_reg_macro.h"
#include "bt_x86_type.h"
#include "bt_generate.h"
#include "qs_io.h"

#define _H2H 0
#define _H2L 1
#define _L2H 2
#define _L2L 3

#define DEL_AL (_X86_AX - _X86_AL)
#define DEL_AH (_X86_AX - _X86_AH)

//int _analyseOperands(int *source, int *index, int direction, int isWord, int *rt, int *rd, int *mem, int *imme);

int _decodeMain(char **x86Source, int **mipsTarget);
int _analyseOperands(char **source, int direction, int isWord, int *rt, int *rd, int *mem, int *imm);
int _getByte(int word, int index);
int _getOpcode(int word, int index);
int _getX86Reg(int index, int isWord);
int _getX86RegSeg(int index);
void _adaptIndex(int *source, int *index);
void _regToReg(int type, int rt, int rd, int **target);
int _getX86Data(int isWord, int isSigned, char **source);
void _regImm(int type, int isSigned, int isWord, int rd, char **source, int **target);
void _getMemAddr(int oo, int mmm, char **source, int **target);
void _push(int reg, int **target);
void _push16H(int reg, int **target);
void _pop16H(int reg, int **target);
void _pop(int reg, int **target);
void _pushIP(int cpc, int **target);
void _popIP(int reg, int **target);
int _keyGenerate(int type, int rt, int rd, int **target);
void _shiftEntrance(int type, int isWord, int rd, int imm, int isImm, int **target);
int _keyGenerateShift(int type, int rt, int rd, int imm, int isImm);
int _convertItype(int type);
int _convertAccType(int type);


void _assSet(int sourceReg, int targetReg, int mode, int **target);
void _assMove(int sourceReg, int targetReg, int mode, int **target);
int _findNewReg(int reg1, int reg2);
void _saveX86Flag(int **target);

void _pushRegToMips(int reg, int **target);
void _pushDataToMips(int data, int **target);
void _popRegFromMips(int reg, int **target);
void _saveBranchInfo(int type, int spc, int cpc, int **target);
void _saveBranchInfoReg(int type, int spcReg, int cpc, int **target);

void _jrInterface(int reg, int **target);
void _jalrInterface(int reg, int **target);

void _genSaveX86Registers(int **target);
void _genLoadX86Registers(int **target);

#endif
