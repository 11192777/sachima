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

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.ast.expr.SQLVariantRefExpr;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLCallStatement extends SQLStatementImpl {

    private boolean             brace      = false;

    private SQLVariantRefExpr   outParameter;

    private SQLName             procedureName;

    private final List<SQLExpr> parameters = new ArrayList<>();
    
    public SQLCallStatement() {
        
    }
    
    public SQLCallStatement(DbType dbType) {
        super (dbType);
    }

    public SQLVariantRefExpr getOutParameter() {
        return outParameter;
    }

    public void setOutParameter(SQLVariantRefExpr outParameter) {
        this.outParameter = outParameter;
    }

    public SQLName getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(SQLName procedureName) {
        this.procedureName = procedureName;
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    public boolean isBrace() {
        return brace;
    }

    public void setBrace(boolean brace) {
        this.brace = brace;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.outParameter);
            acceptChild(visitor, this.procedureName);
            acceptChild(visitor, this.parameters);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        children.add(outParameter);
        children.add(procedureName);
        children.addAll(parameters);
        return null;
    }
}
