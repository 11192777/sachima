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
package org.zaizai.sachima.sql.dialect.oracle.ast;

import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.sql.ast.SQLDataType;
import org.zaizai.sachima.sql.ast.SQLObjectImpl;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTOutputVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public abstract class OracleSQLObjectImpl extends SQLObjectImpl implements OracleSQLObject {

    public OracleSQLObjectImpl(){

    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof OracleASTVisitor) {
            this.accept0((OracleASTVisitor) v);
            return;
        }

        if (v instanceof SQLASTOutputVisitor) {
            ((SQLASTOutputVisitor) v).print(this.toString());
        }
    }

    public abstract void accept0(OracleASTVisitor visitor);

    public OracleSQLObject clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public SQLDataType computeDataType() {
        return null;
    }

    public String toString() {
        return SQLUtils.toOracleString(this);
    }

}
