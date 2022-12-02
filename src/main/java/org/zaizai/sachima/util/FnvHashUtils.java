package org.zaizai.sachima.util;

import java.util.Arrays;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/2 15:42
 */
public class FnvHashUtils {

    public static final long BASIC = 0xcbf29ce484222325L;
    public static final long PRIME = 0x100000001b3L;

    protected FnvHashUtils() {
    }

    public static long fnv1a64(CharSequence input) {
        if (input == null) {
            return 0;
        }
        long hash = BASIC;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            hash ^= c;
            hash *= PRIME;
        }
        return hash;
    }

    public static long fnv1a64lower(String key) {
        long hashCode = BASIC;
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }
            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    public static long fnv1a64lower(CharSequence key) {
        return fnv1a64lower(BASIC, key);
    }

    public static long fnv1a64lower(CharSequence key, int offset, int end) {
        return fnv1a64lower(BASIC, key, offset, end);
    }

    public static long fnv1a64lower(long basic, CharSequence key) {
        return fnv1a64lower(basic, key, 0, key.length());
    }

    public static long fnv1a64lower(long basic, CharSequence key, int offset, int end) {
        long hashCode = basic;
        for (int i = offset; i < end; i++) {
            char ch = key.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }
            hashCode ^= ch;
            hashCode *= PRIME;
        }
        return hashCode;
    }

    public static long[] fnv1a64lower(String[] strings, boolean sort) {
        long[] hashCodes = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            hashCodes[i] = fnv1a64lower(strings[i]);
        }
        if (sort) {
            Arrays.sort(hashCodes);
        }
        return hashCodes;
    }

    public static long hashCode64(String key) {
        if (key == null) {
            return 0;
        }
        return isQuote(key) ? fnv1a64lower(key, 1, key.length() - 1) : fnv1a64lower(key, 0, key.length());
    }

    public static long hashCode64(long basic, String key) {
        if (key == null) {
            return basic;
        }
        boolean quote = isQuote(key);
        if (quote) {
            int offset = 1;
            int end = key.length() - 1;
            for (int i = end - 1; i >= 0; i--) {
                if (key.charAt(i) != ' ') {
                    break;
                }
                end--;
            }
            return fnv1a64lower(basic, key, offset, end);
        } else {
            return fnv1a64lower(basic, key, 0, key.length());
        }
    }


    private static boolean isQuote(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }

        int len = name.length();
        if (len > 2) {
            char c0 = name.charAt(0);
            char c1 = name.charAt(len - 1);
            return (c0 == '`' && c1 == '`') || (c0 == '"' && c1 == '"') || (c0 == '\'' && c1 == '\'') || (c0 == '[' && c1 == ']');
        }
        return false;
    }

    public static long quoteHash(long basic, String key) {
        boolean quote = isQuote(key);
        int start = quote ? 1 : 0;
        int end = quote ? key.length() - 1 : key.length();
        return fnv1a64lower(basic, key, start, end);
    }

    public static long hashCode64(String owner, String name) {
        long hashCode = BASIC;
        if (owner != null) {
            hashCode = quoteHash(hashCode, owner);
            hashCode ^= '.';
            hashCode *= PRIME;
        }
        if (name != null) {
            hashCode = quoteHash(hashCode, name);
        }
        return hashCode;
    }
}
