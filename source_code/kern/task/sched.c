#include "task.h"
#include "sched.h"
#include "../../arch/arch.h"
#include "../../tool/tool.h"
#include "../time/time.h"
#include "../../arch/intr.h"
#include "../vga/print.h"
#include "../../arch/intr.h"

struct list_head ready_tasks;
struct list_head block_tasks;
struct intr_work sched_work;
struct task_struct *current = NULL;

void sched_insert_head(struct list_head *head, struct list_head *sched)
{
	list_add(sched, head);
}

void sched_insert_tail(struct list_head *head, struct list_head *sched)
{
	list_add_tail(sched, head);
}

struct task_struct *sched_remove_first(struct list_head *head)
{
	struct task_struct *task;
	struct list_head *ptr;

	ptr = head->next;
	list_del_init(ptr);
	task = container_of(ptr, struct task_struct, sched);

	return task;
}

void sched_remove_by_task(struct list_head *head, struct task_struct *task)
{
	if (list_empty(&(task->sched)))
		return;

	list_del_init(&(task->sched));
}

void sched(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	struct task_struct *next;
	struct task_struct *old;

	unsigned int a = 50000000;

	current->state = _TASK_READY;
	sched_insert_tail(&ready_tasks, &(current->sched));

	next = sched_remove_first(&ready_tasks);
	if (next == NULL) {
		printk("scheduler : next task is NULL!\n");
		die();
	}

	if (next != current) {
		old = current;
		current = next;
		clean_icr(_INTR_CLOCK);
		switch_to(&(old->context), &(current->context));
	}
}

void timer_sched(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	--(current->counter);
	// printk("%x\n", current->counter);
	if (current->counter) 
		return;

	current->counter = _DEFAULT_TICKS >> 1;
	sched(regs, status, errArg, errPc);
}

void init_sched()
{
	struct intr_work *sched_work_pointer = &sched_work;

	INIT_LIST_HEAD(&ready_tasks);
	INIT_LIST_HEAD(&block_tasks);

	current = &(init->task);

	sched_work_pointer->work = timer_sched;
	INIT_LIST_HEAD(&(sched_work_pointer->node));
	if (register_work(&(sched_work_pointer->node), time_index))
		return;

	printk("Init Sched ok\n");
	printk("\tcurrent work : %x\n", current);
}

