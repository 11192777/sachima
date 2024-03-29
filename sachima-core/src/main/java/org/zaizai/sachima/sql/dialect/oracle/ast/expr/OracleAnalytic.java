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

import org.zaizai.sachima.sql.ast.SQLDataType;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.SQLOver;
import org.zaizai.sachima.sql.ast.SQLReplaceable;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleAnalytic extends SQLOver implements SQLReplaceable, OracleExpr {

    private OracleAnalyticWindowing windowing;

    public OracleAnalytic() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.windowing);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        children.addAll(this.partitionBy);
        if (this.orderBy != null) {
            children.add(orderBy);
        }
        if (this.windowing != null) {
            children.add(windowing);
        }
        return children;
    }

    public OracleAnalyticWindowing getWindowing() {
        return this.windowing;
    }

    public OracleAnalytic clone() {
        OracleAnalytic x = new OracleAnalytic();

        cloneTo(x);

        if (windowing != null) {
            x.setWindowing(windowing.clone());
        }

        return x;
    }

    public void setWindowing(OracleAnalyticWindowing x) {
        if (x != null) {
            x.setParent(this);
        }
        this.windowing = x;
    }

    public SQLDataType computeDataType() {
        return null;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {

        for (int i = 0; i < partitionBy.size(); i++) {
            if (partitionBy.get(i) == expr) {
                partitionBy.set(i, target);
                return true;
            }
        }

        return false;
    }
}
