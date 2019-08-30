#
# boot.s
#

.set noreorder
.set noat
.align 2

boot:
	li	$t0, 0x3000
	jr	$t0
	nop
dead:
	j	dead
	nop

