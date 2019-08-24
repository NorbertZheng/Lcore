#include "../../arch/arch.h"
#include "../../tool/tool.h"
#include "buddy.h"
#include "slub.h"

struct kmem_cache kmalloc_caches[PAGE_SHIFT];

static unsigned int size_index[24] = {
	3	,	4	,	5	,	5	,	6	,	6	,
	6	,	6	,	1	,	1	,	1	,	1	,
	7	,	7	,	7	,	7	,	2	,	2	,
	2	,	2	,	2	,	2	,	2	,	2
};

struct kmem_cache *get_slub(unsigned int size)
{
	if (size <= 192)
		return kmalloc_caches + size_index[(size - 1) >> 3];
	
	return kmalloc_caches + (highest_set(size - 1) + 1);
}

void init_kmem_cpu(struct kmem_cache_cpu *kcpu)
{
	kcpu->page = 0;
	kcpu->freeobj = 0;
}

void init_kmem_node(struct kmem_cache_node *knode)
{
	INIT_LIST_HEAD(&(knode->full));
	INIT_LIST_HEAD(&(knode->partial));
}

void init_each_slub(struct kmem_cache *cache, unsigned int size)
{
	cache->objsize = size;
	cache->objsize += (BYTES_PER_LONG - 1);
	cache->objsize &= ~(BYTES_PER_LONG - 1);
	cache->offset = cache->objsize;
	cache->size = cache->objsize + sizeof(void *);
	init_kmem_cpu(&(cache->cpu));
	init_kmem_node(&(cache->node));
}

void init_slub()
{
	unsigned int order;
	struct kmem_cache *kmem_cache_pointer;

	init_each_slub(&(kmalloc_caches[1]), 96);
	init_each_slub(&(kmalloc_caches[2]), 192);

	for (order = 3; order <= 11; ++order) {
		init_each_slub(&(kmalloc_caches[order]), 1 << order);
	}

	printk("Setup Slub ok : \n");
	printk("\t");
	for (order = 1; order < PAGE_SHIFT; ++order) {
		kmem_cache_pointer = &(kmalloc_caches[order]);
		printk("%x ", kmem_cache_pointer->objsize);
	}
	printk("\n");
}

void format_slubpage(struct kmem_cache *cache, struct page *page)
{
	unsigned char *m = (unsigned char *) ((page - pages) << PAGE_SHIFT);
	struct slub_head *s_head = (struct slub_head *) m;
	unsigned int remaining = 1 << PAGE_SHIFT;
	unsigned int *ptr;
	struct kmem_cache_cpu *cache_cpu = &(cache->cpu);

	set_flag(page, _PAGE_SLUB);
	s_head->nr_objs = 0;
	do {
		ptr = (unsigned int *) (m + cache->offset);
		m += cache->size;
		*ptr = (unsigned int) m;
		remaining -= cache->size;
	} while (remaining >= cache->size);

	*ptr = (unsigned int) m & ~((1 << PAGE_SHIFT) - 1);
	s_head->end_ptr = (void **) ptr;
	s_head->nr_objs = 0;
	cache_cpu->page = page;
	cache_cpu->freeobj = (void **) (*ptr + cache->offset);
	page->private = (unsigned int) (*(cache_cpu->freeobj));
	page->virtual = (void *) cache;
}

void *slub_alloc(struct kmem_cache *cache)
{
	struct slub_head *s_head;
	void *object = 0;
	struct page *new;
	struct kmem_cache_cpu *cache_cpu = &(cache->cpu);
	struct kmem_cache_node *cache_node = &(cache->node);
	struct list_head *cache_node_partial_next;

	if (cache_cpu->freeobj)
		object = *(cache_cpu->freeobj);

check:
	if (is_bound((unsigned int) object, 1 << PAGE_SHIFT)) {
		if (cache_cpu->page) {
			list_add_tail(&(cache_cpu->page->list), &(cache_node->full));
		}

		if (list_empty(&(cache_node->partial))) {
			goto new_slub;
		}

		cache_node_partial_next = cache_node->partial.next;
		cache_cpu->page = container_of(cache_node_partial_next, struct page, list);
		list_del(cache_node_partial_next);
		object = (void *) (cache_cpu->page->private);
		cache_cpu->freeobj = (void **) ((unsigned char *) object + cache->offset);
		goto check;
	}

	cache_cpu->freeobj = (void **) ((unsigned char *) object + cache->offset);
	cache_cpu->page->private = (unsigned int) (*(cache_cpu->freeobj));
	s_head = (struct slub_head *) ((cache_cpu->page - pages) << PAGE_SHIFT);
	++(s_head->nr_objs);
	if (is_bound(cache_cpu->page->private, 1 << PAGE_SHIFT)) {
		list_add_tail(&(cache_cpu->page->list), &(cache_node->full));
		init_kmem_cpu(&(cache->cpu));
	}
	return object;

new_slub:
	new = _alloc_pages(0);
	if (!new) {
		printk("ERROR : slub_alloc error!\n");
		die();
	}

	printk("\n *** %x\n", new - pages);

	format_slubpage(cache, new);
	object = *(cache_cpu->freeobj);
	goto check;
}

void slub_free(struct kmem_cache *cache, void *obj)
{
	struct page *page = pages + ((unsigned int) obj >> PAGE_SHIFT);
	struct slub_head *s_head = (struct slub_head *) ((page - pages) << PAGE_SHIFT);

	unsigned int *ptr;

	if (!(s_head->nr_objs)) {
		printk("ERROR : slub_free error!\n");
		die();
	}

	ptr = (unsigned int *) ((unsigned char *) obj + cache->offset);
	*ptr = *((unsigned int *) (s_head->end_ptr));
	*((unsigned int *) (s_head->end_ptr)) = (unsigned int) obj;
	--(s_head->nr_objs);

	if (list_empty(&(page->list)))
		return;

	if (!(s_head->nr_objs)) {
		_free_pages(page, 0, 0);
		return;
	}

	list_del_init(&(page->list));
	list_add_tail(&(page->list), &(cache->node.partial));
}

void *kmalloc(unsigned int size)
{
	struct kmem_cache *cache;

	if (!size)
		return 0;

	if (size > kmalloc_caches[PAGE_SHIFT - 1].objsize) {
		size += ((1 << PAGE_SHIFT) - 1);
		size &= ~((1 << PAGE_SHIFT) - 1);
		return alloc_pages(size >> PAGE_SHIFT);
	}

	cache = get_slub(size);
	if (!cache) {
		printk("ERROR : kmalloc error!\n");
		die();
	}

	return slub_alloc(cache);
}

void kfree(void *obj)
{
	struct page *page;

	page = pages + ((unsigned int) obj >> PAGE_SHIFT);
	if (!has_flag(page, _PAGE_SLUB))
		return free_pages((void *) ((unsigned int) obj & ~((1 << PAGE_SHIFT) - 1)), page->private);

	return slub_free(page->virtual, obj);
}

