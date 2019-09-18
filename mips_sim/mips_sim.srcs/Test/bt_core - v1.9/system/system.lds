SECTIONS
{
	. = 0x0000;
	.text : { *(.text) }
	_gp = 0xDC00;
	.data : {*(.data)}
}