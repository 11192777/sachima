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
package org.zaizai.sachima.sql.ast.expr;

import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.sql.ast.SQLDataType;
import org.zaizai.sachima.sql.ast.SQLExprImpl;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.statement.SQLCharacterDataType;
import org.zaizai.sachima.sql.visitor.SQLASTOutputVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLTempExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr, Comparable<SQLTempExpr> {

    //emm... I don't know what to do.
    public static final SQLDataType DATA_TYPE = new SQLCharacterDataType("char");

    private Target target;

    public SQLTempExpr() {
    }

    public SQLTempExpr(Object o) {
        this.target = new Target(o);
    }

    public SQLTempExpr(Object o, SQLObject parent) {
        this.target = new Target(o);
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SQLTempExpr other = (SQLTempExpr) obj;
        if (target == null) {
            return other.target == null;
        } else {
            return target.equals(other.target);
        }
    }

    @Override
    public int hashCode() {
        return target.getValue().hashCode();
    }


    public void output(Appendable buf) {
        this.accept(new SQLASTOutputVisitor(buf));
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public String getValue() {
        return target.getValue().toString();
    }

    public String toString() {
        return SQLUtils.toSQLString(this);
    }

    public SQLTempExpr clone() {
        return new SQLTempExpr(this.target);
    }

    public SQLDataType computeDataType() {
        return DATA_TYPE;
    }

    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public int compareTo(SQLTempExpr o) {
        if (this.target.hashCode() == o.hashCode()) {
            return 0;
        } else {
            return target.hashCode() > o.hashCode() ? 1 : -1;
        }
    }
}
