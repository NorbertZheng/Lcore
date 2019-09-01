#include "task.h"
#include "vma.h"
#include "../../arch/page.h"
#include "../mm/slub.h"
#include "../mm/buddy.h"
#include "../../arch/arch.h"
#include "../../tool/tool.h"
#include "sched.h"

#define IDMAP_MAX			16
#define MAX_PID				(IDMAP_MAX * sizeof(unsigned char))
static unsigned char idmap[IDMAP_MAX];
//									0b00000001		0b00000100		0b00010000		0b01000000
static unsigned int bits_map[8] = 	{	1	,	2	,	4	,	8	,	16	,	32	,	64	,	128};
//											0b00000010		0b00001000		0b00100000		0b10000000

struct list_head tasks;		// global obj, unable to be pointer

union task_union *init = NULL;

unsigned int get_emptypid()
{
	unsigned int no = 0;
	unsigned int tmp;
	unsigned int index, bits;

	for (index = 0; index < IDMAP_MAX; ++index) {
		tmp = idmap[index];
		for (bits = 0; bits < sizeof(unsigned char); ++bits) {
			if (!(tmp & 0x01)) {
				idmap[index] |= bits_map[bits];
				break;
			}
			tmp >>= 1;
			++no;
		}
		if (bits < sizeof(unsigned char))
			break;
	}
	
	return no;
}

// return pid back to idmap
void put_pid(unsigned int pid)
{
	unsigned int remain;
	unsigned int index = division(pid, sizeof(unsigned char), &remain);

	idmap[index] &= (~(bits_map[remain - 1]));
}

// 1 inavailed, 0 availed
unsigned int inavailed_pid(unsigned int pid)
{
	return (pid >= MAX_PID);
}

void add_tasks(struct list_head *node)
{
	list_add_tail(node, &tasks);
}

void del_tasks(struct list_head *node)
{
	if (list_empty(node))
		return;

	list_del_init(node);
}

void init_task(void *phy_code)
{
	struct task_struct *init_task;

	INIT_LIST_HEAD(&tasks);
	memset(idmap, 0, 16 * sizeof(unsigned char));

	init = (union task_union *) (KERN_STACK_BOTTOM - KERN_STACK_SIZE);
	if (!init)
		goto error;

	init_task = &(init->task);
	init_task->pid = get_emptypid();
	if (inavailed_pid(init_task->pid))
		goto error;

	init_task->parent = init_task->pid;
	init_task->state = _TASK_UNINIT;
	init_task->counter = _DEFAULT_TICKS;
	init_task->pgd = pgd;

	memset(&(init_task->context), 0, sizeof(struct regs_context));

	INIT_LIST_HEAD(&(init_task->stack_vma_head));
	INIT_LIST_HEAD(&(init_task->user_vma_head));
	INIT_LIST_HEAD(&(init_task->heap_vma_head));
	INIT_LIST_HEAD(&(init_task->node));
	INIT_LIST_HEAD(&(init_task->sched));
	add_tasks(&(init_task->node));		// tasks recommand

	if (add_vmas(&(init->task), _TASK_CODE_START, PAGE_SIZE)) {
		printk("init_task : add_vmas (code) failed!\n");
		die();
	}
	if (do_one_mapping(init_task->pgd, _TASK_CODE_START, (unsigned int) phy_code, USER_DEFAULT_ATTR)) {
		printk("Init task-0 failed : do_one_mapping (code) failed!\n");
		die();
	}

	memset(init_task->sigaction, 0, MAX_SIGNS * sizeof(struct signal));
	init_task->state = _TASK_RUNNING;	// init proc is running(kernel)

	printk("Init task-0 ok : \n");
	printk("\tpid : %x\n", init_task->pid);
	printk("\tphy_code : %x\n", phy_code);
	printk("\tkernel stack's end at %x\n", init);
	return;
error:
	printk("Init task-0 failed!\n");
	die();
}

void inc_reference_by_pt(unsigned int *pt)
{
	unsigned int index;
	unsigned int *pt_index;
	struct buddy_sys *buddy_pointer = &buddy;

	// printk("inc_reference_by_pt : pt(%x) pages(%x)\n", pt, pages);
	for (index = 0; index < (PAGE_SIZE >> 2); ++index) {
		pt_index = &(pt[index]);
		/* if (((*pt_index) >> PAGE_SHIFT) >= buddy_pointer->buddy_end_pfn) {
			continue;
		} */
		if (is_P(pt_index)) {
			// printk("inc_reference_by_pt : pt[%x] = %x\n", index, *pt_index);
			inc_reference(pages + ((*pt_index) >> PAGE_SHIFT), 1);
		}
	}
	// printk("inc_reference_by_pt : prepare to exit!\n");
}

