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
import org.zaizai.sachima.sql.visitor.SQLEvalVisitorUtils;

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Right implements Function {

    public static final Right instance = new Right();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getArguments().size() != 2) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = x.getArguments().get(0);
        SQLExpr param1 = x.getArguments().get(1);
        param0.accept(visitor);
        param1.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        Object param1Value = param1.getAttributes().get(EVAL_VALUE);
        if (param0Value == null || param1Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        String strValue = param0Value.toString();
        int intValue = SQLEvalVisitorUtils.castToInteger(param1Value);

        int start = strValue.length() - intValue;
        if (start < 0) {
            start = 0;
        }
        String result = strValue.substring(start, strValue.length());
        return result;
    }
}
