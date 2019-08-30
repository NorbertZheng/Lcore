#ifndef _LCORE_TASK_H
#define _LCORE_TASK_H

#include "../list.h"
#include "sign.h"

/*
 * Memory Alloc
 * 0x4000,0000		~	0x8000,0000 - 1					user stack
 * 0x8000,0000		~	0xC000,0000 - 1					user code / static data
 * 0xC000,0000		~	0xFF00,0000 - 1					user heap
 *
 * RAM space alloc
 * 0 				~	_KERNEL_VIRT_END(page.h)		kernel space
 * _KERNEL_VIRT_END	~	ROM_START(arch.h)				user space
 * 		_KERNEL_VIRT_END	~	_TASK_CODE_START			user stack
 * 		_TASK_CODE_START	~	_TASK_HEAP_END				user code / static data
 * 		_TASK_HEAP_END		~	ROM_START					user heap
 * ROM_START		~	0xFFFF,FFFF						ROM / IO space
 */
#define _TASK_CODE_START			0x80000000
#define _TASK_USER_STACK			_TASK_CODE_START
#define _TASK_HEAP_END				0xC0000000

#define PAGE_SIZE					0x1000
#define KERN_STACK_BOTTOM			0x3000
#define KERN_STACK_SIZE				(2 * PAGE_SIZE)

// task state
#define _TASK_UNINIT				0
#define _TASK_READY					1
#define _TASK_RUNNING				2
#define _TASK_BLOCKED				3

#define _DEFAULT_TICKS				4

struct regs_context {
	unsigned int v0, v1;
	unsigned int a0, a1, a2, a3;
	unsigned int t0, t1, t2, t3, t4, t5, t6, t7;
	unsigned int s0, s1, s2, s3, s4, s5, s6, s7;
	unsigned int t8, t9;
	unsigned int gp, sp, fp, ra, k0, k1;
	unsigned int epc, ear;
};

struct task_struct {
	struct regs_context context;
	unsigned int *pgd;

	unsigned int pid;
	unsigned int parent;
	unsigned int state;
	unsigned int counter;

	struct list_head sched;
	struct list_head node;
	struct signal sigaction[MAX_SIGNS];

	unsigned int heaptop;
	struct list_head stack_vma_head;
	struct list_head user_vma_head;		// user code / static data
	struct list_head heap_vma_head;
};

union task_union {
	struct task_struct task;
	unsigned char kernel_stack[KERN_STACK_SIZE];
};

extern unsigned char _ctx_restore;
extern union task_union *init;			// init(pid = 0) proc's kernel stack pointer(low addr)
extern void init_task(void *phy_code);
extern void add_tasks(struct list_head *node);
extern void del_tasks(struct list_head *node);
extern unsigned int do_fork(unsigned int *args);

#endif

