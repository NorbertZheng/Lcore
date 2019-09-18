#include "main.h"

int main() {
    char *idAddr = (char *) 0x40000fff;
	int ascii = 0;
	int x = 0;
	int y = 0;
	int i = 0;
	int count = 0;

	// asm("mfc0 $s7, $9"); // counter
	// asm("addi $s7, $s7, 0x7fff");
	// asm("mtc0 $s7, $11"); // comparer
	// asm("mfc0 $s7, $12"); // status
	// asm("lui $s6, 0x8000");
	// asm("or $s7, $s7, $s6");
	// asm("mtc0 $s7, $12"); // Interrupt enable
	putchar('B');

	while (1) {
		putchar('A');
		count++;
		if (count == 2400) {
            clear();
            getchar();
		}
	}

	clear();
    _initialSystem();

    if (_IS_M2M) {
        if (_IS_MASTER) {
            sendBootCommand();
        } else {
            ID = *idAddr;
            if (ID == 2 || ID == 3) { // N3
                graphics_base = (int *) 0x00100000;
            }
        }
    }
    shellMain();
    return 0;
}

void slave() {
    process_command_slave();
}

void backspace() {
    addCursorForFontX(-8);
    putchar(' ');
    addCursorForFontX(-8);
}

int get_command() {
    int ascii;
    int valid = -1;
    int count = 0;

    while(1) {
        ascii = getchar();
        if (ascii == ' ' && count == 0) {
            continue;
        }
        if (ascii == '\n') {
            putchar(ascii);
            if (count == 0) {
                print_prompt_T();
                continue;
            } else if (count < COMMAND_BUFFER_SIZE - 1) {
                command_buffer[count++] = ascii;
                command_buffer[count] = 0;
                valid = 1;
            } else {
                valid = 0;
            }
        } else if (ascii == BKSP) {
            if (count == 0) {
                continue;
            } else {
                backspace();
                count--;
            }
        } else {
            if (count < COMMAND_BUFFER_SIZE) {
                command_buffer[count] = ascii;
            }
            count++;
            putchar(ascii);
        }

        if (valid != -1) {
            break;
        }
    }
    return valid;
}

void send_command() {
    int i = 0;
    int ack = -1;
    int count = 0;
    while (1) {
        count++;
        signal(2);
        ack = receiveAck();
        if (ack >= 0) {
            break;
        }
    }

    printf("count: %d\n", count);
//	printf("ACK\n");
    waitSignal();
//    printf("RECEIVED\n");
    while(command_buffer[i] != 0) {
        rs232SendByte(command_buffer[i++]);
    }
    rs232ReceiveByte();
//    printf("SEND COMMAND\n");
//    getchar();
}

void send_command1() {
    int i = 0;
    while(command_buffer[i] != 0) {
        rs232SendByte(command_buffer[i++]);
    }
    rs232ReceiveByte();
}

int waitSignal() {
    return rs232ReceiveByte();
}


