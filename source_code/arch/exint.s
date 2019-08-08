#
# exint.s
#

.global _exint_handler
.global _end_ex

.set noreorder
.set noat
.align 2

_exint_handler:
	# save context

	# 
	# handle process
	# 

	# enable intr

	# restore context
	eret
_end_ex:

