#
# start.s
#

.extern init_kernel

.set noreorder
.set noat
.align 2

start:
	li	$sp, 0x3000
	la	$gp, _gp
	jal	init_kernel
	nop

