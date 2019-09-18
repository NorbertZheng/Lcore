#include "bt_branch.h"

/**
 * x86 interrupt routines emulator
 * @author ҦԪ
 * @date 2012-03-19
 */

//
// JMP: far jump, near jump, short jump, indirect branch, conditional branch
// far jump(direct branch): the CS and IP are specified. Thus the maximum accessible range is CS << 4 + IP = 1MB
//   e.g.:
//       jmp 1234h:5678h
// near jump(relative branch): displacement is stored in two bytes (maximum accessible range is 64KB, -32KB <= displacement <= 32KB)
//   e.g.:
//       jmp -1234h
// short jump(relative branch): displacement is stored in only one byte (maximum accessible range is 256B, -128B <= displacement <= 128B)
//   e.g.:
//       jmp -12h
//
// !!Note!!: the assembler will choose the most suitable one of the three jump modes listed above.
//
//
// indirect branch:
//   e.g.:
//       jmp ax          ; oo = 2'b11
//       jmp [bx]        ; oo = 2'b00
//       jmp [bx+12h]    ; oo = 2'b01
//       jmp [bx+1234h]  ; oo = 2'b10
//
//
// conditional branch: branch if the condition is met (must be direct and near or short)
//  e.g.:
//      jne 1234h
//
int _branchHandler(int brType, int spc, int flag, int cpc)
{
    int tpcEval;
    int taken = 0;

//    asm("add %0, $ra, $zero\t\n"
//    :"=r"(nextBlock)
//       );
    switch (brType) {
        case _X86_JE:
            taken = (flag & 0x4) != 0;
            break;
        case _X86_JNE:
            taken = (flag & 0x4) == 0;
            break;
        case _X86_POP1_PUSH1_CALL0_CALL3_JMP0_JMP2:
        case _X86_JMP1:
        case _X86_RET0:
            taken = 1;
            break;
        case _X86_JO:
            taken = (flag & 0x1) != 0;
            break;
        case _X86_JNO:
            taken = (flag & 0x1) == 0;
            break;
        case _X86_JC:
            taken = (flag & 0x8) != 0;
            break;
        case _X86_JNC:
            taken = (flag & 0x8) == 0;
            break;
        case _X86_JBE:
            taken = ((flag & 0x8) | (flag & 0x4)) != 0;
            break;
        case _X86_JA:
            taken = ((flag & 0x8) | (flag & 0x4)) == 0;
            break;
        case _X86_JS:
            taken = (flag & 0x2) != 0;
            break;
        case _X86_JNS:
            taken = (flag & 0x2) == 0;
            break;
        case _X86_JL:
            taken = (flag & 0x2) != 0;
            break;
        case _X86_JGE:
            taken = (flag & 0x2) == 0;
            break;
        case _X86_JLE:
            taken = ((flag & 0x4) | (flag & 0x2)) != 0;
            break;
        case _X86_JG:
            taken = ((flag & 0x4) | (flag & 0x2)) == 0;
            break;
    }
    if(taken)
        tpcEval = _jlut_lookup(spc);
    else
        tpcEval = _jlut_lookup(cpc);

    return tpcEval;
}