int process_command1() {
    int i, j, type = 0;
    int length, baseAddr = 0;
    int totalLength = 0;
    char *basePtr = 0;
    int index = 0;
    char buffer[1024 + 8];
    int begin = 0;
    int end = -1;

    int received = 0;

    int exit = 0;

//	printf("Begin\n");
//	for (i = 0; i < 10000; i++) {
//
//	}
    type = getWordFromBuffer(buffer, &begin, &end);
//	printf("Type: %d\n", type);
    while (1) {
        length = getWordFromBuffer(buffer, &begin, &end);
        totalLength += length;
//        printf("Length: %d\n", length);
        if (length == 0) {
            break;
        }

        baseAddr = getWordFromBuffer(buffer, &begin, &end);
//		printf("base: %d\n", baseAddr);

        basePtr = (char *) baseAddr;

        if (type == 0) {
            printf("Loading...\n");
        } else if (type == 1) {
            basePtr = responseBuffer;
        } else if (type == 86) {
            basePtr += 0x100;
            baseAddr = (int)basePtr;
        }
//        printf("basePtr: %d\n", basePtr);

        for (i = 0; i < length; i++) {
            basePtr[index++] = getByteFromBuffer(buffer, &begin, &end);
            received++;
            if (received == length / 16 && isExe(type)) {
                putcharWithFore(0x4, 0x07FF);
                received = 0;
            }
            if (type == 1 && index == 2400) {
                for (j = 0; j < index; j++) {
                    putchar(basePtr[i]);
                }
                index = 0;
            }
        }
        if (isExe(type)) {
            printf("\n");
        }
    }

    switch (type) {
    case 0:	// user program
        asm("cache 0, 0");
        asm("add $zero, $zero, $zero");
        user_func = (void *)0x8c00;
        (*user_func)();
        clear();
        break;
    case 1:	// print string
//        printMessage(responseBuffer);
//		printf("%s\n", "String");
        printf("%s", responseBuffer);
        break;
    case 2:	// clear
        clear();
        exit = 1;
        break;
    case 86: // x86 program
//        printf("x86 program. %d %d\n", baseAddr, totalLength);
//        getchar();
        rectfilled(0, 0, 400, 400, 0xf800);
        getchar();
        asm("cache 0, 0");
        asm("add $zero, $zero, $zero");
        btMain(baseAddr, totalLength);
        break;
    default:
        break;
    }
    return exit;
}

int isExe(int type) {
    if (type == 0 || type == 86) {
        return 1;
    }
    return 0;
}

void process_command_slave() {
    int i, type;
    int shift_amount;
    int length, baseAddr = 0, data;
    int totalLength = 0;
    int *basePtr = 0;
    int count = 0;
    int index = 0;
//    char idReceive = 0xff;

    type = 0;
    shift_amount = 24;

    type = rs232ReceiveWordWithId();
//    printf("type: %d\n", type);
//    getchar();
    while ((length = rs232ReceiveWordWithId()) != 0) {
        totalLength += length;
        baseAddr = rs232ReceiveWordWithId();
        basePtr = (int *) baseAddr;
        if (type == 0) {

        } else if (type == 1) {
            basePtr = (int *) responseBuffer;
        } else if (type == 86) {
            basePtr += 0x100 >> 2;
            baseAddr = (int)basePtr;
        }

        count = 0;
        data = 0;
        for (i = 0; i < length; i++) {
            data |= rs232ReceiveByteWithId() << (24 - 8 * count);
            count++;
            if (count == 4) {	// a word
                basePtr[index++] = data;
                data = 0;
                count = 0;
            }
        }
        if (count != 0) {	// 尾巴：不足一个字
            basePtr[index++] = data;
        }

        if (type == 1) {
            basePtr[index] = 0;
        }
    }

//    printf("receive ok\n");
//    getchar();
//    printf("type: %d total length: %d\n", type, totalLength);
//    getchar();

    switch (type) {
    case 0:	// user program
        if (totalLength != 0) {
            asm("cache 0, 0");
            user_func = (void *)0x8c00;
            (*user_func)();
            clear();
        }
        break;
    case 1:	// print string
//        printMessage(responseBuffer);
        printf("%s", responseBuffer);
        break;
    case 2:	// clear
        clear();
        break;
    case 86: // x86 program
//        printString("x86 program.\n");
        asm("cache 0, 0");
        btMain(baseAddr, totalLength);
//        printf("out\n");
        break;
    default:
        break;
    }
}


