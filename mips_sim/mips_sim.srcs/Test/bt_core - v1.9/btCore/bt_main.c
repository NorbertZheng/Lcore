#include "bt_main.h"

/**
 * x86翻译模块
 * @author 卢忠勇
 * @date 2012-03-05
 */

int _mipsSp = 0xC0000;

int *_x86BaseAddr = (int *) 0xF000;
const int _x86Limit = 0x10000; // 64K, one segment
int _x86Length = 0;
int *_mipsBaseAddr = 0;

//int _x86Register[REGISTER_NUM];
unsigned int *_x86Register = (unsigned int *) 0xE0000080;
int _x86Flag;
int _mipsRegister[REGISTER_NUM];
int *_registerBase;
void (*saveRegister)();

void (*mipsBlock)();

int _x86Terminal = 0;

int _x86Segment = 0x20000;

#define DEBUG_MODE 0

/**
 * x86翻译主函数
 * @param baseAddr x86程序基址
 * @param length x86程序长度
 */
void btMain(int *baseAddr, int length) {
    char *source;
    char *sourceBackup;
    int *target;

    int runTarget;
    int targetTemp;
//    int i;

    _addExitCode((char *) baseAddr, &length);

//    _testBranch();
//    getchar();

    _jlut_init();
    _x86Terminal = 0;

    _x86BaseAddr = baseAddr;
    _x86Length = length;
    if (_x86Length > _x86Limit) {
        printString("Too large. Limited: 64K.\n");
        return;
    }

    if (DEBUG_MODE) {
        printString("In btMain() now.\n");
        getchar();
    }

    _mipsBaseAddr = _x86BaseAddr + _x86Limit / 4;

    source = (char *)_x86BaseAddr;
    target = _mipsBaseAddr;

    _initialX86Registers();

	setCursor(640 - 11 * 8, 16 * 3);
//	printf("X86 APP");
	putcharWithFore('X', COLOR_RED);
	putcharWithFore('8', COLOR_RED);
	putcharWithFore('6', COLOR_RED);
	putcharWithFore(' ', COLOR_RED);
	putcharWithFore('A', COLOR_RED);
	putcharWithFore('P', COLOR_RED);
	putcharWithFore('P', COLOR_RED);

    while (1) {
        if (_x86Terminal) {
            break;
        }

        // 翻译块要块对齐（末4位）
        targetTemp = (int) target;
        if ((targetTemp & 0xf) != 0 ) { // 不对齐
            targetTemp &= 0xfffffff0;
            targetTemp += 0x10;
            target = (int *) targetTemp;
        }
        _jlut_insert((int)source, (int)target);

        sourceBackup = source;
        runTarget = (int)target;

        if (DEBUG_MODE) {
            printf("Decode Start: %d %d\n", (int)source, (int)target);
            getchar();
        }
        _decodeMain(&source, &target);

        mipsBlock = (void *)runTarget;
        while (1) {
//            asm("cache 0, 0");
//            asm("add $zero, $zero, $zero");

            runTarget = runX86();
            if (DEBUG_MODE) {
                printX86();
                printf("RUN: %d\n", runTarget);
                getchar();
            }
            if (runTarget < 0 || _x86Terminal == 1) {
                break;
            }
            mipsBlock = (void *)runTarget;
        }
        source = (char *) (runTarget & 0x7fffffff);
        if (_jlut_index == _JLUT_ENTRY_NUM - 1) { // full
        	_jlut_init();
        	asm("cache 0, 0");
        	target = _mipsBaseAddr;
//        	printf("addr: %d\n", (int)target);
//        	getchar();
        }
//        printf("MIB: %d %d\n", (int)source, (int)mipsBlock);
//        getchar();
    }
    printf("EXIT\n");
    getchar();

    clear();
    printX86();

    getchar();
    clear();
}

/**
 * not finished
 */
int runX86() {
    int (*_branchHandlePreP)(); // 函数指针

    _contextSwitch(_X86_TYPE);
    (*mipsBlock)();
    _contextSwitch(_MIPS_TYPE);

    asm("lw $a0, 0($sp)"); // cpc
    asm("lw $a1, 4($sp)"); // spc
    asm("lw $a2, 8($sp)"); // type
    asm("lw $a3, 12($sp)"); // flag
    asm("addi $sp, $sp, 16");
    _branchHandlePreP = _branchHandlePre;
    return (*_branchHandlePreP)();
}


