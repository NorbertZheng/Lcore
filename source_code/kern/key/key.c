#include "../../arch/arch.h"
#include "../../arch/intr.h"
#include "../../tool/tool.h"
#include "../list.h"
#include "../kern.h"
#include "../vga/vga.h"
#include "../vga/print.h"
#include "../lock/lock.h"
#include "key.h"

struct keyb_buffer {
	unsigned char buffer[KEYBUFF_SIZE];
	struct lock_t key_spin;
	struct list_head wait;
	unsigned int head, tail;
	unsigned int count;
};

struct keyb_buffer keyb_buffer;
static unsigned int key_index;
unsigned char *io_key_data;
unsigned int f0;
unsigned char keymap[112] = {
				/*	 0		 1		 2		 3		 4		 5		 6		 7		 8		 9		 a		 b		 c		 d		 e		 f	*/
/* 0x00 - 0x0f */	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,
/* 0x10 - 0x1f */	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	'q'	,	'1'	,	 0 	,	 0 	,	 0 	,	'z'	,	's'	,	'a'	,	'w'	,	'2'	,	 0 	,
/* 0x20 - 0x2f */	 0 	,	'c'	,	'x'	,	'd'	,	'e'	,	'4'	,	'3'	,	 0 	,	 0 	,	' '	,	'v'	,	'f'	,	't'	,	'r'	,	'e'	,	 0 	,
/* 0x30 - 0x3f */	 0 	,	'n'	,	'b'	,	'h'	,	'g'	,	'y'	,	'6'	,	 0 	,	 0 	,	 0 	,	'm'	,	'j'	,	'u'	,	'7'	,	'8'	,	 0 	,
/* 0x40 - 0x4f */	 0 	,	','	,	'k'	,	'i'	,	'o'	,	'0'	,	'9'	,	 0 	,	 0 	,	'.'	,	'/'	,	'l'	,	':'	,	'p'	,	 0 	,	 0 	,
/* 0x50 - 0x5f */	 0 	,	 0 	,	'\'',	 0 	,	'['	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	'\n',	']'	,	 0 	,	 0 	,	 0 	,	 0 	,
																/* BackSpace */
/* 0x60 - 0x6f */	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	0x08,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	,	 0 	
};


struct intr_work scancode_work;

void keyb_handler(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	struct list_head *pos;
	struct intr_work *entry;
	
	list_for_each(pos, &(interrupt[key_index].head)) {
		entry = list_entry(pos, struct intr_work, node);
		entry->work(regs, status, errArg, errPc);
	}
}

void init_keyboard()
{
	struct keyb_buffer *keyb_buffer_pointer = &keyb_buffer;

	io_key_data = (unsigned char *) _IO_KEYB_DATA;

	key_index = highest_set(_INTR_KEYB);
	if (register_handler(keyb_handler, key_index))
		return;

	scancode_work.work = get_scancode;
	INIT_LIST_HEAD(&scancode_work.node);
	if (register_work(&(scancode_work.node), key_index))
		return;

	keyb_buffer_pointer->count = 0;
	keyb_buffer_pointer->head = 0;
	keyb_buffer_pointer->tail = 0;
	memset(keyb_buffer_pointer->buffer, 0, KEYBUFF_SIZE);
	init_lock(&(keyb_buffer_pointer->key_spin));
	INIT_LIST_HEAD(&(keyb_buffer_pointer->wait));

	f0 = 0;

	printk("Setup Keyboard ok : \n");
	printk("\tkeyboard-buffer size %x Bytes\n", KEYBUFF_SIZE);
}

void get_scancode(unsigned int *regs, unsigned int status, unsigned int errArg, unsigned int errPc)
{
	unsigned char ch = *io_key_data;
	struct keyb_buffer *keyb_buffer_pointer = &keyb_buffer;
	
	if (keyb_buffer_pointer->count >= KEYBUFF_SIZE)
		goto out;

	if (ch == 0xf0) {
		f0 = 1;
		goto out;
	} else if (ch >= 0x70) {
		goto out;
	}

	if (!f0) 
		goto out;

	if (!keymap[ch])
		goto out;

	keyb_buffer_pointer->buffer[keyb_buffer_pointer->head] = keymap[ch];
	// printk("%c", keymap[ch]);
	if (!(keyb_buffer_pointer->count)) {
		if (!list_empty(&keyb_buffer_pointer->wait)) {
			return;		// wake up all process
		}
	}
	++keyb_buffer_pointer->count;
	++keyb_buffer_pointer->head;
	keyb_buffer_pointer->head %= KEYBUFF_SIZE;

	f0 = 0;
out:
	return;
}

void get_ch(unsigned char *buf)
{
	struct keyb_buffer *keyb_buffer_pointer = &keyb_buffer;

	lockup(&(keyb_buffer_pointer->key_spin));
	if (!(keyb_buffer_pointer->count)) {
		return;			// sleep current process, after handling remember to release the lock
	}

	*buf = keyb_buffer_pointer->buffer[keyb_buffer_pointer->tail];
	--keyb_buffer_pointer->count;
	++keyb_buffer_pointer->tail;
	keyb_buffer_pointer->tail %= KEYBUFF_SIZE;
	unlock(&(keyb_buffer_pointer->key_spin));
}

