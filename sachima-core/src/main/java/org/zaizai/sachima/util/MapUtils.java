package org.zaizai.sachima.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/12 11:12
 */
public class MapUtils {

    private MapUtils() {}

    public static boolean isEmpty(Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <K, V> Map<K, V> newHashMap(int size) {
        return new HashMap<>((int) (size / 0.75) + 1);
    }
}
