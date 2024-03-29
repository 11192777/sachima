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

import org.zaizai.sachima.sql.ast.SQLExprImpl;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLDefaultExpr extends SQLExprImpl implements SQLLiteralExpr {

    @Override
    public boolean equals(Object o) {
        return o instanceof SQLDefaultExpr;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String toString() {
        return "DEFAULT";
    }

    public SQLDefaultExpr clone() {
        return new SQLDefaultExpr();
    }

    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }
}
