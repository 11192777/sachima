package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.statement.SQLAlterTableItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlObjectImpl;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-03 15:43
 */
public class MySqlAlterTableForce extends MySqlObjectImpl implements SQLAlterTableItem {
    @Override
    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