/****************************** context switch ******************************/

/**
 * 保存寄存器组,13个,s0-s7,t4-t9
 * @param type 0:保存mips寄存器组,other:保存x86寄存器组
 */
void _saveRegisters(int type) {
//    // $a1: base address
//    asm("lui $a1, 0xE000");
//    if (type == _X86_TYPE) {
//         save x86 registers
//        asm("sw $s0, 128($a1)");
//        asm("sw $s1, 132($a1)");
//        asm("sw $s2, 136($a1)");
//        asm("sw $s3, 140($a1)");
//        asm("sw $s4, 144($a1)");
//        asm("sw $s5, 148($a1)");
//        asm("sw $s6, 152($a1)");
//        asm("sw $s7, 156($a1)");
//        asm("sw $t4, 160($a1)");
//        asm("sw $t5, 164($a1)");
//        asm("sw $t6, 168($a1)");
//        asm("sw $t7, 172($a1)");
//        asm("sw $t8, 176($a1)");
//        asm("sw $t9, 180($a1)");
//        asm("sw $t0, 184($a1)");
//        asm("sw $t1, 188($a1)");
//        asm("sw $t2, 192($a1)");
//    } else {
//         save mips registers
//        asm("sw $s0, 0($a1)");
//        asm("sw $s1, 4($a1)");
//        asm("sw $s2, 8($a1)");
//        asm("sw $s3, 12($a1)");
//        asm("sw $s4, 16($a1)");
//        asm("sw $s5, 20($a1)");
//        asm("sw $s6, 24($a1)");
//        asm("sw $s7, 28($a1)");
//        asm("sw $t4, 32($a1)");
//        asm("sw $t5, 36($a1)");
//        asm("sw $t6, 40($a1)");
//        asm("sw $t7, 44($a1)");
//        asm("sw $t8, 48($a1)");
//        asm("sw $t9, 52($a1)");
//        asm("sw $t0, 56($a1)");
//        asm("sw $t1, 60($a1)");
//        asm("sw $t2, 64($a1)");
//    }

    if (type == _MIPS_TYPE) {
        saveRegister = _saveMipsRegister;
    } else {
        saveRegister = _saveX86Register;
    }

    asm("add $a0, $s0, $zero");
    asm("addi $a1, $zero, 0");
    (*saveRegister)();
    asm("add $a0, $s1, $zero");
    asm("addi $a1, $zero, 1");
    (*saveRegister)();
    asm("add $a0, $s2, $zero");
    asm("addi $a1, $zero, 2");
    (*saveRegister)();
    asm("add $a0, $s3, $zero");
    asm("addi $a1, $zero, 3");
    (*saveRegister)();
    asm("add $a0, $s4, $zero");
    asm("addi $a1, $zero, 4");
    (*saveRegister)();
    asm("add $a0, $s5, $zero");
    asm("addi $a1, $zero, 5");
    (*saveRegister)();
    asm("add $a0, $s6, $zero");
    asm("addi $a1, $zero, 6");
    (*saveRegister)();
    asm("add $a0, $s7, $zero");
    asm("addi $a1, $zero, 7");
    (*saveRegister)();
    asm("add $a0, $t4, $zero");
    asm("addi $a1, $zero, 8");
    (*saveRegister)();
    asm("add $a0, $t5, $zero");
    asm("addi $a1, $zero, 9");
    (*saveRegister)();
    asm("add $a0, $t6, $zero");
    asm("addi $a1, $zero, 10");
    (*saveRegister)();
    asm("add $a0, $t7, $zero");
    asm("addi $a1, $zero, 11");
    (*saveRegister)();
    asm("add $a0, $t8, $zero");
    asm("addi $a1, $zero, 12");
    (*saveRegister)();
    asm("add $a0, $t9, $zero");
    asm("addi $a1, $zero, 13");
    (*saveRegister)();
    asm("add $a0, $t0, $zero");
    asm("addi $a1, $zero, 14");
    (*saveRegister)();
    asm("add $a0, $t1, $zero");
    asm("addi $a1, $zero, 15");
    (*saveRegister)();
    asm("add $a0, $t2, $zero");
    asm("addi $a1, $zero, 16");
    (*saveRegister)();
}

