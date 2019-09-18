#include "bt_decode.h"

/**
 * x86指令解析
 * @author 卢忠勇
 * @date 2012-03-07
 */

int saveFlag = 0; // 是否需要保存标志寄存器

/** not finished
 * 解析总入口
 * @param x86Source - x86源程序地址
 * @param mipsTarget - 生成mips程序地址
 * @return
 */
int _decodeMain(char **x86Source, int **mipsTarget) {
    int opcode;
    int direction;
    int isWord;

    int opType;

    int oo;
    int rrr;
    int mmm;
    int disp;
//    int data;
    int isData;
    int imm;
    int isImm;
    int isMem;
    char c;
    int x86Reg1;
    int x86Reg2;
//    int shiftAmout;
//    int type = 0;
    int flag = 1; // 结束标志,int 4ch

    int rt;
    int rd = 0;
    int addr;
    int addrH;
    int addrL;

    // 跳转指令
    int cpc;
    int spc;
    int ip;

//    _push(_MIPS_RA, mipsTarget);
    _pushRegToMips(_MIPS_RA, mipsTarget);

    while (1) {
        saveFlag = 0;
        c = *(*x86Source)++;
        if (c == 0xcd) { // int
            flag = 0;
            c = *(*x86Source)++;

            // save registers
            _genSaveX86Registers(mipsTarget);

            addr = (int) _intrEntr;
            addrH = (addr >> 16) & 0xffff;
            addrL = (addr >> 0) & 0xffff;
            *(*mipsTarget)++ = _gen_lui(_MIPS_V0, addrH);
            *(*mipsTarget)++ = _gen_ori(_MIPS_V0, _MIPS_V0, addrL);
            *(*mipsTarget)++ = _gen_ori(_MIPS_ZERO, _MIPS_A0, c);
            *(*mipsTarget)++ = _gen_jalr(_MIPS_V0, _MIPS_RA);
            *(*mipsTarget)++ = MIPS_NOP;

            // load registers
            _genLoadX86Registers(mipsTarget);

            // get return address to mips
//            _popRegFromMips(_MIPS_RA, mipsTarget);
//
//            cpc = (int)*x86Source;
//            spc = cpc;
//            _saveBranchInfo(_X86_JMP1, spc, cpc, mipsTarget);
//
//            _jrInterface(_MIPS_RA, mipsTarget);
//            break;
			continue;
        }

/////////////////////////////////////////////////////////////////////////////
// 跳转指令
        opcode = c;
        if (c == 0xeb) { // jmp short, 8位偏移
            // get return address to mips
            _popRegFromMips(_MIPS_RA, mipsTarget);

            spc = sign8Extend(*(*x86Source)++);
            cpc = (int)*x86Source;
            spc += cpc;
            _saveBranchInfo(_X86_JMP1, spc, cpc, mipsTarget);
//            _pushRegToMips(_X86_FLAG, mipsTarget); // flag
//            _pushDataToMips(_X86_JMP1, mipsTarget); // type
//            _pushDataToMips(spc, mipsTarget); // spc
//            _pushDataToMips(cpc, mipsTarget); // cpc

//            _pop(_MIPS_RA, mipsTarget);
            _jrInterface(_MIPS_RA, mipsTarget);

            break;
        } else if (c == 0xe9) { // jmp near, 16位偏移
            // get return address to mips
            _popRegFromMips(_MIPS_RA, mipsTarget);


            spc = *(*x86Source)++ & 0xff;
            spc |= *(*x86Source)++ << 8;
            cpc = (int) *x86Source;
//            printf("SPC: %d CPC: %d\n", spc, cpc);
//            getchar();
            spc = signExtend(spc) + cpc;
            _saveBranchInfo(_X86_JMP1, spc, cpc, mipsTarget);
//            printf("SPC: %d\n", spc);
//            getchar();
//            _pushRegToMips(_X86_FLAG, mipsTarget);
//            _pushDataToMips(_X86_JMP1, mipsTarget);
//            _pushDataToMips(spc, mipsTarget);
//            _pushDataToMips(cpc, mipsTarget);

//            _pop(_MIPS_RA, mipsTarget);
            _jrInterface(_MIPS_RA, mipsTarget);

            break;
        } else if (c == 0xff) { // jmp reg	11111111 oo100mmm
            c = *(*x86Source)++;
            if (((c >> 3) & 0x7) == 0x4) {
                oo = (c >> 6) & 0x3;
                rrr = c & 0x7;
                switch (oo) {
                case 3:
                    // get return address to mips
                    _popRegFromMips(_MIPS_RA, mipsTarget);


                    cpc = (int) *x86Source;
                    *(*mipsTarget)++ = _gen_srl(_X86_DS, _MIPS_T0, 12);
                    rt = _getX86Reg(rrr, 1);
                    *(*mipsTarget)++ = _gen_srl(rt, _MIPS_T1, 16);
                    *(*mipsTarget)++ = _gen_add(_MIPS_T1, _MIPS_T0, _MIPS_T0);
                    _saveBranchInfoReg(_X86_JMP1, _MIPS_T0, cpc, mipsTarget);
//                    _pushRegToMips(_X86_FLAG, mipsTarget);
//                    _pushDataToMips(_X86_JMP1, mipsTarget);
//                    _pushRegToMips(_MIPS_T0, mipsTarget);
//                    _pushDataToMips(cpc, mipsTarget);

//                    _pop(_MIPS_RA, mipsTarget);
                    _jrInterface(_MIPS_RA, mipsTarget);
                    break;

                default:
                    break;
                }

                break;
            }
        }

        if (((c >> 4) & 0xf) == 0x7) { // Jcnd label, 8位偏移
            // get return address to mips
            _popRegFromMips(_MIPS_RA, mipsTarget);

            opType = c & 0xf;

            spc = sign8Extend(*(*x86Source)++);
            cpc = (int)*x86Source;
            spc += cpc;
            _saveBranchInfo(opType, spc, cpc, mipsTarget);
//            _pushRegToMips(_X86_FLAG, mipsTarget);
//            _pushDataToMips(opType, mipsTarget);
//            _pushDataToMips(spc, mipsTarget); // spc
//            _pushDataToMips(cpc, mipsTarget); // cpc

//            _pop(_MIPS_RA, mipsTarget);
            _jrInterface(_MIPS_RA, mipsTarget);

            break;
        } else if (c == 0x0f) { // Jcnd label, 16位偏移
            c = *(*x86Source)++;
            if (((c >> 4) & 0xf) == 0x8) {
                // get return address to mips
                _popRegFromMips(_MIPS_RA, mipsTarget);

                opType = c & 0xf;

                spc = *(*x86Source)++ & 0xff;
                spc |= *(*x86Source)++ << 8;
                cpc = (int) *x86Source;
                spc = signExtend(spc) + cpc;
                _saveBranchInfo(opType, spc, cpc, mipsTarget);
//                _pushRegToMips(_X86_FLAG, mipsTarget);
//                _pushDataToMips(opType, mipsTarget);
//                _pushDataToMips(spc, mipsTarget);
//                _pushDataToMips(cpc, mipsTarget);

//                _pop(_MIPS_RA, mipsTarget);
                _jrInterface(_MIPS_RA, mipsTarget);

                break;
            }
        }

        opcode = c;
        if (c == 0xe8) { // near call,16位地址
            // get return address to mips
            _popRegFromMips(_MIPS_RA, mipsTarget);

            spc = *(*x86Source)++ & 0xff;
            spc |= *(*x86Source)++ << 8;
            cpc = (int) *x86Source;
            spc = signExtend(spc) + cpc;
            _saveBranchInfo(_X86_CALL1, spc, cpc, mipsTarget);
//            _pushRegToMips(_X86_FLAG, mipsTarget);
//            _pushDataToMips(_X86_CALL1, mipsTarget);
//            _pushDataToMips(spc, mipsTarget);
//            _pushDataToMips(cpc, mipsTarget);

			ip = cpc - _x86Segment;
			*(*mipsTarget)++ = _gen_lui(_X86_IP, ip);
			_push16H(_X86_IP, mipsTarget);


            _jrInterface(_MIPS_RA, mipsTarget);

            break;
        } else if (c == 0xff) { // call reg
            c = *(*x86Source)++;
            if (((c >> 3) & 0x7) == 0x2) {
                oo = (c >> 6) & 0x3;
                rrr = c & 0x7;
                switch (oo) {
                case 3:
                    // get return address to mips
                    _popRegFromMips(_MIPS_RA, mipsTarget);


                    cpc = (int) *x86Source;
                    *(*mipsTarget)++ = _gen_srl(_X86_DS, _MIPS_T0, 12);
                    rt = _getX86Reg(rrr, 1);
                    *(*mipsTarget)++ = _gen_srl(rt, _MIPS_T1, 16);
                    *(*mipsTarget)++ = _gen_add(_MIPS_T1, _MIPS_T0, _MIPS_T0);
                    _saveBranchInfoReg(_X86_CALL1, _MIPS_T0, cpc, mipsTarget);
//                    _pushRegToMips(_X86_FLAG, mipsTarget);
//                    _pushDataToMips(_X86_CALL1, mipsTarget);
//                    _pushRegToMips(_MIPS_T0, mipsTarget);
//                    _pushDataToMips(cpc, mipsTarget);

//                    _pop(_MIPS_RA, mipsTarget);

                    // push cpc to x86 stack
                    *(*mipsTarget)++ = _gen_lui(_MIPS_T0, cpc >> 16);
                    *(*mipsTarget)++ = _gen_ori(_MIPS_T0, _MIPS_T0, cpc & 0xffff);
                    _push(_MIPS_T0, mipsTarget);

                    // return to mips handler
                    _jrInterface(_MIPS_RA, mipsTarget);
                    break;

                default:
                    break;
                }

                break;
            }
        }

        opcode = c;
        if (opcode == 0xc3) { // ret near
            // get return address to mips
            _popRegFromMips(_MIPS_RA, mipsTarget);

            cpc = (int) *x86Source;

//            _pop(_MIPS_RA, mipsTarget);

//            _pop(_MIPS_T1, mipsTarget); // spc
			_pop16H(_X86_IP, mipsTarget);
			*(*mipsTarget)++ = _gen_srl(_X86_IP, _MIPS_T0, 16);
//			*(*mipsTarget)++ = _gen_srl(_X86_CS, _MIPS_T1, 12); // cs
			*(*mipsTarget)++ = _gen_add(_MIPS_T0, _X86_CS, _MIPS_T0);

            _saveBranchInfoReg(_X86_CALL1, _MIPS_T0, cpc, mipsTarget);
//            _pushRegToMips(_X86_FLAG, mipsTarget);
//            _pushDataToMips(_X86_CALL1, mipsTarget);
//            _pushRegToMips(_MIPS_T0, mipsTarget);
//            _pushDataToMips(cpc, mipsTarget);

            _jrInterface(_MIPS_RA, mipsTarget);
            break;
        }

/////////////////////////////////////////////////////////////////////////////
// 堆栈操作
// mov
        opcode = (c >> 2) & 0x3f;
        direction = (c >> 1) & 0x1;
        isWord = (c >> 0) & 0x1;

        opType = opcode;
        disp = 0;

        if (((c >> 6) & 0x3) == 0x0) {
            if ((c & 0x7) == 0x6) { // 00sss110, push seg
                rrr = (c >> 3) & 0x7;
                x86Reg1 = _getX86RegSeg(rrr);
                _push16H(x86Reg1, mipsTarget);
            } else if ((c & 0x7) == 0x7) { // 00sss111, pop seg
                rrr = (c >> 3) & 0x7;
                x86Reg1 = _getX86RegSeg(rrr);
                _pop16H(x86Reg1, mipsTarget);
            }
        } else if (((c >> 3) & 0x1f) == _X86_PUSH0_0) { // push reg
            rrr = c & 0x7;
            x86Reg1 = _getX86Reg(rrr, 1);
            _push16H(x86Reg1, mipsTarget);
        } else if (((c >> 3) & 0x1f) == _X86_POP0_0) {
            rrr = c & 0x7;
            x86Reg1 = _getX86Reg(rrr, 1);
            _pop16H(x86Reg1, mipsTarget);
        } else if (((c >> 1) & 0x7f) == 0x63) {
            c = *(*x86Source)++;
            oo = (c >> 6) & 0x3;
            rrr = (c >> 3) & 0x7;
            mmm = (c >> 0) & 0x7;

            if (rrr == 0x0) { // mov mem, imm
                opType = _X86_MOV3;
                _push(_MIPS_T1, mipsTarget);
                _push(_MIPS_T2, mipsTarget);
                _getMemAddr(oo, mmm, x86Source, mipsTarget); // 内存地址在t0
                *(*mipsTarget)++ = _gen_add(_MIPS_T0, _MIPS_ZERO, _MIPS_T2);

                if (isWord) {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 1);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 8);
                    *(*mipsTarget)++ = _gen_or(_MIPS_T0, _MIPS_T1, _MIPS_T0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 16);

                    x86Reg2 = _MIPS_T0;
                } else {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 24);

                    x86Reg2 = _MIPS_T0 - DEL_AH;
                }

                rd = x86Reg2;
                _regImm(opType, 0, isWord, rd, x86Source, mipsTarget); // 结果在rd

                if (!isWord) {
                    rd += DEL_AH;
                    *(*mipsTarget)++ = _gen_srl(rd, rd, 24);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, rd, 0);
                } else {
                    *(*mipsTarget)++ = _gen_srl(rd, _MIPS_T1, 16);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_srl(_MIPS_T1, _MIPS_T1, 8);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 1);
                }

                _pop(_MIPS_T2, mipsTarget);
                _pop(_MIPS_T1, mipsTarget);
            }
        } else if (((c >> 4) & 0xf) == 0x0b) { // mov reg, imm
            opType = _X86_MOV3;
            isWord = (c >> 3) & 0x1;
            rrr = c & 0x7;
            rd = _getX86Reg(rrr, isWord);
//            printf("isWord: %d rd: %d\n", isWord, rd);
//            getchar();
            _regImm(opType, 0, isWord, rd, x86Source, mipsTarget); // 结果在rd
        }

