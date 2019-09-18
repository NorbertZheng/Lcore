#include "bt_cache.h"

void (*func_ptr)();

//
// function description: JLUT spc lookup
// return value: if ret_val[31] = 0, it means JLUT hit, and ret_val is index;
//               if ret_val[31] = 1, it means JLUT miss, and ret_val is useless data.
//
int _jlut_spc_lookup(int spc) {
    int ret_val;
    // $a0 = spc
    asm("add $t0, %0, $zero\t\n"
    :
    :"r"(spc)
            :"$t0", "$t1"
           );
	asm("add $t1, $t0, $zero"); // 待替换
//    _campi(_MIPS_T0, _MIPS_T1);
    // $t1 = index
    asm("add %0, $t1, $zero\t\n"
    :"=r"(ret_val)
       );
    return ret_val;
}

//
// function description: JLUT tpc read
// return value: ret_val is {tpc, valid} indexed by index
//
int _jlut_tpc_rd(int index) {
    int ret_val;
    // $a0 = index
    asm("add $t0, %0, $zero\t\n"
    :
    :"r"(index)
            :"$t0", "$t1"
           );
	asm("add $t1, $t0, $zero");
//    _ramri(_MIPS_T0, _MIPS_T1);
    // $t1 = tpc
    asm("add %0, $t1, $zero\t\n"
    :"=r"(ret_val)
       );
    return ret_val;
}

//
// function description: JLUT spc write
//
void _jlut_spc_wr(int index, int spc) {
    // $a0 = index, $a1 = spc
    asm("add $t0, %0, $zero\t\n"
        "add $t1, %1, $zero\t\n"
    :
    :"r"(index),"r"(spc)
            :"$t0","$t1"
           );
	asm("add $zero, $t1, $t0");
//    _camwi(_MIPS_T0, _MIPS_T1);
}

//
// function description: JLUT tpc write
//
void _jlut_tpc_wr(int index, int tpc, int valid) {
    // $a0 = index, $a1 = tpc
    tpc = (tpc & 0x7fffffff) | (valid << 31);
    asm("add $t0, %0, $zero\t\n"
        "add $t1, %1, $zero\t\n"
    :
    :"r"(index),"r"(tpc)
            :"$t0","$t1"
           );
	asm("add $zero, $t1, $t0");
//    _ramwi(_MIPS_T0, _MIPS_T1);
}


////////////////////////////////////////////////////////////////////
// four new instructions
// campi, camwi, ramri, ramwi
////////////////////////////////////////////////////////////////////
void _campi(int rs, int rd) {
    int buff[3];

    buff[0] = MIPS_OPCODE_SPECIFIC << 26 | rs << 21 | rd << 11 | MIPS_FUNC_CAMPI; // new instruction
    buff[1] = MIPS_OPCODE_RTYPE << 26 | 31 << 21 | MIPS_FUNC_JR; // jr $ra
    buff[2] = MIPS_NOP; // nop

    func_ptr = (void (*) ()) &buff[0];

    // execute the code pushed into the stack
    asm("cache 0, 0");
    func_ptr();
}

void _camwi(int rt, int rs) {
    int buff[3];

    buff[0] = MIPS_OPCODE_SPECIFIC << 26 | rs << 21 | rt << 16 | MIPS_FUNC_CAMWI; // new instruction
    buff[1] = MIPS_OPCODE_RTYPE << 26 | 31 << 21 | MIPS_FUNC_JR; // jr $ra
    buff[2] = MIPS_NOP; // nop

    func_ptr = (void (*) ()) &buff[0];

    // execute the code pushed into the stack
    asm("cache 0, 0");
    func_ptr();
}

void _ramri(int rs, int rd) {
    int buff[3];

    buff[0] = MIPS_OPCODE_SPECIFIC << 26 | rs << 21 | rd << 11 | MIPS_FUNC_RAMRI; // new instruction
    buff[1] = MIPS_OPCODE_RTYPE << 26 | 31 << 21 | MIPS_FUNC_JR; // jr $ra
    buff[2] = MIPS_NOP; // nop

    func_ptr = (void (*) ()) &buff[0];

    // execute the code pushed into the stack
    asm("cache 0, 0");
    func_ptr();
}

// rt: index, rs: content
void _ramwi(int rt, int rs) {
    int buff[3];

    buff[0] = MIPS_OPCODE_SPECIFIC << 26 | rs << 21 | rt << 16 | MIPS_FUNC_RAMWI; // new instruction
    buff[1] = MIPS_OPCODE_RTYPE << 26 | 31 << 21 | MIPS_FUNC_JR; // jr $ra
    buff[2] = MIPS_NOP; // nop

    func_ptr = (void (*) ()) &buff[0];

    // execute the code pushed into the stack
    asm("cache 0, 0");
    func_ptr();
}
