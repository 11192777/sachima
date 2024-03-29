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

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Elt implements Function {

    public static final Elt instance = new Elt();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getArguments().size() <= 1) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = x.getArguments().get(0);
        param0.accept(visitor);

        Object param0Value = param0.getAttribute(EVAL_VALUE);
        int param0IntValue;
        if (!(param0Value instanceof Number)) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        param0IntValue = ((Number) param0Value).intValue();

        if (param0IntValue >= x.getArguments().size()) {
            return SQLEvalVisitor.EVAL_VALUE_NULL;
        }

        SQLExpr item = x.getArguments().get(param0IntValue);
        item.accept(visitor);

        Object itemValue = item.getAttributes().get(EVAL_VALUE);
        return itemValue;
    }
}
