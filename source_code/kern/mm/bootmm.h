#ifndef _LCORE_BOOTMM_H
#define _LCORE_BOOTMM_H

extern unsigned char _end[];

enum mm_usage {
	_MM_KERNEL,
	_MM_MMMAP,
	_MM_VGABUFF,
	_MM_PDTABLE,
	_MM_PTABLE,
	_MM_DYNAMIC,
	_MM_COUNT
};

struct bootmm_info {
	unsigned int start;
	unsigned int end;
	unsigned int type;
};

#define PAGE_FREE			0x00
#define PAGE_USED			0xff

#define MAX_INFO			10
struct bootmm {
	unsigned int phymm;
	unsigned int max_pfn;
	unsigned char *s_map;
	unsigned char *e_map;
	unsigned int last_alloc;
	unsigned int cnt_infos;
	struct bootmm_info info[MAX_INFO];
};

extern struct bootmm boot_mm;
extern void insert_mminfo(struct bootmm *mm, unsigned int start, unsigned int end, unsigned int type);
extern void init_bootmm();
extern unsigned char *bootmm_alloc_pages(unsigned int size, unsigned int type, unsigned int align);
extern void bootmm_free_pages(unsigned int start, unsigned int size);
extern void bootmap_info(unsigned char *msg);

#endif

