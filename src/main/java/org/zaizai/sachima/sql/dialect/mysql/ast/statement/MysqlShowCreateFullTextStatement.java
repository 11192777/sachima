package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.dialect.mysql.ast.FullTextType;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MysqlShowCreateFullTextStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private FullTextType type;

    private SQLName name;


    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public FullTextType getType() {
        return type;
    }

    public void setType(FullTextType type) {
        this.type = type;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

}
