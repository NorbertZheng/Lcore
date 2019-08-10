#ifndef _LCORE_EXCEPT_H
#define _LCORE_EXCEPT_H

#define _EXCEPT_FLAG				0x80000000

#define _OFFSET_(inst)				((inst) & 0x0000ffff)
#define _RS_(inst)					(((inst) & 0x03e00000) >> 21)
#define _RT_(inst)					(((inst) & 0x001f0000) >> 16)
#define _CODE_(inst)				(((inst) & 0xfc000000) >> 26)
#define _STACK_OFFSET(idx, regs)	(*(regs + ((idx) - 2)))
#define _STACK_EPC(regs)			(*(regs + 28))

#define _RESERVE_INST				0x3
#define _CODE_LWL					0x22
#define _CODE_LWR					0x26
#define _CODE_SWR					0x2e
#define _CODE_SWL					0x2a

extern void do_exception(unsigned int status, unsigned errArg, unsigned int errPc, unsigned int *regs);

#endif

