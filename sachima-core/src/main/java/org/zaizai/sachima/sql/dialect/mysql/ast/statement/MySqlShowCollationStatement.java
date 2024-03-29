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

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowCollationStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private SQLExpr where;
    private SQLExpr pattern;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, where);
            acceptChild(visitor, pattern);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    public SQLExpr getPattern() {
        return pattern;
    }

    public void setPattern(SQLExpr pattern) {
        this.pattern = pattern;
    }

}
