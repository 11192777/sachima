package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLDumpStatement extends SQLStatementImpl {
    private boolean overwrite;
    private SQLExprTableSource into;
    private SQLSelect select;

    public SQLDumpStatement() {

    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect x) {
        if (x != null) {
            x.setParent(this);
        }

        this.select = x;
    }

    public SQLExprTableSource getInto() {
        return into;
    }

    public void setInto(SQLExpr x) {
        if (x == null) {
            return;
        }

        setInto(new SQLExprTableSource(x));
    }

    public void setInto(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }

        this.into = x;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (into != null) {
                into.accept(visitor);
            }

            if (select != null) {
                select.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }
}
