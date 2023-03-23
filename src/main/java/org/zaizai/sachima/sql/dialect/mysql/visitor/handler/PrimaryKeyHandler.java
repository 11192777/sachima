package org.zaizai.sachima.sql.dialect.mysql.visitor.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.statement.SQLSelectOrderByItem;
import org.zaizai.sachima.sql.ast.statement.SQLTableElement;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlPrimaryKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.MapUtils;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 17:42
 */
public class PrimaryKeyHandler {

    private static final Log LOG = LogFactory.getLog(PrimaryKeyHandler.class);

    private static PrimaryKeyHandler instance;
    private final Supplier<?> idGenerator;
    private final Map<Long, String> tablesPrimaryKeyMap;

    private PrimaryKeyHandler(Map<Long, String> map, Supplier<?> idGenerator) {
        this.idGenerator = idGenerator;
        this.tablesPrimaryKeyMap = new HashMap<>((int) (map.size() / 0.75) + 1);
        map.forEach((key, value) -> tablesPrimaryKeyMap.put(key, SQLUtils.normalize(value).toUpperCase().intern()));
    }

    /**
     * @param tablesPrimaryKeyMap       Map[tableName , columnName]
     * @param idGenerator               getId();
     */
    public static void apply(Map<String, String> tablesPrimaryKeyMap, Supplier<?> idGenerator) {
        if (Objects.nonNull(instance)) {
            return;
        }
        if (MapUtils.isEmpty(tablesPrimaryKeyMap)) {
            LOG.warn("No Oracle primary key information was obtained.");
        }

        HashMap<Long, String> hashTablePrimaryKeyMap = new HashMap<>();
        tablesPrimaryKeyMap.forEach((k, v) -> hashTablePrimaryKeyMap.put(FnvHashUtils.fnv1a64lower(k), v));
        instance = new PrimaryKeyHandler(hashTablePrimaryKeyMap, idGenerator);
    }

    /**
     * apply by {@link DataSource}
     *
     * @param dataSource    {@link DataSource}
     * @param owner         schema of MySQL, username of Oracle
     * @param idGenerator   getId();
     * @throws SQLException  Bad SQL.
     */
    public static void apply(DataSource dataSource, String owner, Supplier<?> idGenerator) throws SQLException {
        if (Objects.nonNull(instance)) {
            return;
        }
        Assert.notNull(owner, "Owner parameter is required.");
        String obtainSql = "SELECT CU.TABLE_NAME, CU.COLUMN_NAME FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU WHERE CU.constraint_name = AU.constraint_name AND AU.constraint_type = 'P' AND AU.TABLE_NAME NOT LIKE 'BIN$%' and AU.OWNER = ?";
        Map<String, String> tablesPrimaryKeyMap = new HashMap<>();
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(obtainSql)) {
            preparedStatement.setString(1, owner);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                String primaryKey = resultSet.getString(2);
                tablesPrimaryKeyMap.put(tableName, primaryKey);
                LOG.debug("Succeeded in obtaining the primary field [" + tableName + "."  + primaryKey + "]");
            }
        } catch (SQLException e) {
            LOG.error("===> SQL execute failed: [" + obtainSql + "]", e);
            throw e;
        } finally {
            if (Objects.nonNull(resultSet)) {
                resultSet.close();
            }
        }
        apply(tablesPrimaryKeyMap, idGenerator);
    }

    /**
     * @return table primary key name.
     */
    public static String getTablePrimaryKey(String tableName) {
        if (Objects.isNull(instance) || StringUtils.isEmpty(tableName)) {
            return null;
        }
        return instance.tablesPrimaryKeyMap.get(FnvHashUtils.fnv1a64lower(tableName));
    }

    public static String generateId() {
        return String.valueOf(instance.idGenerator.get());
    }

    /**
     * <p>rebuild {@link PrimaryKeyHandler#tablesPrimaryKeyMap} by SQLStatement</p>
     *
     * @param x     SqlStatement {@link SQLStatement}
     * @author Qingyu.Meng
     * @since 2023/3/23
     */
    public static void refresh(SQLStatement x) {
        if (Objects.isNull(instance)) {
            return;
        }
        if (x instanceof MySqlCreateTableStatement) {
            refresh((MySqlCreateTableStatement) x);
        }
    }

    /**
     * Obtain the primary key name from MySqlCreateTableStatement.
     *
     * @param x {@link MySqlCreateTableStatement}
     */
    private static void refresh(MySqlCreateTableStatement x) {
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
                    instance.expand(tableName, primary);
                }
            }
        }
    }

    /**
     * <p>Add columns to {@link PrimaryKeyHandler#tablesPrimaryKeyMap}</p>
     *
     * @param tableName     tableName
     * @param columnName    columnName
     */
    private synchronized void expand(String tableName, String columnName) {
        String fixedColumnName = SQLUtils.normalize(columnName).toUpperCase().intern();
        this.tablesPrimaryKeyMap.put(FnvHashUtils.fnv1a64lower(tableName), fixedColumnName);
    }

}

