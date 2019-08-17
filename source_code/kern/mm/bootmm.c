#include "bootmm.h"
#include "../../arch/arch.h"
#include "../vga/print.h"
#include "../../tool/tool.h"

#define PAGE_SHIFT			12

struct bootmm boot_mm;

unsigned char *mem_msg[] = {"Kernel code/data", "Mm Bitmap", "Vga Buffer", "Kernel page directory", "Kernel page table", "Dynamic"};

void set_mminfo(struct bootmm_info *info, unsigned int start, unsigned int end, unsigned int type)
{
	info->start = start;
	info->end = end;
	info->type = type;
}

void remove_mminfo(struct bootmm *mm, unsigned int index)
{
	unsigned int tmp;
	
	if (index >= mm->cnt_infos)
		return;

	for (tmp = (index + 1); tmp != mm->cnt_infos; ++tmp) {
		mm->info[tmp - 1] = mm->info[tmp];
	}

	--(mm->cnt_infos);
}

void split_mminfo(struct bootmm *mm, unsigned int index, unsigned int split_start)
{
	unsigned int start, end;
	unsigned int tmp;
	struct bootmm_info *boot_mm_info_pointer = &(mm->info[index]);
	struct bootmm_info *boot_mm_info_next_pointer = &(mm->info[index + 1]);

	if (index + 1 >= MAX_INFO) {
		return;
	}

	start = boot_mm_info_pointer->start;
	end = boot_mm_info_pointer->end;
	split_start &= (~((1 << PAGE_SHIFT) - 1));

	if ((split_start <= start) || (split_start >= end))
		return;

	for (tmp = mm->cnt_infos - 1; tmp >= index; --tmp) {
		mm->info[tmp + 1] = mm->info[tmp];
	}

	boot_mm_info_pointer->end = split_start - 1;
	boot_mm_info_next_pointer->start = split_start;
	++(mm->cnt_infos);
}

void insert_mminfo(struct bootmm *mm, unsigned int start, unsigned int end, unsigned int type)
{
	unsigned int index;
	struct bootmm_info *mm_info, *mm_info_next;

	for (index = 0; index < mm->cnt_infos; ++index) {
		mm_info = &(mm->info[index]);
		if (mm_info->type != type) 
			continue;
		if (mm_info->end == start - 1) {
			if ((index + 1) << mm->cnt_infos) {
				mm_info_next = &(mm->info[index + 1]);
				if (mm_info_next->type != type)
					goto merge1;
				if (mm_info_next->start - 1 == end) {
					mm_info->end = mm_info_next->end;
					remove_mminfo(mm, index);
					return;
				}
			}
merge1:
			mm_info->end = end;
			return;
		}
	}

	set_mminfo(mm->info + mm->cnt_infos, start, end, type);
	++(mm->cnt_infos);
}

void init_bootmm()
{
	unsigned int index;
	unsigned char *t_map;
	struct bootmm *boot_mm_pointer = &boot_mm;

	boot_mm_pointer->phymm = get_phymm_size();
	boot_mm_pointer->max_pfn = boot_mm_pointer->phymm >> PAGE_SHIFT;

	boot_mm_pointer->s_map = _end + ((1 << PAGE_SHIFT) - 1);
	boot_mm_pointer->s_map = (unsigned char *) ((unsigned int) (boot_mm_pointer->s_map) & (~((1 << PAGE_SHIFT) - 1)));
	boot_mm_pointer->e_map = boot_mm_pointer->s_map + boot_mm_pointer->max_pfn;
	boot_mm_pointer->e_map += ((1 << PAGE_SHIFT) - 1);
	boot_mm_pointer->e_map = (unsigned char *) ((unsigned int) (boot_mm_pointer->e_map) & (~((1 << PAGE_SHIFT) - 1)));

	boot_mm_pointer->cnt_infos = 0;
	memset(boot_mm_pointer->s_map, PAGE_USED, boot_mm_pointer->e_map - boot_mm_pointer->s_map);
	insert_mminfo(&boot_mm, 0, (unsigned int) (boot_mm_pointer->s_map - 1), _MM_KERNEL);
	insert_mminfo(&boot_mm, (unsigned int) (boot_mm_pointer->s_map), (unsigned int) (boot_mm_pointer->e_map - 1), _MM_MMMAP);
	boot_mm_pointer->last_alloc = (((unsigned int) (boot_mm_pointer->e_map) >> PAGE_SHIFT) - 1);
	
	for (index = ((unsigned int) (boot_mm_pointer->e_map) >> PAGE_SHIFT); index < boot_mm_pointer->max_pfn; ++index) {
		boot_mm_pointer->s_map[index] = PAGE_FREE;
	}
}

