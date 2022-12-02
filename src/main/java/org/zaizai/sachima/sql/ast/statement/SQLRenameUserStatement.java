package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLRenameUserStatement extends SQLStatementImpl {
    private SQLName name ;
    private SQLName to;

    public SQLRenameUserStatement() {
        dbType = DbType.mysql;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, name);
            acceptChild(v, to);
        }
        v.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLName getTo() {
        return to;
    }

    public void setTo(SQLName to) {
        this.to = to;
    }
}
