main:
addi $t0 , $0, 50
add $t1, $0, $0
add $t2, $0, $0
loop:
addi $t1, $t1, 1
add $t2, $t2, $t1
sub $t3, $t0, $t1
bgez $t3, loop