package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.statement.SQLSelect;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019/9/5 10:48
 */
public class DrdsBaselineStatement extends MySqlStatementImpl implements SQLStatement {

    private String operation;
    private List<Long> baselineIds = new ArrayList<>();

    private SQLSelect select;

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void addBaselineId(long id) {
        baselineIds.add(id);
    }

    public List<Long> getBaselineIds() {
        return baselineIds;
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect select) {
        this.select = select;
    }

}