/**
 * 保存单个mips寄存器
 * @param $a0 隐含参数,寄存器值
 * @param $a1 隐含参数,寄存器索引
 */
void _saveMipsRegister() {
    _registerBase = _mipsRegister;
    asm("sll $a1, $a1, 2");
    asm("addu $a1, $a1, $v0");
    asm("sw $a0, 0($a1)");
}

/**
 * 保存单个x86寄存器
 * @param $a0 隐含参数,寄存器值
 * @param $a1 隐含参数,寄存器索引
 */
void _saveX86Register() {
    _registerBase = _x86Register;
    asm("sll $a1, $a1, 2");
    asm("addu $a1, $a1, $v0");
    asm("sw $a0, 0($a1)");
}

/**
 * 载入寄存器组
 * @param type 0:载入mips寄存器组,other:载入x86寄存器组
 */
void _loadRegisters(int type) {
//    // $a1: base address
//    asm("lui $a1, 0xE000");
//    if (type == _MIPS_TYPE) {
//        // load mips registers
//        asm("lw $s0, 0($a1)");
//        asm("lw $s1, 4($a1)");
//        asm("lw $s2, 8($a1)");
//        asm("lw $s3, 12($a1)");
//        asm("lw $s4, 16($a1)");
//        asm("lw $s5, 20($a1)");
//        asm("lw $s6, 24($a1)");
//        asm("lw $s7, 28($a1)");
//        asm("lw $t4, 32($a1)");
//        asm("lw $t5, 36($a1)");
//        asm("lw $t6, 40($a1)");
//        asm("lw $t7, 44($a1)");
//        asm("lw $t8, 48($a1)");
//        asm("lw $t9, 52($a1)");
//        asm("lw $t0, 56($a1)");
//        asm("lw $t1, 60($a1)");
//        asm("lw $t2, 64($a1)");
//    } else {
//        // load x86 registers
//        asm("lw $s0, 128($a1)");
//        asm("lw $s1, 132($a1)");
//        asm("lw $s2, 136($a1)");
//        asm("lw $s3, 140($a1)");
//        asm("lw $s4, 144($a1)");
//        asm("lw $s5, 148($a1)");
//        asm("lw $s6, 152($a1)");
//        asm("lw $s7, 156($a1)");
//        asm("lw $t4, 160($a1)");
//        asm("lw $t5, 164($a1)");
//        asm("lw $t6, 168($a1)");
//        asm("lw $t7, 172($a1)");
//        asm("lw $t8, 176($a1)");
//        asm("lw $t9, 180($a1)");
//        asm("lw $t0, 184($a1)");
//        asm("lw $t1, 188($a1)");
//        asm("lw $t2, 192($a1)");
//    }

    if (type == _MIPS_TYPE) {
        _registerBase = _mipsRegister;
    } else {
        _registerBase = _x86Register;
    }

    asm("lw $s0, 0($v0)");		// s0
    asm("addi $v0, $v0, 4");
    asm("lw $s1, 0($v0)");		// s1
    asm("addi $v0, $v0, 4");
    asm("lw $s2, 0($v0)");		// s2
    asm("addi $v0, $v0, 4");
    asm("lw $s3, 0($v0)");		// s3
    asm("addi $v0, $v0, 4");
    asm("lw $s4, 0($v0)");		// s4
    asm("addi $v0, $v0, 4");
    asm("lw $s5, 0($v0)");		// s5
    asm("addi $v0, $v0, 4");
    asm("lw $s6, 0($v0)");		// s6
    asm("addi $v0, $v0, 4");
    asm("lw $s7, 0($v0)");		// s7
    asm("addi $v0, $v0, 4");
    asm("lw $t4, 0($v0)");		// t4
    asm("addi $v0, $v0, 4");
    asm("lw $t5, 0($v0)");		// t5
    asm("addi $v0, $v0, 4");
    asm("lw $t6, 0($v0)");		// t6
    asm("addi $v0, $v0, 4");
    asm("lw $t7, 0($v0)");		// t7
    asm("addi $v0, $v0, 4");
    asm("lw $t8, 0($v0)");		// t8
    asm("addi $v0, $v0, 4");
    asm("lw $t9, 0($v0)");		// t9
    asm("addi $v0, $v0, 4");
    asm("lw $t0, 0($v0)");		// t0
    asm("addi $v0, $v0, 4");
    asm("lw $t1, 0($v0)");		// t1
    asm("addi $v0, $v0, 4");
    asm("lw $t2, 0($v0)");		// t2
}

