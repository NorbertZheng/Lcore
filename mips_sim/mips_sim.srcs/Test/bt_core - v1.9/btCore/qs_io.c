#include "qs_io.h"

/**
 * 输入输出函数
 * @author 卢忠勇
 * @date 2012-03-04
 */

/**
 * 操作led
 * @param leds 低8位有效
 */
void _lightLeds(int leds) {
	*led_base = leds;
}

/**
 * 读取键盘值
 * @return 返回键值,标准ascii码
 */
char getchar() {
	char ascii;
    int c = 0;
    int flag = 0;
    c = *ps2_base;
    while (1) {
        flag = (c >> 31) & 0x1;
        if (flag == 1) {
            break;
        }
        c = ps2_base[0];
    }
    ascii = c & 0xff;
    return ascii;
}

/**
 * check whether a key is pressed
 * @return if a key is pressed, return 1, otherwise, return 0
 */
int checkkey() {
    int c;
    int d;
    c = *ps2_base;
    d = (c >> 31) & 0x1;	// the highest bit of the word of keyboard buffer
    return d;
}

/**
 * check and get key
 * @param key store the key
 * @return if a key is pressed, return 1, otherwise, return 0
 */
int checkandgetkey(int *key) {
    int c;
    int d = 0;
    c = *ps2_base;
    d = (c >> 31) & 0x1;	// the highest bit of the word of keyboard buffer
    *key= c & 0xff;
    return d;
}

void putchar(int ascii) {
    putcharWithForeBack(ascii, COLOR_FRG, COLOR_BKG);
}

void putcharWithFore(int ascii, int foregroundColor) {
    putcharWithForeBack(ascii, foregroundColor, COLOR_BKG);
}

void putcharWithColor(int asciiWithColor) {
    putcharWithFore(asciiWithColor & 0xfff, asciiWithColor >> 16);
}

void putcharWithForeBack(int ascii, int foregroundColor, int backgroundColor) {
	int isAscii;
	int *fontBase = font_base;
    int *vgaBase = graphics_base;
    int w = _cursorX;
    int h = _cursorY;
    int font;
    int i;
    int j;
    int position;
    int pixel;
    int shiftSize = 16;
    int shiftAmount = 32 - shiftSize;
    int pixelShift = shiftAmount;
    int breakLine = 0;
    int breakLineLimit;
    int fontHSize;
    int wordsPerFont;

	ascii &= 0xfff;
    if (ascii == '\n') {
        setCursor(0, _cursorY + 16);
        return;
    }

	isAscii = ascii < 128;
    breakLineLimit = isAscii ? 4 : 8;
    fontHSize = isAscii ? 8 : 16;
    wordsPerFont = isAscii ? 4 : 8;

	foregroundColor &= 0xffff;
    backgroundColor &= 0xffff;

    adaptCursorForFont(ascii);

    w = _cursorX;
    h = _cursorY;

	if (isAscii) {
		fontBase += _multiply(ascii, wordsPerFont);
	} else {
		fontBase += _multiply(ascii - 64, wordsPerFont);
	}

//    fontBase += _multiply(ascii, wordsPerFont);
    position = (_multiply(h, RESOLUTION_X) + w) / 2;

    for (i = 0; i < wordsPerFont; i++) {
        font = fontBase[i];
        pixel = 0;
        for (j = 0; j < 32; j++) {
            if ((font >> (31 - j)) & 0x1) {
                pixel |= foregroundColor << pixelShift;
            } else {
                pixel |= backgroundColor << pixelShift;
            }
            if (pixelShift == 0) {	// pixel is full
                pixelShift = shiftAmount;
                vgaBase[position] = pixel;
                pixel = 0;
                ++breakLine;
                ++position;
                if (breakLine == breakLineLimit) {
                    breakLine = 0;
                    position += (RESOLUTION_X - fontHSize) / 2; // 80 - 2;
                }
            } else {
                pixelShift -= shiftSize;
            }
        }
    }

    _cursorX += fontHSize;
    adaptCursorForFont(0);

//    add_cursor_T();
}
/****************************** rs232 related ******************************/

/**
 * 检查rs232是否busy
 * @return 返回值1为忙,0为空闲
 */
int rs232Check() {
    return *rs232_busy;
}

/**
 * 从rs232接收一个字节,轮询方式
 * @return 返回一个字节
 */
char rs232ReceiveByte() {
	char b;
    int word;
    word = rs232_base[0];
    while(1) {
        if((word & 0x80000000) != 0) {
            b = word & 0xff;
            break;
        }
        word = rs232_base[0];
    }
    return b;
}

char rs232ReceiveByteWithId() {
	char id;
	char data;
	while (1) {
		id = rs232ReceiveByte();
		data = rs232ReceiveByte();
		if (id == ID) {
			break;
		}
	}

	return data;
}

/**
 * 给rs232发送一个字节
 * @param b 要发送的字节
 */
void rs232SendByte(char b) {
    int busy;
    busy = rs232Check();
    while(1) {
        if(!busy) {
            rs232_base[0] = b;
            break;
        }
        busy = rs232Check();
    }
}

