#include "bt_jlut.h"

/**
 * x86 interrupt routines emulator
 * @author ҦԪ
 * @date 2012-03-09
 */

int _jlut_index = 0;

int _blockIndex = 0;

struct BlockInfo _bt_block[_BLOCK_NUM];


//
// function description: JLUT initialize
//
void _jlut_init() {
    int i;
    for(i = 0; i < _JLUT_ENTRY_NUM; i++)
        _jlut_invalid(i);
    _jlut_index = 0;
    for (i = 0; i < _BLOCK_NUM; i++) {
        _bt_block[i].valid = 0;
    }
    _blockIndex = 0;
}

//
// function description: JLUT invalidate
//
void _jlut_invalid(int index) {
    _jlut_tpc_wr(index, 0x00000000, _JLUT_ENTRY_INVALID);
}

//
// function description: JLUT insert
//
void _jlut_insert(int spc, int tpc) {
	_jlut_insert_cache(spc, tpc);
//	_jlut_insert_block(spc, tpc);
}

void _jlut_insert_cache(int spc, int tpc) {
    _jlut_spc_wr(_jlut_index, spc);
    _jlut_tpc_wr(_jlut_index, tpc, _JLUT_ENTRY_VALID);
    _jlut_index++;
//    if(_jlut_index++ == _JLUT_ENTRY_NUM - 1) {
//        _jlut_index = 0;
//    }
}

void _jlut_insert_block(int spc, int tpc) {
    _bt_block[_blockIndex].x86Base = spc;
    _bt_block[_blockIndex].mipsBase = tpc;
    _bt_block[_blockIndex].valid = 1;
    if (++_blockIndex == _BLOCK_NUM) {
        _blockIndex = 0;
    }
}

/**
 * return - if found, return target address, if not, return source address | 0x80000000, MSB is 1
 */
//
// function description: JLUT  lookup
// return value: if ret_val[31] = 0, it means JLUT hit, and ret_val is {tpc, valid};
//               if ret_val[31] = 1, it means JLUT miss, and ret_val is useless data.
//
int _jlut_lookup(int spc) {
    int ret_val;
    int index = _jlut_spc_lookup(spc);
//    printf("index: %d %d\n", index, _jlut_index);
    if(index & 0x80000000) // JLUT miss
        ret_val = spc | 0x80000000;
    else { // JLUT hit
        ret_val = _jlut_tpc_rd(index);
//        printf("ret val: %d\n", ret_val);
        if(ret_val & 0x80000000) // valid
            ret_val &= 0x7fffffff;
        else // hit but invalid
            ret_val = spc | 0x80000000;
    }

//    if (ret_val < 0) { // not found
//		ret_val = _block_lookup(spc);
//    }

    return ret_val;
}

int _block_lookup(int spc) {
	int i = 0;
	int targetAddr = spc | 0x80000000;
	for (i = 0; i < _BLOCK_NUM; i++) {
		if ((_bt_block[i].valid == 1) && (_bt_block[i].x86Base == spc)) {
			targetAddr = _bt_block[i].mipsBase;
			_jlut_insert_cache(spc, targetAddr);
//			printf("index: %d spc: %d tpc: %d\n", i, spc, targetAddr);
//			getchar();
			break;
		}
	}

	return targetAddr;
}

