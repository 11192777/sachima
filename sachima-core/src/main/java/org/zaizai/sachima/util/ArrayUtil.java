package org.zaizai.sachima.util;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtil {

    private ArrayUtil() {
    }

    /**
     * 数组是否为非空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static <T> boolean isEmpty(T[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (ArrayUtil.isArray(obj)) {
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }


}
