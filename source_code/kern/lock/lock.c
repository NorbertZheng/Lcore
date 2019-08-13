#include "../../arch/intr.h"
#include "lock.h"

void init_lock(struct lock_t *lock)
{
	lock->spin = 0;
	INIT_LIST_HEAD(&(lock->wait));
}

unsigned int lockup(struct lock_t *lock)
{
	unsigned int old_ier;
	
	old_ier = disable_intr(_INTR_GLOBAL);
	if (lock->spin) {

	}
	
	lock->spin = 1;
	if (old_ier & _INTR_GLOBAL) {
		enable_intr(_INTR_GLOBAL);
	}

	return 1;
}

unsigned int unlock(struct lock_t *lock)
{
	unsigned int old_ier;
	
	old_ier = disable_intr(_INTR_GLOBAL);
	if (lock->spin) {
		lock->spin = 0;
	}
	if (old_ier & _INTR_GLOBAL) {
		enable_intr(_INTR_GLOBAL);
	}
	return 1;
}

