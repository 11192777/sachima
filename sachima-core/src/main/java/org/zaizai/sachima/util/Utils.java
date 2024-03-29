/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package org.zaizai.sachima.util;

import org.zaizai.sachima.lang.Assert;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public class Utils {

    private Utils() {}

    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String read(InputStream in) {
        if (in == null) {
            return null;
        }

        InputStreamReader reader;
        reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        return read(reader);
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        final int EOF = -1;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String read(Reader reader) {
        if (reader == null) {
            return null;
        }

        try {
            StringWriter writer = new StringWriter();

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }

            return writer.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("read error", ex);
        }
    }

    public static String read(Reader reader, int length) {
        if (reader == null) {
            return null;
        }

        try {
            char[] buffer = new char[length];

            int offset = 0;
            int rest = length;
            int len;
            while ((len = reader.read(buffer, offset, rest)) != -1) {
                rest -= len;
                offset += len;

                if (rest == 0) {
                    break;
                }
            }

            return new String(buffer, 0, length - rest);
        } catch (IOException ex) {
            throw new IllegalStateException("read error", ex);
        }
    }

    public static String toString(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    public static String toString(StackTraceElement[] stackTrace) {
        StringBuilder buf = new StringBuilder();
        for (StackTraceElement item : stackTrace) {
            buf.append(item.toString());
            buf.append("\n");
        }
        return buf.toString();
    }

    public static Boolean getBoolean(Properties properties, String key) {
        String property = properties.getProperty(key);
        if ("true".equals(property)) {
            return Boolean.TRUE;
        } else if ("false".equals(property)) {
            return Boolean.FALSE;
        }
        return null;
    }

    public static Integer getInteger(Properties properties, String key) {
        String property = properties.getProperty(key);

        if (property == null) {
            return null;
        }
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException ex) {
            // skip
        }
        return null;
    }


    private static Date startTime;

    public static Date getStartTime() {
        if (startTime == null) {
            startTime = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        }
        return startTime;
    }

    public static byte[] md5Bytes(String text) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        msgDigest.update(text.getBytes());

        return msgDigest.digest();
    }

    public static String md5(String text) {
        byte[] bytes = md5Bytes(text);
        return HexBin.encode(bytes, false);
    }

    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off] = (byte) (val >>> 56);
    }

    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static String hex(int hash) {
        byte[] bytes = new byte[4];

        bytes[3] = (byte) (hash);
        bytes[2] = (byte) (hash >>> 8);
        bytes[1] = (byte) (hash >>> 16);
        bytes[0] = (byte) (hash >>> 24);


        char[] chars = new char[8];
        for (int i = 0; i < 4; ++i) {
            byte b = bytes[i];

            int a = b & 0xFF;
            int b0 = a >> 4;
            int b1 = a & 0xf;

            chars[i * 2] = (char) (b0 + (b0 < 10 ? 48 : 55));
            chars[i * 2 + 1] = (char) (b1 + (b1 < 10 ? 48 : 55));
        }

        return new String(chars);
    }

    public static String hex(long hash) {
        byte[] bytes = new byte[8];

        bytes[7] = (byte) (hash);
        bytes[6] = (byte) (hash >>> 8);
        bytes[5] = (byte) (hash >>> 16);
        bytes[4] = (byte) (hash >>> 24);
        bytes[3] = (byte) (hash >>> 32);
        bytes[2] = (byte) (hash >>> 40);
        bytes[1] = (byte) (hash >>> 48);
        bytes[0] = (byte) (hash >>> 56);

        char[] chars = new char[16];
        for (int i = 0; i < 8; ++i) {
            byte b = bytes[i];

            int a = b & 0xFF;
            int b0 = a >> 4;
            int b1 = a & 0xf;

            chars[i * 2] = (char) (b0 + (b0 < 10 ? 48 : 55));
            chars[i * 2 + 1] = (char) (b1 + (b1 < 10 ? 48 : 55));
        }

        return new String(chars);
    }

    public static String hex_t(long hash) {
        byte[] bytes = new byte[8];

        bytes[7] = (byte) (hash);
        bytes[6] = (byte) (hash >>> 8);
        bytes[5] = (byte) (hash >>> 16);
        bytes[4] = (byte) (hash >>> 24);
        bytes[3] = (byte) (hash >>> 32);
        bytes[2] = (byte) (hash >>> 40);
        bytes[1] = (byte) (hash >>> 48);
        bytes[0] = (byte) (hash >>> 56);

        char[] chars = new char[18];
        chars[0] = 'T';
        chars[1] = '_';
        for (int i = 0; i < 8; ++i) {
            byte b = bytes[i];

            int a = b & 0xFF;
            int b0 = a >> 4;
            int b1 = a & 0xf;

            chars[i * 2 + 2] = (char) (b0 + (b0 < 10 ? 48 : 55));
            chars[i * 2 + 3] = (char) (b1 + (b1 < 10 ? 48 : 55));
        }

        return new String(chars);
    }

    public static void loadFromFile(String path, Set<String> set) {
        try (InputStream is = Assert.notNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while (Objects.nonNull(line = br.readLine())) {
                line = line.trim().toLowerCase();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                set.add(line);
            }
        } catch (Exception e) {
            //skip
        }
    }
}
