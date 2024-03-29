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

import java.util.Collections;
import java.util.List;

public class OracleIsSetExpr extends SQLExprImpl implements OracleExpr, SQLReplaceable {

    private SQLExpr nestedTable;

    public OracleIsSetExpr(){
    }

    public OracleIsSetExpr(SQLExpr nestedTable){
        this.nestedTable = nestedTable;
    }

    public OracleIsSetExpr clone() {
        OracleIsSetExpr x = new OracleIsSetExpr();
        if (nestedTable != null) {
            x.setNestedTable(nestedTable.clone());
        }
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.nestedTable == expr) {
            setNestedTable(target);
            return true;
        }
        return false;
    }

    public SQLExpr getNestedTable() {
        return nestedTable;
    }

    public void setNestedTable(SQLExpr nestedTable) {
        if (nestedTable != null) {
            nestedTable.setParent(this);
        }
        this.nestedTable = nestedTable;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, nestedTable);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(this.nestedTable);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nestedTable == null) ? 0 : nestedTable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OracleIsSetExpr other = (OracleIsSetExpr) obj;
        if (nestedTable == null) {
            if (other.nestedTable != null) {
                return false;
            }
        } else if (!nestedTable.equals(other.nestedTable)) {
            return false;
        }
        return true;
    }

}