void set_maps(unsigned int s_pfn, unsigned int cnt, unsigned int val)
{
	struct bootmm *boot_mm_pointer = &boot_mm;

	while (cnt) {
		boot_mm_pointer->s_map[s_pfn] = val;
		--cnt;
		++s_pfn;
	}
}

unsigned char *find_pages(unsigned int count, unsigned int s_pfn, unsigned int e_pfn, unsigned int align_pfn)
{
	unsigned int index, tmp;
	unsigned int cnt;
	struct bootmm *boot_mm_pointer = &boot_mm;

	s_pfn += (align_pfn - 1);
	s_pfn &= ~(align_pfn - 1);

	for (index = s_pfn; index < e_pfn; ) {
		if (boot_mm_pointer->s_map[index]) {
			++index;
			continue;
		}

		tmp = index;
		cnt = count;
		while (cnt) {
			if (tmp >= e_pfn)
				goto end;

			if (boot_mm_pointer->s_map[tmp])
				goto next;

			++tmp;
			--cnt;
		}

		boot_mm_pointer->last_alloc = index + count - 1;
		set_maps(index, count, PAGE_USED);
	
		return (unsigned char *) (index << PAGE_SHIFT);
next:
		index = tmp + align_pfn;
	}
end:
	return 0;
}

unsigned char *bootmm_alloc_pages(unsigned int size, unsigned int type, unsigned int align)
{
	unsigned int index, tmp;
	unsigned int cnt, t_cnt;
	unsigned char *res;
	struct bootmm *boot_mm_pointer = &boot_mm;

	size += ((1 << PAGE_SHIFT) - 1);
	size &= (~((1 << PAGE_SHIFT) - 1));
	cnt = size >> PAGE_SHIFT;

	res = find_pages(cnt, boot_mm_pointer->last_alloc + 1, boot_mm_pointer->max_pfn, align >> PAGE_SHIFT);
	if (res) {
		insert_mminfo(&boot_mm, (unsigned int) res, (unsigned int) res + size - 1, type);
		return res;
	}

	res = find_pages(cnt, 0, boot_mm_pointer->last_alloc, align >> PAGE_SHIFT);
	if (res)
		insert_mminfo(&boot_mm, (unsigned int) res, (unsigned int) res + size - 1, type);

	return res;
}

void bootmm_free_pages(unsigned int start, unsigned int size)
{
	unsigned int index, cnt;
	struct bootmm *boot_mm_pointer = &boot_mm;
	struct bootmm_info *boot_mm_info_pointer;

	size &= ~((1 << PAGE_SHIFT) - 1);
	cnt = size >> PAGE_SHIFT;
	if (!cnt)
		return;

	start &= ~((1 << PAGE_SHIFT) - 1);
	for (index = 0; index < boot_mm_pointer->cnt_infos; ++index) {
		boot_mm_info_pointer = &(boot_mm_pointer->info[index]);
		if (boot_mm_info_pointer->end < start)
			continue;
		if (boot_mm_info_pointer->start > start)
			continue;
		if (start + size - 1 > boot_mm_info_pointer->end)
			continue;
		break;
	}
	if (index == boot_mm_pointer->cnt_infos) {
		printk("bootmm_free_pages : not alloc space(%x:%x)\n", start, size);
		return;
	}

	set_maps(start >> PAGE_SHIFT, cnt, PAGE_FREE);
	if (boot_mm_info_pointer->start == start) {
		if (boot_mm_info_pointer->end == (start + size - 1))
			remove_mminfo(&boot_mm, index);
		else
			set_mminfo(&(boot_mm_pointer->info[index]), boot_mm_info_pointer->end, start + size - 1, boot_mm_info_pointer->type);
	} else {
		if (boot_mm_info_pointer->end == (start + size - 1))
			set_mminfo(&(boot_mm_pointer->info[index]), start + size, boot_mm_info_pointer->end, boot_mm_info_pointer->type);
		else {
			split_mminfo(&boot_mm, index, start);
			split_mminfo(&boot_mm, index + 1, start + size);
			remove_mminfo(&boot_mm, index + 1);
		}
	}
}

void bootmap_info(unsigned char *msg)
{
	unsigned int index;
	struct bootmm *boot_mm_pointer = &boot_mm;
	struct bootmm_info *boot_mm_info_pointer;

	printk("%s :\n", msg);
	for (index = 0; index < boot_mm_pointer->cnt_infos; ++index) {
		boot_mm_info_pointer = &(boot_mm_pointer->info[index]);
		printk("\t%x-%x : %s\n", boot_mm_info_pointer->start, boot_mm_info_pointer->end, mem_msg[boot_mm_info_pointer->type]);
	}
}

