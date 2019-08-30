#include "syscall.h"
#include "../kern.h"
#include "../task/task.h"
#include "../vga/print.h"
#include "../mm/slub.h"
#include "../vga/vga.h"

unsigned int nr_syscall = -1;
void *syscall_table[MAX_SYSCALL];

static void _return_value(unsigned int *args, unsigned int val)
{
	args[V0_OFFSET] = val;
}

SYSCALL_DEFINE0(sys_reserved)
{
	printk("Unused syscall-0\n");
	_return_value(args, 0);
}

SYSCALL_DEFINE0(sys_fork)
{
	unsigned int r = do_fork(args);
	_return_value(args, r);
}

SYSCALL_DEFINE3(sys_putchar, unsigned int, ch, unsigned int, bg, unsigned int, fg)
{
	put_char(ch, bg, fg, VGA_ROW_CONSOLE);
}

void add_syscall(void *func, unsigned int no)
{
	syscall_table[no] = func;
	++nr_syscall;
}

void init_syscall()
{
	add_syscall(sys_reserved, 0);
	add_syscall(sys_fork, 1);
	add_syscall(sys_putchar, 2);

	printk("Setup syscall ok : \n");
	printk("\thas %x syscalls\n", nr_syscall);
}

