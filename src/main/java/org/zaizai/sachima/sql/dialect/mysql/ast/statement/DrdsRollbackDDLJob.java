package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-18 23:37
 */
public class DrdsRollbackDDLJob extends MySqlStatementImpl implements SQLStatement {

    private List<Long> jobIds = new ArrayList<Long>();

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public List<Long> getJobIds() {
        return jobIds;
    }

    public void addJobId(long id) {
        jobIds.add(id);
    }
}
