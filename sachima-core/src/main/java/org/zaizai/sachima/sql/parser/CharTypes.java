/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaizai.sachima.sql.parser;

import static org.zaizai.sachima.sql.parser.LayoutCharacters.EOI;

public class CharTypes {

    private static final boolean[] hexFlags = new boolean[256];
    static {
        for (char c = 0; c < hexFlags.length; ++c) {
            if ((c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f') || (c >= '0' && c <= '9')) {
                hexFlags[c] = true;
            }
        }
    }

    public static boolean isHex(char c) {
        return c < 256 && hexFlags[c];
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static final boolean[] firstIdentifierFlags = new boolean[256];
    static {
        for (char c = 0; c < firstIdentifierFlags.length; ++c) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                firstIdentifierFlags[c] = true;
            }
        }
        firstIdentifierFlags['`'] = true;
        firstIdentifierFlags['_'] = true;
        firstIdentifierFlags['$'] = true;
    }

    public static boolean isFirstIdentifierChar(char c) {
        if (c <= firstIdentifierFlags.length) {
            return firstIdentifierFlags[c];
        }
        return c != '　' && c != '，';
    }

    private static final String[] stringCache = new String[256];
    private static final boolean[] identifierFlags = new boolean[256];
    static {
        for (char c = 0; c < identifierFlags.length; ++c) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                identifierFlags[c] = true;
            }
        }
        // identifierFlags['`'] = true;
        identifierFlags['_'] = true;
        identifierFlags['$'] = true;
        identifierFlags['#'] = true;

        for (int i = 0; i < identifierFlags.length; i++) {
            if (identifierFlags[i]) {
                char ch = (char) i;
                stringCache[i] = Character.toString(ch);
            }
        }
    }

    public static boolean isIdentifierChar(char c) {
        if (c <= identifierFlags.length) {
            return identifierFlags[c];
        }
        return c != '　' && c != '，' && c != '）' && c != '（';
    }

    public static String valueOf(char ch) {
        if (ch < stringCache.length) {
            return stringCache[ch];
        }
        return null;
    }

    private static final boolean[] whitespaceFlags = new boolean[256];
    static {
        for (int i = 0; i <= 32; ++i) {
            whitespaceFlags[i] = true;
        }
        
        whitespaceFlags[EOI] = false;
        for (int i = 0x7F; i <= 0xA0; ++i) {
            whitespaceFlags[i] = true;
        }
   
        whitespaceFlags[160] = true; // 特别处理
        whitespaceFlags[223] = true; // 特别处理, odps
        whitespaceFlags[229] = true; // 特别处理, odps
        whitespaceFlags[231] = true; // 特别处理, odps
    }

    /**
     * @return false if {@link LayoutCharacters#EOI}
     */
    public static boolean isWhitespace(char c) {
        return (c <= whitespaceFlags.length && whitespaceFlags[c]) //
               || c == '　'; // Chinese space
    }

    public static String trim(String value) {
        int len = value.length();
        int st = 0;

        while ((st < len) && (isWhitespace(value.charAt(st)))) {
            st++;
        }
        while ((st < len) && isWhitespace(value.charAt(len - 1))) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }
}
