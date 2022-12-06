package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLForStatement extends SQLStatementImpl {
    protected SQLName index;
    protected SQLExpr range;

    protected List<SQLStatement> statements = new ArrayList<SQLStatement>();

    public SQLForStatement() {

    }

    public SQLName getIndex() {
        return index;
    }

    public void setIndex(SQLName index) {
        this.index = index;
    }

    public SQLExpr getRange() {
        return range;
    }

    public void setRange(SQLExpr range) {
        if (range != null) {
            range.setParent(this);
        }
        this.range = range;
    }

    public List<SQLStatement> getStatements() {
        return statements;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, index);
            acceptChild(v, range);
            acceptChild(v, statements);
        }
        v.endVisit(this);

    }
}