/**
 * context switch
 * @param type - 0:从x86切换到mips,other:从mips切换到x86
 */
void _contextSwitch(int type) {
	if (type == _MIPS_TYPE) {
		_saveX86Registers();
		_loadMipsRegisters();
	} else {
		_saveMipsRegisters();
		_loadX86Registers();
	}

//    // $a1: base address
//    asm("lui $a1, 0xE000");
//    if (type == _MIPS_TYPE) {
//        // save x86 registers
//        asm("sw $s0, 128($a1)");
//        asm("sw $s1, 132($a1)");
//        asm("sw $s2, 136($a1)");
//        asm("sw $s3, 140($a1)");
//        asm("sw $s4, 144($a1)");
//        asm("sw $s5, 148($a1)");
//        asm("sw $s6, 152($a1)");
//        asm("sw $s7, 156($a1)");
//        asm("sw $t4, 160($a1)");
//        asm("sw $t5, 164($a1)");
//        asm("sw $t6, 168($a1)");
//        asm("sw $t7, 172($a1)");
//        asm("sw $t8, 176($a1)");
//        asm("sw $t9, 180($a1)");
//        asm("sw $t0, 184($a1)");
//        asm("sw $t1, 188($a1)");
//        asm("sw $t2, 192($a1)");
//
//        // load mips registers
//        asm("lw $s0, 0($a1)");
//        asm("lw $s1, 4($a1)");
//        asm("lw $s2, 8($a1)");
//        asm("lw $s3, 12($a1)");
//        asm("lw $s4, 16($a1)");
//        asm("lw $s5, 20($a1)");
//        asm("lw $s6, 24($a1)");
//        asm("lw $s7, 28($a1)");
//        asm("lw $t4, 32($a1)");
//        asm("lw $t5, 36($a1)");
//        asm("lw $t6, 40($a1)");
//        asm("lw $t7, 44($a1)");
//        asm("lw $t8, 48($a1)");
//        asm("lw $t9, 52($a1)");
//        asm("lw $t0, 56($a1)");
//        asm("lw $t1, 60($a1)");
//        asm("lw $t2, 64($a1)");
//    } else {
//        // save mips registers
//        asm("sw $s0, 0($a1)");
//        asm("sw $s1, 4($a1)");
//        asm("sw $s2, 8($a1)");
//        asm("sw $s3, 12($a1)");
//        asm("sw $s4, 16($a1)");
//        asm("sw $s5, 20($a1)");
//        asm("sw $s6, 24($a1)");
//        asm("sw $s7, 28($a1)");
//        asm("sw $t4, 32($a1)");
//        asm("sw $t5, 36($a1)");
//        asm("sw $t6, 40($a1)");
//        asm("sw $t7, 44($a1)");
//        asm("sw $t8, 48($a1)");
//        asm("sw $t9, 52($a1)");
//        asm("sw $t0, 56($a1)");
//        asm("sw $t1, 60($a1)");
//        asm("sw $t2, 64($a1)");
//
//        // load x86 registers
//        asm("lw $s0, 128($a1)");
//        asm("lw $s1, 132($a1)");
//        asm("lw $s2, 136($a1)");
//        asm("lw $s3, 140($a1)");
//        asm("lw $s4, 144($a1)");
//        asm("lw $s5, 148($a1)");
//        asm("lw $s6, 152($a1)");
//        asm("lw $s7, 156($a1)");
//        asm("lw $t4, 160($a1)");
//        asm("lw $t5, 164($a1)");
//        asm("lw $t6, 168($a1)");
//        asm("lw $t7, 172($a1)");
//        asm("lw $t8, 176($a1)");
//        asm("lw $t9, 180($a1)");
//        asm("lw $t0, 184($a1)");
//        asm("lw $t1, 188($a1)");
//        asm("lw $t2, 192($a1)");
//    }
}

/**
 * 保存x86寄存器组
 */
