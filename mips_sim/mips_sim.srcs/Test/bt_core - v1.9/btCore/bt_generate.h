#ifndef BT_GENERATE_H
#define BT_GENERATE_H

#include "global.h"
#include "util.h"

// MIPS registers
#define _MIPS_ZERO 0
#define _MIPS_AT 1
#define _MIPS_V0 2
#define _MIPS_V1 3
#define _MIPS_A0 4
#define _MIPS_A1 5
#define _MIPS_A2 6
#define _MIPS_A3 7
#define _MIPS_T0 8
#define _MIPS_T1 9
#define _MIPS_T2 10
#define _MIPS_T3 11
#define _MIPS_T4 12
#define _MIPS_T5 13
#define _MIPS_T6 14
#define _MIPS_T7 15
#define _MIPS_S0 16
#define _MIPS_S1 17
#define _MIPS_S2 18
#define _MIPS_S3 19
#define _MIPS_S4 20
#define _MIPS_S5 21
#define _MIPS_S6 22
#define _MIPS_S7 23
#define _MIPS_T8 24
#define _MIPS_T9 25
#define _MIPS_K0 26
#define _MIPS_K1 27
#define _MIPS_GP 28
#define _MIPS_SP 29
#define _MIPS_S8 30
#define _MIPS_RA 31

// opcode
#define MIPS_OPCODE_RTYPE   0x00
#define MIPS_OPCODE_ADDI    0x08
#define MIPS_OPCODE_ANDI    0x0c
#define MIPS_OPCODE_ORI     0x0d
#define MIPS_OPCODE_XORI    0x0e
#define MIPS_OPCODE_LUI     0x0f
#define MIPS_OPCODE_LW      0x23
#define MIPS_OPCODE_SW      0x2b
#define MIPS_OPCODE_BEQ     0x04
#define MIPS_OPCODE_BNE     0x05
#define MIPS_OPCODE_LB      0x20
#define MIPS_OPCODE_LBU     0x24
#define MIPS_OPCODE_LH      0x21
#define MIPS_OPCODE_LHU     0x25
#define MIPS_OPCODE_SB      0x28
#define MIPS_OPCODE_SH      0x29
#define MIPS_OPCODE_MFC0	0x10

// R type, func
#define MIPS_FUNC_SLL 0x00
#define MIPS_FUNC_SRL 0x02
#define MIPS_FUNC_SRA 0x03
#define MIPS_FUNC_SLLV 04
#define MIPS_FUNC_SRLV 0x06
#define MIPS_FUNC_SRAV 0x07
#define MIPS_FUNC_JR 0x08
#define MIPS_FUNC_JALR 0x09
#define MIPS_FUNC_ADD 0x20
#define MIPS_FUNC_SUB 0x22
#define MIPS_FUNC_AND 0x24
#define MIPS_FUNC_OR  0x25
#define MIPS_FUNC_XOR 0x26
#define MIPS_FUNC_NOR 0x27

// nop
#define MIPS_NOP 0x00000000

// specific
#define MIPS_OPCODE_SPECIFIC 0x1c
#define MIPS_FUNC_CAMPI 0
#define MIPS_FUNC_CAMWI 1
#define MIPS_FUNC_RAMRI 2
#define MIPS_FUNC_RAMWI 3

// shift amout
#define MIPS_SHIFT_OPCODE 26
#define MIPS_SHIFT_RS 21
#define MIPS_SHIFT_RT 16
#define MIPS_SHIFT_RD 11
#define MIPS_SHIFT_SHM 6

// r type
int _gen_add(int rs, int rt, int rd);
int _gen_sub(int rs, int rt, int rd);
int _gen_and(int rs, int rt, int rd);
int _gen_or(int rs, int rt, int rd);
int _gen_xor(int rs, int rt, int rd);
int _gen_nor(int rs, int rt, int rd);
int _gen_sll(int rt, int rd, int sa);
int _gen_sllv(int rs, int rt, int rd);
int _gen_srl(int rt, int rd, int sa);
int _gen_srlv(int rs, int rt, int rd);
int _gen_sra(int rt, int rd, int sa);
int _gen_srav(int rs, int rt, int rd);
int _gen_jalr(int rs, int rd);

// i type
int _gen_lui(int rt, int imm);
int _gen_addi(int rs, int rt, int imm);
int _gen_ori(int rs, int rt, int imm);
int _gen_xori(int rs, int rt, int imm);
int _gen_lw(int rs, int rt, int offset);
int _gen_lh(int rs, int rt, int offset);
int _gen_lhu(int rs, int rt, int offset);
int _gen_lb(int rs, int rt, int offset);
int _gen_lbu(int rs, int rt, int offset);
int _gen_sw(int rs, int rt, int offset);
int _gen_sh(int rs, int rt, int offset);
int _gen_sb(int rs, int rt, int offset);
int _gen_bne(int rs, int rt, int offset);
int _gen_beq(int rs, int rt, int offset);
int _gen_jr(int rs);

// mfc
int _gen_mfc0(int rt);

#endif
