#include "arch.h"
#include "intr.h"
#include "../kern/list.h"

struct intr_block interrupt[_INTR_MAX];

void init_exint()
{
	unsigned int entry = _INTR_ENTRY;
	unsigned int index;

	for (index = 0; index < ARRAY_SIZE(interrupt); ++index) {
		interrupt[index].entry_fn = 0;
		INIT_LIST_HEAD(&interrupt[index].head);
	}

	memcpy(EXCEPT_ENTRY, &_exint_handler, ((unsigned char *) &_end_ex - (unsigned char *) &_exint_handler));

	asm volatile(
		"mtc0	%0, $3"
		:
		:"r"(entry)
	);

	disable_intr(_INTR_GLOBAL | _INTR_CLOCK | _INTR_KEYB | _INTR_SPI);
}

void do_interrupt(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	unsigned int icr, ier, index;
	
	if (!(status & _INTR_FLAG))
		return;
	
	ier = get_ier();
	icr = get_icr();
	icr = (icr & ier);
	
	if (icr & _INTR_CLOCK) {
		intr_fn intr_handler = interrupt[0].entry_fn;
		if (intr_handler) {
			intr_handler(regs, status, errArg, errPc);
			clean_icr(_INTR_CLOCK);
		}
	}

	if (icr & _INTR_KEYB) {
		intr_fn intr_handler = interrupt[3].entry_fn;
		if (intr_handler) {
			intr_handler(regs, status, errArg, errPc);
			clean_icr(_INTR_KEYB);
		}
	}

	if (icr & _INTR_SPI) {
		intr_fn intr_handler = interrupt[5].entry_fn;
		if (intr_handler) {
			intr_handler(regs, status, errArg, errPc);
			clean_icr(_INTR_SPI);
		}
	}
}

unsigned int enable_intr(unsigned int val)
{
	unsigned int old;
	
	asm volatile(
		"mfc0	%0, $4"
		:"=r"(old)
	);
	
	old |= val;
	
	asm volatile(
		"mtc0	%0, $4"
		:
		:"r"(old)
	);

	return old;
}

unsigned int disable_intr(unsigned int val)
{
	unsigned int old, tmp;

	asm volatile(
		"mfc0	%0, $4"
		:"=r"(old)
	);
	
	tmp = old;
	tmp &= (~val);

	asm volatile(
		"mtc0	%0, $4"
		:
		:"r"(tmp)
	);

	return old;
}

unsigned int get_timer()
{
	unsigned int interval;
	
	asm volatile(
		"mfc0	%0, $7"
		:"=r"(interval)
	);

	return interval;
}

void set_timer(unsigned int interval)
{
	interval &= 0x00000fff;
	
	asm volatile(
		"mtc0	%0, $7"
		:
		:"r"(interval)
	);
}

unsigned int get_ier()
{
	unsigned int ier;
	
	asm volatile(
		"mfc0	%0, $4"
		:"=r"(ier)
	);

	return ier;
}

void set_ier(unsigned int val)
{
	asm volatile(
		"mtc0	%0, $4"
		:
		:"r"(val)
	);
}

unsigned int get_icr()
{
	unsigned int icr;
	
	asm volatile(
		"mfc0	%0, $5"
		:"=r"(icr)
	);
	
	return icr;
}

void clean_icr(unsigned int val)
{
	val &= 0x7fffffff;
	
	asm volatile(
		"mtc0	%0, $5"
		:
		:"r"(val)
	);
}

unsigned int register_handler(intr_fn handler, unsigned int index)
{
	if (interrupt[index].entry_fn) {
		printk("KERN : Register_handler failed [unsigned int(%x) already registered!]\n", index);
		return 1;
	}

	interrupt[index].entry_fn = handler;
	return 0;
}

void unregister_handler(unsigned int index)
{
	if (!interrupt[index].entry_fn) {
		printk("WARN : unsigned int(%x) already unregistered!\n", index);
		return;
	}

	interrupt[index].entry_fn = 0;
}

unsigned int register_work(struct list_head *work_node, unsigned int index)
{
	if (!interrupt[index].entry_fn) {
		printk("KERN : Register_work failed [unsigned int(%x)'s handler not defined!]\n", index);
		return 1;
	}

	list_add_tail(work_node, &interrupt[index].head);
	return 0;
}

void unregister_work(struct list_head *work_node)
{
	if (!list_empty(work_node)) {
		printk("WARN : the WORK already unregistered!\n");
		return;
	}

	list_del_init(work_node);
}

