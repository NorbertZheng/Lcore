#ifndef BT_JLUT_H
#define BT_JLUT_H

#include "qs_io.h"
#include "bt_cache.h"
#include "bt_generate.h"


#define _JLUT_ENTRY_NUM      64
#define _JLUT_ENTRY_VALID    1
#define _JLUT_ENTRY_INVALID  0

#define _BLOCK_NUM 0

void _jlut_init();
void _jlut_invalid(int index);
void _jlut_insert(int spc, int tpc);
int _jlut_lookup(int spc);

int _block_lookup(int spc);
void _jlut_insert_cache(int spc, int tpc);
void _jlut_insert_block(int spc, int tpc);


struct BlockInfo {
    int x86Base;
//    int x86Length;
    int mipsBase;
//    int mipsLength;
    int valid;
};

extern int _jlut_index;
extern int _blockIndex;
extern struct BlockInfo _bt_block[_BLOCK_NUM];

#endif
