package org.zaizai.sachima.sql.dialect.mysql.visitor.handler;

import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.MapUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/12 10:06
 */
public class NclobTypeHandler {

    long[] tableColumnHashArray;

    public NclobTypeHandler(Map<String, Set<String>> tableColumnMap) {
        if (MapUtils.isEmpty(tableColumnMap)) {
            tableColumnHashArray = null;
        } else {
            AtomicInteger index = new AtomicInteger();
            this.tableColumnHashArray = new long[tableColumnMap.values().stream().mapToInt(Set::size).sum()];
            tableColumnMap.forEach((table, columns) -> columns.forEach(column -> this.tableColumnHashArray[index.getAndIncrement()] = this.getIndexHash(table, column)));
            Arrays.sort(this.tableColumnHashArray);
        }
    }

    private long getIndexHash(String tableName, String columnName) {
        return FnvHashUtils.fnv1a64lower(tableName + "," + columnName);
    }

    public boolean contains(String tableName, String columnName) {
        if (Objects.isNull(tableColumnHashArray) || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
            return false;
        }
        return Arrays.binarySearch(this.tableColumnHashArray, getIndexHash(tableName, columnName)) >= 0;
    }

}
