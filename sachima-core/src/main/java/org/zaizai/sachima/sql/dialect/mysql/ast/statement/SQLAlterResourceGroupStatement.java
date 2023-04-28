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
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.ast.statement.SQLCreateStatement;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.HashMap;
import java.util.Map;

public class SQLAlterResourceGroupStatement
        extends SQLStatementImpl
        implements SQLCreateStatement {
    private SQLName name;
    private Map<String, SQLExpr> properties = new HashMap<>();
    private Boolean enable;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public void addProperty(String name, SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        properties.put(name, value);
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Map<String, SQLExpr> getProperties()
    {
        return properties;
    }

    public void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, name);
            for (SQLExpr value : properties.values()) {
                acceptChild(v, value);
            }
        }
        v.endVisit(this);
    }
}
