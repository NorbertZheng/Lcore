#include "qs_debug.h"

/**
 * debug function
 * @author 卢忠勇
 * @date 2012.03.06
 */

/**
 * assert that two ints are equal
 * @param expected - expected int value
 * @param actual - actual int value
 */
void assertEquals(int expected, int actual) {
	if (expected == actual) {
		printString("equal\n");
	} else {
		printString("not equal\n");
	}
}

/**
 * assert that the condition is true
 * @param condition - condition to be checked
 */
void assertTrue(int condition) {
	if (condition == 1) {
		printString("true\n");
	} else {
		printString("false\n");
	}
}

/**
 * assert that the condition is false
 * @param condition - condition to be checked
 */
void assertFalse(int condition) {
	assertTrue(!condition);
}



