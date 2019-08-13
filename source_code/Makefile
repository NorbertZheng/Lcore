ARCH := ./arch
BOOT := $(ARCH)/boot
KERN := ./kern
VGA := $(KERN)/vga
TIME := $(KERN)/time
TOOL := ./tool

CC := sde-gcc
AS := sde-as
LD := sde-ld

OBJ := start.o arch.o page.o init.o except.o exint.o intr.o vga.o time.o print.o tool.o

kernel.elf : $(OBJ)
	$(LD) -EL -Ttext 0x00003000 -e start -Map kernel.map -o kernel.elf $(OBJ)

start.o : $(ARCH)/start.s
	$(AS) -EL -o start.o $(ARCH)/start.s

arch.o : $(ARCH)/arch.c
	$(CC) -EL -fno-builtin -o arch.o -c $(ARCH)/arch.c

page.o : $(ARCH)/page.c
	$(CC) -EL -fno-builtin -o page.o -c $(ARCH)/page.c

init.o : $(KERN)/init.c
	$(CC) -EL -fno-builtin -o init.o -c $(KERN)/init.c

except.o : $(ARCH)/except.c
	$(CC) -EL -fno-builtin -o except.o -c $(ARCH)/except.c

exint.o : $(ARCH)/exint.s
	$(AS) -EL -o exint.o $(ARCH)/exint.s

intr.o : $(ARCH)/intr.c
	$(CC) -EL -fno-builtin -o intr.o -c $(ARCH)/intr.c

vga.o : $(VGA)/vga.c
	$(CC) -EL -fno-builtin -o vga.o -c $(VGA)/vga.c

time.o : $(TIME)/time.c
	$(CC) -EL -fno-builtin -o time.o -c $(TIME)/time.c

print.o : $(VGA)/print.c
	$(CC) -EL -fno-builtin -o print.o -c $(VGA)/print.c

tool.o : $(TOOL)/tool.c
	$(CC) -EL -fno-builtin -o tool.o -c $(TOOL)/tool.c


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