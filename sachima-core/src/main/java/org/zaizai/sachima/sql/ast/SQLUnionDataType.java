package org.zaizai.sachima.sql.ast;

import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLUnionDataType extends SQLDataTypeImpl {
    private final List<SQLDataType> items = new ArrayList<>();

    @Override
    public String getName() {
        return "UNIONTYPE";
    }

    public List<SQLDataType> getItems() {
        return items;
    }

    public void add(SQLDataType item) {
        if (item != null) {
            item.setParent(this);
        }
        items.add(item);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    public SQLUnionDataType clone() {
        SQLUnionDataType x = new SQLUnionDataType();

        for (SQLDataType item : items) {
            x.add(item.clone());
        }

        return x;
    }
}
