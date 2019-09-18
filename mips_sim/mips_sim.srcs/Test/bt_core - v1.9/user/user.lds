SECTIONS
{
	. = 0x12C00;
	.text : { *(.text) }
	_gp = 0xDC00;
	.data : {*(.data)}
}