package org.zaizai.sachima.util;

public class CharUtils {

    private CharUtils() {
    }

    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    public static boolean isNotBlankChar(char c) {
        return !isBlankChar(c);
    }

    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a' || c == '\u0000';
    }

    public static boolean equalsAny(char target, char... items) {
        for (char item : items) {
            if (target == item) {
                return true;
            }
        }
        return false;
    }
}
