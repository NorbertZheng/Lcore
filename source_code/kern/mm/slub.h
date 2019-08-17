#ifndef _LCORE_SLUB_H
#define _LCORE_SLUB_H

#include "buddy.h"
#include "../list.h"

struct slub_head {
	void **end_ptr;
	unsigned int nr_objs;
};

struct kmem_cache_node {
	struct list_head partial;
	struct list_head full;
};

struct kmem_cache_cpu {
	void **freeobj;
	struct page *page;
};

struct kmem_cache {
	unsigned int size;
	unsigned int objsize;
	unsigned int offset;
	struct kmem_cache_node node;
	struct kmem_cache_cpu cpu;
	unsigned char name[16];
};

extern void init_slub();
extern void *kmalloc(unsigned int size);
extern void kfree(void *obj);

#endif

