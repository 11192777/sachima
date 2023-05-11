package org.zaizai.sachima.sql.adapter.visitor;

import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.ast.SQLDataTypeImpl;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableItem;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableStatement;
import org.zaizai.sachima.sql.ast.statement.SQLColumnDefinition;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;

import java.util.Objects;

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
        for (SQLAlterTableItem item : x.getItems()) {
            if (item instanceof MySqlAlterTableModifyColumn) {
                SQLColumnDefinition columnDefinition = ((MySqlAlterTableModifyColumn) item).getNewColumnDefinition();
                String replaceType = super.adapter.getMappingDataType(columnDefinition.getDataType().getName());
                if (Objects.nonNull(replaceType)) {
                    columnDefinition.setDataType(new SQLDataTypeImpl(replaceType));
                }
            }
        }
        this.adapter.onEvent(x);
        return super.visit(x);
    }

}
