package org.zaizai.sachima.util;

import java.util.Collection;
import java.util.Objects;

public class CollectionUtils {

    private CollectionUtils() {
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static int sizeOf(Collection<?> c) {
        return Objects.isNull(c) ? 0 : c.size();
    }
}
