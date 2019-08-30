#!/bin/sh

echo ".c :"
find . -name "*.c" | xargs cat | grep -v ^$| wc -l

echo ".h :"
find . -name "*.h" | xargs cat | grep -v ^$| wc -l

echo ".s :"
find . -name "*.s" | xargs cat | grep -v ^$| wc -l
