#ifndef _LCORE_SYSCALL_H
#define _LCORE_SYSCALL_H

#define V0_OFFSET			0				// v0'offset of regs

#define SYSCALL_DEFINE0(funcname) \
		void funcname(unsigned int *args)
#define SYSCALL_DEFINE1(funcname, type1, arg1) \
		void funcname(unsigned int *args, type1 arg1)
#define SYSCALL_DEFINE2(funcname, type1, arg1, type2, arg2) \
		void funcname(unsigned int *args, type1 arg1, type2 arg2)
#define SYSCALL_DEFINE3(funcname, type1, arg1, type2, arg2, type3, arg3) \
		void funcname(unsigned int *args, type1 arg1, type2 arg2, type3 arg3)

#define MAX_SYSCALL			1024

extern void *syscall_table[MAX_SYSCALL];
extern void init_syscall();

#endif

