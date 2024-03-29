package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.statement.SQLShowStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-16 21:37
 */
public class DrdsShowDDLJobs extends MySqlStatementImpl implements SQLShowStatement {

    private boolean full = false;
    private List<Long> jobIds = new ArrayList<>();

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public List<Long> getJobIds() {
        return jobIds;
    }

    public void addJobId(long id) {
        jobIds.add(id);
    }
}
