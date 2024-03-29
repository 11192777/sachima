package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.statement.SQLShowStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * version 1.0
 * Author zzy
 * Date 2019/10/8 20:06
 */
public class DrdsShowGlobalIndex extends MySqlStatementImpl implements SQLShowStatement {

    private SQLName tableName = null;

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        tableName.setParent(this);
        this.tableName = tableName;
    }

}
