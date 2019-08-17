#include "../../arch/arch.h"
#include "bootmm.h"
#include "buddy.h"
#include "../kern.h"
#include "../../tool/tool.h"

unsigned int kernel_start_pfn, kernel_end_pfn;

struct page *pages;
struct buddy_sys buddy;

void set_private(struct page *page, unsigned int order)
{
	page->private = order;
}

void init_pages(unsigned int start_pfn, unsigned int end_pfn)
{
	unsigned int index;
	struct page *curr_page;

	for (index = start_pfn; index < end_pfn; ++index) {
		clear_flag(pages + index, -1);
		set_flag(pages + index, _PAGE_RESERVED);
		curr_page = pages + index;
		curr_page->reference = 0;
		curr_page->virtual = 0;
		curr_page->private = -1;
		INIT_LIST_HEAD(&(curr_page->list));
	}
}

void buddy_info()
{
	unsigned int index;
	struct buddy_sys *buddy_pointer = &buddy;
	struct freelist *buddy_freelist_pointer;

	printk("Setup Buddy-system : \n");
	printk("\tstart page-frame number : %x\n", buddy_pointer->buddy_start_pfn);
	printk("\tend page-frame number : %x\n", buddy_pointer->buddy_end_pfn);
	for (index = 0; index <= MAX_BUDDY_ORDER; ++index) {
		buddy_freelist_pointer = &(buddy_pointer->freelist[index]);
		printk("\t(%x)# : %x frees\n", index, buddy_freelist_pointer->nr_free);
	}
}

void init_buddy()
{
	unsigned int index = sizeof(struct page);
	struct bootmm *boot_mm_pointer = &boot_mm;
	struct bootmm_info *boot_mm_info_pointer;
	struct buddy_sys *buddy_pointer = &buddy;
	struct freelist *buddy_freelist_pointer;

	pages = (struct page *) bootmm_alloc_pages(multiply(sizeof(struct page), boot_mm_pointer->max_pfn), _MM_KERNEL, 1 << PAGE_SHIFT);
	if (!pages) {
		printk("\nERROR : bootmm_alloc_pages failed!n");
		die();
	}

	init_pages(0, boot_mm_pointer->max_pfn);

	kernel_start_pfn = 0;
	kernel_end_pfn = 0;
	for (index = 0; index < boot_mm_pointer->cnt_infos; ++index) {
		boot_mm_info_pointer = &(boot_mm_pointer->info[index]);
		if (boot_mm_info_pointer->end > kernel_end_pfn)
			kernel_end_pfn = boot_mm_info_pointer->end;
	}
	kernel_end_pfn >>= PAGE_SHIFT;

	buddy_pointer->buddy_start_pfn = (kernel_end_pfn + (1 << MAX_BUDDY_ORDER) - 1) & ~((1 << MAX_BUDDY_ORDER) - 1);
	buddy_pointer->buddy_end_pfn = boot_mm_pointer->max_pfn & ~((1 << MAX_BUDDY_ORDER) - 1);
	for (index = 0; index <= MAX_BUDDY_ORDER; ++index) {
		buddy_freelist_pointer = &(buddy_pointer->freelist[index]);
		buddy_freelist_pointer->nr_free = 0;
		INIT_LIST_HEAD(&(buddy_freelist_pointer->free_head));
	}
	buddy_pointer->start_page = pages + buddy_pointer->buddy_start_pfn;
	init_lock(&(buddy_pointer->lock));

	for (index = buddy_pointer->buddy_start_pfn; index < buddy_pointer->buddy_end_pfn; ++index) {
		free_pages(pages + index, 0);
	}

	buddy_info();
}

unsigned int is_buddy(struct page *buddy, unsigned int order)
{
	return (buddy->private == order);
}

void _free_pages(struct page *page, unsigned int order)
{
	unsigned int page_idx, buddy_idx;
	unsigned int combined_idx;
	struct page *buddy_page;
	struct buddy_sys *buddy_pointer;
	struct freelist *buddy_freelist_pointer;

	clear_flag(page, -1);

	lockup(&(buddy_pointer->lock));

	page_idx = page - buddy_pointer->start_page;
	while (order < MAX_BUDDY_ORDER) {
		buddy_idx = page_idx ^ (1 << order);
		buddy_page = page + (buddy_idx - page_idx);
		if (!is_buddy(buddy_page, order))
			break;
		list_del_init(&buddy_page->list);
		buddy_freelist_pointer = &(buddy_pointer->freelist[order]);
		--(buddy_freelist_pointer->nr_free);
		clear_order(buddy_page);
		combined_idx = buddy_idx & page_idx;
		page += (combined_idx - page_idx);
		page_idx = combined_idx;
		++order;
	}
	set_order(page, order);
	buddy_freelist_pointer = &(buddy_pointer->freelist[order]);
	list_add(&(page->list), &(buddy_freelist_pointer->free_head));
	++(buddy_freelist_pointer->nr_free);
	
	unlock(&(buddy_pointer->lock));
}

struct page *_alloc_pages(unsigned int order)
{
	unsigned int current_order, size;
	struct page *page, *buddy_page;
	struct freelist *free;
	struct buddy_sys *buddy_pointer;

	lockup(&(buddy_pointer->lock));

	for (current_order = order; current_order <= MAX_BUDDY_ORDER; ++current_order) {
		free = buddy_pointer->freelist + current_order;
		if (!list_empty(&(free->free_head))) 
			goto found;
	}

	unlock(&(buddy_pointer->lock));
	return 0;

found:
	page = container_of(free->free_head.next, struct page, list);
	list_del_init(&(page->list));
	clear_order(page);
	set_flag(page, _PAGE_ALLOCED);
	--(free->nr_free);
	
	size = 1 << current_order;
	while (current_order > order) {
		--free;
		--current_order;
		size >>= 1;
		buddy_page = page + size;
		list_add(&(buddy_page->list), &(free->free_head));
		++(free->nr_free);
		set_order(buddy_page, current_order);
	}

	unlock(&(buddy_pointer->lock));
	return page;
}

void *alloc_pages(unsigned int order)
{
	struct page *page = _alloc_pages(order);

	if (!page)
		return 0;

	return (void *) ((page - pages) << PAGE_SHIFT);
}

void free_pages(void *addr, unsigned int order)
{
	_free_pages(pages + ((unsigned int) addr >> PAGE_SHIFT), order);
}

