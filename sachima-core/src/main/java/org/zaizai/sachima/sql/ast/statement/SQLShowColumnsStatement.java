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

import org.zaizai.sachima.sql.ast.*;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.expr.SQLPropertyExpr;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLShowColumnsStatement extends SQLStatementImpl implements SQLShowStatement, SQLReplaceable {

    private boolean full;

    private SQLName table;
    private SQLName database;
    private SQLExpr like;
    private SQLExpr where;

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public SQLName getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        if (table instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) table;
            this.setDatabase((SQLName) propExpr.getOwner());
            this.table = new SQLIdentifierExpr(propExpr.getName());
            return;
        }
        this.table = table;
    }

    public SQLName getDatabase() {
        return database;
    }

    public void setDatabase(SQLName database) {
        this.database = database;
    }

    public SQLExpr getLike() {
        return like;
    }

    public void setLike(SQLExpr like) {
        this.like = like;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, table);
            acceptChild(visitor, database);
            acceptChild(visitor, like);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (table != null) {
            children.add(table);
        }
        if (database != null) {
            children.add(database);
        }
        if (like != null) {
            children.add(like);
        }
        if (where != null) {
            children.add(where);
        }
        return children;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (table == expr) {
            setTable((SQLName) target);
            return true;
        }

        if (database == expr) {
            setDatabase((SQLName) target);
            return true;
        }

        if (like == expr) {
            setLike(target);
            return true;
        }

        if (where == expr) {
            setWhere(target);
            return true;
        }

        return false;
    }
}
