package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.statement.SQLAlterTableItem;
import org.zaizai.sachima.sql.ast.statement.SQLSelectOrderByItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlObjectImpl;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-03 15:59
 */
public class MySqlAlterTableOrderBy extends MySqlObjectImpl implements SQLAlterTableItem {

    private List<SQLSelectOrderByItem> columns = new ArrayList<SQLSelectOrderByItem>();

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    public List<SQLSelectOrderByItem> getColumns() {
        return columns;
    }

    public void addColumn(SQLSelectOrderByItem column) {
        columns.add(column);
    }
}
