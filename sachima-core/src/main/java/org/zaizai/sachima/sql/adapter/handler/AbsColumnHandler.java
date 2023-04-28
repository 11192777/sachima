package org.zaizai.sachima.sql.adapter.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.adapter.bean.TabColumn;
import org.zaizai.sachima.sql.adapter.event.ColumnAlterEvent;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/3/31 18:22
 */
public abstract class AbsColumnHandler implements ColumnAlterEvent {

    private static final Log LOG = LogFactory.getLog(AbsColumnHandler.class);

    @Override
    public void onStatement(SQLStatement x) {
        if (x instanceof MySqlCreateTableStatement) {
            onStatement((MySqlCreateTableStatement) x);
        } else if (x instanceof SQLAlterTableStatement) {
            onStatement((SQLAlterTableStatement) x);
        }
    }

    /**
     * Parse sql: {@link AbsColumnHandler#sourceSql(String)} execution result rows.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link TabColumn}
     * @throws SQLException Such as DB permission.
     */
    protected abstract TabColumn parseColumn(ResultSet resultSet) throws SQLException;

    /**
     * Initialize the sql of AbsColumnHandler.
     *
     * @param owner Oracle DB owner.
     */
    protected abstract String sourceSql(String owner);

    /**
     * @param tabColumns columns.
     */
    protected abstract void initialize(List<TabColumn> tabColumns) ;

    /**
     * Build one instance.
     *
     * <pre>
     *     1. Obtain source sql. {@link AbsColumnHandler#sourceSql(String)}
     *     2. Execute source sql and convert to {@link TabColumn}
     * </pre>
     *
     * @param dataSource {@link DataSource}
     * @param owner      Nonnull. Oracle DB owner.
     * @throws SQLException May be DB permission.
     */
    protected void initialize(DataSource dataSource, String owner) throws SQLException {
        Assert.notNull(owner, "Owner parameter is required.");
        List<TabColumn> tabColumns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(this.sourceSql(owner))) {
            while (resultSet.next()) {
                TabColumn tabColumn = this.parseColumn(resultSet);
                LOG.debug("Succeeded in obtaining the file type [" + tabColumn + "]");
                tabColumns.add(tabColumn);
            }
        } catch (SQLException e) {
            LOG.error("===> SQL execute failed: [" + this.sourceSql(owner) + "]", e);
            throw e;
        }
        this.initialize(tabColumns);
    }

}