void welcome() {
    resetCursor();
    // WELCOME TO QS-I !
    setCursorForNonAscii(1, 12);
    putchar(0x40f2);
    putchar(0x406c);
    putchar(0x40e7);
    putchar(0x406a);
    putchar(0x40ea);
    putchar(0x40e8);
    putchar(0x406c);
    addCursorForFontX(16);
    putchar(0x40ef);
    putchar(0x40ea);
    addCursorForFontX(16);
    putchar(0x60ec);
    putchar(0x60ee);
    putchar(0x6009);
    putchar(0x60ae);
    //浙江大学计算机学院硬件课程教学改革系统
    setCursorForNonAscii(3, 10);
    putchar(0x1142);
    putchar(0x1143);
    putchar(0x1144);
    putchar(0x1127);
    putchar(0x111b);
    putchar(0x111c);
    putchar(0x111d);
    putchar(0x1127);
    putchar(0x1145);
    putchar(0x1147);
    putchar(0x1148);
    putchar(0x1121);
    putchar(0x1122);
    putchar(0x1153);
    putchar(0x1127);
    putchar(0x124c);
    putchar(0x124d);
    putchar(0x111e);
    putchar(0x111f);
    //逻辑与计算机设计基础、计算机组成与体系结构课程三位一体教学体系
    setCursorForNonAscii(6, 4);
    putchar(0x2179);
    putchar(0x217a);
    putchar(0x2152);
    putchar(0x211b);
    putchar(0x211c);
    putchar(0x211d);
    putchar(0x2124);
    putchar(0x211b);
    putchar(0x217b);
    putchar(0x217c);
    putchar(0x2001);
    putchar(0x211b);
    putchar(0x211c);
    putchar(0x211d);
    putchar(0x217d);
    putchar(0x217e);
    putchar(0x2152);
    putchar(0x2161);
    putchar(0x211e);
    putchar(0x2181);
    putchar(0x2182);
    putchar(0x2121);
    putchar(0x2122);
    putchar(0x2235);
    putchar(0x2236);
    putchar(0x21cc);
    putchar(0x2161);
    putchar(0x2153);
    putchar(0x2127);
    putchar(0x2161);
    putchar(0x211e);
    //给学生想象和自由发挥的空间
    setCursorForNonAscii(8, 12);
    putchar(0x5126);
    putchar(0x5127);
    putchar(0x5128);
    putchar(0x5129);
    putchar(0x512a);
    putchar(0x512b);
    putchar(0x512c);
    putchar(0x512d);
    putchar(0x512e);
    putchar(0x512f);
    putchar(0x5130);
    putchar(0x5131);
    putchar(0x5132);
    //--基于DIY的RISC计算机系统实践系统
    setCursorForNonAscii(10, 16);
    putchar(0x3009);
    putchar(0x3009);
    putchar(0x317b);
    putchar(0x31be);
    putchar(0x306b);
    putchar(0x30e4);
    putchar(0x30f4);
    putchar(0x3130);
    putchar(0x30ed);
    putchar(0x30e4);
    putchar(0x30ee);
    putchar(0x306a);
    putchar(0x311b);
    putchar(0x311c);
    putchar(0x311d);
    putchar(0x311e);
    putchar(0x311f);
    putchar(0x3149);
    putchar(0x314a);
    putchar(0x311e);
    putchar(0x311f);
    //浙大logo
    setCursorForNonAscii(12, 17);
    putchar(0x727b);
    putchar(0x727c);
    putchar(0x727d);
    putchar(0x727e);
    setCursorForNonAscii(13, 17);
    putchar(0x727f);
    putchar(0x7280);
    putchar(0x7281);
    putchar(0x7282);
    setCursorForNonAscii(14, 17);
    putchar(0x7283);
    putchar(0x7284);
    putchar(0x7285);
    putchar(0x7286);
    setCursorForNonAscii(15, 17);
    putchar(0x7287);
    putchar(0x7288);
    putchar(0x7289);
    putchar(0x728a);
    //指导老师：施青松
    setCursorForNonAscii(18, 15);
    putchar(0x621c);
    putchar(0x61f9);
    putchar(0x626f);
    putchar(0x6270);
    putchar(0x60d5);
    putchar(0x6137);
    putchar(0x6138);
    putchar(0x6139);
    //学生：姚元、卢忠勇、孙龙
    setCursorForNonAscii(20, 13);
    putchar(0x6127);
    putchar(0x6128);
    putchar(0x60d5);
    putchar(0x6274);
    putchar(0x6276);
    putchar(0x6001);
    putchar(0x6277);
    putchar(0x6278);
    putchar(0x6279);
    putchar(0x6001);
    putchar(0x627a);
    putchar(0x61ce);
    //press ENTER进入系统
    setCursorForNonAscii(25, 1);
    putchar(0x70eb);
    putchar(0x710d);
    putchar(0x7100);
    putchar(0x710e);
    putchar(0x710e);
    addCursorForFontX(16);
    putchar(0x4100);
    putchar(0x4109);
    putchar(0x410f);
    putchar(0x4100);
    putchar(0x410d);
    putchar(0x7164);
    putchar(0x7160);
    putchar(0x711e);
    putchar(0x711f);
}

