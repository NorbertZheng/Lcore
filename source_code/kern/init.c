#include "../arch/arch.h"
#include "../arch/intr.h"
#include "../arch/page.h"
#include "vga/vga.h"
#include "time/time.h"
#include "key/key.h"
#include "../tool/tool.h"

extern unsigned char _end[];

void machine_info()
{
	printk("About this machine:\n");
	printk("\tCPU : %x MHz\n", get_cpu_hz());
	printk("\tRAM size is %x Bytes\n", get_phymm_size());
	printk("\tDisk size is %x Bytes\n", get_sd_size());
}

void init_kernel()
{
	unsigned int pg_end;
	unsigned int *config;

	// pg_end = init_pgtable();
	// enable_paging(pgd);
	init_exint();
	// init_vga((pg_end + 0x0000ffff) & 0xffff0000);
	init_vga(0x00110000);
	init_keyboard();
	init_time(_CLOCK_INTERVAL);
	machine_info();
	enable_intr(_INTR_GLOBAL | _INTR_CLOCK | _INTR_KEYB | _INTR_SPI);
}

