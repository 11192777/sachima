package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-07-22 10:09
 */
public class DrdsClearDDLJobCache extends MySqlStatementImpl implements SQLStatement {

    private boolean allJobs = false;
    private List<Long> jobIds = new ArrayList<>();

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public boolean isAllJobs() {
        return allJobs;
    }

    public void setAllJobs(boolean allJobs) {
        this.allJobs = allJobs;
    }

    public List<Long> getJobIds() {
        return jobIds;
    }

    public void addJobId(long id) {
        jobIds.add(id);
    }

}
