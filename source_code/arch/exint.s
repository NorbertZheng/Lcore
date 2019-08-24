#
# exint.s
#

.global _exint_handler
.global _end_ex

.extern do_exception

.set noreorder
.set noat
.align 2

.macro enable_global
	mfc0	$k0, $4
	lui		$k1, 0x8000
	or		$k0, $k0, $k1
	mtc0	$k0, $4
.endm

.macro disable_global
	mfc0	$k0, $4
	lui		$k1, 0x7fff
	ori		$k1, $k1, 0xffff
	and		$k0, $k0, $k1
	mtc0	$k0, $4
.endm

.macro save_context
	ori		$at, $sp, 0
	# save gpr
	addiu	$sp, $sp, -128
	sw		$v0, 0($sp)
	sw		$v1, 4($sp)
	sw		$a0, 8($sp)
	sw		$a1, 12($sp)
	sw		$a2, 16($sp)
	sw		$a3, 20($sp)
	sw		$t0, 24($sp)
	sw		$t1, 28($sp)
	sw		$t2, 32($sp)
	sw		$t3, 36($sp)
	sw		$t4, 40($sp)
	sw		$t5, 44($sp)
	sw		$t6, 48($sp)
	sw		$t7, 52($sp)
	sw		$s0, 56($sp)
	sw		$s1, 60($sp)
	sw		$s2, 64($sp)
	sw		$s3, 68($sp)
	sw		$s4, 72($sp)
	sw		$s5, 76($sp)
	sw		$s6, 80($sp)
	sw		$s7, 84($sp)
	sw		$t8, 88($sp)
	sw		$t9, 92($sp)
	sw		$gp, 96($sp)
	sw		$at, 100($sp)
	sw		$fp, 104($sp)
	sw		$ra, 108($sp)
	sw		$k0, 112($sp)
	sw		$k1, 116($sp)
	# save EPC
	mfc0	$k1, $2
	sw		$k1, 120($sp)
	# save EAR
	mfc0	$k1, $1
	sw		$k1, 124($sp)
.endm

.macro restore_context
	disable_global
	# restore EPC
	lw		$k1, 124($sp)
	mtc0	$k1, $2
	# restore EAR
	lw		$k1, 120($sp)
	mtc0	$k1, $1
	lw		$k1, 116($sp)
	lw		$k0, 112($sp)
	lw		$v0, 0($sp)
	lw		$v1, 4($sp)
	lw		$a0, 8($sp)
	lw		$a1, 12($sp)
	lw		$a2, 16($sp)
	lw		$a3, 20($sp)
	lw		$t0, 24($sp)
	lw		$t1, 28($sp)
	lw		$t2, 32($sp)
	lw		$t3, 36($sp)
	lw		$t4, 40($sp)
	lw		$t5, 44($sp)
	lw		$t6, 48($sp)
	lw		$t7, 52($sp)
	lw		$s0, 56($sp)
	lw		$s1, 60($sp)
	lw		$s2, 64($sp)
	lw		$s3, 68($sp)
	lw		$s4, 72($sp)
	lw		$s5, 76($sp)
	lw		$s6, 80($sp)
	lw		$s7, 84($sp)
	lw		$t8, 88($sp)
	lw		$t9, 92($sp)
	lw		$gp, 96($sp)
	lw		$at, 100($sp)
	lw		$fp, 104($sp)
	lw		$ra, 108($sp)
	addiu	$sp, $sp, 128
	ori		$sp, $at, 0
.endm

_exint_handler:
	# save context
	save_context

	# 
	# handle process
	# 
	mfc0	$a0, $0
	mfc0	$a1, $1
	mfc0	$a2, $2
	addiu	$a3, $sp, 0
	addiu	$sp, $sp, -32
	jal		do_exception
	nop

	jal		do_interrupt
	nop
	addiu	$sp, $sp, 32

	# enable global intr
	enable_global

	# restore context
	restore_context

	eret
_end_ex:
	j		_end_ex

