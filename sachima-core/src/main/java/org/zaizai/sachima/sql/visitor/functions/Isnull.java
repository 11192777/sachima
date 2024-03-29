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
package org.zaizai.sachima.sql.visitor.functions;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.SQLMethodInvokeExpr;
import org.zaizai.sachima.sql.visitor.SQLEvalVisitor;

import java.util.List;

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.*;
import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_ERROR;
import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;
import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE_NULL;

public class Isnull implements Function {

    public static final Isnull instance = new Isnull();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() == 0) {
            return EVAL_ERROR;
        }

        SQLExpr condition = arguments.get(0);
        condition.accept(visitor);
        Object itemValue = condition.getAttributes().get(EVAL_VALUE);
        if (itemValue == EVAL_VALUE_NULL) {
            return Boolean.TRUE;
        } else if (itemValue == null) {
            return null;
        } else {
            return Boolean.FALSE;
        }
    }
}
