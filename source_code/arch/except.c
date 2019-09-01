#include "except.h"
#include "../kern/vga/vga.h"
#include "arch.h"
#include "page.h"
#include "../kern/task/task.h"
#include "../kern/task/sched.h"
#include "../kern/mm/buddy.h"
#include "../kern/mm/slub.h"

void do_ri(unsigned int errArg, unsigned int errPc, unsigned int *regs)
{
	unsigned int inst = *((unsigned int *) errArg);
	unsigned int rs, rt;
	unsigned int va, mem, byte;
	
	rs = _STACK_OFFSET(_RS_(inst), regs);
	rt = _STACK_OFFSET(_RT_(inst), regs);
	va = rs + _OFFSET_(inst);
	byte = va & 3;

	switch (_CODE_(inst)) {
		case _CODE_LWL:
			mem = *(unsigned int *) (va - byte);
			mem = mem << multiply(3 - byte, 8);
			rt = (rt & ~(-1 << multiply(3 - byte, 8))) | mem;
			_STACK_OFFSET(_RT_(inst), regs) = rt;
			break;
		case _CODE_LWR:
			mem = *(unsigned int  *)(va - byte);
			mem = mem >> multiply(byte, 8);
			rt = (rt & ~(-1 >> multiply(byte, 8))) | mem;
			_STACK_OFFSET(_RT_(inst), regs) = rt;
			break;
		case _CODE_SWL:
			mem = *(unsigned int  *)(va - byte);
			mem = mem & ~(-1 >> multiply(3 - byte, 8));
			rt = (rt >> multiply(3 - byte, 8)) | mem;
			*((unsigned int  *)(va - byte)) = rt;
			break;
		case _CODE_SWR:
			mem = *(unsigned int  *)(va - byte);
			mem = mem & ~(-1 << multiply(byte, 8));
			rt = (rt << multiply(byte, 8)) | mem;
			*((unsigned int  *)(va - byte)) = rt;
			break;
		default:
			// printk("do_ri : unknown instruction!\n");
			while (1) ;
			break;
	}
	errPc += 4;
	_STACK_EPC(regs) = errPc;
}

void do_copyonwrite(unsigned int errArg, unsigned int errPc, unsigned int *regs)
{
	unsigned int *pgd = current->pgd;
	unsigned int pgd_index = errArg >> PGD_SHIFT;
	unsigned int pte_index = (errArg >> PAGE_SHIFT) & 0x3FF;
	unsigned int *pt;
	unsigned int index, pg;
	struct page *old;
	void *new;
	struct page *new_pg;

	// printk("do_copyonwrite : errArg(%x)\n", errArg);
	if (errArg < _KERNEL_VIRT_END || errArg >= ROM_START) {
		printk("Error : task(pid:%x) want write addr:%x(pc:%x), ", current->pid, errArg, errPc);
		printk("it must through kernel mode.\n");
		goto kill;
	}

	if (!pgd[pgd_index]) {
		printk("Impossible : pde is 0 while WriteFault\n");
		goto kill;
	}

	if (!is_P(&(pgd[pgd_index]))) {
		printk("Impossible : unpresent while WriteFault\n");
		goto kill;
	}

	if (!is_W(&(pgd[pgd_index]))) {
		printk("Impossible : pde non-write while WriteFault\n");
		goto kill;
	}

	pt = (unsigned int *) (pgd[pgd_index] & (~((1 << PAGE_SHIFT) - 1)));
	if (!pt[pte_index]) {
		printk("Impossible : pte is 0 while WriteFault\n");
		goto kill;
	}

	if (!is_P(&(pt[pte_index]))) {
		printk("Impossible : unpresent while WriteFault\n");
		goto kill;
	}

	if (is_W(&(pt[pte_index]))) {
		printk("Impossible : pte write while WriteFault\n");
		goto kill;
	}

	pg = pt[pte_index] & (~((1 << PAGE_SHIFT) - 1));
	old = pages + (pg >> PAGE_SHIFT);
	if (old->reference == 1) {
		// printk("old->reference: %x\n", old->reference);
		set_W(&(pt[pte_index]));
		goto ok;
	}

	new = kmalloc(PAGE_SIZE);
	if (!new) {
		printk("do_copyonwrite : kmalloc failed!\n");
		goto kill;
	}

	dec_reference(old, 1);
	// printk("old->reference: %x, old: %x, new: %x\n", old->reference, old, new);
	memcpy(new, (void *) pg, PAGE_SIZE);
	pt[pte_index] &= ((1 << PAGE_SHIFT) - 1);
	pt[pte_index] |= (unsigned int) new;
	// set reference
	new_pg = pages + (((unsigned int) new) >> PAGE_SHIFT);
	new_pg->reference = 1;
	// set W bit
	set_W(&(pt[pte_index]));
ok:
	flush_tlb(pgd);
	return;
kill:
	printk("Kill this task(pid:%x)!\n", current->pid);
	// ... kill the task!!!
	die();
}

void do_pg_unpresent(unsigned int errArg, unsigned int errPc, unsigned int *regs)
{
	void *newpg;

	// printk("errArg : %x\n", errArg);
	if (errArg < _KERNEL_VIRT_END) {
		printk("Error : task(pid:%x) want to access addr:%x(pc:%x), ", current->pid, errArg, errPc);
		printk("but it belongs to kernel address space.\n");
		printk("Kill this task(pid:%x)!\n", current->pid);
		// ... kill the task!!!
		die();		// because we don't have kill yet, die
	} else if (errArg < _TASK_CODE_START) {
		newpg = kmalloc(PAGE_SIZE);
		if (!newpg) {
			printk("do_pg_unpresent : kamlloc failed. (task-pid:%x, errarg:%x, errpc:%x)\n", current->pid, errArg, errPc);
			//...... kill the task!!!!
			die();
		}

		errArg &= ~(PAGE_SIZE - 1);
		// printk("errArg : %x\n", errArg);
		if (add_vmas(current, errArg, PAGE_SIZE)) {
			printk("do_pg_unpresent : kmalloc failed. (task-pid:%x, errarg:%x, errpc:%x)\n", current->pid, errArg, errPc);
			kfree(newpg);
			//...... kill the task!!!!
			die();
		}

		do_one_mapping(current->pgd, errArg, (unsigned int) newpg, USER_DEFAULT_ATTR);
	} else if (errArg < _TASK_HEAP_END) {
		// user code / static data cause, read from disk
		printk("Warn : task(pid:%x) miss code / data addr: %x, pc: %x", current->pid, errArg, errPc);
		die();
	} else if (errArg < ROM_START) {
		printk("Warn : task(pid:%x) miss heap addr: %x, pc: %x", current->pid, errArg, errPc);
		die();
	} else {		// IO space, can cause access right prob, but can't cause PF
		printk("Error : task(pid:%x) want to access addr:%x(pc:%x), ", current->pid, errArg, errPc);
		printk("it belongs to ROM/IO address space and impossible cause unpresent-fault\n");
		die();
	}
}

void do_exception(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{		
	if (!(status & _EXCEPT_FLAG))
		return;

	switch ((status & 0x0000f800) >> 11) {
		case _RESERVE_INST:
			do_ri(errArg, errPc, regs);
			break;
		case _PG_UNPRESENT:
			do_pg_unpresent(errArg, errPc, regs);
			break;
		case _PG_COPYONWRITE:
			do_copyonwrite(errArg, errPc, regs);
			break;
		default:
			printk("%x\n", (status & 0x0000f800) >> 11);
			printk("args:%x, epc:%x, regs:%x\n", errArg, errPc, regs);
			while(1) ;
			break;
	}
}

