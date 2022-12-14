/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLHint;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLReplaceable;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.expr.SQLListExpr;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 23/02/2017.
 */
public class SQLValuesTableSource extends SQLTableSourceImpl implements SQLSelectQuery, SQLReplaceable {
    private boolean bracket;
    private List<SQLListExpr> values = new ArrayList<>();
    private List<SQLName> columns = new ArrayList<>();

    public SQLValuesTableSource() {

    }

    public List<SQLListExpr> getValues() {
        return values;
    }

    public void addValue(SQLListExpr row) {
        if (row == null) {
            return;
        }

        row.setParent(this);
        values.add(row);
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void addColumn(SQLName column) {
        column.setParent(this);
        columns.add(column);
    }

    public void addColumn(String column) {
        addColumn(new SQLIdentifierExpr(column));
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, values);
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean isBracket() {
        return bracket;
    }

    @Override
    public void setBracket(boolean bracket) {
        this.bracket = bracket;
    }

    @Override
    public SQLValuesTableSource clone() {

        SQLValuesTableSource x = new SQLValuesTableSource();

        x.setAlias(this.alias);

        for (SQLListExpr e : this.values) {
            SQLListExpr e2 = e.clone();
            e2.setParent(x);
            x.getValues().add(e2);
        }

        for (SQLName e : this.columns) {
            SQLName e2 = e.clone();
            e2.setParent(x);
            x.getColumns().add(e2);
        }

        if (this.flashback != null) {
            x.setFlashback(this.flashback.clone());
        }

        if (this.hints != null) {
            for (SQLHint e : this.hints) {
                SQLHint e2 = e.clone();
                e2.setParent(x);
                x.getHints().add(e2);
            }
        }

        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == expr) {
                target.setParent(this);
                values.set(i, (SQLListExpr) expr);
                return true;
            }
        }

        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i) == expr) {
                target.setParent(this);
                columns.set(i, (SQLName) expr);
                return true;
            }
        }

        return false;
    }
}
