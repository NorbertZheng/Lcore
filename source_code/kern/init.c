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
	pg_end = enable_paging();
	init_vga((pg_end + 0x0000ffff) & 0xffff0000);
	
	machine_info();
	enable_intr();
}

