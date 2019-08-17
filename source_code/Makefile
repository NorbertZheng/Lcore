ARCH := ./arch
BOOT := $(ARCH)/boot
KERN := ./kern
VGA := $(KERN)/vga
TIME := $(KERN)/time
KEY := $(KERN)/key
LOCK := $(KERN)/lock
MM := $(KERN)/mm
TOOL := ./tool

CC := sde-gcc
AS := sde-as
LD := sde-ld

CFLAGS := -EL -fno-builtin

OBJ := start.o arch.o page.o init.o except.o exint.o intr.o vga.o time.o key.o \
	   bootmm.o buddy.o slub.o lock.o print.o tool.o

kernel.elf : $(OBJ)
	$(LD) -EL -Ttext 0x00003000 -e start -Map kernel.map -o kernel.elf $(OBJ)

start.o : $(ARCH)/start.s
	$(AS) -EL -o start.o $(ARCH)/start.s

arch.o : $(ARCH)/arch.c
	$(CC) $(CFLAGS) -o arch.o -c $(ARCH)/arch.c

page.o : $(ARCH)/page.c
	$(CC) $(CFLAGS) -o page.o -c $(ARCH)/page.c

init.o : $(KERN)/init.c
	$(CC) $(CFLAGS) -o init.o -c $(KERN)/init.c

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

lock.o : $(LOCK)/lock.c
	$(CC) $(CFLAGS) -o lock.o -c $(LOCK)/lock.c

print.o : $(VGA)/print.c
	$(CC) $(CFLAGS) -o print.o -c $(VGA)/print.c

tool.o : $(TOOL)/tool.c
	$(CC) $(CFLAGS) -o tool.o -c $(TOOL)/tool.c


boot : boot.elf

boot.elf : $(BOOT)/boot.s
	$(AS) -EL -o boot.elf $(BOOT)/boot.s


objcopy :
	sde-objcopy -O binary kernel.elf kernel.bin
	sde-objcopy -O binary boot.elf boot.bin

disassembly :
	sde-objdump -S kernel.elf > kernel.txt

clean :
	rm -f *.o
	rm -f *.elf
	rm -f *.bin
	rm -f *.txt
