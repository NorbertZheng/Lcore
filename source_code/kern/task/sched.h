#ifndef _LCORE_SCHED_H
#define _LCORE_SCHED_H

#include "../list.h"

extern struct list_head ready_tasks;
extern struct list_head block_tasks;
extern struct task_struct *current;
extern void sched_insert_head(struct list_head *head, struct list_head *sched);
extern void sched_insert_tail(struct list_head *head, struct list_head *sched);
extern struct task_struct *sched_remove_first(struct list_head *head);
extern void sched_remove_by_task(struct list_head *head, struct task_struct *task);
extern void init_sched();
extern void switch_to(struct regs_context *old, struct regs_context *new);

#endif

