#!/bin/sh

clear
make clean
make
make boot
make objcopy
make disassembly