/////////////////////////////////////////////////////////////////////////////
// 移位指令,opcode有七位
        opcode = (c >> 1) & 0x7f;
        imm = 0;
        isImm = 0;
        isMem = 0;
        isData = 0;
        isWord = c & 0x1;
        if ((opcode == 0x68) || (opcode == 0x69) || (opcode == 0x60)) {
//        	printf("SHIFT\n");
//        	getchar();
            _push(_MIPS_T1, mipsTarget);
            _push(_MIPS_T2, mipsTarget);
            switch (opcode) {
            case 0x68:
                imm = 1;
                isImm = 1;
            case 0x69:
                break;
            case 0x60:
                isData = 1;
                isImm = 1;
                break;
            default:
                break;
            }

            c = *(*x86Source)++;
            oo = (c >> 6) & 0x3;
            rrr = (c >> 3) & 0x7;
            mmm = (c >> 0) & 0x7;

            switch (oo) {
            case 0:
            case 1:
            case 2:
                isMem = 1;
                _getMemAddr(oo, mmm, x86Source, mipsTarget); // 内存地址在t0
                *(*mipsTarget)++ = _gen_add(_MIPS_T0, _MIPS_ZERO, _MIPS_T2); // save t0 to t2

                if (isWord) {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 1);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 8);
                    *(*mipsTarget)++ = _gen_or(_MIPS_T0, _MIPS_T1, _MIPS_T0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 16); // t0: content

                    x86Reg2 = _MIPS_T0;
                } else {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 24);

                    x86Reg2 = _MIPS_T0 - DEL_AH;
                }

                rd = x86Reg2;

                break;
            case 3: // reg, imm
                rd = _getX86Reg(mmm, isWord);
                break;
            }

            if (isData) {
                imm = _getX86Data(0, 0, x86Source);
//                printf("IMM: %d\n", imm);
//                getchar();
            }

            _shiftEntrance(rrr, isWord, rd, imm, isImm, mipsTarget);

            if (isMem) { // rd, write to mem
                if (!isWord) {
                    rd += DEL_AH;
                    *(*mipsTarget)++ = _gen_srl(rd, rd, 24);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, rd, 0);
                } else {
                    *(*mipsTarget)++ = _gen_srl(rd, _MIPS_T1, 16);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_srl(_MIPS_T1, _MIPS_T1, 8);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 1);
                }
            }

            _pop(_MIPS_T2, mipsTarget);
            _pop(_MIPS_T1, mipsTarget);
            continue;
        }


