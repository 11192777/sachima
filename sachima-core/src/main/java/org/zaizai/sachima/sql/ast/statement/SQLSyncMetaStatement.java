package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLSyncMetaStatement extends SQLStatementImpl {
    private Boolean restrict;
    private Boolean ignore;

    private SQLName from;
    private SQLExpr like;

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, from);
            acceptChild(v, like);
        }
        v.endVisit(this);
    }

    public Boolean getRestrict() {
        return restrict;
    }

    public void setRestrict(Boolean restrict) {
        this.restrict = restrict;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public SQLName getFrom() {
        return from;
    }

    public void setFrom(SQLName from) {
        this.from = from;
    }

    public SQLExpr getLike() {
        return like;
    }

    public void setLike(SQLExpr like) {
        this.like = like;
    }
}
