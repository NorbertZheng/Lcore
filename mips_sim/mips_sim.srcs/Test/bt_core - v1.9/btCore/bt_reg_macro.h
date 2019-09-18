#ifndef BT_REG_MACRO_H
#define BT_REG_MACRO_H

// mips
#define _MIPS_ZERO 0
#define _MIPS_S0 16
#define _MIPS_S1 17
#define _MIPS_S2 18
#define _MIPS_S3 19
#define _MIPS_S4 20
#define _MIPS_S5 21
#define _MIPS_S6 22
#define _MIPS_S7 23
#define _MIPS_T0 8
#define _MIPS_T1 9
#define _MIPS_T2 10
#define _MIPS_T3 11
#define _MIPS_T4 12
#define _MIPS_T5 13
#define _MIPS_T6 14
#define _MIPS_T7 15
#define _MIPS_T8 24
#define _MIPS_T9 25
#define _MIPS_RA 31

// x86
#define _X86_AL 0
#define _X86_CL 1
#define _X86_DL 2
#define _X86_BL 3
#define _X86_AH 4
#define _X86_CH 5
#define _X86_DH 6
#define _X86_BH 7
#define _X86_AX _MIPS_S0 // 0		16
#define _X86_CX _MIPS_S1 // 1		17
#define _X86_DX _MIPS_S2 // 2		18
#define _X86_BX _MIPS_S3 // 3		19
#define _X86_SP _MIPS_S4 // 4		20
#define _X86_BP _MIPS_S5 // 5		21
#define _X86_SI _MIPS_S6 // 6		22
#define _X86_DI _MIPS_S7 // 7		23
#define _X86_CS _MIPS_T4 // 8		12
#define _X86_DS _MIPS_T5 // 9		13
#define _X86_ES _MIPS_T6 // 10		14
#define _X86_SS _MIPS_T7 // 11		15
#define _X86_IP _MIPS_T8 // 12		24
#define _X86_FLAG _MIPS_T9 // 13	25

#endif