/////////////////////////////////////////////////////////////////////////////
        opcode = (c >> 2) & 0x3f; // 6位opcode
        switch (opcode) {
        case _X86_ADD0: // 常规寄存器
        case _X86_AND0:
        case _X86_OR0:
        case _X86_SUB0:
        case _X86_XOR0:
        case _X86_MOV3:
        case _X86_CMP0:
            saveFlag = 1;
            c = *(*x86Source)++;
            oo = (c >> 6) & 0x3;
            rrr = (c >> 3) & 0x7;
            mmm = (c >> 0) & 0x7;

            x86Reg1 = _getX86Reg(rrr, isWord);

            switch (oo) {
            case 0:
            case 1:
            case 2:
                _push(_MIPS_T1, mipsTarget);
                _push(_MIPS_T2, mipsTarget);
                _getMemAddr(oo, mmm, x86Source, mipsTarget); // 内存地址在t0
                *(*mipsTarget)++ = _gen_add(_MIPS_T0, _MIPS_ZERO, _MIPS_T2);

                if (isWord) {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 1);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 8);
                    *(*mipsTarget)++ = _gen_or(_MIPS_T0, _MIPS_T1, _MIPS_T0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 16);

                    x86Reg2 = _MIPS_T0;
                } else {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 24);

                    x86Reg2 = _MIPS_T0 - DEL_AH;
                }

                rd = direction ? x86Reg1 : x86Reg2;
                rt = direction ? x86Reg2 : x86Reg1;

                _regToReg(opType, rt, rd, mipsTarget);

                if (direction == 0) { // rd, write to mem
                    if (!isWord) {
                        rd += DEL_AH;
                        *(*mipsTarget)++ = _gen_srl(rd, rd, 24);
                        *(*mipsTarget)++ = _gen_sb(_MIPS_T2, rd, 0);
                    } else {
                        *(*mipsTarget)++ = _gen_srl(rd, _MIPS_T1, 16);
                        *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 0);
                        *(*mipsTarget)++ = _gen_srl(_MIPS_T1, _MIPS_T1, 8);
                        *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 1);
                    }
                }

                _pop(_MIPS_T2, mipsTarget);
                _pop(_MIPS_T1, mipsTarget);
                break;
            case 3: // reg, reg
                x86Reg2 = _getX86Reg(mmm, isWord);
                rd = direction ? x86Reg1 : x86Reg2;
                rt = direction ? x86Reg2 : x86Reg1;
                _regToReg(opType, rt, rd, mipsTarget);
                break;
            }
            break;
        case _X86_ADC2_ADD2_AND2_CMP2_SUB2_XOR2_OR2: // imm
            saveFlag = 1;
            c = *(*x86Source)++;
            oo = (c >> 6) & 0x3;
            rrr = (c >> 3) & 0x7;
            mmm = (c >> 0) & 0x7;

            opType = rrr;

            switch (oo) {
            case 0:
            case 1:
            case 2:
                _push(_MIPS_T1, mipsTarget);
                _push(_MIPS_T2, mipsTarget);
                _getMemAddr(oo, mmm, x86Source, mipsTarget); // 内存地址在t0
                *(*mipsTarget)++ = _gen_add(_MIPS_T0, _MIPS_ZERO, _MIPS_T2);

                if (isWord) {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 1);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 8);
                    *(*mipsTarget)++ = _gen_or(_MIPS_T0, _MIPS_T1, _MIPS_T0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 16);

                    x86Reg2 = _MIPS_T0;
                } else {
                    *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 0);
                    *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 24);

                    x86Reg2 = _MIPS_T0 - DEL_AH;
                }

                rd = x86Reg2;
                _regImm(opType, direction, isWord, rd, x86Source, mipsTarget); // 结果在rd

                if (!isWord) {
                    rd += DEL_AH;
                    *(*mipsTarget)++ = _gen_srl(rd, rd, 24);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, rd, 0);
                } else {
                    *(*mipsTarget)++ = _gen_srl(rd, _MIPS_T1, 16);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_srl(_MIPS_T1, _MIPS_T1, 8);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 1);
                }

                _pop(_MIPS_T2, mipsTarget);
                _pop(_MIPS_T1, mipsTarget);
                break;
            case 3:
                rd = _getX86Reg(mmm, isWord);
