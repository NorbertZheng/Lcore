ARCH := ./arch
BOOT := $(ARCH)/boot
KERN := ./kern
VGA := $(KERN)/vga
TOOL := ./tool

CC := sde-gcc
AS := sde-as
LD := sde-ld

OBJ := start.o arch.o page.o init.o exint.o intr.o vga.o tool.o

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

exint.o : $(ARCH)/exint.s
	$(AS) -EL -o exint.o $(ARCH)/exint.s

intr.o : $(ARCH)/intr.c
	$(CC) -EL -fno-builtin -o intr.o -c $(ARCH)/intr.c

vga.o : $(VGA)/vga.c
	$(CC) -EL -fno-builtin -o vga.o -c $(VGA)/vga.c

tool.o : $(TOOL)/tool.c
	$(CC) -EL -fno-builtin -o tool.o -c $(TOOL)/tool.c


boot : boot.bin

boot.bin : $(BOOT)/boot.s
	$(AS) -EL -o boot.bin $(BOOT)/boot.s


objcopy :
	sde-objcopy -O binary kernel.elf kernel.bin

disassembly :
	sde-objdump -S kernel.elf > kernel.txt

clean :
	rm -f *.o
	rm -f *.elf
	rm -f *.bin
	rm -f *.txt