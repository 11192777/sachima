package org.zaizai.sachima.sql.adapter.handler;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.adapter.bean.TabColumn;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableStatement;
import org.zaizai.sachima.sql.ast.statement.SQLSelectOrderByItem;
import org.zaizai.sachima.sql.ast.statement.SQLTableElement;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlPrimaryKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.MapUtils;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 17:42
 */
public class PrimaryKeyHandler extends AbsColumnHandler {

    private static final Log LOG = LogFactory.getLog(PrimaryKeyHandler.class);

    private Supplier<?> idGenerator;

    /**
     * Map (TableName Hash, Primary key name)
     */
    private Map<Long, String> tablesPrimaryKeyMap;

    private PrimaryKeyHandler() {
        idGenerator = IdWorker::getId;
    }

    public void setIdGenerator(Supplier<?> idGenerator) {
        this.idGenerator = idGenerator;
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
        String sql = "SELECT CU.TABLE_NAME, CU.COLUMN_NAME FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU " +
                "WHERE CU.constraint_name = AU.constraint_name AND AU.constraint_type = 'P' " +
                "AND AU.TABLE_NAME NOT LIKE 'BIN$%' AND AU.OWNER = '{}'";
        return StringUtils.format(sql, owner);
    }

    @Override
    protected void initialize(List<TabColumn> tabColumns) {
        this.tablesPrimaryKeyMap = MapUtils.newHashMap(tabColumns.size());
        tabColumns.forEach(c -> this.tablesPrimaryKeyMap.put(FnvHashUtils.fnv1a64lower(c.getTableName()),
                SQLUtils.normalize(c.getColumnName()).toUpperCase().intern()));
    }

    @Override
    public void onStatement(SQLAlterTableStatement x) {
        //skip
    }

    /**
     * Obtain the primary key name from MySqlCreateTableStatement.
     *
     * @param x {@link MySqlCreateTableStatement}
     */
    @Override
    public void onStatement(MySqlCreateTableStatement x) {
        String tableName = x.getTableSource().getTableName(true);
        for (SQLTableElement e : x.getTableElementList()) {
            if (e instanceof MySqlPrimaryKey) {
                List<SQLSelectOrderByItem> columns = ((MySqlPrimaryKey) e).getIndexDefinition().getColumns();
                if (columns.size() != 1) {
                    return;
                }
                SQLExpr expr = columns.get(0).getExpr();
                if (expr instanceof SQLIdentifierExpr) {
                    String primary = ((SQLIdentifierExpr) expr).getSimpleName();
                    this.expand(tableName, primary);
                }
            }
        }
    }

    /**
     * <p>Add columns to {@link PrimaryKeyHandler#tablesPrimaryKeyMap}</p>
     *
     * @param tableName  tableName
     * @param columnName columnName
     */
    private synchronized void expand(String tableName, String columnName) {
        String fixedColumnName = SQLUtils.normalize(columnName).toUpperCase().intern();
        this.tablesPrimaryKeyMap.put(FnvHashUtils.fnv1a64lower(tableName), fixedColumnName);
    }

    /**
     * @return table primary key name.
     */
    public String getTablePrimaryKey(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }
        return this.tablesPrimaryKeyMap.get(FnvHashUtils.fnv1a64lower(tableName));
    }

    /**
     * @param dataSource {@link DataSource}
     * @param owner      Nonnull. Oracle DB owner.
     * @return {@link PrimaryKeyHandler}
     * @throws SQLException May be DB permission.
     */
    public static PrimaryKeyHandler build(DataSource dataSource, String owner) throws SQLException {
        PrimaryKeyHandler handler = new PrimaryKeyHandler();
        handler.initialize(dataSource, owner);
        return handler;
    }

    /**
     * @param tablesPrimaryKeyMap Map (tableName , columnName)
     */
    public static PrimaryKeyHandler build(Map<String, String> tablesPrimaryKeyMap) {
        if (MapUtils.isEmpty(tablesPrimaryKeyMap)) {
            LOG.warn("No Oracle primary key information was obtained.");
        }
        ArrayList<TabColumn> tabColumns = new ArrayList<>(tablesPrimaryKeyMap.size());
        tablesPrimaryKeyMap.forEach((k, v) -> tabColumns.add(new TabColumn(k ,v)));
        PrimaryKeyHandler primaryKeyHandler = new PrimaryKeyHandler();
        primaryKeyHandler.initialize(tabColumns);
        return primaryKeyHandler;
    }

    public String generateId() {
        return String.valueOf(this.idGenerator.get());
    }

}

