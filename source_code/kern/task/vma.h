#ifndef _LCORE_VMA_H
#define _LCORE_VMA_H

#include "../list.h"
#include "task.h"

struct vma {
	struct list_head node;
	unsigned int start;		// page-aligned
	unsigned int cnt;		// nr of pages
	unsigned int vend;
};

extern unsigned int add_vmas(struct task_struct *task, unsigned int va, unsigned int size);
extern unsigned int delete_vmas(struct task_struct *task, unsigned int va, unsigned int vend);

#endif

