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
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;
import org.zaizai.sachima.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterTableAddClusteringKey extends SQLObjectImpl implements SQLAlterTableItem {
    private SQLName                          name;
    private final List<SQLName> columns = new ArrayList<>();

    public SQLAlterTableAddClusteringKey() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (name != null) {
                name.accept(visitor);
            }
            for (SQLName column : columns) {
                column.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public List<SQLName> getColumns() {
        return columns;
    }
}
