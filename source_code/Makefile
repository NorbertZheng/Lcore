ARCH := ./arch
BOOT := $(ARCH)/boot
KERN := ./kern
VGA := $(KERN)/vga
TIME := $(KERN)/time
KEY := $(KERN)/key
LOCK := $(KERN)/lock
MM := $(KERN)/mm
TASK := $(KERN)/task
TOOL := ./tool
CALL := $(KERN)/call
LIB := ./lib

CC := sde-gcc
AS := sde-as
LD := sde-ld

CFLAGS := -EL -fno-builtin

KERN-OBJ := start.o arch.o page.o init.o except.o exint.o intr.o vga.o time.o key.o \
	   bootmm.o buddy.o slub.o lock.o task.o sched.o switch.o print.o syscall.o vma.o \
	   tool.o 

kernel.bin : core.bin main.bin writefile.out
	dd if=/dev/zero of=kernel.bin count=0x1000
	dd if=core.bin of=kernel.bin conv=notrunc
	dd if=main.bin of=kernel.bin seek=128 conv=notrunc
	./writefile.out kernel.bin 65536 4096 

writefile.out : util/writefile.c
	gcc -g util/writefile.c -o writefile.out
	chmod a+x writefile.out

core.bin : core.elf
	sde-objcopy -O binary core.elf core.bin

core.elf : $(KERN-OBJ)
	$(LD) -EL -T default.lds -Map core.map -o core.elf $(KERN-OBJ)
	#$(LD) -EL -Ttext 0x00003000 -e start -Map core.map -o core.elf $(KERN-OBJ)

start.o : $(ARCH)/start.s
	$(AS) -EL -o start.o $(ARCH)/start.s

arch.o : $(ARCH)/arch.c
	$(CC) $(CFLAGS) -o arch.o -c $(ARCH)/arch.c

page.o : $(ARCH)/page.c
	$(CC) $(CFLAGS) -o page.o -c $(ARCH)/page.c

init.o : $(KERN)/init.c
	$(CC) $(CFLAGS) -o init.o -c $(KERN)/init.c

syscall.o : $(CALL)/syscall.c
	$(CC) $(CFLAGS) -o syscall.o -c $(CALL)/syscall.c

except.o : $(ARCH)/except.c
	$(CC) $(CFLAGS) -o except.o -c $(ARCH)/except.c

exint.o : $(ARCH)/exint.s
	$(AS) -EL -o exint.o $(ARCH)/exint.s

intr.o : $(ARCH)/intr.c
	$(CC) $(CFLAGS) -o intr.o -c $(ARCH)/intr.c

vga.o : $(VGA)/vga.c
	$(CC) $(CFLAGS) -o vga.o -c $(VGA)/vga.c

time.o : $(TIME)/time.c
	$(CC) $(CFLAGS) -o time.o -c $(TIME)/time.c

key.o : $(KEY)/key.c
	$(CC) $(CFLAGS) -o key.o -c $(KEY)/key.c

bootmm.o : $(MM)/bootmm.c
	$(CC) $(CFLAGS) -o bootmm.o -c $(MM)/bootmm.c

buddy.o : $(MM)/buddy.c
	$(CC) $(CFLAGS) -o buddy.o -c $(MM)/buddy.c

slub.o : $(MM)/slub.c
	$(CC) $(CFLAGS) -o slub.o -c $(MM)/slub.c

task.o : $(TASK)/task.c
	$(CC) $(CFLAGS) -o task.o -c $(TASK)/task.c

vma.o : $(TASK)/vma.c
	$(CC) $(CFLAGS) -o vma.o -c $(TASK)/vma.c

sched.o : $(TASK)/sched.c
	$(CC) $(CFLAGS) -o sched.o -c $(TASK)/sched.c

switch.o : $(TASK)/switch.s
	$(AS) -EL -o switch.o $(TASK)/switch.s

lock.o : $(LOCK)/lock.c
	$(CC) $(CFLAGS) -o lock.o -c $(LOCK)/lock.c

print.o : $(VGA)/print.c
	$(CC) $(CFLAGS) -o print.o -c $(VGA)/print.c

tool.o : $(TOOL)/tool.c
	$(CC) $(CFLAGS) -o tool.o -c $(TOOL)/tool.c


USER-OBJ := main.o libs.o ulib.o

main.bin : main.elf
	sde-objcopy -O binary main.elf main.bin

main.elf : $(USER-OBJ)
	$(LD) -EL -Ttext 0x80000000 -e main -Map main.map -o main.elf $(USER-OBJ)

main.o : $(KERN)/main.c
	$(CC) $(CFLAGS) -o main.o -c $(KERN)/main.c

ulib.o : $(LIB)/ulib.c
	$(CC) $(CFLAGS) -o ulib.o -c $(LIB)/ulib.c

libs.o : $(LIB)/libs.s
	$(AS) -EL -o libs.o $(LIB)/libs.s



boot : boot.elf

boot.elf : $(BOOT)/boot.s
	$(AS) -EL -o boot.elf $(BOOT)/boot.s
	sde-objcopy -O binary boot.elf boot.bin


disassembly :
	sde-objdump -S core.elf > core.txt
	sde-objdump -S main.elf > main.txt

clean :
	rm -f *.o
	rm -f *.elf
	rm -f *.bin
	rm -f *.txt
	rm -f *.out
	#rm -f lcore.*
	rm -f *.map
	rm -f *.bak