void _saveX86Registers() {
    // $a1: base address
    asm("lui $a1, 0xE000");
    // save x86 registers
    asm("sw $s0, 128($a1)");
    asm("sw $s1, 132($a1)");
    asm("sw $s2, 136($a1)");
    asm("sw $s3, 140($a1)");
    asm("sw $s4, 144($a1)");
    asm("sw $s5, 148($a1)");
    asm("sw $s6, 152($a1)");
    asm("sw $s7, 156($a1)");
    asm("sw $t4, 160($a1)");
    asm("sw $t5, 164($a1)");
    asm("sw $t6, 168($a1)");
    asm("sw $t7, 172($a1)");
    asm("sw $t8, 176($a1)");
    asm("sw $t9, 180($a1)");
    asm("sw $t0, 184($a1)");
    asm("sw $t1, 188($a1)");
    asm("sw $t2, 192($a1)");
}

/**
 * 保存mips寄存器组
 */
void _saveMipsRegisters() {
    // $a1: base address
    asm("lui $a1, 0xE000");
    // save mips registers
    asm("sw $s0, 0($a1)");
    asm("sw $s1, 4($a1)");
    asm("sw $s2, 8($a1)");
    asm("sw $s3, 12($a1)");
    asm("sw $s4, 16($a1)");
    asm("sw $s5, 20($a1)");
    asm("sw $s6, 24($a1)");
    asm("sw $s7, 28($a1)");
    asm("sw $t4, 32($a1)");
    asm("sw $t5, 36($a1)");
    asm("sw $t6, 40($a1)");
    asm("sw $t7, 44($a1)");
    asm("sw $t8, 48($a1)");
    asm("sw $t9, 52($a1)");
    asm("sw $t0, 56($a1)");
    asm("sw $t1, 60($a1)");
    asm("sw $t2, 64($a1)");
}

/**
 * 载入x86寄存器组
 */
void _loadX86Registers() {
    // $a1: base address
    asm("lui $a1, 0xE000");
    // load x86 registers
    asm("lw $s0, 128($a1)");
    asm("lw $s1, 132($a1)");
    asm("lw $s2, 136($a1)");
    asm("lw $s3, 140($a1)");
    asm("lw $s4, 144($a1)");
    asm("lw $s5, 148($a1)");
    asm("lw $s6, 152($a1)");
    asm("lw $s7, 156($a1)");
    asm("lw $t4, 160($a1)");
    asm("lw $t5, 164($a1)");
    asm("lw $t6, 168($a1)");
    asm("lw $t7, 172($a1)");
    asm("lw $t8, 176($a1)");
    asm("lw $t9, 180($a1)");
    asm("lw $t0, 184($a1)");
    asm("lw $t1, 188($a1)");
    asm("lw $t2, 192($a1)");
}

/**
 * 载入mips寄存器组
 */
void _loadMipsRegisters() {
    // $a1: base address
    asm("lui $a1, 0xE000");
    // load mips registers
    asm("lw $s0, 0($a1)");
    asm("lw $s1, 4($a1)");
    asm("lw $s2, 8($a1)");
    asm("lw $s3, 12($a1)");
    asm("lw $s4, 16($a1)");
    asm("lw $s5, 20($a1)");
    asm("lw $s6, 24($a1)");
    asm("lw $s7, 28($a1)");
    asm("lw $t4, 32($a1)");
    asm("lw $t5, 36($a1)");
    asm("lw $t6, 40($a1)");
    asm("lw $t7, 44($a1)");
    asm("lw $t8, 48($a1)");
    asm("lw $t9, 52($a1)");
    asm("lw $t0, 56($a1)");
    asm("lw $t1, 60($a1)");
    asm("lw $t2, 64($a1)");
}

/**
 * 初始化x86寄存器组
 */
void _initialX86Registers() {
    int i;
    char *base;
    int *x86RegisterBase = (int *) 0xE0000080;

    for (i = 0; i < REGISTER_NUM; i++) {
        x86RegisterBase[i] = 0;
    }

    base = (char *)_x86BaseAddr;
    base -= 0x100;
    _x86Segment = (int) base;

    // cs
//    x86RegisterBase[8] = (int)base << 12;
	x86RegisterBase[8] = (int) base;
    // ds
    x86RegisterBase[9] = (int)base << 12;
    // es
    x86RegisterBase[10] = (int)base << 12;
    // ss
//    x86RegisterBase[11] = (int)base << 12;
    x86RegisterBase[11] = (int) base;
    // sp
    x86RegisterBase[4] = 0xfffe0000;
}

