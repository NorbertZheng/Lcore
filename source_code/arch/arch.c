#include "arch.h"

/*
 * machine params
 */
unsigned int get_phymm_size()
{
	return MACHINE_MMSIZE;
}

unsigned int get_sd_size()
{
	return MACHINE_SDSIZE;
}

unsigned int get_cpu_hz()
{
	return MACHINE_CPUHZ;
}

