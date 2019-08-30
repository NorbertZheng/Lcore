#
# libs.s
#

.set noreorder
.set noat
.align 2

.global enter_syscall3
.global enter_syscall2
.global enter_syscall1
.global enter_syscall0

enter_syscall3:
	addiu	$sp, $sp, -64
	sw		$s8, 0($sp)
	move	$s8, $sp

	sw		$ra, 4($s8)
	sw		$a0, 8($s8)		# arg1
	sw		$a1, 12($s8)	# arg2
	sw		$a2, 16($s8)	# arg3
	sw		$a3, 20($s8)	# no

	move	$v0, $a3		# no
	move	$a3, $a2		# arg3
	move	$a2, $a1		# arg2
	move	$a1, $a0		# arg1
	syscall
	nop

	lw		$ra, 4($s8)
	lw		$a0, 8($s8)
	lw		$a1, 12($s8)
	lw		$a2, 16($s8)
	lw		$a3, 20($s8)

	move	$sp, $s8		# restore $sp
	lw		$s8, 0($sp)
	addiu	$sp, $sp, 64
	jr		$ra
	nop

enter_syscall2:
	addiu	$sp, $sp, -64
	sw		$s8, 0($sp)
	move	$s8, $sp

	sw		$ra, 4($s8)
	sw		$a0, 8($s8)		# arg1
	sw		$a1, 12($s8)	# arg2
	sw		$a2, 16($s8)	# no

	move	$v0, $a2		# no
	move	$a2, $a1		# arg2
	move	$a1, $a0		# arg1
	syscall
	nop

	lw		$ra, 4($s8)
	lw		$a0, 8($s8)
	lw		$a1, 12($s8)
	lw		$a2, 16($s8)

	move	$sp, $s8		# restore $sp
	lw		$s8, 0($sp)
	addiu	$sp, $sp, 64
	jr		$ra
	nop

enter_syscall1:
	addiu	$sp, $sp, -64
	sw		$s8, 0($sp)
	move	$s8, $sp

	sw		$ra, 4($s8)
	sw		$a0, 8($s8)		# arg1
	sw		$a1, 12($s8)	# no

	move	$v0, $a1		# no
	move	$a1, $a0		# arg1
	syscall
	nop

	lw		$ra, 4($s8)
	lw		$a0, 8($s8)
	lw		$a1, 12($s8)

	move	$sp, $s8		# restore $sp
	lw		$s8, 0($sp)
	addiu	$sp, $sp, 64
	jr		$ra
	nop

enter_syscall0:
	addiu	$sp, $sp, -64
	sw		$s8, 0($sp)
	move	$s8, $sp

	sw		$ra, 4($s8)
	sw		$a0, 8($s8)		# no

	move	$v0, $a0		# no
	syscall
	nop

	lw		$ra, 4($s8)
	lw		$a0, 8($s8)

	move	$sp, $s8		# restore $sp
	lw		$s8, 0($sp)
	addiu	$sp, $sp, 64
	jr		$ra
	nop

