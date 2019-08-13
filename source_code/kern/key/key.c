#include "../../arch/arch.h"
#include "../../arch/intr.h"
#include "../../tool/tool.h"
#include "../list.h"
#include "../kern.h"
#include "../vga/vga.h"
#include "../vga/print.h"
#include "key.h"

unsigned char key_buffer[KEYBUFF_SIZE];
static unsigned int key_index;
unsigned char *io_key_data;

struct intr_work scancode_work;

void keyb_handler()
{
	struct list_head *pos;
	struct intr_work *entry;
	
	list_for_each(pos, &(interrupt[key_index].head)) {
		entry = list_entry(pos, struct intr_work, node);
		entry->work();
	}
}

void init_keyboard()
{
	io_key_data = (unsigned char *) _IO_KEYB_DATA;

	key_index = highest_set(_INTR_KEYB);
	if (register_handler(keyb_handler, key_index))
		return;

	scancode_work.work = get_scancode;
	INIT_LIST_HEAD(&scancode_work.node);
	if (register_work(&(scancode_work.node), key_index))
		return;
}

void get_scancode()
{
	unsigned char a = *io_key_data;
	printk("#");
}

