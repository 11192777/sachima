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

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleRaiseStatement extends OracleStatementImpl {

    private SQLExpr exception;

    public SQLExpr getException() {
        return exception;
    }

    public void setException(SQLExpr exception) {
        this.exception = exception;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, exception);
        }
        visitor.endVisit(this);
    }

}
