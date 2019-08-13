#ifndef _LCORE_LOCK_H
#define _LCORE_LOCK_H

#include "../list.h"

struct lock_t {
	unsigned int spin;
	struct list_head wait;		// wait for unlock
};

extern void init_lock(struct lock_t *lock);
extern unsigned int lockup(struct lock_t *lock);
extern unsigned int unlock(struct lock_t *lock);

#endif