//                printf("isWord: %d rd: %d\n", isWord, rd);
//                getchar();
                _regImm(opType, direction, isWord, rd, x86Source, mipsTarget); // direction used as sign
                break;
            }
            break;
        case _X86_ADD1: // ACC
        case _X86_AND1:
        case _X86_OR1:
        case _X86_SUB1:
        case _X86_XOR1:
        case _X86_CMP1:
            saveFlag = 1;
            if (direction == 0) {
                rd = _getX86Reg(0, isWord);
                opType = _convertAccType(opType);
                _regImm(opType, direction, isWord, rd, x86Source, mipsTarget); // direction used as sign
            }
            break;
        case _X86_MOV0: // mov mem, acc
            oo = 0;
            mmm = 6;
            opType = _X86_MOV3;
            x86Reg1 = _getX86Reg(0, isWord);

            _push(_MIPS_T1, mipsTarget);
            _push(_MIPS_T2, mipsTarget);
            _getMemAddr(oo, mmm, x86Source, mipsTarget); // 内存地址在t0
            *(*mipsTarget)++ = _gen_add(_MIPS_T0, _MIPS_ZERO, _MIPS_T2);

            if (isWord) {
                *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T1, 0);
                *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 1);
                *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 8);
                *(*mipsTarget)++ = _gen_or(_MIPS_T0, _MIPS_T1, _MIPS_T0);
                *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 16);

                x86Reg2 = _MIPS_T0;
            } else {
                *(*mipsTarget)++ = _gen_lbu(_MIPS_T2, _MIPS_T0, 0);
                *(*mipsTarget)++ = _gen_sll(_MIPS_T0, _MIPS_T0, 24);

                x86Reg2 = _MIPS_T0 - DEL_AH;
            }

            rt = direction ? x86Reg1 : x86Reg2;
            rd = direction ? x86Reg2 : x86Reg1;

            _regToReg(opType, rt, rd, mipsTarget);

            if (direction == 1) { // rd, write to mem
                if (!isWord) {
                    rd += DEL_AH;
                    *(*mipsTarget)++ = _gen_srl(rd, rd, 24);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, rd, 0);
                } else {
                    *(*mipsTarget)++ = _gen_srl(rd, _MIPS_T1, 16);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 0);
                    *(*mipsTarget)++ = _gen_srl(_MIPS_T1, _MIPS_T1, 8);
                    *(*mipsTarget)++ = _gen_sb(_MIPS_T2, _MIPS_T1, 1);
                }
            }

            _pop(_MIPS_T2, mipsTarget);
            _pop(_MIPS_T1, mipsTarget);
            break;
        default:
            break;
        }
    }


    return 0;
}

int _getX86Reg(int index, int isWord) {
    int reg = 0;

    index &= 0x7;

    switch (index) {
    case 0:
        reg = isWord ? _X86_AX : _X86_AL;
        break;
    case 1:
        reg = isWord ? _X86_CX : _X86_CL;
        break;
    case 2:
        reg = isWord ? _X86_DX : _X86_DL;
        break;
    case 3:
        reg = isWord ? _X86_BX : _X86_BL;
        break;
    case 4:
        reg = isWord ? _X86_SP : _X86_AH;
        break;
    case 5:
        reg = isWord ? _X86_BP : _X86_CH;
        break;
    case 6:
        reg = isWord ? _X86_SI : _X86_DH;
        break;
    case 7:
        reg = isWord ? _X86_DI : _X86_BH;
        break;
    }

    return reg;
}

/**
 * 段寄存器索引
 */
int _getX86RegSeg(int index) {
    int reg = 0;

    index &= 0x7;
    switch (index) {
    case 0:
        reg = _X86_ES;
        break;
    case 1:
        reg = _X86_CS;
        break;
    case 2:
        reg = _X86_SS;
        break;
    case 3:
        reg = _X86_DS;
        break;
    default:
        break;
    }

    return reg;
}


/**
 * 寄存器到寄存器指令类型
 * rt, rd不能为t1和t2
 */
void _regToReg(int type, int rt, int rd, int **target) {
    int delAl = _X86_AX - _X86_AL;
    int delAh = _X86_AX - _X86_AH;
    int rtNew;
    int rdNew;

    if ((rt > _X86_BH) || (rd > _X86_BH)) { // ok
        _keyGenerate(type, rt, rd, target);
    } else {
        _push(_MIPS_T1, target);
        _push(_MIPS_T2, target);

        if ((rt < _X86_AH) && (rt >= _X86_AL)) {
            if ((rd < _X86_AH) && (rd >= _X86_AL) ) { // rt = al, rd = al ok

                rtNew = rt + delAl;
                rdNew = rd + delAl;
                _assSet(rdNew, _MIPS_T1, _L2H, target);
                _assSet(rtNew, _MIPS_T2, _L2H, target);
                _keyGenerate(type, _MIPS_T2, _MIPS_T1, target);
                _assMove(_MIPS_T1, rdNew, _H2L, target);

            } else { // rt = al, rd = ah ok
                rtNew = rt + delAl;
                rdNew = rd + delAh;
                _assSet(rdNew, _MIPS_T1, _H2H, target);
                _assSet(rtNew, _MIPS_T2, _L2H, target);
                _keyGenerate(type, _MIPS_T2, _MIPS_T1, target);
                _assMove(_MIPS_T1, rdNew, _H2H, target);
            }
        } else {
            if ((rd < _X86_AH) && (rd >= _X86_AL)) { // rt = ah, rd = al
                rtNew = rt + delAh;
                rdNew = rd + delAl;
                _assSet(rdNew, _MIPS_T1, _L2H, target);
                _assSet(rtNew, _MIPS_T2, _H2H, target);
                _keyGenerate(type, _MIPS_T2, _MIPS_T1, target);
                _assMove(_MIPS_T1, rdNew, _H2L, target);
            } else { // rt = ah, rd = ah ok
                rtNew = rt + delAh;
                rdNew = rd + delAh;
                _assSet(rdNew, _MIPS_T1, _H2H, target);
                _assSet(rtNew, _MIPS_T2, _H2H, target);
                _keyGenerate(type, _MIPS_T2, _MIPS_T1, target);
                _assMove(_MIPS_T1, rdNew, _H2H, target);
            }
        }

        _pop(_MIPS_T2, target);
        _pop(_MIPS_T1, target);
    }
}

int _getX86Data(int isWord, int isSigned, char **source) {
    int data;
    char c;

    c = *(*source)++;
    data = c;
    if (isWord && !isSigned) {
        c = *(*source)++;
        data |= c << 8;
    } else if (isSigned) {
        data <<= 24;
        data >>= 24;
        data &= 0xffff;
    }

    return data;
}

/**
 * 寄存器与立即数之间
 */
