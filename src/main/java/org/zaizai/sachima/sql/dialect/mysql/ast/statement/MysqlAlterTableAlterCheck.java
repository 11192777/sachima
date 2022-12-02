package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlObjectImpl;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MysqlAlterTableAlterCheck extends MySqlObjectImpl implements SQLAlterTableItem {

    private SQLName name;
    private Boolean enforced;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (getName() != null) {
                getName().accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public Boolean getEnforced() {
        return enforced;
    }

    public void setEnforced(Boolean enforced) {
        this.enforced = enforced;
    }
}
