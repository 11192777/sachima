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
import org.zaizai.sachima.sql.ast.expr.SQLIntegerExpr;
import org.zaizai.sachima.sql.ast.expr.SQLMethodInvokeExpr;
import org.zaizai.sachima.sql.ast.expr.SQLValuableExpr;
import org.zaizai.sachima.sql.visitor.SQLEvalVisitor;
import org.zaizai.sachima.sql.visitor.SQLEvalVisitorUtils;

import java.util.List;

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Greatest implements Function {

    public static final Greatest instance = new Greatest();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        Object result = null;
        for (SQLExpr item : x.getArguments()) {
            item.accept(visitor);

            Object itemValue = item.getAttributes().get(EVAL_VALUE);
            if (result == null) {
                result = itemValue;
            } else {
                if (SQLEvalVisitorUtils.gt(itemValue, result)) {
                    result = itemValue;
                }
            }
        }

        return result;
    }

    public Object eval(SQLMethodInvokeExpr x) {
        List<SQLExpr> parameters = x.getArguments();

        if (!parameters.isEmpty()) {
            SQLExpr p0 = parameters.get(0);
            if (p0 instanceof SQLIntegerExpr && ((SQLIntegerExpr) p0).getNumber() instanceof Integer) {
                int val = ((SQLIntegerExpr) p0).getNumber().intValue();
                for (int i = 1; i < parameters.size(); i++) {
                    SQLExpr param = parameters.get(i);
                    if (param instanceof SQLIntegerExpr && ((SQLIntegerExpr) param).getNumber() instanceof Integer) {
                        int paramVal = ((SQLIntegerExpr) param).getNumber().intValue();
                        if (paramVal > val) {
                            val = paramVal;
                        }
                    } else {
                        return SQLEvalVisitor.EVAL_ERROR;
                    }
                }
                return val;
            }
        }

        return SQLEvalVisitor.EVAL_ERROR;
    }
}
