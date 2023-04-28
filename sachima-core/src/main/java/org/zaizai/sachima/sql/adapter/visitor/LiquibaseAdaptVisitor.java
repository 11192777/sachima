package org.zaizai.sachima.sql.adapter.visitor;

import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/3/31 16:44
 */
public class LiquibaseAdaptVisitor extends MySQLToOracleAdaptVisitor {

    protected String sourceSql;

    public LiquibaseAdaptVisitor(Appendable appender, MySqlToOracleAdapterImpl handler, String sourceSql) {
        super(appender, handler);
        this.sourceSql = sourceSql;
    }

    @Override
    public boolean visit(MySqlCreateTableStatement x) {
        print0(sourceSql);
        this.adapter.onEvent(x);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        print0(sourceSql);
        this.adapter.onEvent(x);
        return false;
    }

}