/**
 * 从rs232接收一个字节,轮询方式
 * @return the word received from rs232
 */
int rs232ReceiveWord() {
    int i;
    int data = 0;

    for (i = 0; i < 4; i++) {
        data |= rs232ReceiveByte() << (24 - 8 * i);
    }

    return data;
}

int rs232ReceiveWordWithId() {
    int i;
    int data = 0;

    for (i = 0; i < 4; i++) {
        data |= rs232ReceiveByteWithId() << (24 - 8 * i);
    }

    return data;
}

/**
 * send a word to rs232
 * @param word to be sent
 */
void rs232SendWord(int word) {
    int i;

    for (i = 0; i < 4; i++) {
        rs232SendByte(word >> (24 - 8 * i));
    }
}


/**
 * send a word to rs232 slave
 * @param word to be sent
 */
void rs232SendWordToSlave(int word) {
    int i;

    for (i = 0; i < 4; i++) {
        rs232SendByteToSlave(word >> (24 - 8 * i));
    }
}

/**
 * 给rs232 slave发送一个字节
 * @param b 要发送的字节
 */
void rs232SendByteToSlave(char b) {
    int busy;
    busy = rs232CheckSlave();
    while(1) {
        if(!busy) {
            rs232_slave_base[0] = b;
            break;
        }
        busy = rs232CheckSlave();
    }
}

/**
 * 检查rs232是否busy
 * @return 返回值1为忙,0为空闲
 */
int rs232CheckSlave() {
    return *rs232_slave_busy;
}

int getWordFromBuffer(char *buffer, int *begin, int *end) {
    int i;
    int data = 0;
    for (i = 0; i < 4; i++) {
        data |= getByteFromBuffer(buffer, begin, end) << (24 - 8 *i);
    }

    return data;
}

char getByteFromBuffer(char *buffer, int *begin, int *end) {
    int size = *end - *begin + 1;
    int beginTemp;

    if (size < 1) { // buffer为空
        *begin = 0;
        *end = -1;
        receivePackageNew(buffer, end);
    }

    beginTemp = *begin;
    *begin += 1;
//	printf("begin: %d\n", *begin);
    return buffer[beginTemp];
}

void receivePackage(char *buffer, int *length) {
    int i;
    *length = rs232ReceiveWord();
    for (i = 0; i < *length; i++) {
        buffer[i] = rs232ReceiveByte();
    }
}

void receivePackageNew(char *buffer, int *end) {
    int i;
    int length;

    signal(1);
    length = rs232ReceiveWord();
    for (i = 0; i < length; i++) {
        *end += 1;
        buffer[*end] = rs232ReceiveByte();
    }
}

int getWordFromBytes(char *buffer) {
    int data = 0;
    int i;
    for (i = 0; i < 4; i++) {
        data |= buffer[i] << (24 - 8 * i);
    }

    return data;
}

void signal(int data) {
    rs232SendByte(data);
}

int receiveAck() {
    int TRY_TIME = 1000;
    int i;
    int ack = -1;
    int word;

    for (i = 0; i < TRY_TIME; i++) {
        word = rs232ReadByteNonBlock();
        if(word < 0) {
            ack = word & 0xff;
            break;
        }
    }

    return ack;
}

/**
 * 很有必要，防止被编译器优化掉
 */
int rs232ReadByteNonBlock() {
    return rs232_base[0];
}

/**
 * 从rs232接收一个字节,轮询方式
 * @return 返回一个字节
 */
char rs232ReceiveByteSlave() {
    char b;
    int word;
    word = rs232_slave_base[0];
    while(1) {
        if((word & 0x80000000) != 0) {
            b = word & 0xff;
            break;
        }
        word = rs232_slave_base[0];
    }
    return b;
}

/**
 * 给rs232发送一个字节
 * @param b 要发送的字节
 */
void rs232SendByteSlave1(char b) {
	rs232SendByteSlaveWithAddr(ID);
	rs232SendByteSlaveWithAddr(b);
}


/**
 * 给rs232发送一个字节
 * @param b 要发送的字节
 */
void rs232SendByteSlave(char b) {
	rs232SendByteSlaveWithAddr(b);
}


void rs232SendByteSlaveWithAddr(char b) {
	int busy;
    busy = rs232CheckSlave();
    while(1) {
        if(!busy) {
            rs232_slave_base[0] = b;
            break;
        }
        busy = rs232CheckSlave();
    }
}

/**
 * 从rs232接收一个字节,轮询方式
 * @return the word received from rs232
 */
int rs232ReceiveWordSlave() {
    int i;
    int data = 0;

    for (i = 0; i < 4; i++) {
        data |= rs232ReceiveByteSlave() << (24 - 8 * i);
    }

    return data;
}

/**
 * send a word to rs232
 * @param word to be sent
 */
void rs232SendWordSlave(int word) {
    int i;

    for (i = 0; i < 4; i++) {
        rs232SendByteSlave(word >> (24 - 8 * i));
    }
}


/**
 * send a word to rs232
 * @param word to be sent
 */
