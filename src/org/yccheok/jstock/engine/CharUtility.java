// package com.wallyflint.util;
// The package statement has been commented out so that every class can reside in the same package. This allows a java 1.1 demo applet to
// run without throwing security exceptions.

package org.yccheok.jstock.engine;

public class CharUtility {

	/** Returns an int value that is negative if cCompare comes before cRef in the alphabet, zero if
	*   the two are equal, and positive if cCompare comes after cRef in the alphabet.
	*/
	public static int compareCharsAlphabetically(char cCompare, char cRef) {
		return (alphabetizeChar(cCompare) - alphabetizeChar(cRef));
	}
	
	private static int alphabetizeChar(char c) {
		if(c < 65) return c;
		if(c < 89) return (2 * c) - 65;
		if(c < 97) return c + 24;
		if(c < 121) return (2 * c) - 128;
		return c;
	}
	
}
