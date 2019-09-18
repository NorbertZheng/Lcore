#include "util.h"

/**
 * 提供常用函数
 * @author 卢忠勇
 * @date 2012-03-02
 */

/**
 * 求绝对值
 * @param x
 * @return x的绝对值
 */
int _abs(int x) {
	if (x < 0) {
		x = -x;
	}
	return x;
}

/**
 * 乘法,转换为加法运算
 * @param a 乘数1
 * @param b 乘数2
 * @return 乘法结果
 */
int _multiply(int a, int b) {
    int isNegative = 0;
    int result = 0;
    int i;
    int temp;

    if (a == 0 || b == 0) {
        return 0;
    }

    if (a < 0) {
        isNegative = 1 - isNegative;
        a = -a;
    }
    if (b < 0) {
        isNegative = 1 - isNegative;
        b = -b;
    }

    if (a > b) {
        temp = a;
        a = b;
        b = temp;
    }

    // a is smaller than b now
    for (i = 0; i < a; i++) {
        result += b;
    }

    if (isNegative) {
        result = -result;
    }

    return result;
}

/**
 * 求余,a % b
 * @param a 被除数
 * @param b 除数,除数为正数
 * @return 余数,为正数
 */
int _remainder(int a, int b) {
	b = _abs(b);
	while (a < b) {
		a += b;
	}
	while (a >= b) {
		a -= b;
	}
	return a;
}

/**
 * 低16位符号扩展
 */
int signExtend(int imm) {
	imm <<= 16;
	imm >>= 16;
	return imm;
}

/**
 * 低16位无符号扩展
 */
int unsignedExtend(int imm) {
	imm &= 0xffff;
	return imm;
}

/**
 * 低8位符号扩展
 */
int sign8Extend(int imm) {
	imm <<= 24;
	imm >>= 24;
	return imm;
}

/**
 * 低8位无符号扩展
 */
int unsign8Extend(int imm) {
	imm &= 0xff;
	return imm;
}
