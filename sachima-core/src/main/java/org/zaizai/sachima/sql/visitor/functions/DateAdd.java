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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_ERROR;
import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class DateAdd implements Function {

    public static final DateAdd instance = new DateAdd();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() != 3) {
            return EVAL_ERROR;
        }

        for (SQLExpr arg : arguments) {
            arg.accept(visitor);
        }

        Object v0 = arguments.get(0).getAttributes().get(EVAL_VALUE);
        Object v1 = arguments.get(1).getAttributes().get(EVAL_VALUE);
        Object v2 = arguments.get(2).getAttributes().get(EVAL_VALUE);

        if (v0 instanceof Date
                && v1 instanceof Integer
                && v2 instanceof String) {
            Date date = (Date) v0;
            int delta = ((Integer) v1);

            if ("day".equalsIgnoreCase((String) v2)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, delta);

                return calendar.getTime();
            }

        }

        SQLExpr arg = arguments.get(0);
        arg.accept(visitor);
        Object itemValue = arg.getAttributes().get(EVAL_VALUE);
        if (itemValue == null) {
            return null;
        }

        return null;
    }
}