void _regImm(int type, int isSigned, int isWord, int rd, char **source, int **target) {
    int delAl = _X86_AX - _X86_AL;
    int delAh = _X86_AX - _X86_AH;

    int data;
//    char c;

    type = _convertItype(type);

//    c = *(*source)++;
//    data = c;
//    if (isWord && !isSigned) {
//        c = *(*source)++;
//        data |= c << 8;
//    } else if (isSigned) {
//        data <<= 24;
//        data >>= 24;
//        data &= 0xffff;
//    }

    data = _getX86Data(isWord, isSigned, source);

    _push(_MIPS_T1, target);

    if (isWord) { // add cx, ?
        *(*target)++ = _gen_lui(_MIPS_T1, data);
        _keyGenerate(type, _MIPS_T1, rd, target);
    } else {
        if ((rd < _X86_AH) && (rd >=_X86_AL)) { // al
            _push(_MIPS_T2, target);

            rd += delAl;
            _assSet(rd, _MIPS_T1, _L2H, target);
            *(*target)++ = _gen_lui(_MIPS_T2, data);
            *(*target)++ = _gen_sll(_MIPS_T2, _MIPS_T2, 8);
            _keyGenerate(type, _MIPS_T2, _MIPS_T1, target);
            _assMove(_MIPS_T1, rd, _H2L, target);

            _pop(_MIPS_T2, target);
        } else {
            _push(_MIPS_T2, target);

            rd += delAh;
            _assSet(rd, _MIPS_T1, _H2H, target);
            *(*target)++ = _gen_lui(_MIPS_T2, data);
            *(*target)++ = _gen_sll(_MIPS_T2, _MIPS_T2, 8);
            _keyGenerate(type, _MIPS_T2, _MIPS_T1, target);
            _assMove(_MIPS_T1, rd, _H2H, target);

            _pop(_MIPS_T2, target);
//            rd += delAh;
//            *(*target)++ = _gen_lui(_MIPS_T1, data);
//            *(*target)++ = _gen_sll(_MIPS_T1, _MIPS_T1, 8);
//            if (type == _X86_MOV3) {
//
//            } else {
//				_keyGenerate(type, _MIPS_T1, rd, target);
//            }
        }
    }

    _pop(_MIPS_T1, target);
}

/**
 * 计算内存目标地址,放在$t0
 */
void _getMemAddr(int oo, int mmm, char **source, int **target) {
    // 内存地址计算后放到t0, 低20位
    int isSS = 0; // 堆栈段寄存器
    int segmentReg = _X86_DS;
    int indexReg1 = -1; // null
    int indexReg2 = -1; // null
//    int hasIndex = 1;
    int disp = 0;

    switch (oo) {
    case 0:
        if (mmm == 0x6) {
            mmm = 0x8;
            disp = *(*source)++;
            disp |= (*(*source)++) << 8;
        }
        break;
    case 1:
        disp = *(*source)++;
        break;
    case 2:
        disp = *(*source)++;
        disp |= (*(*source)++) << 8;
        break;
    }

    switch (mmm) {
    case 0: // ds: [bx + si]
        indexReg1 = _X86_BX;
        indexReg2 = _X86_SI;
        break;
    case 1: // ds: [bx + di]
        indexReg1 = _X86_BX;
        indexReg2 = _X86_DI;
        break;
    case 2: // ss: [bp + si]
		isSS = 1;
        segmentReg = _X86_SS;
        indexReg1 = _X86_BP;
        indexReg2 = _X86_SI;
        break;
    case 3: // ss: [bp + di]
		isSS = 1;
        segmentReg = _X86_SS;
        indexReg1 = _X86_BP;
        indexReg2 = _X86_DI;
        break;
    case 4: // ds: [si]
        indexReg1 = _X86_SI;
        break;
    case 5: // ds: [di]
        indexReg1 = _X86_DI;
        break;
    case 6: // ss: [bp]
		isSS = 1;
        segmentReg = _X86_SS;
        indexReg1 = _X86_BP;
        break;
    case 7: // ds: [bx]
        indexReg1 = _X86_BX;
        break;
    case 8: // ds: [0]
        break;
    default:
        break;
    }

    _push(_MIPS_T1, target);

	if (!isSS) {
		*(*target)++ = _gen_srl(segmentReg, _MIPS_T0, 12);
	} else {
		*(*target)++ = _gen_add(segmentReg, _MIPS_ZERO, _MIPS_T0);
	}

    if (indexReg1 != -1) {
        *(*target)++ = _gen_add(indexReg1, _MIPS_ZERO, _MIPS_T1);
        if (indexReg2 != -1) {
            *(*target)++ = _gen_add(_MIPS_T1, indexReg2, _MIPS_T1);
        }
        *(*target)++ = _gen_srl(_MIPS_T1, _MIPS_T1, 16);
        *(*target)++ = _gen_add(_MIPS_T0, _MIPS_T1, _MIPS_T0);
    }

    if (disp != 0) {
        *(*target)++ = _gen_ori(_MIPS_ZERO, _MIPS_T1, disp);
        *(*target)++ = _gen_add(_MIPS_T0, _MIPS_T1, _MIPS_T0);
    }

    _pop(_MIPS_T1, target);
}

/**
 * x86堆栈操作之push（32位），针对任意寄存器
 */
void _push(int reg, int **target) {
	*(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -4);
	*(*target)++ = _gen_sw(_MIPS_SP, reg, 0);
//    int temp = _findNewReg(reg, reg);
//    int temp1 = _findNewReg(reg, temp);
//
//    // save temp to mips's stack
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -8);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp1, 0);
//
//    *(*target)++ = _gen_srl(_X86_SS, temp, 12);
//    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
//    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, -4);
//    *(*target)++ = _gen_add(_X86_SP, temp, temp); // temp: 堆栈地址
//    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);
//
////    *(*target)++ = _gen_srl(reg, temp1, 16);
//    *(*target)++ = _gen_sh(temp, reg, 0); // 低16位
//    *(*target)++ = _gen_srl(reg, temp1, 16);
//    *(*target)++ = _gen_sh(temp, temp1, 2); // 高16位
//
//    // save a 32-bit reg
////    *(*target)++ = _gen_sw(temp, reg, 0);
//
//    *(*target)++ = _gen_lw(_MIPS_SP, temp1, 0);
//    *(*target)++ = _gen_lw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 8);
}

/**
 * x86堆栈操作之push（高16位）
 */
void _push16H(int reg, int **target) {
    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, -2);
    *(*target)++ = _gen_add(_X86_SS, _X86_SP, _MIPS_T0);
    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);

    // save a high 16-bit reg
    *(*target)++ = _gen_srl(reg, _MIPS_T1, 16);
    *(*target)++ = _gen_sb(_MIPS_T0, _MIPS_T1, 0); // 低8位
    *(*target)++ = _gen_srl(reg, _MIPS_T1, 24);
    *(*target)++ = _gen_sb(_MIPS_T0, _MIPS_T1, 1); // 高8位

//    int temp = _findNewReg(reg, reg);
//    int temp1 = _findNewReg(reg, temp);
//
//    // save temp to mips's stack
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -8);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp1, 0);
//
//    *(*target)++ = _gen_srl(_X86_SS, temp, 12);
//    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
//    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, -2);
//    *(*target)++ = _gen_add(_X86_SP, temp, temp);
//    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);
//
//    // save a high 16-bit reg
//    *(*target)++ = _gen_srl(reg, temp1, 16);
//    *(*target)++ = _gen_sb(temp, temp1, 0); // 低8位
//    *(*target)++ = _gen_srl(reg, temp1, 24);
//    *(*target)++ = _gen_sb(temp, temp1, 1); // 高8位
//
//    *(*target)++ = _gen_lw(_MIPS_SP, temp1, 0);
//    *(*target)++ = _gen_lw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 8);
}

