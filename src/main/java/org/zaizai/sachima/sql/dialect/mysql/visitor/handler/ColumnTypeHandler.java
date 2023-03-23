package org.zaizai.sachima.sql.dialect.mysql.visitor.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.constant.StrPool;
import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.statement.*;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
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
        return FnvHashUtils.fnv1a64lower(SQLUtils.normalize(tableName) + "," + SQLUtils.normalize(columnName));
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
        return Arrays.binarySearch(hashArray, getIndexHash(tableName, columnName)) > -1;
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

    /**
     * <p>rebuild {@link ColumnTypeHandler#columnTypeIndexMap} by SQLStatement</p>
     *
     * @param x     SqlStatement {@link SQLStatement}
     * @author Qingyu.Meng
     * @since 2023/3/23
     */
    public static void refresh(SQLStatement x) {
        if (!nonNull()) {
            return;
        }
        if (x instanceof MySqlCreateTableStatement) {
            refresh((MySqlCreateTableStatement) x);
        } else if (x instanceof SQLAlterTableStatement) {
            refresh((SQLAlterTableStatement) x);
        }
    }

    /**
     * <pre>
     *     case: {@link SQLAlterTableAddColumn}
     *     case: {@link SQLAlterTableDropColumnItem}
     *     case: {@link MySqlAlterTableChangeColumn}
     * </pre>
     *
     * @param x {@link SQLAlterTableStatement}
     */
    private static void refresh(SQLAlterTableStatement x) {
        String tableName = x.getTableSource().getTableName(true);
        for (SQLAlterTableItem item : x.getItems()) {
            if (item instanceof SQLAlterTableAddColumn) {
                for (SQLColumnDefinition column : ((SQLAlterTableAddColumn) item).getColumns()) {
                    expand(new ColumnType(tableName, column));
                }
            } else if (item instanceof SQLAlterTableDropColumnItem) {
                for (SQLName column : ((SQLAlterTableDropColumnItem) item).getColumns()) {
                    if (column instanceof SQLIdentifierExpr) {
                        instance.reduce(tableName, column.getSimpleName());
                    }
                }
            } else if (item instanceof MySqlAlterTableChangeColumn) {
                instance.reduce(tableName, ((MySqlAlterTableChangeColumn) item).getColumnName().getSimpleName());
                expand(new ColumnType(tableName, ((MySqlAlterTableChangeColumn) item).getNewColumnDefinition()));
            }
        }
    }

    /**
     * Simple remove from {@link ColumnTypeHandler#columnTypeIndexMap} by SQLStatement
     *
     * <p>Reset hash index to {@link Long#MAX_VALUE}</p>
     *
     * @param tableName       normalize or not
     * @param columnName      normalize or not
     */
    private synchronized void reduce(String tableName, String columnName) {
        this.columnTypeIndexMap.forEach((key, array) -> {
            int index = Arrays.binarySearch(array, getIndexHash(tableName, columnName));
            if (index > -1) {
                array[index] = Long.MAX_VALUE;
                Arrays.sort(array);
            }
            LOG.debug("Reduce data type columns. tableName:" + tableName + ", columnName:" + columnName);
        });
    }

    /**
     * Obtain the column type from MySQLCreateTableStatement.
     *
     * @param x {@link MySqlCreateTableStatement}
     */
    private static void refresh(MySqlCreateTableStatement x) {
        String tableName = x.getTableSource().getTableName(true);
        ArrayList<ColumnType> columnTypeList = new ArrayList<>(x.getTableElementList().size());
        for (SQLTableElement e : x.getTableElementList()) {
            if (e instanceof SQLColumnDefinition) {
                columnTypeList.add(new ColumnType(tableName, (SQLColumnDefinition) e));
            }
        }
        instance.expand(columnTypeList);
    }

    /**
     * @see ColumnTypeHandler#expand(List)
     */
    private static void expand(ColumnType columnType) {
        instance.expand(Collections.singletonList(columnType));
    }

    /**
     * <p>Add columns to {@link ColumnTypeHandler#columnTypeIndexMap}</p>
     *
     * @param columnTypeList    columns
     * @author Qingyu.Meng
     * @since 2023/3/23
     */
    private synchronized void expand(List<ColumnType> columnTypeList) {
        columnTypeList.stream().collect(Collectors.groupingBy(ColumnType::getDataType)).forEach((dataType, paramColumns) -> {
            long[] targetArray;
            AtomicInteger index;
            List<ColumnType> columns = paramColumns.stream().filter(c -> !ColumnTypeHandler.contains(c)).collect(Collectors.toList());
            long[] tableColumnHashArray = this.columnTypeIndexMap.get(FnvHashUtils.fnv1a64lower(dataType));
            if (Objects.isNull(tableColumnHashArray)) {
                index = new AtomicInteger();
                targetArray = new long[columns.size()];
            } else {
                index = new AtomicInteger(tableColumnHashArray.length);
                targetArray = Arrays.copyOf(tableColumnHashArray, tableColumnHashArray.length + columns.size());
            }
            columns.forEach(column -> targetArray[index.getAndIncrement()] = getIndexHash(column.getTableName(), column.getColumnName()));
            Arrays.sort(targetArray);
            LOG.debug("Expand data type columns:" + paramColumns);
            this.columnTypeIndexMap.put(FnvHashUtils.fnv1a64lower(dataType), targetArray);
        });
    }

    /**
     * @see ColumnTypeHandler#contains(String, String, long) 
     */
    private static boolean contains(ColumnType columnType) {
        return contains(columnType.getTableName(), columnType.getColumnName(), FnvHashUtils.fnv1a64lower(columnType.getDataType()));
    }

    public static String print() {
        if (!nonNull()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        instance.columnTypeIndexMap.forEach((key, values) -> {
            sb.append(ColumnTypeHandler.class.getSimpleName()).append(" Item: (").append(key).append(") ===> [");
            sb.append(Arrays.stream(values).mapToObj(String::valueOf).collect(Collectors.joining(StrPool.COMMA)));
            sb.append("];\n");
        });
        return sb.toString();
    }

    public static class ColumnType {
        private final String tableName;
        private final String columnName;
        private final String dataType;

        public ColumnType(String tableName, String columnName, String dataType) {
            this.tableName = tableName;
            this.columnName = columnName;
            int leftBracketIndex = dataType.indexOf("(");
            this.dataType = leftBracketIndex == -1 ? dataType : dataType.substring(0, leftBracketIndex);
        }

        public ColumnType(String tableName, SQLColumnDefinition sqlColumnDefinition) {
            this.tableName = tableName;
            this.columnName = sqlColumnDefinition.getColumnName();
            this.dataType = sqlColumnDefinition.getDataType().getName();
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
