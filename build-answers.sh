#!/bin/bash
make
rm -rf tmp
mkdir tmp
SPIM="./spim -stat -file tmp/output.s"
CCC=gcc
RUN="tmp/a"
DIFF="diff -s tmp/sp.out tmp/gcc.out"

for source in `ls testcases/*.c`; do
	echo "==========================================================================================="
	echo $source
	$CC $source > tmp/output.s
	$SPIM >tmp/sp.out
	$CCC $source -o tmp/a 1>/dev/null 2>&1
	$RUN > tmp/gcc.out  
	$DIFF
done

echo "==========================================================================================="
