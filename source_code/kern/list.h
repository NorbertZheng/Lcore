#ifndef _LCORE_LIST_H
#define _LCORE_LIST_H

#include "kern.h"

#define LIST_POISON1			(void *) 0x10101010
#define LIST_POISON2			(void *) 0x20202020

/*
 * 2 operations on linkedlist element initialization
 */
#define LIST_HEAD_INIT(name)	{&(name), &(name)}

#define LIST_HEAD(name)			struct list_head name = LIST_HEAD_INIT(name)

static inline void INIT_LIST_HEAD(struct list_head *list)
{
	list->prev = list;
	list->next = list;
}

/*
 * insert new between prev and next
 */
static inline void _list_add(struct list_head *new, struct list_head *prev, struct list_head *next)
{
	new->next = next;
	new->prev = prev;
	prev->next = new;
	next->prev = new;
}

/*
 * add new behind head
 */
static inline void list_add(struct list_head *new, struct list_head *head)
{
	_list_add(new, head, head->next);
}

/*
 * add new before head
 */
static inline void list_add_tail(struct list_head *new, struct list_head *head)
{
	_list_add(new, head->prev, head);
}

/*
 * cut the part between prev and next
 */
static inline void _list_del(struct list_head *prev, struct list_head *next)
{
	prev->next = next;
	next->prev = prev;
}

/*
 * delete entry from linkedlist
 */
static inline void list_del(struct list_head *entry)
{
	_list_del(entry->prev, entry->next);
	entry->prev = LIST_POISON1;
	entry->next = LIST_POISON2;
}

/*
 * delete entry from linkedlist and init entry again
 */
static inline void list_del_init(struct list_head *entry)
{
	_list_del(entry->prev, entry->next);
	INIT_LIST_HEAD(entry);
}

/*
 * delete entry from its linkedlist and add it to another linkedlist head
 */
static inline void list_move(struct list_head *entry, struct list_head *head)
{
	_list_del(entry->prev, entry->next);
	list_add(entry, head);
}

static inline void list_move_tail(struct list_head *entry, struct list_head *head)
{
	_list_del(entry->prev, entry->next);
	list_add_tail(entry, head);
}

static inline unsigned int list_empty(struct list_head *head)
{
	return head->next == head;
}

/*
 * get its struct
 */
#define list_entry(ptr, type, member)		container_of(ptr, type, member)

/*
 * from head to tail
 * if may cause error, please use list_for_each_safe
 */
#define list_for_each(pos, head)			for (pos = (head)->next; pos != (head); pos = pos->next)

#define list_for_each_safe(pos, n, head)	for (pos = (head)->next, n = pos->next; pos != (head); pos = n, n = pos->next)

#endif
	