void clear_part(int start, int end) {
    int i;
    for(i = start * 40; i < end * 40; i++)
        text_base[i] = 0;
}

void wait_enter() {
    while (getchar() != '\n') {
    }
}

void print_prompt_T() {
    putchar('>');
}

int shellMain() {
//    int c;
    clear();

    resetCursor();

    _saveMipsSp();
    _systemMain();

    return 0;
}

void _systemMain() {
	clear();
    if (_IS_M2M) {
        if (_IS_MASTER) {
            while(1) {
                print_prompt_T();
                if (get_command()) {
                    send_command_master();
                    process_command_master1();
                } else {
                    printString("Invalid command!\n");
                }
            }
        } else {
            while (1) {
                clear();
                printf("ID: %d\nWaiting...\n", ID);
                slave();
            }
        }
    } else {
        while(1) {
            print_prompt_T();
            if (get_command()) {
                send_command1();
                process_command1();
            } else {
                printString("Invalid command!\n");
            }
        }
    }
}

void setCursorForNonAscii(int y, int x) {
    setCursor(x * 16, y * 16);
}

/**
 * 初始化系统，如中断处理程序的入口地址
 */
void _initialSystem() {
    _intrEntr = _interruptMain; // 中断处理程序之入口地址
    _sysMain = (void *) _systemMain;
}


void sendBootCommand() {
    command_buffer[0] = 'B';
    command_buffer[1] = 'O';
    command_buffer[2] = 'O';
    command_buffer[3] = 'T';
    command_buffer[4] = '\n';
    command_buffer[5] = '\0';
    send_command_master();
    printf("Booting...\n");
    process_command_master();
}

void send_command_master() {
    int i = 0;
    int ack = -1;
    while (1) {
        signal(2);
        ack = receiveAck();
        if (ack >= 0) {
            break;
        }
    }

//	printf("ACK\n");
    waitSignal();
//    printf("RECEIVED\n");
    while(command_buffer[i] != 0) {
        rs232SendByte(command_buffer[i++]);
    }
    rs232ReceiveByte();
//    printf("SEND COMMAND\n");
//    getchar();
}

