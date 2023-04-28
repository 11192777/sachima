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

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class MySqlIndexHintImpl extends MySqlObjectImpl implements MySqlIndexHint {

    private Option option;

    private List<SQLName>         indexList = new ArrayList<>();

    @Override
    public abstract void accept0(MySqlASTVisitor visitor);

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public List<SQLName> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<SQLName> indexList) {
        this.indexList = indexList;
    }

    public abstract MySqlIndexHintImpl clone();

    public void cloneTo(MySqlIndexHintImpl x) {
        x.option = option;
        for (SQLName name : indexList) {
            SQLName name2 = name.clone();
            name2.setParent(x);
            x.indexList.add(name2);
        }
    }
}