/**
 * x86堆栈操作之push（低16位）
 */
void _push16L(int reg, int **target) {
    int temp = _findNewReg(reg, reg);
    int temp1 = _findNewReg(reg, temp);

    // save temp to mips's stack
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -8);
    *(*target)++ = _gen_sw(_MIPS_SP, temp, 4);
    *(*target)++ = _gen_sw(_MIPS_SP, temp1, 0);

    *(*target)++ = _gen_srl(_X86_SS, temp, 12);
    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, -2);
    *(*target)++ = _gen_add(_X86_SP, temp, temp);
    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);

    // save a low 16-bit reg
    *(*target)++ = _gen_srl(reg, temp1, 0);
    *(*target)++ = _gen_sb(temp, temp1, 0); // 低8位
    *(*target)++ = _gen_srl(reg, temp1, 8);
    *(*target)++ = _gen_sb(temp, temp1, 1); // 高8位

    *(*target)++ = _gen_lw(_MIPS_SP, temp1, 0);
    *(*target)++ = _gen_lw(_MIPS_SP, temp, 4);
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 8);
}

/**
 * x86堆栈操作之pop，针对任意寄存器
 */
void _pop(int reg, int **target) {
    *(*target)++ = _gen_lw(_MIPS_SP, reg, 0);
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 4);


//    int temp = _findNewReg(reg, reg);
//    int temp1 = _findNewReg(reg, temp);
//
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -8);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp1, 0);
//
//    *(*target)++ = _gen_srl(_X86_SS, temp, 12);
//    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
//    *(*target)++ = _gen_add(_X86_SP, temp, temp); // temp: 堆栈基址
//    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, 4);
//    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);
//
//	*(*target)++ = _gen_lhu(temp, reg, 0); // 低16位
//    *(*target)++ = _gen_lhu(temp, temp1, 2); // 高16位
//    *(*target)++ = _gen_sll(temp1, temp1, 16);
//    *(*target)++ = _gen_or(reg, temp1, reg);
//
////    *(*target)++ = _gen_lw(temp, reg, 0);
//
////    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, 4);
////    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);
//
//    *(*target)++ = _gen_lw(_MIPS_SP, temp1, 0);
//    *(*target)++ = _gen_lw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 8);
}

/**
 * x86堆栈操作之pop(高16位)，针对任意寄存器
 */
void _pop16H(int reg, int **target) {
    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
    *(*target)++ = _gen_add(_X86_SS, _X86_SP, _MIPS_T0);
    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, 2);
    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);

	*(*target)++ = _gen_lbu(_MIPS_T0, reg, 0);
    *(*target)++ = _gen_sll(reg, reg, 16);
    *(*target)++ = _gen_lbu(_MIPS_T0, _MIPS_T1, 1);
    *(*target)++ = _gen_sll(_MIPS_T1, _MIPS_T1, 24);
    *(*target)++ = _gen_or(reg, _MIPS_T1, reg);

//
//    int temp = _findNewReg(reg, reg);
//    int temp1 = _findNewReg(reg, temp);
//
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -8);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_sw(_MIPS_SP, temp1, 0);
//
//    *(*target)++ = _gen_srl(_X86_SS, temp, 12);
//    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
//    *(*target)++ = _gen_add(_X86_SP, temp, temp);
//    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, 2);
//    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);
//
//    *(*target)++ = _gen_lbu(temp, reg, 0);
//    *(*target)++ = _gen_sll(reg, reg, 16);
//    *(*target)++ = _gen_lbu(temp, temp1, 1);
//    *(*target)++ = _gen_sll(temp1, temp1, 24);
//    *(*target)++ = _gen_or(reg, temp1, reg);
//
//
//    *(*target)++ = _gen_lw(_MIPS_SP, temp1, 0);
//    *(*target)++ = _gen_lw(_MIPS_SP, temp, 4);
//    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 8);
}

/**
 * x86堆栈操作之pop(高16位)，针对任意寄存器
 */
void _pop16L(int reg, int **target) {
    int temp = _findNewReg(reg, reg);
    int temp1 = _findNewReg(reg, temp);

    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -8);
    *(*target)++ = _gen_sw(_MIPS_SP, temp, 4);
    *(*target)++ = _gen_sw(_MIPS_SP, temp1, 0);

    *(*target)++ = _gen_srl(_X86_SS, temp, 12);
    *(*target)++ = _gen_srl(_X86_SP, _X86_SP, 16);
    *(*target)++ = _gen_add(_X86_SP, temp, temp);
    *(*target)++ = _gen_addi(_X86_SP, _X86_SP, 2);
    *(*target)++ = _gen_sll(_X86_SP, _X86_SP, 16);

    *(*target)++ = _gen_lbu(temp, reg, 0);
//    *(*target)++ = _gen_sll(reg, reg, 16);
    *(*target)++ = _gen_lbu(temp, temp1, 1);
    *(*target)++ = _gen_sll(temp1, temp1, 8);
    *(*target)++ = _gen_or(reg, temp1, reg);


    *(*target)++ = _gen_lw(_MIPS_SP, temp1, 0);
    *(*target)++ = _gen_lw(_MIPS_SP, temp, 4);
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 8);
}

/**
 * x86运算与mips对应关系
 */
int _keyGenerate(int type, int rt, int rd, int **target) {
    int instruction = 0;

    switch (type) {
    case _X86_ADD0:
        instruction = _gen_add(rd, rt, rd);
        break;
    case _X86_AND0:
        instruction = _gen_and(rd, rt, rd);
        break;
    case _X86_OR0:
        instruction = _gen_or(rd, rt, rd);
        break;
    case _X86_SUB0:
        instruction = _gen_sub(rd, rt, rd);
        break;
    case _X86_XOR0:
        instruction = _gen_xor(rd, rt, rd);
        break;
    case _X86_MOV3:
        instruction = _gen_add(rt, _MIPS_ZERO, rd);
        break;
    case _X86_CMP0:
        instruction = _gen_sub(rd, rt, _MIPS_ZERO);
        break;
    default:
        break;
    }

    *(*target)++ = instruction;
//    _saveX86Flag(target);

    if (saveFlag) {
        *(*target)++ = _gen_mfc0(_X86_FLAG);
    }

    return instruction;
}

