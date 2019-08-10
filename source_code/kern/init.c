#include "../arch/arch.h"
#include "../arch/intr.h"
#include "../arch/page.h"
#include "vga/vga.h"
#include "../tool/tool.h"

void machine_info()
{

}

void init_kernel()
{
	init_exint();
	init_pgtable();
	enable_paging();
	init_vga(0x00010000);
	
	machine_info();
	enable_intr();
}

