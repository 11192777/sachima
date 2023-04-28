package org.zaizai.sachima.sql.adapter.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.constant.StrPool;
import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.adapter.bean.TabColumn;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.statement.*;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.util.*;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <H1>Initialize all field types in the database</H1>
 *
 * <pre>
 *     Use binary search algorithm. {@link ColumnTypeHandler#contains(String, String, long)}
 *     Time complexity: O(logn)
 * </pre>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/16 10:09
 */
public class ColumnTypeHandler extends AbsColumnHandler {

    private static final Log LOG = LogFactory.getLog(ColumnTypeHandler.class);

    /**
     * Map ({@link TokenFnvConstants}, {@link ColumnTypeHandler#getIndexHash(String, String)}: Array)
     */
    private Map<Long, long[]> columnTypeIndexMap;

    @Override
    protected TabColumn parseColumn(ResultSet resultSet) throws SQLException {
        TabColumn tabColumn = new TabColumn();
        tabColumn.setTableName(Assert.notNull(resultSet.getString("TABLE_NAME")));
        tabColumn.setColumnName(Assert.notNull(resultSet.getString("COLUMN_NAME")));
        tabColumn.setDataType(Assert.notNull(resultSet.getString("DATA_TYPE")));
        return tabColumn;
    }

    @Override
    protected String sourceSql(String owner) {
        return StringUtils.format("SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLUMNS WHERE OWNER = '{}'", owner);
    }

    @Override
    protected void initialize(List<TabColumn> tabColumns) {
        if (CollectionUtils.isEmpty(tabColumns)) {
            LOG.warn("No Oracle field information was obtained.");
        }
        Map<Long, long[]> hashMap = new HashMap<>();
        tabColumns.stream().collect(Collectors.groupingBy(TabColumn::getDataType)).forEach((dataType, columns) -> {
            AtomicInteger index = new AtomicInteger();
            long[] tabColumnHashArray = new long[columns.size()];
            columns.forEach(tabColumn -> tabColumnHashArray[index.getAndIncrement()] = this.getIndexHash(tabColumn.getTableName(), tabColumn.getColumnName()));
            Arrays.sort(tabColumnHashArray);
            hashMap.put(FnvHashUtils.fnv1a64lower(dataType), tabColumnHashArray);
        });
        this.columnTypeIndexMap = hashMap;
    }

    /**
     * to lower hash with (tableName, columnName)
     */
    private long getIndexHash(String tableName, String columnName) {
        return FnvHashUtils.fnv1a64lower(SQLUtils.normalize(tableName) + "," + SQLUtils.normalize(columnName));
    }

    /**
     * <p>Check table field type is exists.</p>
     *
     * @param dataType {@link TokenFnvConstants}
     */
    public boolean contains(String tableName, String columnName, long dataType) {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
            return false;
        }
        long[] hashArray = this.columnTypeIndexMap.get(dataType);
        if (Objects.isNull(hashArray)) {
            return false;
        }
        return Arrays.binarySearch(hashArray, this.getIndexHash(tableName, columnName)) > -1;
    }

    /**
     * @see ColumnTypeHandler#contains(String, String, long)
     */
    public boolean containsAny(String tableName, String columnName, Long... dataTypes) {
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
     * <pre>
     *     case: {@link SQLAlterTableAddColumn}
     *     case: {@link SQLAlterTableDropColumnItem}
     *     case: {@link MySqlAlterTableChangeColumn}
     * </pre>
     *
     * @param x {@link SQLAlterTableStatement}
     */
    @Override
    public void onStatement(SQLAlterTableStatement x) {
        String tableName = x.getTableSource().getTableName(true);
        for (SQLAlterTableItem item : x.getItems()) {
            if (item instanceof SQLAlterTableAddColumn) {
                for (SQLColumnDefinition column : ((SQLAlterTableAddColumn) item).getColumns()) {
                    expand(new TabColumn(tableName, column));
                }
            } else if (item instanceof SQLAlterTableDropColumnItem) {
                for (SQLName column : ((SQLAlterTableDropColumnItem) item).getColumns()) {
                    if (column instanceof SQLIdentifierExpr) {
                        this.reduce(tableName, column.getSimpleName());
                    }
                }
            } else if (item instanceof MySqlAlterTableChangeColumn) {
                this.reduce(tableName, ((MySqlAlterTableChangeColumn) item).getColumnName().getSimpleName());
                this.expand(new TabColumn(tableName, ((MySqlAlterTableChangeColumn) item).getNewColumnDefinition()));
            }
        }
    }

    /**
     * Obtain the column type from MySQLCreateTableStatement.
     *
     * @param x {@link MySqlCreateTableStatement}
     */
    @Override
    public void onStatement(MySqlCreateTableStatement x) {
        String tableName = x.getTableSource().getTableName(true);
        ArrayList<TabColumn> tabColumns = new ArrayList<>(x.getTableElementList().size());
        for (SQLTableElement e : x.getTableElementList()) {
            if (e instanceof SQLColumnDefinition) {
                tabColumns.add(new TabColumn(tableName, (SQLColumnDefinition) e));
            }
        }
        this.expand(tabColumns);
    }

    /**
     * @see ColumnTypeHandler#expand(List)
     */
    private void expand(TabColumn tabColumn) {
        this.expand(Collections.singletonList(tabColumn));
    }

    /**
     * <p>Add columns to {@link ColumnTypeHandler#columnTypeIndexMap}</p>
     *
     * @param tabColumns columns
     * @author Qingyu.Meng
     * @since 2023/3/23
     */
    private synchronized void expand(List<TabColumn> tabColumns) {
        tabColumns.stream().collect(Collectors.groupingBy(TabColumn::getDataType)).forEach((dataType, paramColumns) -> {
            long[] targetArray;
            AtomicInteger index;
            List<TabColumn> subTabColumns = paramColumns.stream().filter(c -> !this.contains(c)).collect(Collectors.toList());
            long[] tabColumnHashArray = this.columnTypeIndexMap.get(FnvHashUtils.fnv1a64lower(dataType));
            if (Objects.isNull(tabColumnHashArray)) {
                index = new AtomicInteger();
                targetArray = new long[subTabColumns.size()];
            } else {
                index = new AtomicInteger(tabColumnHashArray.length);
                targetArray = Arrays.copyOf(tabColumnHashArray, tabColumnHashArray.length + subTabColumns.size());
            }
            subTabColumns.forEach(tabColumn -> targetArray[index.getAndIncrement()] = getIndexHash(tabColumn.getTableName(), tabColumn.getColumnName()));
            Arrays.sort(targetArray);
            LOG.debug("Expand data type tabColumns:" + paramColumns);
            this.columnTypeIndexMap.put(FnvHashUtils.fnv1a64lower(dataType), targetArray);
        });
    }

    /**
     * @see ColumnTypeHandler#contains(String, String, long)
     */
    private boolean contains(TabColumn tabColumn) {
        return this.contains(tabColumn.getTableName(), tabColumn.getColumnName(), FnvHashUtils.fnv1a64lower(tabColumn.getDataType()));
    }

    /**
     * format toString
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        this.columnTypeIndexMap.forEach((key, values) -> {
            sb.append(ColumnTypeHandler.class.getSimpleName()).append(" Item: (").append(key).append(") ===> [");
            sb.append(Arrays.stream(values).mapToObj(String::valueOf).collect(Collectors.joining(StrPool.COMMA)));
            sb.append("];\n");
        });
        return sb.toString();
    }

    /**
     * Simple remove from {@link ColumnTypeHandler#columnTypeIndexMap} by SQLStatement
     *
     * <p>Reset hash index to {@link Long#MAX_VALUE}</p>
     *
     * @param tableName  normalize or not
     * @param columnName normalize or not
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
     * @param dataSource {@link DataSource}
     * @param owner      Nonnull. Oracle DB owner.
     * @return {@link ColumnTypeHandler}
     * @throws SQLException May be DB permission.
     */
    public static ColumnTypeHandler build(DataSource dataSource, String owner) throws SQLException {
        ColumnTypeHandler handler = new ColumnTypeHandler();
        handler.initialize(dataSource, owner);
        return handler;
    }

    /**
     * <pre>
     *     Param usefully fields:
     *     {@link TabColumn#getTableName()}
     *     {@link TabColumn#getColumnName()}
     *     {@link TabColumn#getDataType()}
     * </pre>
     *
     * @param tabColumns columns
     * @return {@link ColumnTypeHandler}
     */
    public static ColumnTypeHandler build(List<TabColumn> tabColumns) {
        ColumnTypeHandler handler = new ColumnTypeHandler();
        handler.initialize(tabColumns);
        return handler;
    }
}
