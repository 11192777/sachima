package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLCopyFromStatement extends SQLStatementImpl {
    private SQLExprTableSource table;
    private final List<SQLName> columns = new ArrayList<>();
    private SQLExpr from;
    private SQLExpr accessKeyId;
    private SQLExpr accessKeySecret;
    private final List<SQLAssignItem> options = new ArrayList<>();
    private final List<SQLAssignItem> partitions = new ArrayList<>();

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, table);
            acceptChild(v, columns);
            acceptChild(v, partitions);
            acceptChild(v, from);
            acceptChild(v, options);
        }
        v.endVisit(this);
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.table = x;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.from = x;
    }

    public SQLExpr getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.accessKeyId = x;
    }

    public SQLExpr getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.accessKeySecret = x;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }
}
