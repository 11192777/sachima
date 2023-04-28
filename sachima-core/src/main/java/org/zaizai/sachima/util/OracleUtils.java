/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package org.zaizai.sachima.util;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.SQLBinaryOperator;
import org.zaizai.sachima.sql.ast.expr.SQLIntegerExpr;

import java.util.*;

import static org.zaizai.sachima.sql.ast.expr.SQLBinaryOperator.GreaterThan;
import static org.zaizai.sachima.sql.ast.expr.SQLBinaryOperator.LessThanOrEqual;

public class OracleUtils {

    private static Set<String> keywords;

    public static boolean isKeyword(String name) {
        if (name == null) {
            return false;
        }

        String name_lower = name.toLowerCase();

        Set<String> words = keywords;

        if (words == null) {
            words = new HashSet<>();
            Utils.loadFromFile("parser/oracle/keywords", words);
            keywords = words;
        }

        return words.contains(name_lower);
    }


    /**
     * <H2>针对MySQL IF函数的适配</H2>
     *
     * <p> example: MySQL ==> if(length(column_name) > 10, 'y', 'n') </p>
     * <pre>
     *      Oracle 不支持判断条件语句的返回值，example: select 2 > 1 from dual;
     *      适配时，使用SIGN、DECODE函数，首先对SQLBinaryOperator做差值处理
     * </pre>
     *
     * <p>MySQL IF预期与Oracle实现</p>
     * <pre>
     *     if(length(column_name) >  10, 'y', 'n')      ==>     DECODE(SIGN(LENGTH(column_name) - 10),  1, 'y', 'n')
     *     if(length(column_name) >= 10, 'y', 'n')      ==>     DECODE(SIGN(LENGTH(column_name) - 10), -1, 'n', 'y')
     *     if(length(column_name) <  10, 'y', 'n')      ==>     DECODE(SIGN(LENGTH(column_name) - 10), -1, 'y', 'n')
     *     if(length(column_name) <= 10, 'y', 'n')      ==>     DECODE(SIGN(LENGTH(column_name) - 10),  1, 'n', 'y')
     * </pre>
     *
     * @param operator 操作符 > , >= , < , <=
     * @param expr1 if true then Expr
     * @param expr2 if false then Expr
     * @return  {@link java.util.List<org.zaizai.sachima.sql.ast.SQLExpr>}  Args other than the condition.
     * @author Qingyu.Meng
     * @since 2023/3/4
     * @throws IllegalArgumentException Nonsupport operator.
     */
    public static List<SQLExpr> listMySqlIfMethodInvokeArgs(SQLBinaryOperator operator, SQLExpr expr1, SQLExpr expr2) {
        SQLIntegerExpr signResult = operator == GreaterThan || operator == LessThanOrEqual ?
                new SQLIntegerExpr(1) : new SQLIntegerExpr(-1);
        ArrayList<SQLExpr> decodeArgs = new ArrayList<>(3);
        decodeArgs.add(signResult);
        switch (operator) {
            case GreaterThanOrEqual:
            case LessThanOrEqual:
                decodeArgs.add(expr2);
                decodeArgs.add(expr1);
                break;
            case GreaterThan:
            case LessThan:
                decodeArgs.add(expr1);
                decodeArgs.add(expr2);
                break;
            default:
                throw new IllegalArgumentException("Nonsupport operator. [" + operator.getName() + "]");
        }
        return decodeArgs;
    }
}