void process_command_master() {
    int i;
//    int j;
    int type = 0;
//    int shift_amount;
    int length, baseAddr = 0;
//    int data;
//    int totalLength = 0;
    char *basePtr = 0;
//    int count = 0;
//    int index = 0;
    char buffer[1024 + 8];
    int begin = 0;
    int end = -1;

    int received = 0;

//	printf("Begin\n");
//	for (i = 0; i < 10000; i++) {
//
//	}
    type = getWordFromBuffer(buffer, &begin, &end);
    rs232SendWordSlave(type);
//	printf("Type: %d\n", type);
    while (1) {
        length = getWordFromBuffer(buffer, &begin, &end);
        rs232SendWordSlave(length);
//        printf("Length: %d\n", length);
        if (length == 0) {
            break;
        }

        baseAddr = getWordFromBuffer(buffer, &begin, &end);
        rs232SendWordSlave(baseAddr);
//		printf("base: %d\n", baseAddr);

//        basePtr = (char *) baseAddr;
        if (type == 0) {
            printf("Loading...\n");
        } else if (type == 1) {
            basePtr = responseBuffer;
        }
//        printf("basePtr: %d\n", basePtr);

        for (i = 0; i < length; i++) {
            rs232SendByteSlave(getByteFromBuffer(buffer, &begin, &end));
//            basePtr[index++] = getByteFromBuffer(buffer, &begin, &end);
            received++;
            if (received == length / 16 && canRunType(type)) {
                putcharWithFore(0x4, 0x07FF);
                received = 0;
            }
        }
        if (canRunType(type)) {
            printf("\n");
        }
    }

//    switch (type) {
//    case 0:	// user program
//        user_func = (void *)0x8c00;
//        (*user_func)();
//        clear();
//        break;
//    case 1:	// print string
////        printMessage(responseBuffer);
////		printf("%s\n", "String");
//		printf("%s", responseBuffer);
//        break;
//    case 2:	// clear
//        clear();
//        break;
//    case 86: // x86 program
//        printString("x86 program.\n");
//        btMain(baseAddr, totalLength);
//        printf("out\n");
//        break;
//    default:
//        break;
//    }
}


void process_command_master1() {
    int i, j, type = 0;
//    int shift_amount;
    int length, baseAddr = 0;
//    int data;
    int totalLength = 0;
    char *basePtr = 0;
//    int count = 0;
    int index = 0;
    char buffer[1024 + 8];
    int begin = 0;
    int end = -1;
    char b;

    int received = 0;

//	printf("Begin\n");
//	for (i = 0; i < 10000; i++) {
//
//	}
    type = getWordFromBuffer(buffer, &begin, &end);
    if (canRunType(type) && (type != 0x8)) { // with ID
        ID = getWordFromBuffer(buffer, &begin, &end);
    }
    if (isToSlave(type, ID)) { // run, x86
        rs232SendWordSlave1(type);
    }
    while (1) {
        length = getWordFromBuffer(buffer, &begin, &end);
        totalLength += length;
        if (isToSlave(type, ID)) {
            rs232SendWordSlave1(length);
        }
//        printf("Length: %d\n", length);
        if (length == 0) {
            break;
        }

        baseAddr = getWordFromBuffer(buffer, &begin, &end);
        if (isToSlave(type, ID)) { // run
            rs232SendWordSlave1(baseAddr);
        }
//		printf("base: %d\n", baseAddr);

        basePtr = (char *) baseAddr;

        if (type == 0) {
            printf("Loading...\n");
        } else if (type == 1) {
            basePtr = responseBuffer;
        } else if (type == 86) {
            basePtr += 0x100;
            baseAddr = (int)basePtr;
        }
//        if (isToSlave(type, ID)) {
//            printf("Loading...\n");
//        } else if (type == 1) {
//            basePtr = responseBuffer;
//        }
//        printf("basePtr: %d\n", basePtr);

        for (i = 0; i < length; i++) {
            b = getByteFromBuffer(buffer, &begin, &end);
            if (isToSlave(type, ID)) {
                rs232SendByteSlave1(b);
            } else {
                basePtr[index++] = b;
            }
            received++;
            if (received == length / 16 && isExe(type)) {
                putcharWithFore(0x4, 0x07FF);
                received = 0;
            }
            if (type == 1 && index == 2400) {
                for (j = 0; j < index; j++) {
                    putchar(basePtr[i]);
                }
                index = 0;
            }
        }
        if (isExe(type)) {
            printf("\n");
        }
    }

//    printf("type: %d\n", type);
//    getchar();

    switch (type) {
    case 0:	// user program, do nothing
		if (ID == 0) {
			asm("cache 0, 0");
			user_func = (void *)0x8c00;
			(*user_func)();
			clear();
		}
		break;
    case 86:
		if (ID == 0) {
			asm("cache 0, 0");
			btMain(baseAddr, totalLength);
			clear();
		}
//        user_func = (void *)0x8c00;
//	printf("Type: %d\n", type);
//        (*user_func)();
        break;
    case 1:	// print string
//        printMessage(responseBuffer);
//		printf("%s\n", "String");
        printf("%s", responseBuffer);
        break;
    case 2:	// clear
        clear();
        break;
//    case 86: // x86 program
//        printString("x86 program.\n");
//        btMain(baseAddr, totalLength);
//        printf("out\n");
//        break;
    default:
        break;
    }
}

