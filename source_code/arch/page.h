#ifndef _LCORE_PAGE_H
#define _LCORE_PAGE_H
/*
 * page table item ops
 */
#define set_val(p, paddr)	(*p = (paddr & 0xfffff000))
#define get_val(p)			((*p) & 0xfffff000)

#define set_pde(p, paddr)	set_val((p), paddr)
#define get_pde(p)			get_val((p))

#define set_pte(p, paddr)	set_val((p), paddr)
#define get_pte(p)			get_val((p))

#define clean(p)			(*(p) = 0)
#define clean_attr(p)		(*(p) &= 0xfffff000)

#define set_X(p)			(*(p) |= 0x00000008)
#define clean_X(p)			(*(p) &= 0xfffffff7)

#define set_W(p)			(*(p) |= 0x00000004)
#define clean_W(p)			(*(p) &= 0xfffffffb)
#define is_W(p)				(*(p) & 0x00000004)

#define set_U(p)			(*(p) |= 0x00000002)
#define clean_U(p)			(*(p) &= 0xfffffffd)

#define set_P(p)			(*(p) |= 0x00000001)
#define clean_P(p)			(*(p) &= 0xfffffffe)
#define is_P(p)				(*(p) & 0x00000001)

#define KERN_DEFAULT_ATTR	0x01
#define USER_DEFAULT_ATTR	0x0f

/*
 * kernel page table
 */
#define PAGE_SIZE			0x1000
#define PAGE_SHIFT			12

#define PGD_SHIFT			22
#define PTE_SHIFT			12
#define INDEX_MASK			0x3ff
#define OFFSET_MASK			0xfff

/*
 * virtual address space
 */
#define _KERNEL_VIRT_END	0x40000000
#define _USER_VIRT_START	_KERNEL_VIRT_END

extern unsigned int *pgd;

extern unsigned int init_pgtable();
extern void flush_tlb(unsigned int *pgd);
extern unsigned int get_pgbase();
extern void enable_paging();
extern void disable_paging();
extern void set_pgd_entry(unsigned int *pgd, unsigned int p_pt, unsigned int vaddr, unsigned int w, unsigned int u, unsigned int p);
extern void set_pt_entry(unsigned int *pt, unsigned int p_page, unsigned int vaddr, unsigned int w, unsigned int u, unsigned int p);
extern unsigned int do_one_mapping(unsigned int *pgd, unsigned int va, unsigned int pa, unsigned int attr);
extern void set_pagetable_attr(unsigned int *table, unsigned int attr);

#endif

