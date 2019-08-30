#include "arch.h"
#include "page.h"
#include "../tool/tool.h"
#include "../kern/mm/bootmm.h"
#include "../kern/task/vma.h"
#include "../kern/task/task.h"
#include "../kern/task/sched.h"

unsigned int *pgd;

void flush_tlb(unsigned int *pgd)
{
	if (pgd == NULL) {
		pgd = (unsigned int *) get_pgbase();
	}

	enable_paging(pgd);
}

/*
 * init kernel page table
 * return the end of last page table(followed by vga buffer)
 */
unsigned int init_pgtable()
{
	unsigned int *pt;
	unsigned int vaddr = 0;		// incr 4MB
	unsigned int paddr = 0;		// incr 4KB
	unsigned int index, n;
	unsigned int max_mm = get_phymm_size();

	pgd = (unsigned int *) bootmm_alloc_pages(PAGE_SIZE, _MM_PDTABLE, PAGE_SIZE);
	if ((unsigned int) pgd == 0)
		die();
	
	/*
	 * kernel normal ram pt
	 */
	while (paddr < max_mm) {
		pt = (unsigned int *) bootmm_alloc_pages(PAGE_SIZE, _MM_PTABLE, PAGE_SIZE);
		if ((unsigned int) pt == 0)
			die();
		set_pgd_entry(pgd, (unsigned int) pt, vaddr, 1, 0, 1);
		for (index = 0; index < 1024; ++index) {
			set_pt_entry(pt, paddr, paddr, 1, 0, 1);
			paddr += PAGE_SIZE;
		}
		vaddr += (PAGE_SIZE * 1024);
	}

	/*
	 * ROM/IO addr pt
	 */
	vaddr = ROM_START;
	paddr = ROM_START;
	n = 4;		// IO addr 16MB = 4 x 4MB
	while(n--) {
		pt = (unsigned int *) bootmm_alloc_pages(PAGE_SIZE, _MM_PTABLE, PAGE_SIZE);
		if ((unsigned int) pt == 0)
			die();
		set_pgd_entry(pgd, (unsigned int) pt, vaddr, 1, 0, 1);
		for (index = 0; index < 1024; ++index) {
			set_pt_entry(pt, paddr, paddr, 1, 0, 1);
			paddr += PAGE_SIZE;
		}
		vaddr += (PAGE_SIZE * 1024);
	}
	
	return ((unsigned int) pt + PAGE_SIZE);
}

unsigned int do_one_mapping(unsigned int *pgd, unsigned int va, unsigned int pa, unsigned int attr)
{
	unsigned int pde_index = (va >> PGD_SHIFT) & INDEX_MASK;
	unsigned int pte_index = (va >> PTE_SHIFT) & INDEX_MASK;
	unsigned int *pt;

	pt = (unsigned int *) kmalloc(PAGE_SIZE);
	if (pt == NULL) {
		return 1;		// err
	}

	pgd[pde_index] = (unsigned int) pt & (~OFFSET_MASK);
	pgd[pde_index] |= attr;
	pa &= (~OFFSET_MASK);
	attr &= OFFSET_MASK;
	pa |= attr;
	pt[pte_index] = pa;

	flush_tlb(pgd);

	return 0;
}

void set_pagetable_attr(unsigned int *table, unsigned int attr)
{
	unsigned int index, tmp;

	for (index = 0; index < 1024; ++index) {
		table[index] &= (~OFFSET_MASK);
		attr &= OFFSET_MASK;
		table[index] |= attr;
	}

	flush_tlb(NULL);
}

unsigned int get_pgbase()
{
	unsigned int pgd;

	asm volatile(
		"mfc0	%0, $6"
		:"=r"(pgd)
	);

	return pgd;
}

void enable_paging(unsigned int *pgd)
{
	unsigned int val = ((int) pgd | 0x1);
	asm	volatile(
		"mtc0	%0, $6"
		:
		:"r"(val)
	);
}

void disable_paging()
{
	unsigned int val;
	asm volatile(
		"mfc0	%0, $6"
		:"=r"(val)
	);
	val &= 0xfffffffe;		// clean bit-0 to close pageing
	asm volatile(
		"mtc0	%0, $6"
		:
		:"r"(val)
	);
}

void set_pgd_entry(unsigned int *pgd, unsigned int p_pt, unsigned int vaddr, unsigned int w, unsigned int u, unsigned int p)
{
	unsigned int index = ((vaddr >> PGD_SHIFT) & INDEX_MASK);
	unsigned int *pde = pgd + index;
	clean(pde);
	set_pde(pde, p_pt);
	set_X(pde);
	if(p)
		set_P(pde);
	if(w)
		set_W(pde);
	if(u)
		set_U(pde);
}

void set_pt_entry(unsigned int *pt, unsigned int p_page, unsigned int vaddr, unsigned int w, unsigned int u, unsigned int p)
{
	unsigned int index = ((vaddr >> PTE_SHIFT) & INDEX_MASK);
	unsigned int *pte = pt + index;
	clean(pte);
	set_pte(pte, p_page);
	set_X(pte);
	if (p)
		set_P(pte);
	if (w)
		set_W(pte);
	if (u)
		set_U(pte);
}

