#ifndef BT_X86_OPCODE_MACRO_H
#define BT_X86_OPCODE_MACRO_H

#define _X86_ADC2_ADD2_AND2_CMP2_SUB2_XOR2_OR2 0x20
#define _X86_SAR0_SHL0_SHR0 0x34
#define _X86_POP1_PUSH1_CALL0_CALL3_JMP0_JMP2 0x3f

#define _X86_E_ADC2 0x2
#define _X86_E_ADD2 0x0
#define _X86_E_AND2 0x4
#define _X86_E_CMP2 0x7
#define _X86_E_SUB2 0x5
#define _X86_E_XOR2 0x6
#define _X86_E_OR2  0x1

#define _X86_E_SAR0 0x7
#define _X86_E_SHL0 0x4
#define _X86_E_SHR0 0x5

#define _X86_E_POP1  0x0
#define _X86_E_PUSH1 0x6
#define _X86_E_CALL0 0x3
#define _X86_E_CALL3 0x2
#define _X86_E_JMP0  0x5
#define _X86_E_JMP2  0x4

// ADC
#define _X86_ADC0 0x04
#define _X86_ADC1 0x05
// ADD
#define _X86_ADD0 0x00
#define _X86_ADD1 0x01
// AND
#define _X86_AND0 0x08
#define _X86_AND1 0x09
// CMP
#define _X86_CMP0 0x0e
#define _X86_CMP1 0x0f
// INT
#define _X86_INT0 0x33
// MOV
#define _X86_MOV0 0x28
#define _X86_MOV1_0 0x2c
#define _X86_MOV1_1 0x2d
#define _X86_MOV1_2 0x2e
#define _X86_MOV1_3 0x2f
#define _X86_MOV2 0x31
#define _X86_MOV3 0x22
#define _X86_MOV4 0x23
// NOT
#define _X86_NOT0 0x3d
// OR
#define _X86_OR0 0x02
#define _X86_OR1 0x03
// POP
#define _X86_POP0_0 0x0b
//#define _X86_POP0_1 0x17
// PUSH
#define _X86_PUSH0_0 0x0a
//#define _X86_PUSH0_1 0x25
// RET
#define _X86_RET0 0x30
#define _X86_RET1 0x32
// SUB
#define _X86_SUB0 0x0a
#define _X86_SUB1 0x0b
// XOR
#define _X86_XOR0 0x0c
#define _X86_XOR1 0x0d
// CALL
#define _X86_CALL1 0x3a
#define _X86_CALL2 0x26
// Jcc

// JMP
#define _X86_JMP1 0x3a

// JCC
//0		O
//1		NO
//2		C/B/NAE
//3		NC/AE/NB
//4		E/Z
//5		NE/NZ
//6		BE/NA
//7		A/NBE
//8		S
//9		NS
//A		P/PE
//B		NP/PO
//C		L/NGE
//D		GE/NL
//E		LE/NG
//F		G/NLE
#define _X86_JO     0x0
#define _X86_JNO    0x1
#define _X86_JC     0x2
#define _X86_JNC    0x3
#define _X86_JE     0x4
#define _X86_JNE    0x5
#define _X86_JBE    0x6
#define _X86_JA     0x7
#define _X86_JS     0x8
#define _X86_JNS    0x9
//#define _X86_JP     0xa
//#define _X86_JNP    0xb
#define _X86_JL     0xc
#define _X86_JGE    0xd
#define _X86_JLE    0xe
#define _X86_JG     0xf



#endif
