CC_X86 = cl /O1 /nologo
CP = copy
RM = del
DWIN32 = -DWIN32
LINUX_PWD =
GCC_MIPS  = gcc.exe $(CFLAGS)
AS_MIPS   = as
LD_MIPS   = ld
DUMP_MIPS = objdump
CONVERT_BIN = convert_bin.exe

CFLAGS_SW_MULT = -mno-mul -DUSE_SW_MULT
CFLAGS = -O2 -Wall -c -s -G
CFLAGS += $(CFLAGS_SW_MULT)

CODE_DIR = ../btCore/
MAIN = main
GLOBAL = global
BT_MAIN = bt_main
QS_DEBUG = qs_debug
BT_DECODE = bt_decode
BT_GENERATE = bt_generate
BT_INTR = bt_interrupt
BT_BRANCH = bt_branch
BT_JLUT = bt_jlut
BT_CACHE = bt_cache

MODE = user

clean:
	-$(RM) *.o *.obj *.map *.lst *.hex *.txt *.exe *.axf

compile:
	$(GCC_MIPS) $(CODE_DIR)$(GLOBAL).c
	$(GCC_MIPS) $(CODE_DIR)util.c
	$(GCC_MIPS) $(CODE_DIR)qs_io.c 
	$(GCC_MIPS) $(CODE_DIR)graphics.c
	$(GCC_MIPS) $(CODE_DIR)$(QS_DEBUG).c
	$(GCC_MIPS) $(CODE_DIR)$(BT_INTR).c
	$(GCC_MIPS) $(CODE_DIR)$(BT_DECODE).c
	$(GCC_MIPS) $(CODE_DIR)$(BT_GENERATE).c
	$(GCC_MIPS) $(CODE_DIR)$(BT_JLUT).c
	$(GCC_MIPS) $(CODE_DIR)$(BT_BRANCH).c
	$(GCC_MIPS) $(CODE_DIR)$(BT_MAIN).c
	$(GCC_MIPS) $(CODE_DIR)$(MAIN).c

	$(LD_MIPS) -T $(MODE).lds  -Map $(MAIN).map -s -N -o test.axf $(MAIN).o \
		qs_io.o global.o util.o graphics.o $(BT_MAIN).o $(QS_DEBUG).o \
		$(BT_DECODE).o $(BT_GENERATE).o $(BT_INTR).o $(BT_BRANCH).o \
		$(BT_JLUT).o $(BT_CACHE).o
	-@$(DUMP_MIPS) --disassemble test.axf > $(MODE).lst
	$(CONVERT_BIN)
	
	$(CP) test.bin ..\\..\\..\\user.bin