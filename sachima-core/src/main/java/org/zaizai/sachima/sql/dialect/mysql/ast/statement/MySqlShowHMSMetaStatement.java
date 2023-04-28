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
package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.expr.SQLPropertyExpr;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowHMSMetaStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private SQLName    name;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public String getSchema() {
        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnerName();
        }
        return null;
    }

    public String getTableName() {
        return name.getSimpleName();
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }
}
