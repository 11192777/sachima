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

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Lpad implements Function {

    public static final Lpad instance = new Lpad();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        List<SQLExpr> params = x.getArguments();
        int paramSize = params.size();
        if (paramSize != 3) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = params.get(0);
        SQLExpr param1 = params.get(1);
        SQLExpr param2 = params.get(2);

        param0.accept(visitor);
        param1.accept(visitor);
        param2.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        Object param1Value = param1.getAttributes().get(EVAL_VALUE);
        Object param2Value = param2.getAttributes().get(EVAL_VALUE);
        if (param0Value == null || param1Value == null || param2Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        String strValue0 = param0Value.toString();
        int len = ((Number) param1Value).intValue();
        String strValue1 = param2Value.toString();
        
        String result = strValue0;
        if (result.length() > len) {
            return result.substring(0, len);
        }
        
        while (result.length() < len) {
            result = strValue1 + result;
        }

        return result;
    }
}
