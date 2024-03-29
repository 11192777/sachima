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
package org.zaizai.sachima.sql.dialect.oracle.ast.expr;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLExprImpl;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.SQLReplaceable;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleIsOfTypeExpr extends SQLExprImpl implements OracleExpr, SQLReplaceable {
    private SQLExpr expr;
    private List<SQLExpr> types = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OracleIsOfTypeExpr that = (OracleIsOfTypeExpr) o;

        if (expr != null ? !expr.equals(that.expr) : that.expr != null) return false;
        return types != null ? types.equals(that.types) : that.types == null;
    }

    @Override
    public int hashCode() {
        int result = expr != null ? expr.hashCode() : 0;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
            acceptChild(visitor, types);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }

        for (int i = 0; i < types.size(); i++) {
            if (types.get(i) == expr) {
                target.setParent(this);
                types.set(i, target);
                return true;
            }
        }

        return false;
    }

    @Override
    public SQLExpr clone() {
        OracleIsOfTypeExpr x = new OracleIsOfTypeExpr();
        if (expr != null) {
            x.setExpr(expr);
        }
        return null;
    }

    @Override
    public List<SQLObject> getChildren() {
        List children = new ArrayList<>();
        if (expr != null) {
            children.add(expr);
        }
        children.addAll(types);
        return children;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public List<SQLExpr> getTypes() {
        return types;
    }
}
