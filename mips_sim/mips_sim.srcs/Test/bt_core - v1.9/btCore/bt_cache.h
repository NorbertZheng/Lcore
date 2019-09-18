#ifndef BT_CACHE
#define BT_CACHE

#include "qs_io.h"
#include "bt_generate.h"

int _jlut_spc_lookup(int spc);
int _jlut_tpc_rd(int index);
void _jlut_spc_wr(int index, int spc);
void _jlut_tpc_wr(int index, int tpc, int valid);

void _campi(int rs, int rd);
void _camwi(int rs, int rt);
void _ramri(int rs, int rd);
void _ramwi(int rs, int rt);

#endif // BT_CACHE

