package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.statement.SQLAlterStatement;
import org.zaizai.sachima.sql.ast.statement.SQLAssignItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.FullTextType;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MysqlAlterFullTextStatement extends MySqlStatementImpl implements SQLAlterStatement {

    private FullTextType type;

    private SQLName name;

    private SQLAssignItem item;

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

    public SQLAssignItem getItem() {
        return item;
    }

    public void setItem(SQLAssignItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.item = item;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

}