//
//int process_command_master1() {
//    int i, j, type = 0;
//    int length, baseAddr = 0;
//    int totalLength = 0;
//    char *basePtr = 0;
//    int index = 0;
//    char buffer[1024 + 8];
//    int begin = 0;
//    int end = -1;
//
//    int received = 0;
//
//    int exit = 0;
//    char b;
//
////	printf("Begin\n");
////	for (i = 0; i < 10000; i++) {
////
////	}
//    type = getWordFromBuffer(buffer, &begin, &end);
////	printf("Type: %d\n", type);
//    if (canRunType(type) && (type != 0x8)) { // with ID
//        ID = getWordFromBuffer(buffer, &begin, &end);
//    }
//    if (isToSlave(type, ID)) { // run, x86
//        rs232SendWordSlave1(type);
//    }
//    while (1) {
//        length = getWordFromBuffer(buffer, &begin, &end);
//		if (isToSlave(type, ID)) {
//			rs232SendWordSlave1(type);
//		}
//        totalLength += length;
////        printf("Length: %d\n", length);
//        if (length == 0) {
//            break;
//        }
//
//        baseAddr = getWordFromBuffer(buffer, &begin, &end);
//		if (isToSlave(type, ID)) {
//			rs232SendWordSlave1(type);
//		}
//
//        basePtr = (char *) baseAddr;
//
//        if (type == 0) {
//            printf("Loading...\n");
//        } else if (type == 1) {
//            basePtr = responseBuffer;
//        } else if (type == 86) {
//            basePtr += 0x100;
//            baseAddr = (int)basePtr;
//        }
//
//        for (i = 0; i < length; i++) {
//        	b = getByteFromBuffer(buffer, &begin, &end);
//        	if (isToSlave(type, ID)) {
//        		rs232SendByteSlave1(b);
//        	} else {
//				basePtr[index++] = getByteFromBuffer(buffer, &begin, &end);
//        	}
//            received++;
//            if (received == length / 16 && isExe(type)) {
//                putcharWithFore(0x4, 0x07FF);
//                received = 0;
//            }
//            if (type == 1 && index == 2400) {
//                for (j = 0; j < index; j++) {
//                    putchar(basePtr[i]);
//                }
//                index = 0;
//            }
//        }
//        if (isExe(type)) {
//            printf("\n");
//        }
//    }
//
//    switch (type) {
//    case 0:	// user program
//		if (ID == 0) {
//        asm("cache 0, 0");
//        user_func = (void *)0x8c00;
//        (*user_func)();
//        clear();
//		}
//        break;
//    case 1:	// print string
////        printMessage(responseBuffer);
////		printf("%s\n", "String");
//        printf("%s", responseBuffer);
//        break;
//    case 2:	// clear
//        clear();
////        exit = 1;
//        break;
//    case 86: // x86 program
////        printf("x86 program. %d %d\n", baseAddr, totalLength);
////        getchar();
////        rectfilled(0, 0, 400, 400, 0xf800);
////        getchar();
//		if (ID == 0) {
//        asm("cache 0, 0");
//        btMain(baseAddr, totalLength);
//		}
//        break;
//    default:
//        break;
//    }
//    return exit;
//}
//

int canRunType(int type) {
    if ((type == 0) || (type == 8) || (type == 86)) {
        return 1;
    }
    return 0;
}

int isToSlave(int type, int id) {
	if (canRunType(type) && (id != 0)) {
		return 1;
	}
	return 0;
}
