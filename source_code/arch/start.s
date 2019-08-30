#
# start.s
#

.extern init_kernel
.extern init
.global start
.global meta

.set noreorder
.set noat
.align 2

entry:
	j		start
	nop
meta:
	nop
	nop
	nop
	nop
	nop
	nop
	nop
	nop
start:
	li		$sp, 0x3000
	la		$gp, _gp
	jal		init_kernel
	nop

	la		$k0, init
	lw		$k0, 0($k0)

	lui		$k1, 0x8000		# code start(stack end)
	add		$sp, $0, $k1	# stack

	ori		$k1, $k1, 1		# user_mode = 1
	mtc0	$k1, $2			# set EPC

	eret					# return user mode, give the execute right to 0x8000,0000
	nop

