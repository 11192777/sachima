package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLImportDatabaseStatement extends SQLStatementImpl {
    private SQLName db;
    private SQLName status;

    public SQLName getDb() {
        return db;
    }

    public void setDb(SQLName db) {
        this.db = db;
    }

    public SQLName getStatus() {
        return status;
    }

    public void setStatus(SQLName status) {
        this.status = status;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, db);
            acceptChild(v, status);
        }
        v.endVisit(this);
    }
}
