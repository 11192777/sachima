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
import org.zaizai.sachima.sql.parser.ParserException;
import org.zaizai.sachima.sql.visitor.SQLEvalVisitor;
import org.zaizai.sachima.util.HexBin;

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Hex implements Function {

    public static final Hex instance = new Hex();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getArguments().size() != 1) {
            throw new ParserException("argument's != 1, " + x.getArguments().size());
        }

        SQLExpr param0 = x.getArguments().get(0);
        param0.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        if (param0Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        if (param0Value instanceof String) {
            byte[] bytes = ((String) param0Value).getBytes();
            String result = HexBin.encode(bytes);
            return result;
        }

        if (param0Value instanceof Number) {
            long value = ((Number) param0Value).longValue();
            String result = Long.toHexString(value).toUpperCase();
            return result;
        }

        return SQLEvalVisitor.EVAL_ERROR;
    }
}
