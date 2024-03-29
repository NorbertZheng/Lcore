#include "../arch/arch.h"
#include "../arch/intr.h"
#include "../arch/page.h"
#include "vga/vga.h"
#include "time/time.h"
#include "key/key.h"
#include "mm/bootmm.h"
#include "mm/buddy.h"
#include "mm/slub.h"
#include "task/task.h"
#include "call/syscall.h"
#include "../tool/tool.h"

extern void main();

void machine_info()
{
	printk("About this machine:\n");
	printk("\tCPU : %x MHz\n", get_cpu_hz());
	printk("\tRAM size is %x Bytes\n", get_phymm_size());
	printk("\tDisk size is %x Bytes\n", get_sd_size());
}

void init_kernel()
{
	init_exint();
	init_bootmm();
	init_vga();
	init_keyboard();
	init_time(_CLOCK_INTERVAL);
	init_pgtable();
	enable_paging(pgd);
	bootmap_info("BootMM info");
	init_buddy();
	init_slub();
	init_task((void *) firstusercode_start);
	init_sched();
	init_syscall();
	machine_info();
	enable_intr(_INTR_GLOBAL | _INTR_CLOCK | _INTR_KEYB | _INTR_SPI);
}

