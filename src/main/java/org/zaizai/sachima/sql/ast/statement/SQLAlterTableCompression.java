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

import org.zaizai.sachima.sql.ast.SQLObjectImpl;
import org.zaizai.sachima.sql.ast.expr.SQLCharExpr;
import org.zaizai.sachima.sql.ast.expr.SQLIntegerExpr;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLAlterTableCompression extends SQLObjectImpl implements SQLAlterTableItem {

    private SQLCharExpr name;

    public SQLAlterTableCompression(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLCharExpr getName() {
        return name;
    }

    public void setName(SQLCharExpr name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

}
