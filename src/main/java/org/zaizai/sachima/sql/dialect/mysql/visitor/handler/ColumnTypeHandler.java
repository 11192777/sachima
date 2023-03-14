package org.zaizai.sachima.sql.dialect.mysql.visitor.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.util.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <H1>Initialize all field types in the database</H1>
 *
 * <p>Singleton {@link ColumnTypeHandler#instance}</p>
 * <pre>
 *     Use binary search algorithm. {@link ColumnTypeHandler#contains(String, String, long)}
 *     Time complexity: O(logn)
 * </pre>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/16 10:09
 */
public class ColumnTypeHandler {

    private static final Log LOG = LogFactory.getLog(ColumnTypeHandler.class);

    private static ColumnTypeHandler instance;
    private final Map<Long, long[]> columnTypeIndexMap;

    /**
     * <p> Hash user: {@link FnvHashUtils}</p>
     *
     * @param map Map[DataTypeHash , Hash of (tableName + columnName)]
     */
    private ColumnTypeHandler(Map<Long, long[]> map) {
        this.columnTypeIndexMap = map;
    }

    /**
     * @param columnTypeList {@link ColumnType}
     */
    public static ColumnTypeHandler apply(List<ColumnType> columnTypeList) {
        if (CollectionUtils.isEmpty(columnTypeList)) {
            LOG.warn("No Oracle field information was obtained.");
        }
        Map<Long, long[]> hashMap = new HashMap<>();
        columnTypeList.stream().collect(Collectors.groupingBy(ColumnType::getDataType)).forEach((dataType, columns) -> {
            AtomicInteger index = new AtomicInteger();
            long[] tableColumnHashArray = new long[columns.size()];
            columns.forEach(column -> tableColumnHashArray[index.getAndIncrement()] = getIndexHash(column.getTableName(), column.getColumnName()));
            Arrays.sort(tableColumnHashArray);
            hashMap.put(FnvHashUtils.fnv1a64lower(dataType), tableColumnHashArray);
        });
        instance = new ColumnTypeHandler(hashMap);
        return instance;
    }

    /**
     * @param columnTypeMap Map[DataType, [ColumnType, columnNames]]
     */
    public static ColumnTypeHandler apply(Map<String, Map<String, List<String>>> columnTypeMap) {
        if (MapUtils.isEmpty(columnTypeMap)) {
            return null;
        }
        ArrayList<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeMap.forEach((dataType, columnsMap) -> columnsMap.forEach((tableName, columnNames) -> columnNames.forEach(columnName -> columnTypeList.add(new ColumnType(tableName, columnName, dataType)))));
        return apply(columnTypeList);
    }

    /**
     * apply by {@link DataSource}
     *
     * @param dataSource {@link DataSource}
     * @param owner      schema of MySQL, username of Oracle
     */
    public static ColumnTypeHandler apply(DataSource dataSource, String owner) throws SQLException {
        if (Objects.nonNull(instance)) {
            return instance;
        }
        Assert.notNull(owner, "Owner parameter is required.");
        String obtainSql = "select TABLE_NAME, COLUMN_NAME, DATA_TYPE from ALL_TAB_COLUMNS where OWNER = ?";
        List<ColumnType> columnTypeList = new ArrayList<>();
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(obtainSql)) {
            preparedStatement.setString(1, owner);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ColumnType columnType = new ColumnType(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                LOG.debug("Succeeded in obtaining the file type [" + columnType + "]");
                columnTypeList.add(columnType);
            }
        } catch (SQLException e) {
            LOG.error("===> SQL execute failed: [" + obtainSql + "]", e);
            throw e;
        } finally {
            if (Objects.nonNull(resultSet)) {
                resultSet.close();
            }
        }
        return apply(columnTypeList);
    }

    /**
     * use this?
     */
    public static boolean nonNull() {
        return Objects.nonNull(instance);
    }

    /**
     * to lower hash with ([tableName],[columnName])
     */
    private static long getIndexHash(String tableName, String columnName) {
        return FnvHashUtils.fnv1a64lower(tableName + "," + columnName);
    }

    /**
     * <p>Check table field type is exists.</p>
     *
     * @param dataType {@link TokenFnvConstants}
     */
    public static boolean contains(String tableName, String columnName, long dataType) {
        if (Objects.isNull(instance) || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
            return false;
        }
        long[] hashArray = instance.columnTypeIndexMap.get(dataType);
        if (Objects.isNull(hashArray)) {
            return false;
        }
        return Arrays.binarySearch(hashArray, getIndexHash(tableName, columnName)) >= 0;
    }

    /**
     * @see ColumnTypeHandler#contains(String, String, long)
     */
    public static boolean containsAny(String tableName, String columnName, Long... dataTypes) {
        if (ArrayUtil.isEmpty(dataTypes)) {
            return false;
        }
        for (Long dataType : dataTypes) {
            if (contains(tableName, columnName, dataType)) {
                return true;
            }
        }
        return false;
    }

    public static class ColumnType {
        private final String tableName;
        private final String columnName;
        private final String dataType;

        public ColumnType(String tableName, String columnName, String dataType) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.dataType = dataType;
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getDataType() {
            return dataType;
        }

        @Override
        public String toString() {
            return StringUtils.format("tableName:{}, columnName:{}, dataType:{}", this.tableName, this.columnName, this.dataType);
        }
    }
}
