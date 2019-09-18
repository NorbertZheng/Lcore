#include "bt_generate.h"

/**
 * x86指令解析
 * @author 姚元
 * @date 2012-03-14
 */

/**
 * add rd, rs, rt
 */
int _gen_add(int rs, int rt, int rd) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rs << 21 | rt << 16 | rd << 11 | MIPS_FUNC_ADD;
    return mips_inst;
}

/**
 * sub rd, rs, rt
 */
int _gen_sub(int rs, int rt, int rd) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rs << 21 | rt << 16 | rd << 11 | MIPS_FUNC_SUB;
    return mips_inst;
}

/**
 * and rd, rs, rt
 */
int _gen_and(int rs, int rt, int rd) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rs << 21 | rt << 16 | rd << 11 | MIPS_FUNC_AND;
    return mips_inst;
}

/**
 * or rd, rs, rt
 */
int _gen_or(int rs, int rt, int rd) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rs << 21 | rt << 16 | rd << 11 | MIPS_FUNC_OR;
    return mips_inst;
}

/**
 * xor rd, rs, rt
 */
int _gen_xor(int rs, int rt, int rd) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rs << 21 | rt << 16 | rd << 11 | MIPS_FUNC_XOR;
    return mips_inst;
}

/**
 * nor rd, rs, rt
 */
int _gen_nor(int rs, int rt, int rd) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rs << 21 | rt << 16 | rd << 11 | MIPS_FUNC_NOR;
    return mips_inst;
}

/**
 * sll rd, rt, sa
 */
int _gen_sll(int rt, int rd, int sa) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rt << 16 | rd << 11 | sa << 6 | MIPS_FUNC_SLL;
    return mips_inst;
}

/**
 * srl rd, rt, sa
 */
int _gen_srl(int rt, int rd, int sa) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << 26 | rt << 16 | rd << 11 | sa << 6 | MIPS_FUNC_SRL;
    return mips_inst;
}

/**
 * lui指令, format: lui rt, imm
 */
int _gen_lui(int rt, int imm) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_LUI << MIPS_SHIFT_OPCODE | rt << MIPS_SHIFT_RT | (imm & 0xffff);
    return mips_inst;
}

/**
 * jr指令, format: jr rs
 */
int _gen_jr(int rs) {
    int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | MIPS_FUNC_JR;
    return mips_inst;
}

/**
 * jalr rd, rs
 * GPR[rd] <- return_addr, PC <- GPR[rs]
 */
int _gen_jalr(int rs, int rd) {
	int mips_inst = 0;
    mips_inst = MIPS_OPCODE_RTYPE << MIPS_SHIFT_OPCODE | _MIPS_V0 << MIPS_SHIFT_RS | rd << MIPS_SHIFT_RD | MIPS_FUNC_JALR;
    return mips_inst;
}

/**
 * addi rt, rs, imm
 */
int _gen_addi(int rs, int rt, int imm) {
	int mips_inst = 0;
	mips_inst = MIPS_OPCODE_ADDI << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | (imm & 0xffff);
	return mips_inst;
}

/**
 * ori rt, rs, imm;
 */
int _gen_ori(int rs, int rt, int imm) {
	int mips_inst = 0;
    mips_inst = MIPS_OPCODE_ORI << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | (imm & 0xffff);
    return mips_inst;
}

/**
 * xori rt, rs, imm;
 */
int _gen_xori(int rs, int rt, int imm) {
	int mips_inst = 0;
	imm &= 0xffff;
    mips_inst = MIPS_OPCODE_XORI << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | imm;
    return mips_inst;
}

/**
 * lw rt, offset(rs);
 */
int _gen_lw(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_LW << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * lh rt, offset(rs);
 */
int _gen_lh(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_LH << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * lhu rt, offset(rs);
 */
int _gen_lhu(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_LHU << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * lb rt, offset(rs);
 */
int _gen_lb(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_LB << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * lbu rt, offset(rs);
 */
int _gen_lbu(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_LBU << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}


/**
 * sw rt, offset(rs);
 */
int _gen_sw(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_SW << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * sh rt, offset(rs);
 */
int _gen_sh(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_SH << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * sb rt, offset(rs);
 */
int _gen_sb(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_SB << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * bne rs, rt, offset;
 */
int _gen_bne(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_BNE << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * beq rs, rt, offset;
 */
int _gen_beq(int rs, int rt, int offset) {
	int mips_inst = 0;
	offset &= 0xffff;
    mips_inst = MIPS_OPCODE_BEQ << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | offset;
    return mips_inst;
}

/**
 * sra rd, rt, sa;
 */
int _gen_sra(int rt, int rd, int sa) {
	int mips_inst = 0;
	mips_inst = MIPS_OPCODE_RTYPE << 26 | rt << 16 | rd << 11 | sa << 6 | MIPS_FUNC_SRA;
	return mips_inst;
}

/**
 * sllv rd, rs, rt;
 */
int _gen_sllv(int rs, int rt, int rd) {
	int mips_inst = 0;
	mips_inst = MIPS_OPCODE_RTYPE << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | rd << MIPS_SHIFT_RD | MIPS_FUNC_SLLV;
	return mips_inst;
}

/**
 * srlv rd, rs, rt;
 */
int _gen_srlv(int rs, int rt, int rd) {
	int mips_inst = 0;
	mips_inst = MIPS_OPCODE_RTYPE << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | rd << MIPS_SHIFT_RD | MIPS_FUNC_SRLV;
	return mips_inst;
}

/**
 * srav rd, rs, rt;
 */
int _gen_srav(int rs, int rt, int rd) {
	int mips_inst = 0;
	mips_inst = MIPS_OPCODE_RTYPE << MIPS_SHIFT_OPCODE | rs << MIPS_SHIFT_RS | rt << MIPS_SHIFT_RT | rd << MIPS_SHIFT_RD | MIPS_FUNC_SRAV;
	return mips_inst;
}

/**
 * mfc0 rt, rd
 * GPR[rt] <- CPR[0, rd, sel]
 * 低4位：[CF, ZF, SF, OF]
 */
int _gen_mfc0(int rt) {
	int mips_inst = 0;
	int rd = 12;
	mips_inst = MIPS_OPCODE_MFC0 << MIPS_SHIFT_OPCODE | rt << MIPS_SHIFT_RT | rd << MIPS_SHIFT_RD;
	return mips_inst;
}
