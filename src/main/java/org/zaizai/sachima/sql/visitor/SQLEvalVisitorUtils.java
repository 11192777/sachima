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
package org.zaizai.sachima.sql.visitor;

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.exception.FastsqlException;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.ast.statement.SQLExprTableSource;
import org.zaizai.sachima.sql.ast.statement.SQLSelect;
import org.zaizai.sachima.sql.ast.statement.SQLSelectItem;
import org.zaizai.sachima.sql.ast.statement.SQLSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlEvalVisitorImpl;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleEvalVisitor;
import org.zaizai.sachima.sql.visitor.functions.*;
import org.zaizai.sachima.util.HexBin;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.zaizai.sachima.sql.visitor.SQLEvalVisitor.*;

public class SQLEvalVisitorUtils {

    private static final Map<String, Function> functions = new HashMap<>();

    static {
        registerBaseFunctions();
    }

    public static Object eval(DbType dbType, SQLObject sqlObject, Object... parameters) {
        Object value = eval(dbType, sqlObject, Arrays.asList(parameters));

        if (value == EVAL_VALUE_NULL) {
            value = null;
        }

        return value;
    }

    public static Object getValue(SQLObject sqlObject) {
        if (sqlObject instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) sqlObject).getNumber();
        }

        return sqlObject.getAttribute(EVAL_VALUE);
    }

    public static Object eval(DbType dbType, SQLObject sqlObject, List<Object> parameters) {
        return eval(dbType, sqlObject, parameters, true);
    }

    public static Object eval(DbType dbType, SQLObject sqlObject, List<Object> parameters, boolean throwError) {
        SQLEvalVisitor visitor = createEvalVisitor(dbType);
        visitor.setParameters(parameters);

        Object value;
        if (sqlObject instanceof SQLValuableExpr) {
            value = ((SQLValuableExpr) sqlObject).getValue();
        } else {
            sqlObject.accept(visitor);

            value = getValue(sqlObject);

            if (value == null && throwError && !sqlObject.containsAttribute(EVAL_VALUE)) {
                throw new FastsqlException("eval error : " + SQLUtils.toSQLString(sqlObject, dbType));
            }
        }

        return value;
    }

    public static SQLEvalVisitor createEvalVisitor(DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case mysql:
                return new MySqlEvalVisitorImpl();
            case oracle:
                return new OracleEvalVisitor();
            default:
                return new SQLEvalVisitorImpl();
        }
    }

    static void registerBaseFunctions() {
        functions.put("now", Now.instance);
        functions.put("concat", Concat.instance);
        functions.put("concat_ws", Concat.instance);
        functions.put("ascii", Ascii.instance);
        functions.put("bin", Bin.instance);
        functions.put("bit_length", BitLength.instance);
        functions.put("insert", Insert.instance);
        functions.put("instr", Instr.instance);
        functions.put("char", Char.instance);
        functions.put("elt", Elt.instance);
        functions.put("left", Left.instance);
        functions.put("locate", Locate.instance);
        functions.put("lpad", Lpad.instance);
        functions.put("ltrim", Ltrim.instance);
        functions.put("mid", Substring.instance);
        functions.put("substr", Substring.instance);
        functions.put("substring", Substring.instance);
        functions.put("right", Right.instance);
        functions.put("reverse", Reverse.instance);
        functions.put("len", Length.instance);
        functions.put("length", Length.instance);
        functions.put("char_length", Length.instance);
        functions.put("character_length", Length.instance);
        functions.put("trim", Trim.instance);
        functions.put("ucase", Ucase.instance);
        functions.put("upper", Ucase.instance);
        functions.put("lcase", Lcase.instance);
        functions.put("lower", Lcase.instance);
        functions.put("hex", Hex.instance);
        functions.put("unhex", Unhex.instance);
        functions.put("greatest", Greatest.instance);
        functions.put("least", Least.instance);
        functions.put("isnull", Isnull.instance);
        functions.put("if", If.instance);
        functions.put("to_date", ToDate.instance);
        functions.put("to_char", ToChar.instance);
        functions.put("dateadd", DateAdd.instance);

        functions.put("md5", OneParamFunctions.instance);
        functions.put("bit_count", OneParamFunctions.instance);
        functions.put("soundex", OneParamFunctions.instance);
        functions.put("space", OneParamFunctions.instance);
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName().toLowerCase();

        Function function = visitor.getFunction(methodName);

        if (function == null) {
            function = functions.get(methodName);
        }

        if (function != null) {
            Object result = function.eval(visitor, x);

            if (result != SQLEvalVisitor.EVAL_ERROR && result != null) {
                x.putAttribute(EVAL_VALUE, result);
            }
            return false;
        }

        if ("mod".equals(methodName)) {
            if (x.getArguments().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            SQLExpr param1 = x.getArguments().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            long intValue0 = castToLong(param0Value);
            long intValue1 = castToLong(param1Value);

            assert intValue1 != 0;
            long result = intValue0 % intValue1;
            if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE) {
                int intResult = (int) result;
                x.putAttribute(EVAL_VALUE, intResult);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("abs".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            Object result;
            if (paramValue instanceof Integer) {
                result = Math.abs((Integer) paramValue);
            } else if (paramValue instanceof Long) {
                result = Math.abs((Long) paramValue);
            } else {
                result = castToDecimal(paramValue).abs();
            }

            x.putAttribute(EVAL_VALUE, result);
        } else if ("acos".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.acos(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("asin".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.asin(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("atan".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.atan(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("atan2".equals(methodName)) {
            if (x.getArguments().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            SQLExpr param1 = x.getArguments().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            double doubleValue0 = castToDouble(param0Value);
            double doubleValue1 = castToDouble(param1Value);
            double result = Math.atan2(doubleValue0, doubleValue1);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("ceil".equals(methodName) || "ceiling".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.ceil(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("cos".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.cos(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("sin".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.sin(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("log".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.log(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("log10".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.log10(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("tan".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.tan(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("sqrt".equals(methodName)) {
            if (x.getArguments().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.sqrt(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("power".equals(methodName) || "pow".equals(methodName)) {
            if (x.getArguments().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getArguments().get(0);
            SQLExpr param1 = x.getArguments().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            double doubleValue0 = castToDouble(param0Value);
            double doubleValue1 = castToDouble(param1Value);
            double result = Math.pow(doubleValue0, doubleValue1);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("pi".equals(methodName)) {
            x.putAttribute(EVAL_VALUE, Math.PI);
        } else if ("rand".equals(methodName)) {
            x.putAttribute(EVAL_VALUE, Math.random());
        } else if ("chr".equals(methodName) && x.getArguments().size() == 1) {
            SQLExpr first = x.getArguments().get(0);
            Object firstResult = getValue(first);
            if (firstResult instanceof Number) {
                int intValue = ((Number) firstResult).intValue();
                char ch = (char) intValue;
                x.putAttribute(EVAL_VALUE, Character.toString(ch));
            }
        } else if ("current_user".equals(methodName)) {
            x.putAttribute(EVAL_VALUE, "CURRENT_USER");
        }
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLCharExpr x) {
        x.putAttribute(EVAL_VALUE, x.getText());
        return true;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLHexExpr x) {
        String hex = x.getHex();
        byte[] bytes = HexBin.decode(hex);
        if (bytes == null) {
            x.putAttribute(EVAL_VALUE, EVAL_ERROR);
        } else {
            String val = new String(bytes);
            x.putAttribute(EVAL_VALUE, val);
        }
        return true;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLBinaryExpr x) {
        String text = x.getText();

        long[] words = new long[text.length() / 64 + 1];
        for (int i = text.length() - 1; i >= 0; --i) {
            char ch = text.charAt(i);
            if (ch == '1') {
                int wordIndex = i >> 6;
                words[wordIndex] |= (1L << (text.length() - 1 - i));
            }
        }

        Object val;

        if (words.length == 1) {
            val = words[0];
        } else {
            byte[] bytes = new byte[words.length * 8];

            for (int i = 0; i < words.length; ++i) {
                Utils.putLong(bytes, (words.length - 1 - i) * 8, words[i]);
            }

            val = new BigInteger(bytes);
        }

        x.putAttribute(EVAL_VALUE, val);

        return false;
    }

    public static SQLExpr unwrap(SQLExpr expr) {
        if (expr == null) {
            return null;
        }

        if (expr instanceof SQLQueryExpr) {
            SQLSelect select = ((SQLQueryExpr) expr).getSubQuery();
            if (select == null) {
                return null;
            }
            if (select.getQuery() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
                if (queryBlock.getFrom() == null && queryBlock.getSelectList().size() == 1) {
                    return queryBlock.getSelectList().get(0).getExpr();
                }
            }
        }

        return expr;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLBetweenExpr x) {
        SQLExpr testExpr = Assert.notNull(unwrap(x.getTestExpr()));
        testExpr.accept(visitor);

        if (!testExpr.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object value = testExpr.getAttribute(EVAL_VALUE);

        SQLExpr beginExpr = Assert.notNull(unwrap(x.getBeginExpr()));
        beginExpr.accept(visitor);
        if (!beginExpr.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object begin = beginExpr.getAttribute(EVAL_VALUE);

        if (lt(value, begin)) {
            x.getAttributes().put(EVAL_VALUE, x.isNot());
            return false;
        }

        SQLExpr endExpr = Assert.notNull(unwrap(x.getEndExpr()));
        endExpr.accept(visitor);
        if (!endExpr.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object end = endExpr.getAttribute(EVAL_VALUE);

        if (gt(value, end)) {
            x.getAttributes().put(EVAL_VALUE, x.isNot());
            return false;
        }

        x.getAttributes().put(EVAL_VALUE, !x.isNot());
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLNullExpr x) {
        x.getAttributes().put(EVAL_VALUE, EVAL_VALUE_NULL);
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLCaseExpr x) {
        Object value;
        if (x.getValueExpr() != null) {
            x.getValueExpr().accept(visitor);

            if (!x.getValueExpr().getAttributes().containsKey(EVAL_VALUE)) {
                return false;
            }

            value = x.getValueExpr().getAttribute(EVAL_VALUE);
        } else {
            value = null;
        }

        for (SQLCaseExpr.Item item : x.getItems()) {
            item.getConditionExpr().accept(visitor);

            if (!item.getConditionExpr().getAttributes().containsKey(EVAL_VALUE)) {
                return false;
            }

            Object conditionValue = item.getConditionExpr().getAttribute(EVAL_VALUE);

            if ((x.getValueExpr() != null && eq(value, conditionValue))
                    || (x.getValueExpr() == null && conditionValue == Boolean.TRUE)) {
                item.getValueExpr().accept(visitor);

                if (item.getValueExpr().getAttributes().containsKey(EVAL_VALUE)) {
                    x.getAttributes().put(EVAL_VALUE, item.getValueExpr().getAttribute(EVAL_VALUE));
                }

                return false;
            }
        }

        if (x.getElseExpr() != null) {
            x.getElseExpr().accept(visitor);

            if (x.getElseExpr().getAttributes().containsKey(EVAL_VALUE)) {
                x.getAttributes().put(EVAL_VALUE, x.getElseExpr().getAttribute(EVAL_VALUE));
            }
        }

        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLInListExpr x) {
        SQLExpr valueExpr = x.getExpr();
        valueExpr.accept(visitor);
        if (!valueExpr.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }
        Object value = valueExpr.getAttribute(EVAL_VALUE);

        for (SQLExpr item : x.getTargetList()) {
            item.accept(visitor);
            if (!item.getAttributes().containsKey(EVAL_VALUE)) {
                return false;
            }
            Object itemValue = item.getAttribute(EVAL_VALUE);
            if (eq(value, itemValue)) {
                x.getAttributes().put(EVAL_VALUE, !x.isNot());
                return false;
            }
        }

        x.getAttributes().put(EVAL_VALUE, x.isNot());
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLQueryExpr x) {

        if (x.getSubQuery().getQuery() instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) x.getSubQuery().getQuery();

            boolean nullFrom = false;
            if (queryBlock.getFrom() == null) {
                nullFrom = true;
            } else if (queryBlock.getFrom() instanceof SQLExprTableSource) {
                SQLExpr expr = ((SQLExprTableSource) queryBlock.getFrom()).getExpr();
                if (expr instanceof SQLIdentifierExpr && "dual".equalsIgnoreCase(((SQLIdentifierExpr) expr).getName())) {
                    nullFrom = true;
                }
            }

            if (nullFrom) {
                List<Object> row = new ArrayList<>(queryBlock.getSelectList().size());
                for (int i = 0; i < queryBlock.getSelectList().size(); ++i) {
                    SQLSelectItem item = queryBlock.getSelectList().get(i);
                    item.getExpr().accept(visitor);
                    Object cell = item.getExpr().getAttribute(EVAL_VALUE);
                    row.add(cell);
                }
                List<List<Object>> rows = new ArrayList<>(1);
                rows.add(row);

                Object result = rows;
                queryBlock.putAttribute(EVAL_VALUE, result);
                x.getSubQuery().putAttribute(EVAL_VALUE, result);
                x.putAttribute(EVAL_VALUE, result);

                return false;
            }
        }

        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLUnaryExpr x) {
        x.getExpr().accept(visitor);

        Object val = x.getExpr().getAttribute(EVAL_VALUE);
        if (val == EVAL_ERROR) {
            x.putAttribute(EVAL_VALUE, EVAL_ERROR);
            return false;
        }

        if (val == null) {
            x.putAttribute(EVAL_VALUE, EVAL_VALUE_NULL);
            return false;
        }

        switch (x.getOperator()) {
            case BINARY:
            case RAW:
            case Plus:
                x.putAttribute(EVAL_VALUE, val);
                break;
            case NOT:
            case Not: {
                Boolean booleanVal = castToBoolean(val);
                if (booleanVal != null) {
                    x.putAttribute(EVAL_VALUE, !booleanVal);
                }
                break;
            }
            case Negative:
                x.putAttribute(EVAL_VALUE, multi(val, -1));
                break;
            case Compl:
                x.putAttribute(EVAL_VALUE, ~castToInteger(val));
                break;
            default:
                break;
        }

        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLBinaryOpExpr x) {
        System.out.println("Wall visit");
        return false;
    }

    @SuppressWarnings("rawtypes")
    private static Object processValue(Object value) {
        if (value instanceof List) {
            List list = (List) value;
            if (list.size() == 1) {
                return processValue(list.get(0));
            }
        } else if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        return value;
    }

    private static boolean isAlwayTrueLikePattern(SQLExpr x) {
        if (x instanceof SQLCharExpr) {
            String text = ((SQLCharExpr) x).getText();

            if (text.length() > 0) {
                for (char ch : text.toCharArray()) {
                    if (ch != '%') {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLNumericLiteralExpr x) {
        x.putAttribute(EVAL_VALUE, x.getNumber());
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLVariantRefExpr x) {
        if (!"?".equals(x.getName())) {
            return false;
        }

        Map<String, Object> attributes = x.getAttributes();

        int varIndex = x.getIndex();

        List<Object> parameters = visitor.getParameters();
        if (varIndex != -1
                && parameters != null
                && parameters.size() > varIndex) {
            boolean containsValue = attributes.containsKey(EVAL_VALUE);
            if (!containsValue) {
                Object value = parameters.get(varIndex);
                if (value == null) {
                    value = EVAL_VALUE_NULL;
                }
                attributes.put(EVAL_VALUE, value);
            }
        }

        return false;
    }

    public static Boolean castToBoolean(Object val) {
        if (val == null || val == EVAL_VALUE_NULL) {
            return null;
        }

        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        if (val instanceof Number) {
            return ((Number) val).intValue() > 0;
        }

        if (val instanceof String) {
            if ("1".equals(val) || "true".equalsIgnoreCase((String) val)) {
                return true;
            }

            return false;
        }

        throw new IllegalArgumentException(val.getClass() + " not supported.");
    }

    public static String castToString(Object val) {
        if (val == null) {
            return null;
        }

        return val.toString();
    }

    public static Byte castToByte(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Byte) {
            return (Byte) val;
        }

        if (val instanceof String) {
            return Byte.parseByte((String) val);
        }

        return ((Number) val).byteValue();
    }

    public static Short castToShort(Object val) {
        if (val == null || val == EVAL_VALUE_NULL) {
            return null;
        }

        if (val instanceof Short) {
            return (Short) val;
        }

        if (val instanceof String) {
            return Short.parseShort((String) val);
        }

        return ((Number) val).shortValue();
    }

    @SuppressWarnings("rawtypes")
    public static Integer castToInteger(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Integer) {
            return (Integer) val;
        }

        if (val instanceof String) {
            return Integer.parseInt((String) val);
        }

        if (val instanceof List) {
            List list = (List) val;
            if (list.size() == 1) {
                return castToInteger(list.get(0));
            }
        }

        if (val instanceof Boolean) {
            if ((Boolean) val) {
                return 1;
            } else {
                return 0;
            }
        }

        if (val instanceof Number) {
            return ((Number) val).intValue();
        }

        throw new FastsqlException("cast error");
    }

    @SuppressWarnings("rawtypes")
    public static Long castToLong(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Long) {
            return (Long) val;
        }

        if (val instanceof String) {
            return Long.parseLong((String) val);
        }

        if (val instanceof List) {
            List list = (List) val;
            if (list.size() == 1) {
                return castToLong(list.get(0));
            }
        }

        if (val instanceof Boolean) {
            if ((Boolean) val) {
                return 1l;
            } else {
                return 0l;
            }
        }

        return ((Number) val).longValue();
    }

    public static Float castToFloat(Object val) {
        if (val == null || val == EVAL_VALUE_NULL) {
            return null;
        }

        if (val instanceof Float) {
            return (Float) val;
        }

        return ((Number) val).floatValue();
    }

    public static Double castToDouble(Object val) {
        if (val == null || val == EVAL_VALUE_NULL) {
            return null;
        }

        if (val instanceof Double) {
            return (Double) val;
        }

        return ((Number) val).doubleValue();
    }

    public static BigInteger castToBigInteger(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof BigInteger) {
            return (BigInteger) val;
        }

        if (val instanceof String) {
            return new BigInteger((String) val);
        }

        return BigInteger.valueOf(((Number) val).longValue());
    }

    public static Number castToNumber(String val) {
        if (val == null) {
            return null;
        }

        try {
            return Byte.parseByte(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Short.parseShort(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Long.parseLong(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return new BigInteger(val);
        } catch (NumberFormatException ignored) {
        }

        try {
            return new BigDecimal(val);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public static Date castToDate(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            return (Date) val;
        }

        if (val instanceof Number) {
            return new Date(((Number) val).longValue());
        }

        if (val instanceof String) {
            return castToDate((String) val);
        }

        throw new FastsqlException("can cast to date");
    }

    public static Date castToDate(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }

        String format;

        if (text.length() == "yyyy-MM-dd".length()) {
            format = "yyyy-MM-dd";
        } else {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        try {
            return new SimpleDateFormat(format).parse(text);
        } catch (ParseException e) {
            throw new FastsqlException("rowFormat : " + format + ", value : " + text, e);
        }
    }

    public static BigDecimal castToDecimal(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof BigDecimal) {
            return (BigDecimal) val;
        }

        if (val instanceof String) {
            return new BigDecimal((String) val);
        }

        if (val instanceof Float) {
            return BigDecimal.valueOf((Float) val);
        }

        if (val instanceof Double) {
            return BigDecimal.valueOf((Double) val);
        }

        return BigDecimal.valueOf(((Number) val).longValue());
    }

    public static Object rightShift(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) >> castToLong(b);
        }

        return castToInteger(a) >> castToInteger(b);
    }

    public static Object bitAnd(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a == EVAL_VALUE_NULL || b == EVAL_VALUE_NULL) {
            return null;
        }

        if (a instanceof String) {
            a = castToNumber((String) a);
        }

        if (b instanceof String) {
            b = castToNumber((String) b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) & castToLong(b);
        }

        return castToInteger(a) & castToInteger(b);
    }

    public static Object bitOr(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a == EVAL_VALUE_NULL || b == EVAL_VALUE_NULL) {
            return null;
        }

        if (a instanceof String) {
            a = castToNumber((String) a);
        }

        if (b instanceof String) {
            b = castToNumber((String) b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) | castToLong(b);
        }

        return castToInteger(a) | castToInteger(b);
    }

    public static Object div(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a == EVAL_VALUE_NULL || b == EVAL_VALUE_NULL) {
            return null;
        }

        if (a instanceof String) {
            a = castToNumber((String) a);
        }

        if (b instanceof String) {
            b = castToNumber((String) b);
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            BigDecimal decimalA = Assert.notNull(castToDecimal(a));
            BigDecimal decimalB = Assert.notNull(castToDecimal(b));
            if (decimalB.scale() < decimalA.scale()) {
                decimalB = decimalB.setScale(decimalA.scale());
            }
            try {
                return decimalA.divide(decimalB);
            } catch (ArithmeticException ex) {
                return decimalA.divide(decimalB, BigDecimal.ROUND_HALF_UP);
            }
        }

        if (a instanceof Double || b instanceof Double) {
            Double doubleA = castToDouble(a);
            Double doubleB = castToDouble(b);
            if (doubleA == null || doubleB == null) {
                return null;
            }
            return doubleA / doubleB;
        }

        if (a instanceof Float || b instanceof Float) {
            Float floatA = castToFloat(a);
            Float floatB = castToFloat(b);
            if (floatA == null || floatB == null) {
                return null;
            }
            return floatA / floatB;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).divide(castToBigInteger(b));
        }

        if (a instanceof Long || b instanceof Long) {
            Long longA = castToLong(a);
            Long longB = castToLong(b);
            if (longB == 0) {
                if (longA > 0) {
                    return Double.POSITIVE_INFINITY;
                } else if (longA < 0) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.NaN;
                }
            }
            return longA / longB;
        }

        if (a instanceof Integer || b instanceof Integer) {
            Integer intA = castToInteger(a);
            Integer intB = castToInteger(b);
            if (intB == 0) {
                if (intA > 0) {
                    return Double.POSITIVE_INFINITY;
                } else if (intA < 0) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.NaN;
                }
            }
            return intA / intB;
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) / castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) / castToByte(b);
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static boolean gt(Object a, Object b) {
        if (a == null || a == EVAL_VALUE_NULL) {
            return false;
        }

        if (b == null || b == EVAL_VALUE_NULL) {
            return true;
        }

        if (a instanceof String || b instanceof String) {
            return castToString(a).compareTo(castToString(b)) > 0;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return Assert.notNull(castToDecimal(a)).compareTo(castToDecimal(b)) > 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).compareTo(castToBigInteger(b)) > 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) > castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) > castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) > castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) > castToByte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = castToDate(a);
            Date d2 = castToDate(b);

            if (d1 == d2) {
                return false;
            }

            if (d1 == null) {
                return false;
            }

            if (d2 == null) {
                return true;
            }

            return d1.compareTo(d2) > 0;
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static boolean gteq(Object a, Object b) {
        if (eq(a, b)) {
            return true;
        }

        return gt(a, b);
    }

    public static boolean lt(Object a, Object b) {
        if (a == null) {
            return true;
        }

        if (b == null) {
            return false;
        }

        if (a instanceof String || b instanceof String) {
            return (castToString(a)).compareTo(castToString(b)) < 0;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return Assert.notNull(castToDecimal(a)).compareTo(castToDecimal(b)) < 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).compareTo(castToBigInteger(b)) < 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) < castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            Integer intA = castToInteger(a);
            Integer intB = castToInteger(b);
            return intA < intB;
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) < castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) < castToByte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = castToDate(a);
            Date d2 = castToDate(b);

            if (d1 == d2) {
                return false;
            }

            if (d1 == null) {
                return true;
            }

            if (d2 == null) {
                return false;
            }

            return d1.compareTo(d2) < 0;
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static boolean lteq(Object a, Object b) {
        if (eq(a, b)) {
            return true;
        }

        return lt(a, b);
    }

    public static boolean eq(Object a, Object b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a == EVAL_VALUE_NULL || b == EVAL_VALUE_NULL) {
            return false;
        }

        if (a.equals(b)) {
            return true;
        }

        if (a instanceof String || b instanceof String) {
            return castToString(a).equals(castToString(b));
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return Assert.notNull(castToDecimal(a)).compareTo(castToDecimal(b)) == 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).compareTo(castToBigInteger(b)) == 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a).equals(castToLong(b));
        }

        if (a instanceof Integer || b instanceof Integer) {
            Integer inta = castToInteger(a);
            Integer intb = castToInteger(b);
            if (inta == null || intb == null) {
                return false;
            }
            return inta.equals(intb);
        }

        if (a instanceof Short || b instanceof Short) {
            return Assert.notNull(castToShort(a)).equals(castToShort(b));
        }

        if (a instanceof Boolean || b instanceof Boolean) {
            return Assert.notNull(castToBoolean(a)).equals(castToBoolean(b));
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a).equals(castToByte(b));
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = castToDate(a);
            Date d2 = castToDate(b);

            if (d1 == d2) {
                return true;
            }

            if (d1 == null || d2 == null) {
                return false;
            }

            return d1.equals(d2);
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static Object add(Object a, Object b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        if (a == EVAL_VALUE_NULL || b == EVAL_VALUE_NULL) {
            return EVAL_VALUE_NULL;
        }

        if (a instanceof String && !(b instanceof String)) {
            a = castToNumber((String) a);
        }

        if (b instanceof String && !(a instanceof String)) {
            b = castToNumber((String) b);
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return Assert.notNull(castToDecimal(a)).add(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).add(castToBigInteger(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return castToDouble(a) + castToDouble(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return castToFloat(a) + castToFloat(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) + castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) + castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) + castToShort(b);
        }

        if (a instanceof Boolean || b instanceof Boolean) {
            int aI = 0, bI = 0;
            if (Boolean.TRUE.equals(castToBoolean(a))) aI = 1;
            if (Boolean.TRUE.equals(castToBoolean(b))) bI = 1;
            return aI + bI;
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) + castToByte(b);
        }

        if (a instanceof String) {
            return castToString(a) + castToString(b);
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static Object sub(Object a, Object b) {
        if (a == null) {
            return null;
        }

        if (b == null) {
            return a;
        }

        if (a == EVAL_VALUE_NULL || b == EVAL_VALUE_NULL) {
            return EVAL_VALUE_NULL;
        }

        if (a instanceof Date || b instanceof Date) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        if (a instanceof String) {
            a = castToNumber((String) a);
        }

        if (b instanceof String) {
            b = castToNumber((String) b);
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return Assert.notNull(castToDecimal(a)).subtract(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).subtract(castToBigInteger(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return castToDouble(a) - castToDouble(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return castToFloat(a) - castToFloat(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) - castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) - castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) - castToShort(b);
        }

        if (a instanceof Boolean || b instanceof Boolean) {
            int aI = 0, bI = 0;
            if (Boolean.TRUE.equals(castToBoolean(a))) aI = 1;
            if (Boolean.TRUE.equals(castToBoolean(b))) bI = 1;
            return aI - bI;
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) - castToByte(b);
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static Object multi(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a instanceof String) {
            a = castToNumber((String) a);
        }

        if (b instanceof String) {
            b = castToNumber((String) b);
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return Assert.notNull(castToDecimal(a)).multiply(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return Assert.notNull(castToBigInteger(a)).multiply(castToBigInteger(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return castToDouble(a) * castToDouble(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return castToFloat(a) * castToFloat(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) * castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) * castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            Short shortA = castToShort(a);
            Short shortB = castToShort(b);

            if (shortA == null || shortB == null) {
                return null;
            }

            return shortA * shortB;
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) * castToByte(b);
        }

        throw new IllegalArgumentException(a.getClass() + " and " + b.getClass() + " not supported.");
    }

    public static boolean like(String input, String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }

        StringBuilder regexprBuilder = new StringBuilder(pattern.length() + 4);

        final int STAT_NOTSET = 0;
        final int STAT_RANGE = 1;
        final int STAT_LITERAL = 2;

        int stat = STAT_NOTSET;

        int blockStart = -1;
        for (int i = 0; i < pattern.length(); ++i) {
            char ch = pattern.charAt(i);

            if (stat == STAT_LITERAL //
                    && (ch == '%' || ch == '_' || ch == '[')) {
                String block = pattern.substring(blockStart, i);
                regexprBuilder.append("\\Q");
                regexprBuilder.append(block);
                regexprBuilder.append("\\E");
                blockStart = -1;
                stat = STAT_NOTSET;
            }

            if (ch == '%') {
                regexprBuilder.append(".*");
            } else if (ch == '_') {
                regexprBuilder.append('.');
            } else if (ch == '[') {
                if (stat == STAT_RANGE) {
                    throw new IllegalArgumentException("illegal pattern : " + pattern);
                }
                stat = STAT_RANGE;
                blockStart = i;
            } else if (ch == ']') {
                if (stat != STAT_RANGE) {
                    throw new IllegalArgumentException("illegal pattern : " + pattern);
                }
                String block = pattern.substring(blockStart, i + 1);
                regexprBuilder.append(block);

                blockStart = -1;
            } else {
                if (stat == STAT_NOTSET) {
                    stat = STAT_LITERAL;
                    blockStart = i;
                }

                if (stat == STAT_LITERAL && i == pattern.length() - 1) {
                    String block = pattern.substring(blockStart, i + 1);
                    regexprBuilder.append("\\Q");
                    regexprBuilder.append(block);
                    regexprBuilder.append("\\E");
                }
            }
        }
        if ("%".equals(pattern) || "%%".equals(pattern)) {
            return true;
        }

        String regexpr = regexprBuilder.toString();
        return Pattern.matches(regexpr, input);
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLIdentifierExpr x) {
        x.putAttribute(EVAL_EXPR, x);
        return false;
    }
}