void _shiftEntrance(int type, int isWord, int rd, int imm, int isImm, int **target) {
    int rt = 0;
    _push(_MIPS_T1, target);
    _push(_MIPS_T2, target);

    if (isWord) {
        if (!isImm) {
            _assSet(_X86_CX, _MIPS_T2, _L2H, target); // cl -> t2高8位
            *(*target)++ = _gen_srl(_MIPS_T2, _MIPS_T2, 24); // shift to low 8 bits
            rt = _MIPS_T2;
        }
        *(*target)++ = _keyGenerateShift(type, rt, rd, imm, isImm);
        if ((type == 0x5) || (type == 0x7)) { // 右移
            *(*target)++ = _gen_srl(rd, rd, 16);
            *(*target)++ = _gen_sll(rd, rd, 16);
        }
    } else {
        if ((rd < _X86_AH) && (rd >= _X86_AL)) { // al
            rd += DEL_AL;

            _assSet(rd, _MIPS_T1, _L2H, target); // rd -> t1
            if (!isImm) {
                _assSet(_X86_CX, _MIPS_T2, _L2H, target); // cl -> t2高8位
                *(*target)++ = _gen_srl(_MIPS_T2, _MIPS_T2, 24); // shift to low 8 bits
                rt = _MIPS_T2;
            }
            *(*target)++ = _keyGenerateShift(type, rt, _MIPS_T1, imm, isImm);
            _assMove(_MIPS_T1, rd, _H2L, target);
        } else {
            rd += DEL_AH;
            _assSet(rd, _MIPS_T1, _H2H, target); // rd -> t1
            if (!isImm) {
                _assSet(_X86_CX, _MIPS_T2, _L2H, target); // cl -> t2高8位
                *(*target)++ = _gen_srl(_MIPS_T2, _MIPS_T2, 24); // shift to low 8 bits
                rt = _MIPS_T2;
            }
            *(*target)++ = _keyGenerateShift(type, rt, _MIPS_T1, imm, isImm);
            _assMove(_MIPS_T1, rd, _H2H, target);
        }
    }

    _pop(_MIPS_T2, target);
    _pop(_MIPS_T1, target);
}

/**
 * 移位指令,一种是立即数模式,一种是寄存器,rt是由cl内容转到mips寄存器
 */
int _keyGenerateShift(int type, int rt, int rd, int imm, int isImm) {
    int instruction = 0;

    imm &= 0x1f;

    switch (type) {
    case 0x4: // SHL/SAL, 左移
        if (isImm) {
            instruction = _gen_sll(rd, rd, imm);
        } else {
            instruction = _gen_sllv(rt, rd, rd);
        }
        break;
    case 0x5: // SHR, 逻辑右移
        if (isImm) {
            instruction = _gen_srl(rd, rd, imm);
        } else {
            instruction = _gen_srlv(rt, rd, rd);
        }
        break;
    case 0x7: // SAR, 算术右移
        if (isImm) {
            instruction = _gen_sra(rd, rd, imm);
        } else {
            instruction = _gen_srav(rt, rd, rd);
        }
        break;
    default:
        break;
    }

//    *(*target)++ = instruction;

    return instruction;
}

/**
 * reg, imm类型运算转换为reg, reg类型运算
 */
int _convertItype(int type) {
    switch (type) {
    case _X86_E_ADD2:
        type = _X86_ADD0;
        break;
    case _X86_E_AND2:
        type = _X86_AND0;
        break;
    case _X86_E_OR2:
        type = _X86_OR0;
        break;
    case _X86_E_XOR2:
        type = _X86_XOR0;
        break;
    case _X86_E_SUB2:
        type = _X86_SUB0;
        break;
    case _X86_E_CMP2:
        type = _X86_CMP0;
        break;
    default:
        break;
    }
    return type;
}

int _convertAccType(int type) {
    switch (type) {
    case _X86_ADD1:
        type = _X86_ADD0;
        break;
    case _X86_AND1:
        type = _X86_AND0;
        break;
    case _X86_OR1:
        type = _X86_OR0;
        break;
    case _X86_XOR1:
        type = _X86_XOR0;
        break;
    case _X86_SUB1:
        type = _X86_SUB0;
        break;
    case _X86_CMP1:
        type = _X86_CMP0;
        break;
    default:
        break;
    }
    return type;
}

/**
 * 数据在两个寄存器间移动,高16位有效
 */
void _assSet(int sourceReg, int targetReg, int mode, int **target) {
    switch (mode) {
    case _H2H:
        *(*target)++ = _gen_add(sourceReg, _MIPS_ZERO, targetReg);
        *(*target)++ = _gen_srl(targetReg, targetReg, 24);
        *(*target)++ = _gen_sll(targetReg, targetReg, 24);
        break;
    case _H2L:
        *(*target)++ = _gen_add(sourceReg, _MIPS_ZERO, targetReg);
        *(*target)++ = _gen_srl(targetReg, targetReg, 24);
        *(*target)++ = _gen_sll(targetReg, targetReg, 16);
        break;
    case _L2H:
        *(*target)++ = _gen_add(sourceReg, _MIPS_ZERO, targetReg);
        *(*target)++ = _gen_sll(targetReg, targetReg, 8);
        break;
    case _L2L:
        *(*target)++ = _gen_add(sourceReg, _MIPS_ZERO, targetReg);
        *(*target)++ = _gen_sll(targetReg, targetReg, 8);
        *(*target)++ = _gen_srl(targetReg, targetReg, 8);
        break;
    default:
        break;
    }
}

/**
 * 两个寄存器的高低位之间进行移动,两个寄存器可以相同
 * @param sourceReg - 源寄存器
 * @param targetReg - 目标寄存器
 * @param mode - 移动模式:_H2H, _L2L, _H2L, _L2H
 */
void _assMove(int sourceReg, int targetReg, int mode, int **target) {
    int newReg;

    if (sourceReg == targetReg) {
        switch (mode) {
        case _H2H:
        case _L2L:
            // do nothing
            break;
        case _H2L:
            newReg = _findNewReg(sourceReg, targetReg);
            _push(newReg, target);
            *(*target)++ = _gen_lui(newReg, 0xff00);
            *(*target)++ = _gen_and(targetReg, newReg, targetReg);
            *(*target)++ = _gen_srl(targetReg, newReg, 8);
            *(*target)++ = _gen_or(targetReg, newReg, targetReg);
            _pop(newReg, target);
            break;
        case _L2H:
            newReg = _findNewReg(sourceReg, targetReg);
            _push(newReg, target);
            *(*target)++ = _gen_lui(newReg, 0x00ff);
            *(*target)++ = _gen_and(targetReg, newReg, targetReg);
            *(*target)++ = _gen_sll(targetReg, newReg, 8);
            *(*target)++ = _gen_or(targetReg, newReg, targetReg);
            _pop(newReg, target);
            break;
        default:
            break;
        }
    } else {
        newReg = _findNewReg(sourceReg, targetReg);
        _push(newReg, target);
        switch (mode) {
        case _H2H:
            *(*target)++ = _gen_lui(newReg, 0x00ff);
            *(*target)++ = _gen_and(targetReg, newReg, targetReg);
            *(*target)++ = _gen_lui(newReg, 0xff00);
            *(*target)++ = _gen_and(sourceReg, newReg, newReg);
            *(*target)++ = _gen_or(targetReg, newReg, targetReg);
            break;
        case _L2L:
            *(*target)++ = _gen_lui(newReg, 0xff00);
            *(*target)++ = _gen_and(targetReg, newReg, targetReg);
            *(*target)++ = _gen_lui(newReg, 0x00ff);
            *(*target)++ = _gen_and(sourceReg, newReg, newReg);
            *(*target)++ = _gen_or(targetReg, newReg, targetReg);
            break;
        case _H2L:
            *(*target)++ = _gen_lui(newReg, 0xff00);
            *(*target)++ = _gen_and(targetReg, newReg, targetReg);
            *(*target)++ = _gen_and(sourceReg, newReg, newReg);
            *(*target)++ = _gen_srl(newReg, newReg, 8);
            *(*target)++ = _gen_or(targetReg, newReg, targetReg);
            break;
        case _L2H:
            *(*target)++ = _gen_lui(newReg, 0x00ff);
            *(*target)++ = _gen_and(targetReg, newReg, targetReg);
            *(*target)++ = _gen_and(sourceReg, newReg, newReg);
            *(*target)++ = _gen_sll(newReg, newReg, 8);
            *(*target)++ = _gen_or(targetReg, newReg, targetReg);
            break;
        default:
            break;
        }
        _pop(newReg, target);
    }
}