void *copy_pagetables()
{
	unsigned int *old_pgd = NULL;
	unsigned int *pgd = NULL;
	unsigned int *tmp_pt;
	unsigned int *old_pt;
	unsigned int index, ptnr, i_pte;
	unsigned int attr;

	pgd = (unsigned int *) kmalloc(PAGE_SIZE);
	if (pgd == NULL) {
		printk("copy_pagetables : kmalloc pgd failed!\n");
		goto error1;
	}

	// printk("copy_pagetables : enter copy_pagetables!\n");
	old_pgd = current->pgd;
	memcpy(pgd, old_pgd, PAGE_SIZE);

	// page tables in kernel part are common
	for (index = (_USER_VIRT_START >> PGD_SHIFT), ptnr = 0; index < (PAGE_SIZE >> 2); ++index) {
		// printk("I'm in the copy_pagetables cycle!\n");
		if (old_pgd[index]) {
			// printk("copy_pagetables : old[%x] has contents!\n", index);
			// do not consider the case of swap, kernel don't support swap
			tmp_pt = (unsigned int *) kmalloc(PAGE_SIZE);
			if (tmp_pt == NULL) {
				printk("copy_pagetables : kmalloc pt failed!\n");
				goto error2;
			}
			// set new pde
			++ptnr;
			pgd[index] &= OFFSET_MASK;
			pgd[index] |= (unsigned int) tmp_pt;
			// fill new page table
			old_pt = (unsigned int *) (old_pgd[index] & (~OFFSET_MASK));
			memcpy(tmp_pt, old_pt, PAGE_SIZE);
			// clean W bit
			for (i_pte = 0; i_pte < (PAGE_SIZE >> 2); ++i_pte) {
				if (tmp_pt[i_pte] && (index < (ROM_START >> PGD_SHIFT))) {
					// printk("copy_pagetables : %x\n", ((index << 22) + (i_pte << 12)));
					// clean write right(copy on write)
					clean_W(&(tmp_pt[i_pte]));
					clean_W(&(old_pt[i_pte]));
				}
			}
		}
	}

	// if new page tables are all modified successfully, then modify the old page tables
	// increase the corresponding page'reference
	for (index = (_USER_VIRT_START >> PGD_SHIFT), ptnr = 0; index < (ROM_START >> PGD_SHIFT); ++index) {
		if (old_pgd[index]) {
			// printk("copy_pagetables : old_pgd %x\n", index << PGD_SHIFT);
			inc_reference_by_pt((unsigned int *) (old_pgd[index] & (~OFFSET_MASK)));
		}
	}
	// printk("copy_pagetables : ready to flush_tlb!\n");
	flush_tlb(NULL);

	return pgd;
error2:
	if (ptnr) {
		for (index = (_USER_VIRT_START >> PAGE_SHIFT); (index < (PAGE_SIZE >> 2)) && ptnr; ++index) {
			if (pgd[index]) {
				old_pt = (unsigned int *) (old_pgd[index] & (~OFFSET_MASK));
				tmp_pt = (unsigned int *) (pgd[index] & (~OFFSET_MASK));
				if (old_pt == tmp_pt)
					kfree(tmp_pt);
				--ptnr;
			}
		}
	}
error1:
	kfree(pgd);
	return NULL;
}

union task_union *copy_mem(unsigned int *args, union task_union *old)
{
	union task_union *new = NULL;
	unsigned int *pgd;
	unsigned int new_pid = get_emptypid();
	struct task_struct *new_task;
	struct task_struct *old_task;
	struct regs_context *new_task_context;

	if (inavailed_pid(new_pid)) {
		printk("copy_mem failed : inavailed new pid\n");
		goto error1;
	}

	new = (union task_union *) kmalloc(sizeof(union task_union));
	if (!new) {
		printk("copy_mem failed : kmalloc return NULL\n");
		goto error2;
	}

	// printk("copy_mem : prepare to enter copy_pagetables!\n");
	if (!(pgd = (unsigned int *) copy_pagetables())) {
		printk("copy_mem failed : copy_pagetables failed\n");
		goto error3;
	}
	// printk("copy_mem : return from copy_pagetables!\n");

	memcpy(new, old, sizeof(union task_union));
	old_task = &(old->task);
	new_task = &(new->task);
	new_task_context = &(new_task->context);
	new_task->state = _TASK_BLOCKED;
	new_task->parent = old_task->pid;
	new_task->pid = new_pid;
	new_task->counter = _DEFAULT_TICKS;
	new_task->pgd = pgd;
	INIT_LIST_HEAD(&(new_task->node));
	INIT_LIST_HEAD(&(new_task->sched));
	memcpy(&(new_task->context), args, sizeof(struct regs_context));
	new_task_context->ra = (unsigned int) &_ctx_restore;

	add_tasks(&(new_task->node));
	// new created proc has the prioty to execute, put it to the head of list
	sched_insert_head(&ready_tasks, &(new_task->sched));
	new_task->state = _TASK_READY;

	return new;
error3:
	kfree(new);
error2:
	put_pid(new_pid);
error1:
	return NULL;
}

unsigned int do_fork(unsigned int *args)
{
	union task_union *new;
	struct regs_context *ctx;
	unsigned int res;
	struct task_struct *new_task;

	new = copy_mem(args, (union task_union *) current);
	// printk("do_fork : new addr(%x)", new);
	new_task = &(new->task);
	ctx = (struct regs_context *) (&(new_task->context));
	if (!new) {
		res = -1;
	} else {
		res = new_task->pid;
		ctx->v0 = 0;
	}

	// printk("do_fork : %x / %x\n", current->pid, new_task->pid);
	return res;
}