void _initialMipsRegisters() {
    int i;
    for (i = 0; i < 15; i++) {
        _mipsRegister[i] = 0;
    }
}

void printMips() {
    int i = 0;
    printString("mips\n");
    for (i = 0; i < 13; i++) {
        printNum(_mipsRegister[i]);
    }
}

/**
 * 打印x86寄存器组
 */
void printX86() {
    printf("X86 registers: \n");

    printf("AX:%d BX:%d CX:%d DX:%d SP:%d BP:%d\n", _x86Register[0], _x86Register[3], _x86Register[1], _x86Register[2], _x86Register[4], _x86Register[5]);
    printf("SI:%d DI:%d CS:%d IP:%d FLAG:%d T0:%d\n", _x86Register[6], _x86Register[7], _x86Register[8], _x86Register[12], _x86Register[13], _x86Register[14]);
}

/****************************** context switch end ******************************/


void _addExitCode(char *baseAddr, int *length) {
    // mov ah, 4ch; B44C
    // int 21h; CD21
    // int 22h; CD22
    baseAddr[(*length)++] = 0xB4;
    baseAddr[(*length)++] = 0x4C;
    baseAddr[(*length)++] = 0xCD;
    baseAddr[(*length)++] = 0x21;
    baseAddr[(*length)++] = 0xCD;
    baseAddr[(*length)++] = 0x22;
}

void _branchHandleAsm() {
//    void (*_branchHandlePreP)();
//    asm("lw $a0, 0($sp)");
//    asm("lw $a1, 4($sp)");
//
//    _branchHandlePreP = _branchHandlePre;
//    (*_branchHandlePreP)();
}

/**
 * x86跳转预处理
 * flag - 标志寄存器, 在高16位
 * type - 跳转类型
 * spc - 在x86中的跳转地址
 * cpc - 在x86中的当前地址
 */
int _branchHandlePre(int cpc, int spc, int type, int flag) {
    int tpc;
//    flag >>= 16;
    if (DEBUG_MODE) {
        printf("branch handle: %d %d %d %d\n", flag, type, spc, cpc);
    }
//    getchar();
    tpc = _branchHandler(type, spc, flag, cpc);
    return tpc;
}

void _testBranch() {
    int i;
    int spc;
    int tpc;

    int source;
    int target;

    _jlut_init();
    for (i = 0; i < 64; i++) {
        spc = i;
        tpc = i << 4;
        _jlut_insert(spc, tpc);
    }

    // find
    while (1) {
        source = getchar();
        if (source == 'Q' || source == 'q') {
            break;
        }
        if (source >= 'A') {
            source -= 'A';
        } else {
            source = 0;
        }
        target = _jlut_lookup(source);
        printf("source: %d, target: %d\n", source, target);
    }
}

//unsigned int data[] = {
//    0x0, 0x0, 0x0, 0x0,
//    0x1, 0x3, 0x5, 0x7,
//    0x1, 0x3, 0x5, 0x7,
//};
//
//int pmullh(int spc) {
//    int ret_val;
////    // $a0 = spc
////    asm("add $t0, %0, $zero\t\n"
////    :
////    :"r"(spc)
////            :"$t0", "$t1"
////           );
////	asm("add $t1, $t0, $zero"); // 待替换
//////    _campi(_MIPS_T0, _MIPS_T1);
////    // $t1 = index
////    asm("add %0, $t1, $zero\t\n"
////    :"=r"(ret_val)
////       );
////    return ret_val;
//
//    asm(
//        "label:add %0, %1, $t0\t\n"
//        "addi $t0, $t0, 1\t\n"
//        "beq $zero, $zero, label\t\n"
//        "nop\t\n"
//    : "=r"(*(data + 1))
//            : "r"(*data)
//        );
//    asm(
//        "label1:addi $t1, $t0, 1\t\n"
//        "label2:addi $t1, $t0, 1\t\n"
//        "beq $zero, $zero, label1\t\n"
//        "nop\t\n"
//    );
//
//    return 0;
//}

void _saveMipsSp() {
    asm(
        "add %0, $sp, $zero\t\n"
    : "=r"(_mipsSp)
            :
            : "$sp"
        );
}

void _loadMipsSp() {
    asm(
        "add $sp, %0, $zero\t\n"
    :
    :"r"(_mipsSp)
            : "$sp"
        );
}
