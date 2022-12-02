package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLWhoamiStatement extends SQLStatementImpl {
    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {

        }
        v.endVisit(this);
    }
}