void rs232SendWordSlave1(int word) { // 不带id
    int i;

    for (i = 0; i < 4; i++) {
        rs232SendByteSlave1(word >> (24 - 8 * i));
    }
}
/****************************** rs232 related end ******************************/


/****************************** cursor related ******************************/
/**
 * adapt the cursor
 */
void adaptCursor() {
	if (_cursorY >= RESOLUTION_Y) {
		clear();
	}
	_cursorX = _remainder(_cursorX, RESOLUTION_X);
	_cursorY = _remainder(_cursorY, RESOLUTION_Y);
}

/**
 * 调整cursor,x方向的调整会叠加到y方向
 */
void adaptCursorWithWrap() {
	while (_cursorX > RESOLUTION_X) {
		_cursorX -= RESOLUTION_X;
		_cursorY++;
	}
	while (_cursorX < 0) {
		_cursorX += RESOLUTION_X;
		_cursorY--;
	}
	_cursorY = _remainder(_cursorY, RESOLUTION_Y);
}

/**
 * set the cursor
 * @param x
 */
void setCursor(int x, int y) {
	_cursorX = x;
	_cursorY = y;
	adaptCursor();
}

/**
 * reset the cursor, x = 0, y = 0
 */
void resetCursor() {
	_cursorX = 0;
	_cursorY = 0;
}

/**
 * 增加x方向增量
 * @param delX x方向增量
 */
void addCursorX(int delX) {
	_cursorX += delX;
	adaptCursor();
}

/**
 * 增加x方向增量,有翻转
 * @param delX x方向增量
 */
void addCursorXWithWrap(int delX) {
	_cursorX += delX;
	adaptCursorWithWrap();
}

/**
 * 增加y方向增量
 * @param delY y方向增量
 */
void addCursorY(int delY) {
	_cursorY += delY;
	adaptCursor();
}

/**
 * 增加y方向增量
 * @param delY y方向增量
 */
void addCursorYWithWrap(int delY) {
	_cursorY += delY;
	adaptCursorWithWrap();
}

/**
 * 增加x方向和y方向增量
 * @param delX x方向增量
 * @param delY y方向增量
 */
void addCursor(int delX, int delY) {
	_cursorX += delX;
	_cursorY += delY;
	adaptCursor();
}

/**
 * 增加x方向和y方向增量,带翻转
 * @param delX x方向增量
 * @param delY y方向增量
 */
void addCursorWithWrap(int delX, int delY) {
	_cursorX += delX;
	_cursorY += delY;
	adaptCursorWithWrap();
}

/**
 * 调整cursor使有足够的空间显示字符,ascii:8*16,非标准ascii:16*16
 * @param ascii
 */
void adaptCursorForFont(int ascii) {
    int hSize = 8;
    int vSize = 16;

	if (ascii > 127) {
		hSize = 16;
	}

    if (_cursorX + hSize > RESOLUTION_X) {
        _cursorX = 0;
        _cursorY += 16;
    }
    if (_cursorY + vSize > RESOLUTION_Y) {
        _cursorY = 0;
//        clear();
    }
}

void addCursorForFontX(int delX) {
	_cursorX += delX;
	while (_cursorX < 0) {
		_cursorX += RESOLUTION_X;
		_cursorY -= 16;
	}
	while (_cursorX >= RESOLUTION_X) {
		_cursorX -= RESOLUTION_X;
		_cursorY += 16;
	}
	adaptCursor();
}
/****************************** cursor related end ******************************/

void singleBackground(int color) {
    int i;
    int *vgaBase = graphics_base;

    color &= 0xff;
    color |= color << 16;

    for (i = 0; i < VRAM_SIZE; i++) {
        vgaBase[i] = color;
    }
    resetCursor();
}

void clear() {
    singleBackground(0);
}

void newline() {
	setCursor(0, _cursorY + 16);
}

void printString(char *message) {
    while (*message != 0) {
        putchar(*message++);
    }
}

void printMessage(int *message) {
    while (*message != 0) {
        putchar(*message++);
    }
}

/**
 * 打印一个整数,以16进制
 * @param num 要打印的整数
 */
void printNum(int num) {
	int i;
	char c;

	for (i = 0; i < 8; i++) {
		c = (num >> (28 - 4 * i)) & 0xf;
		if (c < 10) {
			putchar(c + '0');
		} else {
			putchar(c - 10 + 'a');
		}
	}
}

/**
 * minimal printf
 */
void printf(char *fmt, ...) {
	va_list ap; // points to each unnamed arg in turn
	char *p, *sval;
	int ival;
//	double dval;

	va_start(ap, fmt); // make ap point to 1st unnamed arg
	for (p = fmt; *p; p++) {
		if (*p != '%') {
			putchar(*p);
			continue;
		}
		switch (*++p) {
		case 'd':
			ival = va_arg(ap, int);
			printNum(ival);
			break;
		case 's':
			for (sval = va_arg(ap, char *); *sval; sval++) {
				putchar(*sval);
			}
			break;
		default:
			putchar(*p);
			break;
		}
	}
	va_end(ap); // clean up when done
}

