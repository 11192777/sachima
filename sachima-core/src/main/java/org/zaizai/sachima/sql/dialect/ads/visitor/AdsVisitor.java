package org.zaizai.sachima.sql.dialect.ads.visitor;

import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlPrimaryKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public interface AdsVisitor extends SQLASTVisitor {
    boolean visit(MySqlPrimaryKey x);
    void endVisit(MySqlPrimaryKey x);

    boolean visit(MySqlCreateTableStatement x);
    void endVisit(MySqlCreateTableStatement x);
}
