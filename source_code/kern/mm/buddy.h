#ifndef _LCORE_BUDDY_H
#define _LCORE_BUDDY_H

#include "../list.h"
#include "../lock/lock.h"

#define _PAGE_RESERVED			(1 << 31)
#define _PAGE_ALLOCED			(1 << 30)
#define _PAGE_SLUB				(1 << 29)

struct page {
	unsigned int flag;
	unsigned int reference;
	struct list_head list;
	void *virtual;
	unsigned int private;
};

#define PAGE_SHIFT				12
#define MAX_BUDDY_ORDER			4

struct freelist {
	unsigned int nr_free;
	struct list_head free_head;
};

struct buddy_sys {
	unsigned int buddy_start_pfn;
	unsigned int buddy_end_pfn;
	struct page *start_page;
	struct lock_t lock;
	struct freelist freelist[MAX_BUDDY_ORDER + 1];
};

#define set_order(page, order)		set_private(page, order)
#define clear_order(page)			set_private(page, -1)
#define set_flag(page, val)			((page)->flag |= (val))
#define clear_flag(page, val)		((page)->flag &= ~(val))
#define has_flag(page, val)			(((page)->flag) & val)
#define set_reference(page, val)	((page)->reference = val)
#define inc_reference(page, val)	((page)->reference += val)
#define dec_reference(page, val)	((page)->reference -= val)

extern struct page *pages;
extern struct buddy_sys buddy;

extern void _free_pages(struct page *page, unsigned int order, unsigned int init_flag);
extern struct page *_alloc_pages(unsigned int order);
extern void free_pages(void *addr, unsigned int order);
extern void *alloc_pages(unsigned int order);
extern void buddy_info();
extern void init_buddy();

#endif

