#
# switch.s
#

.global switch_to

.set noreorder
.set noat
.align 2

.macro save_context
	sw		$v0, 0($at)
	sw		$v1, 4($at)
	sw		$a0, 8($at)
	sw		$a1, 12($at)
	sw		$a2, 16($at)
	sw		$a3, 20($at)
	sw		$t0, 24($at)
	sw		$t1, 28($at)
	sw		$t2, 32($at)
	sw		$t3, 36($at)
	sw		$t4, 40($at)
	sw		$t5, 44($at)
	sw		$t6, 48($at)
	sw		$t7, 52($at)
	sw		$s0, 56($at)
	sw		$s1, 60($at)
	sw		$s2, 64($at)
	sw		$s3, 68($at)
	sw		$s4, 72($at)
	sw		$s5, 76($at)
	sw		$s6, 80($at)
	sw		$s7, 84($at)
	sw		$t8, 88($at)
	sw		$t9, 92($at)
	sw		$gp, 96($at)
	sw		$sp, 100($at)
	sw		$fp, 104($at)
    sw		$ra, 108($at)
    sw		$k0, 112($at)
    sw		$k1, 116($at)
    # save EPC
	mfc0	$k1, $2
	sw		$k1, 120($at)
	# save EAR
	mfc0	$k1, $1
	sw		$k1, 124($at)
.endm


.macro restore_context
	# restore EAR
	lw		$k1, 124($at)
	mtc0	$k1, $1
	# restore EPC
	lw		$k1, 120($at)
	mtc0 	$k1, $2
	lw		$k1, 116($at)
	lw		$k0, 112($at)
	lw		$v0, 0($at)
	lw		$v1, 4($at)
	lw		$a0, 8($at)
	lw		$a1, 12($at)
	lw		$a2, 16($at)
	lw		$a3, 20($at)
	lw		$t0, 24($at)
	lw		$t1, 28($at)
	lw		$t2, 32($at)
	lw		$t3, 36($at)
	lw		$t4, 40($at)
	lw		$t5, 44($at)
	lw		$t6, 48($at)
	lw		$t7, 52($at)
	lw		$s0, 56($at)
	lw		$s1, 60($at)
	lw		$s2, 64($at)
	lw		$s3, 68($at)
	lw		$s4, 72($at)
	lw		$s5, 76($at)
	lw		$s6, 80($at)
	lw		$s7, 84($at)
	lw		$t8, 88($at)
	lw		$t9, 92($at)
	lw		$gp, 96($at)
	lw		$sp, 100($at)
	lw		$fp, 104($at)
	lw		$ra, 108($at) 
.endm

# a0   pointer to old-context
# a1   pointer to new-context

switch_to:
	move	$at, $a0
	save_context

	lw		$at, 128($a1)		# unsigned int *pgd
	ori		$at, $at, 1			# enable new proc paging
	mtc0	$at, $6

	move	$at, $a1
	restore_context

	move	$sp, $at			# kernel stack top
	jr		$ra
	nop