/**
 * 寻找新的不同的寄存器
 * @param reg1 - 寄存器1
 * @param reg2 - 寄存器2
 * @return 新找到的寄存器
 */
int _findNewReg(int reg1, int reg2) {
    int reg[3] = {_MIPS_T0, _MIPS_T1, _MIPS_T2};
    int newReg;
    int i;

    for (i = 0; i < 3; i++) {
        newReg = reg[i];
        if (newReg != reg1 && newReg != reg2) {
            break;
        }
    }

    return newReg;
}

/**
 * 保存标志寄存器（MIPS，硬件）到x86的FLAG（高16位），使用mfc0指令
 */
void _saveX86Flag(int **target) {
    if (saveFlag) {
        *(*target)++ = _gen_mfc0(_X86_FLAG);
        *(*target)++ = _gen_sll(_X86_FLAG, _X86_FLAG, 16);
    }
}

/**
 * 将寄存器压入mips栈
 */
void _pushRegToMips(int reg, int **target) {
    // $sp = $sp - 4;
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -4);
    // sw reg, 0($sp);
    *(*target)++ = _gen_sw(_MIPS_SP, reg, 0);
}

/**
 * 将数据压入mips栈
 */
void _pushDataToMips(int data, int **target) {
    // sw a2, -8($sp)
    *(*target)++ = _gen_sw(_MIPS_SP, _MIPS_T0, -8);


    // $sp = $sp - 4;
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, -4);
    // mov data to $A2
    *(*target)++ = _gen_lui(_MIPS_T0, data >> 16);
    *(*target)++ = _gen_ori(_MIPS_T0, _MIPS_T0, data & 0xffff);
    // sw a2, 0($sp)
    *(*target)++ = _gen_sw(_MIPS_SP, _MIPS_T0, 0);

    // lw a2, 0($sp)
    *(*target)++ = _gen_lw(_MIPS_SP, _MIPS_T0, -4);
}

/**
 * mips出栈
 */
void _popRegFromMips(int reg, int **target) {
    // lw reg, 0($sp);
    *(*target)++ = _gen_lw(_MIPS_SP, reg, 0);
    // $sp = $sp + 4;
    *(*target)++ = _gen_addi(_MIPS_SP, _MIPS_SP, 4);
}

/**
 * jr指令封装
 */
void _jrInterface(int reg, int **target) {
    *(*target)++ = _gen_jr(reg);
    *(*target)++ = 0;
}

/**
 * jalr指令封装
 */
void _jalrInterface(int reg, int **target) {
    *(*target)++ = _gen_jalr(reg, _MIPS_RA);
    *(*target)++ = 0;
}

/**
 * 保存跳转信息，spc为地址
 */
void _saveBranchInfo(int type, int spc, int cpc, int **target) {
    _pushRegToMips(_X86_FLAG, target); // flag
    _pushDataToMips(type, target); // type
    _pushDataToMips(spc, target); // spc
    _pushDataToMips(cpc, target); // cpc
}

/**
 * 保存跳转信息，spc为寄存器
 */
void _saveBranchInfoReg(int type, int spcReg, int cpc, int **target) {
    _pushRegToMips(_X86_FLAG, target); // flag
    _pushDataToMips(type, target); // type
    _pushRegToMips(spcReg, target); // spc
    _pushDataToMips(cpc, target); // cpc
}

void _genSaveX86Registers(int **target) {
	*(*target)++ = _gen_lui(_MIPS_V0, 0xE000);
	*(*target)++ = _gen_sw(_MIPS_V0, _MIPS_S0, 128);
	*(*target)++ = _gen_sw(_MIPS_V0, _MIPS_S1, 132);
	*(*target)++ = _gen_sw(_MIPS_V0, _MIPS_S2, 136);
	*(*target)++ = _gen_sw(_MIPS_V0, _MIPS_S3, 140);

//    // $a1: base address
//    asm("lui $a1, 0xE000");
//    // save x86 registers
//    asm("sw $s0, 128($a1)");
//
//    int addr;
//    int addrH;
//    int addrL;
//    // load registers
//    addr = (int) _saveX86Registers;
//    addrH = (addr >> 16) & 0xffff;
//    addrL = (addr >> 0) & 0xffff;
//    *(*target)++ = _gen_lui(_MIPS_V0, addrH);
//    *(*target)++ = _gen_ori(_MIPS_V0, _MIPS_V0, addrL);
////    *(*target)++ = _gen_addi(_MIPS_ZERO, _MIPS_A0, 1);
//    *(*target)++ = _gen_jalr(_MIPS_V0, _MIPS_RA);
//    *(*target)++ = MIPS_NOP;
}

void _genLoadX86Registers(int **target) {
	*(*target)++ = _gen_lui(_MIPS_V0, 0xE000);
	*(*target)++ = _gen_lw(_MIPS_V0, _MIPS_S0, 128);
	*(*target)++ = _gen_lw(_MIPS_V0, _MIPS_S1, 132);
	*(*target)++ = _gen_lw(_MIPS_V0, _MIPS_S2, 136);
	*(*target)++ = _gen_lw(_MIPS_V0, _MIPS_S3, 140);

//    int addr;
//    int addrH;
//    int addrL;
//    // load registers
//    addr = (int) _loadX86Registers;
//    addrH = (addr >> 16) & 0xffff;
//    addrL = (addr >> 0) & 0xffff;
//    *(*target)++ = _gen_lui(_MIPS_V0, addrH);
//    *(*target)++ = _gen_ori(_MIPS_V0, _MIPS_V0, addrL);
////    *(*target)++ = _gen_addi(_MIPS_ZERO, _MIPS_A0, 1);
//    *(*target)++ = _gen_jalr(_MIPS_V0, _MIPS_RA);
//    *(*target)++ = MIPS_NOP;
}

void _pushIP(int cpc, int **target) {
	*(*target)++ = _gen_lui(_MIPS_T0, cpc >> 16);
    *(*target)++ = _gen_ori(_MIPS_T0, _MIPS_T0, cpc & 0xffff);
    *(*target)++ = _gen_srl(_X86_CS, _X86_CS, 12);
    *(*target)++ = _gen_sub(_MIPS_T0, _X86_CS, _MIPS_T0);
    *(*target)++ = _gen_sll(_X86_CS, _X86_CS, 12);
    _push16L(_MIPS_T0, target);
}

void _popIP(int reg, int **target) {
	_pop16H(reg, target);
}

