package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.statement.SQLColumnDefinition;
import org.zaizai.sachima.sql.dialect.mysql.ast.FullTextType;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MysqlCreateFullTextDictionaryStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private SQLName name;

    private SQLColumnDefinition column;

    private String comment;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLColumnDefinition getColumn() {
        return column;
    }

    public void setColumn(SQLColumnDefinition column) {
        this.column = column;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, column);
        }
        visitor.endVisit(this);
    }

}
