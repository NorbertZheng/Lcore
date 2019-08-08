#include "arch.h"
#include "intr.h"

void init_exint()
{
	unsigned int entry = _INTR_ENTRY;
	memcpy(EXCEPT_ENTRY, &_exint_handler, ((unsigned char *) &_end_ex - (unsigned char *) &_exint_handler));

	asm volatile(
		"mtc0	%0, $3"
		:
		:"r"(entry)
	);
}

void enable_global()
{

}

void disable_global()
{

}

void enable_intr()
{

}

void disable_intr()
{

}

