package org.zaizai.sachima.sql.adapter.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.adapter.bean.TabColumn;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.util.*;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/3/31 17:44
 */
public class NonNullTypeHandler extends AbsColumnHandler{

    private static final Log LOG = LogFactory.getLog(NonNullTypeHandler.class);
    private long[] tableColumnHashArray;


    @Override
    public void onStatement(SQLAlterTableStatement x) {
        //TODO
    }

    @Override
    public void onStatement(MySqlCreateTableStatement x) {
        //TODO
    }

    @Override
    protected TabColumn parseColumn(ResultSet resultSet) throws SQLException {
        TabColumn tabColumn = new TabColumn();
        tabColumn.setTableName(Assert.notNull(resultSet.getString("TABLE_NAME")));
        tabColumn.setColumnName(Assert.notNull(resultSet.getString("COLUMN_NAME")));
        return tabColumn;
    }

    @Override
    protected String sourceSql(String owner) {
        String sql = "SELECT TABLE_NAME, COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE NULLABLE = 'N' AND DATA_DEFAULT IS NOT NULL AND OWNER = '{}'";
        return StringUtils.format(sql, owner);
    }

    @Override
    protected void initialize(List<TabColumn> tabColumns) {
        if (CollectionUtils.isEmpty(tabColumns)) {
            LOG.warn("No Oracle field information was obtained.");
        }
        this.tableColumnHashArray = new long[tabColumns.size()];
        AtomicInteger index = new AtomicInteger();
        tabColumns.forEach(t -> this.tableColumnHashArray[index.getAndIncrement()] = this.getIndexHash(t.getTableName(), t.getColumnName()));
        Arrays.sort(this.tableColumnHashArray);
    }

    /**
     * to lower hash with (tableName, columnName)
     */
    private long getIndexHash(String tableName, String columnName) {
        return FnvHashUtils.fnv1a64lower(SQLUtils.normalize(tableName) + "," + SQLUtils.normalize(columnName));
    }

    /**
     * <p>Check column is not null and has default value.</p>
     */
    public boolean contains(String tableName, String columnName) {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
            return false;
        }
        return Arrays.binarySearch(this.tableColumnHashArray, this.getIndexHash(tableName, columnName)) > -1;
    }

    /**
     * @param dataSource {@link DataSource}
     * @param owner      Nonnull. Oracle DB owner.
     * @return {@link NonNullTypeHandler}
     * @throws SQLException May be DB permission.
     */
    public static NonNullTypeHandler build(DataSource dataSource, String owner) throws SQLException {
        NonNullTypeHandler handler = new NonNullTypeHandler();
        handler.initialize(dataSource, owner);
        return handler;
    }

    /**
     * @param tablesPrimaryKeyMap Map (tableName , columnName)
     */
    public static NonNullTypeHandler build(Map<String, String> tablesPrimaryKeyMap) {
        if (MapUtils.isEmpty(tablesPrimaryKeyMap)) {
            LOG.warn("No Oracle primary key information was obtained.");
        }
        ArrayList<TabColumn> tabColumns = new ArrayList<>(tablesPrimaryKeyMap.size());
        tablesPrimaryKeyMap.forEach((k, v) -> tabColumns.add(new TabColumn(k, v)));
        NonNullTypeHandler handler = new NonNullTypeHandler();
        handler.initialize(tabColumns);
        return handler;
    }
}
