package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLCreateOutlineStatement extends SQLStatementImpl {
    private SQLName name;
    private SQLExpr where;

    private SQLStatement on;
    private SQLStatement to;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public SQLStatement getOn() {
        return on;
    }

    public void setOn(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.on = x;
    }

    public SQLStatement getTo() {
        return to;
    }

    public void setTo(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.to = x;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, name);
            acceptChild(v, where);
            acceptChild(v, on);
            acceptChild(v, to);
        }
        v.endVisit(this);
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.where = x;
    }
}
