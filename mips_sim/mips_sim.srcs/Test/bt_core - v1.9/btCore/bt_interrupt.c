#include "bt_interrupt.h"

/**
 * x86 interrupt routines emulator
 * @author 姚元
 * @date 2012-03-16
 */

 // bios color
 #define BIOS_BLACK 0x0000
 #define BIOS_BLUE 0x002A
 #define BIOS_GREEN 0x0540
 #define BIOS_CYAN 0x056A
 #define BIOS_RED 0xA800
 #define BIOS_MAGENTA 0xA82A
 #define BIOS_BROWN 0xAA80
 #define BIOS_LIGHT_GRAY 0xAD6A
 #define BIOS_DARK_GRAY 0x5295
 #define BIOS_LIGHT_BLUE 0x52BF
 #define BIOS_LIGHT_GREEN 0x57D5
 #define BIOS_LIGHT_CYAN 0x57FF
 #define BIOS_LIGHT_RED 0xFA95
 #define BIOS_LIGHT_MAGENTA 0xFABF
 #define BIOS_YELLOW 0xFFD5
 #define BIOS_WHITE 0xFFFF

 #define BIOS_COLOR_NUM 16
 int _biosColor[BIOS_COLOR_NUM] = {BIOS_BLACK, BIOS_BLUE, BIOS_GREEN, BIOS_CYAN,
	BIOS_RED, BIOS_MAGENTA, BIOS_BROWN, BIOS_LIGHT_GRAY, BIOS_DARK_GRAY, BIOS_LIGHT_BLUE,
	BIOS_LIGHT_GREEN, BIOS_LIGHT_CYAN, BIOS_LIGHT_RED, BIOS_LIGHT_MAGENTA, BIOS_YELLOW,
	BIOS_WHITE};

void exit2Dos() {
	_loadMipsSp();
    (*_sysMain)();
}


int _interruptMain(int intVector) {
    // decode the interrupt vector
//    _saveRegisters(_X86_TYPE);
//    asm("nop\t\n"
//        :
//        :
//        :"s0","s1","s2","s3","a0"
//        );

//    printf("Interrupt Begin!\n");
//    printX86();
//    getchar();
//	unsigned int s1;
//	unsigned int s2;
//	unsigned int cx;
//	unsigned int dx;
//	s1 = _x86Register[1];
//	s2 = _x86Register[2];
//	cx = s1 >> 16;
//	dx = s2 >> 16;
//	printf("cx: %d dx: %d\n", cx, dx);
//	getchar();
    switch (intVector) {
        case _DOS_VECTOR:
            _dosInterruptMain();
            break;
        case _BIOS_VECTOR:
            _biosInterruptMain();
            break;
        case _EXIT_VECTOR:
            printf("x86 program exits.\n");
            break;
        default:
            printf("Interrupt Vector Not Supported Yet %d!\n", intVector);
            break;
    }
//    printf("Interrupt End!\n");
//    printX86();
//    getchar();
//    _loadRegisters(_X86_TYPE);
//    printX86();
//    getchar();
    return 0;
}

void _dosInterruptMain() {
    int s0, s2, tmp;
    char ah, dl, c;
    s0 = _x86Register[0];
    ah = (char)(s0 >> 24);
    // decode AH
    switch (ah) {
        case 0x4c: // exit to DOS
            exit2Dos();
			_x86Terminal = 1;
            break;
        case 0x01: // getchar()
        case 0x07: // without print
            c = getchar();
//            _x86Register[0] = _x86Register[0] | (c << 16);
			_x86Register[0] &= 0xff000000;
			_x86Register[0] |= c << 16;
//			printf("AX: %d\n", _x86Register[0]);
//			getchar();
            break;
        case 0x02: // putchar()
            s2 = _x86Register[2];
            dl = (char)(s2 >> 16);
            putchar(dl);
            break;
        case 0x0b: // check key
            tmp = checkkey();
            _x86Register[0] = tmp? _x86Register[0] | 0x00ff0000 : _x86Register[0] & 0xff00ffff;
            break;
        default:
//            printf("DOS Interrupt Service Not Supported Yet!\n");
            break;
    }
}

void _biosInterruptMain() {
    unsigned int s0, s1, s2, s3;
    unsigned int bx, cx, dx;
//    int x, y;
//    int pixelValue;
    char ah;
    int al;

    s0 = _x86Register[0];
    ah = (char)(s0 >> 24);
    // decode AH
    switch (ah) {
//        case 0x0c: // draw a pixel (BX: pixel value, (CX, DY) = (x, y) )
		case 0x0c:
			al = (s0 >> 16) & 0xff; // color
            s1 = _x86Register[1];
            s2 = _x86Register[2];
            s3 = _x86Register[3];
            bx = s3 >> 16; // pixel value
            cx = s1 >> 16; // x coordinate
            dx = s2 >> 16; // y coordinate
//            putpixel(cx, dx, bx);
//			putpixel(cx, dx, _getBiosColor(al));
//			putpixel(cx, dx, _biosColor[al & 0xf]);
			_putPixelQuick(cx, dx, _biosColor[al & 0xf]);
            break;
        default:
//            printf("BIOS Interrupt Service Not Supported Yet!\n");
            break;
    }
}

int _getBiosColor(int al) {
	int color;
	int red;
	int green;
	int blue;
	al &= 0xf;

	if (al >= 8) {
		red = ((al & 0x4) != 0) ? (255 >> 3) : 0;
		green = ((al & 0x2) != 0) ? (255 >> 3) : 0;
		blue = ((al & 0x1) != 0) ? (255 >> 2) : 0;
	} else if (al == 6) { // brown
		red = 170 >> 3;
		green = 85 >> 3;
		blue = 0;
	} else {
		red = ((al & 0x4) != 0) ? (170 >> 3) : 0;
		green = ((al & 0x2) != 0) ? (170 >> 3) : 0;
		blue = ((al & 0x1) != 0) ? (170 >> 2) : 0;
	}

	color = red << 11 | green << 6 | blue;

	return color;
}

void _putPixelQuick(int x, int y, int color) {
	int byteIndex;
    char *vgaBase = (char *) graphics_base;
    byteIndex = (x + (y << 9) + (y << 7)) << 1;
    vgaBase[byteIndex] = color >> 8;
    vgaBase[byteIndex + 1] = color;
}

