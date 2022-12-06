package org.zaizai.sachima.sql.dialect.oracle.visitor;

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.visitor.SQLASTParameterizedVisitor;

import java.util.List;

public class OracleASTParameterizedVisitor  extends SQLASTParameterizedVisitor implements OracleASTVisitor {
    public OracleASTParameterizedVisitor() {
        super(DbType.oracle);
    }

    public OracleASTParameterizedVisitor(List<Object> parameters) {
        super(DbType.oracle, parameters);
    }
}
