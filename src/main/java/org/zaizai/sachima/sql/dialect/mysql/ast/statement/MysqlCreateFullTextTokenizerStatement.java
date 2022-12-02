package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.expr.SQLTextLiteralExpr;
import org.zaizai.sachima.sql.ast.expr.SQLValuableExpr;
import org.zaizai.sachima.sql.ast.statement.SQLAssignItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.FullTextType;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.zaizai.sachima.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijun.cailj 2018/8/13
 */
public class MysqlCreateFullTextTokenizerStatement extends MySqlStatementImpl {

    private SQLName            name;
    private SQLTextLiteralExpr typeName;
    private SQLTextLiteralExpr userDefinedDict;
    protected final List<SQLAssignItem> options = new ArrayList<SQLAssignItem>();

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, typeName);
            acceptChild(visitor, options);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLTextLiteralExpr getUserDefinedDict() {
        return userDefinedDict;
    }

    public void setUserDefinedDict(SQLTextLiteralExpr userDefinedDict) {
        this.userDefinedDict = userDefinedDict;
    }

    public SQLTextLiteralExpr getTypeName() {
        return typeName;
    }

    public void setTypeName(SQLTextLiteralExpr typeName) {
        if (name != null) {
            name.setParent(this);
        }
        this.typeName = typeName;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public void addOption(String name, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(new SQLIdentifierExpr(name), value);
        assignItem.setParent(this);
        options.add(assignItem);
    }

    public SQLExpr getOption(String name) {
        if (name == null) {
            return null;
        }

        long hash64 = FnvHash.hashCode64(name);

        for (SQLAssignItem item : options) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) target).hashCode64() == hash64) {
                    return item.getValue();
                }
            }
        }

        return null;
    }

    public Object getOptionValue(String name) {
        SQLExpr option = getOption(name);
        if (option instanceof SQLValuableExpr) {
            return ((SQLValuableExpr) option).getValue();
        }

        return null;
    }
}
