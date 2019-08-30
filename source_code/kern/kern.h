#ifndef _LCORE_KERN_H
#define _LCORE_KERN_H

#define container_of(ptr, type, member)			((type *) ((char *) ptr - (char *) &(((type *) 0)->member)))

#define ARRAY_SIZE(arr)							(sizeof(arr) / sizeof(arr[0]))

#define NULL									0

/*
 * list_head		cycle 2-direct linkedlist
 * prev:			point to the previous element
 * next:			point to the next element
 */
struct list_head {
	struct list_head *prev;
	struct list_head *next;
};

/*
 * interrupt handle struct
 */

typedef void (*intr_fn)(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc);

struct intr_work {
	intr_fn work;
	struct list_head node;
};

struct intr_block {
	intr_fn entry_fn;
	struct list_head head;
};

#endif

