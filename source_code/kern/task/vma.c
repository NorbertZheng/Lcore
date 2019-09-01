#include "vma.h"
#include "task.h"
#include "../../arch/arch.h"
#include "../../arch/page.h"
#include "../mm/buddy.h"
#include "../mm/slub.h"

void del_vma(struct vma *vma)
{
	list_del_init(&(vma->node));		// drop it totally
	kfree(vma);
}

struct vma *new_vma(unsigned int va, unsigned int vend)
{
	struct vma *tmp;

	tmp = kmalloc(sizeof(struct vma));
	if (!tmp) {
		return NULL;
	}

	INIT_LIST_HEAD(&(tmp->node));
	tmp->start = va;
	tmp->vend = vend;
	tmp->cnt = ((vend - va) >> PAGE_SHIFT) + 1;

	return tmp;
}

/*
 * seprate vma: vma->start ~ (va - 1) and va ~ vma->end 2 parts
 * caller to check
 */
struct vma *seprate_vma(struct vma *vma, unsigned int va)
{
	struct vma *new;
	struct list_head *vma_node = &(vma->node);

	if (!(new == new_vma(va, vma->vend))) {
		printk("seprate_vma : new_vma failed!\n");
		return NULL;
	}

	vma->vend = va - 1;
	list_add(&(new->node), vma_node->next);

	return new;
}

unsigned int verify_vma(struct task_struct *task, unsigned int *va, unsigned int *vend, struct list_head **target)
{
	*target = NULL;
	(*va) &= ~(PAGE_SIZE - 1);
	(*vend) += (PAGE_SIZE - 1);
	(*vend) &= ~(PAGE_SIZE - 1);
	(*vend)--;

	if ((*va) < _KERNEL_VIRT_END) {
		printk("add_vmas : va(%x) inavailed (kernel space)!\n", (*va));
		return 1;
	} else if ((*va) < _TASK_CODE_START) {
		if ((*vend) >= _TASK_CODE_START) {
			printk("add_vmas : vma(%x:%x) exceed!\n", (*va), (*vend));
			return 1;
		} else {
			// printk("verify_vmas : stack_vma_head!\n");
			*target = &(task->stack_vma_head);
		}
	} else if ((*va) < _TASK_HEAP_END) {
		if ((*vend) >= _TASK_HEAP_END) {
			printk("add_vmas : vma(%x:%x) exceed!\n", (*va), (*vend));
			return 1;
		} else {
			*target = &(task->user_vma_head);
		}
	} else if ((*va) < ROM_START) {
		if ((*vend) >= ROM_START) {
			printk("add_vmas : vma(%x:%x) exceed!\n", (*va), (*vend));
			return 1;
		} else {
			*target = &(task->heap_vma_head);
		}
	} else {		// >= ROM_START
		printk("add_vmas : va(%x) inavailed (rom/io space)!\n", (*va));
		return 1;
	}

	return 0;
}

/*
 * return 1 on error, 0 success
 */
unsigned int add_vmas(struct task_struct *task, unsigned int va, unsigned int size)
{
	struct list_head *target;
	struct list_head *pos;
	struct list_head *n;
	struct vma *tmp;
	struct list_head *prev;
	struct list_head *tmp_node;
	unsigned int vend = va + size;

	if (verify_vma(task, &va, &vend, &target))
		return 1;

	prev = target;
	list_for_each_safe(pos, n, target) {
		tmp = container_of(pos, struct vma, node);
		if (va < tmp->start) {
			if (vend < tmp->start) {
				if ((vend + 1) == tmp->start) {
					tmp->start = va;
					tmp->cnt = ((tmp->vend - tmp->start) >> PAGE_SHIFT) + 1;
					goto ok;
				} else {
					tmp_node = &(tmp->node);
					prev = tmp_node->prev;
					goto new;
				}
			} else if (vend >= tmp->vend) {
				del_vma(tmp);
				continue;
			} else {
				tmp->start = va;
				tmp->cnt = ((tmp->vend - tmp->start) >> PAGE_SHIFT) + 1;
				goto ok;
			}
		} else if (va < tmp->vend) {
			if (vend <= tmp->vend) {
				goto ok;
			} else {
				va = tmp->start;
				del_vma(tmp);
				continue;
			}
		} else {
			if ((va - 1) == tmp->vend) {
				va = tmp->start;
				del_vma(tmp);
				continue;
			} else {
				prev = &(tmp->node);
				continue;
			}
		}
	}
new:
	if (!(tmp = new_vma(va, vend))) {
		printk("insert_vma : new_vma failed!\n");
		return 1;
	}

	list_add(&(tmp->node), prev);
ok:
	return 0;
}

// 0 success, 1 fail
unsigned int delete_vmas(struct task_struct *task, unsigned int va, unsigned int vend)
{
	struct list_head *pos;
	struct vma *tmp, *n;
	struct list_head *list;

	if (verify_vma(task, &va, &vend, &list))
		return 1;

	list_for_each(pos, list) {
		tmp = container_of(pos, struct vma, node);
		if (va < tmp->start) {
			return 1;
		} else if (va == tmp->start) {
			if (vend == tmp->vend) {
				del_vma(tmp);
				return 0;
			} else if (vend < tmp->vend) {
				tmp->start = vend + 1;
				return 0;
			} else {
				return 1;
			}
		} else if (va < tmp->vend) {
			if (vend == tmp->vend) {
				tmp->vend = va - 1;
				return 0;
			} else if (vend < tmp->vend) {
				n = seprate_vma(tmp, va);
				if (!n) {
					printk("delete_vmas : seprate_vma failed!\n");
					return 1;
				}
				tmp = n;
				n = seprate_vma(n, vend + 1);
				if (!n) {
					printk("delete_vmas : seprate_vma failed!\n");
					return 1;
				}
				del_vma(tmp);
				return 0;
			} else {
				return 1;
			}
		} else {
			continue;
		}
	}

	return 0;
}

