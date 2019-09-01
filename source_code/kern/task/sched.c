#include "task.h"
#include "sched.h"
#include "../../arch/arch.h"
#include "../../tool/tool.h"
#include "../time/time.h"
#include "../../arch/intr.h"
#include "../vga/print.h"
#include "../../arch/intr.h"
#include "vma.h"

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
	struct regs_context *current_context;
	struct vma *old_stack_vma;
	struct list_head *pos;

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
		// printk("sched : old addr(%x), next addr(%x)\n", old, next);
		// clean_icr(_INTR_CLOCK);
		/* current_context = &(current->context);
		printk("sched : current->context.v0(%x)\tcurrent->context.fp(%x)\tcurrent->context.ra(%x)\tcurrent->context.epc(%x)\n"
			, current_context->v0, current_context->fp, current_context->ra, current_context->epc);
		printk("sched : enter_syscall0 $ra addr(%x), enter_syscall0 $ra data(%x)"
			, (current_context->fp + 4), *(unsigned int *)(current_context->fp + 4)); */
		/* if (!list_empty(&(old->stack_vma_head))) {
			list_for_each(pos, &(old->stack_vma_head)) {
				old_stack_vma = container_of(pos, struct vma, node);
				printk("old stack_vma_start : %x\n", old_stack_vma->start);
			}
		} */
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

