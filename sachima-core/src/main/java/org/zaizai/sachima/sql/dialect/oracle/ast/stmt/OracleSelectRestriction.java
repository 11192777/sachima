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

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;

public abstract class OracleSelectRestriction extends OracleSQLObjectImpl {

    protected SQLName constraint;

    public OracleSelectRestriction(){

    }

    public SQLName getConstraint() {
        return constraint;
    }

    public void setConstraint(SQLName constraint) {
        if (constraint != null) {
            constraint.setParent(this);
        }
        this.constraint = constraint;
    }

    public static class CheckOption extends OracleSelectRestriction {

        public CheckOption(){

        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.constraint);
            }

            visitor.endVisit(this);
        }

        @Override
        public CheckOption clone() {
            CheckOption x = new CheckOption();
            if (constraint != null) {
                x.setConstraint(constraint.clone());
            }
            return x;
        }
    }

    public static class ReadOnly extends OracleSelectRestriction {

        public ReadOnly(){

        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            visitor.visit(this);

            visitor.endVisit(this);
        }

        @Override
        public ReadOnly clone() {
            ReadOnly x = new ReadOnly();
            return x;
        }
    }


}
