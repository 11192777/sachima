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

import org.zaizai.sachima.sql.ast.SQLCommentHint;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.SQLReplaceable;
import org.zaizai.sachima.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.Collections;
import java.util.List;

public class OracleArgumentExpr extends OracleSQLObjectImpl implements SQLExpr, SQLReplaceable {

    private String  argumentName;
    private SQLExpr value;

    public OracleArgumentExpr(){

    }

    public OracleArgumentExpr(String argumentName, SQLExpr value){
        this.argumentName = argumentName;
        setValue(value);
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        this.value = value;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, value);
        }
        visitor.endVisit(this);
    }

    @Override
    public OracleArgumentExpr clone() {
        OracleArgumentExpr x = new OracleArgumentExpr();
        x.argumentName = argumentName;

        if (value != null) {
            x.setValue(value.clone());
        }

        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (value == expr) {
            setValue(target);
            return true;
        }
        return false;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(this.value);
    }

}