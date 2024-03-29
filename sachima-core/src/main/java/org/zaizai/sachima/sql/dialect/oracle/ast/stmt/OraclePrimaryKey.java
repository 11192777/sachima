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
package org.zaizai.sachima.sql.dialect.oracle.ast.stmt;

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.SQLIndexDefinition;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.statement.SQLPrimaryKey;
import org.zaizai.sachima.sql.ast.statement.SQLPrimaryKeyImpl;
import org.zaizai.sachima.sql.ast.statement.SQLTableConstraint;
import org.zaizai.sachima.sql.ast.statement.SQLTableElement;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class OraclePrimaryKey extends SQLPrimaryKeyImpl implements OracleConstraint, SQLPrimaryKey, SQLTableElement, SQLTableConstraint {

    private OracleUsingIndexClause using;
    private SQLName                exceptionsInto;
    private Boolean                enable;
    private Initially              initially;
    private Boolean                deferrable;

    public OraclePrimaryKey() {
        this.dbType = DbType.oracle;
    }


    public OraclePrimaryKey(SQLIndexDefinition indexDefinition) {
        super(indexDefinition);
        super.dbType = DbType.oracle;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getColumns());
            acceptChild(visitor, using);
            acceptChild(visitor, exceptionsInto);
        }
        visitor.endVisit(this);
    }

    public Boolean getDeferrable() {
        return deferrable;
    }

    public void setDeferrable(Boolean deferrable) {
        this.deferrable = deferrable;
    }

    public OracleUsingIndexClause getUsing() {
        return using;
    }

    public void setUsing(OracleUsingIndexClause using) {
        this.using = using;
    }

    public SQLName getExceptionsInto() {
        return exceptionsInto;
    }

    public void setExceptionsInto(SQLName exceptionsInto) {
        this.exceptionsInto = exceptionsInto;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Initially getInitially() {
        return initially;
    }

    public void setInitially(Initially initially) {
        this.initially = initially;
    }

    public void cloneTo(OraclePrimaryKey x) {
        super.cloneTo(x);
        if (using != null) {
            x.setUsing(using.clone());
        }
        if (exceptionsInto != null) {
            x.setExceptionsInto(exceptionsInto.clone());
        }
        x.enable = enable;
        x.initially = initially;
        x.deferrable = deferrable;
    }

    public OraclePrimaryKey clone() {
        OraclePrimaryKey x = new OraclePrimaryKey();
        cloneTo(x);
        return x;
    }
}
