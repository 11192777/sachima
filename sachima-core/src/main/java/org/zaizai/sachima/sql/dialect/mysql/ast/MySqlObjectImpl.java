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
package org.zaizai.sachima.sql.dialect.mysql.ast;

import org.zaizai.sachima.sql.adapter.visitor.AdaptVisitor;
import org.zaizai.sachima.sql.ast.SQLObjectImpl;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public abstract class MySqlObjectImpl extends SQLObjectImpl implements MySqlObject {

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) v);
        } else if (v instanceof AdaptVisitor) {
            ((AdaptVisitor) v).visit(this);
        }
    }

}