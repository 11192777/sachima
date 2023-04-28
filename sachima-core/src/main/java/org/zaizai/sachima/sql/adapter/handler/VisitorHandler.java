package org.zaizai.sachima.sql.adapter.handler;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLExprImpl;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.dialect.oracle.constant.FunctionConstant;
import org.zaizai.sachima.util.CollectionUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/18 16:57
 */
@Deprecated
public class VisitorHandler {

    private static final int IN_ITEM_LIMIT = 3;
    private static final String IN_ITEM_FORMAT = "(1, {})";
    private static final Number TRUE = 1;
    private static final Number FALSE = 0;

    //oracle limit 1000
    public static Handler<SQLInListExpr> IN_LIST_EXPR = new Handler<>(SQLInListExpr.class, (x) -> {
        if (Objects.isNull(x.getTargetList()) || CollectionUtils.sizeOf(x.getTargetList()) <= IN_ITEM_LIMIT) {
            return;
        }
        ArrayList<SQLExpr> formatTargetList = new ArrayList<>(x.getTargetList().size());
        for (SQLExpr sqlExpr : x.getTargetList()) {
            String formatItem = StringUtils.format(IN_ITEM_FORMAT, sqlExpr.toString());
            formatTargetList.add(new SQLTempExpr(formatItem));
        }
        x.setTargetList(formatTargetList);

        if (x.getExpr() instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) x.getExpr();
            sqlIdentifierExpr.setName(StringUtils.format(IN_ITEM_FORMAT, sqlIdentifierExpr.getName()));
        } else if (x.getExpr() instanceof SQLPropertyExpr) {
            x.setExpr(new SQLIdentifierExpr(StringUtils.format(IN_ITEM_FORMAT, ((SQLPropertyExpr) x.getExpr()).getFullName())));
        }
    });

    //mysql does not adapt to oracle's function
    public static Handler<SQLMethodInvokeExpr> SQL_METHOD_HANDLER = new Handler<>(SQLMethodInvokeExpr.class, (x) -> {
        String methodName = x.getMethodName();
        if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.CHAR_LENGTH)) {
            x.setMethodName(FunctionConstant.LENGTH);
        } else if (StringUtils.equalsIgnoreCase(methodName, x.getMethodName())) {
            x.setMethodName(FunctionConstant.NVL);
        }
    });

    //Boolean ture -> 1 false -> 0
    public static Handler<SQLBinaryOpExpr> BOOLEAN_VALUE_HANDLER = new Handler<>(SQLBinaryOpExpr.class, (x) -> {
        if (Objects.isNull(x.getRight()) || !(x.getRight() instanceof SQLBooleanExpr)) {
            return;
        }
        x.setRight(new SQLNumberExpr(getBooleanNumber((SQLBooleanExpr) x.getRight())));
    });


    private static Number getBooleanNumber(SQLBooleanExpr right) {
        //How do you think it's boolean field?
        //if params has boolean field map. need cache?
        return String.valueOf(right).equalsIgnoreCase(Boolean.TRUE.toString()) ? TRUE : FALSE;
    }


    public static class Handler<T extends SQLExprImpl> {
        Class<T> key;
        Consumer<T> value;

        public Handler(Class<T> key, Consumer<T> value) {
            this.key = key;
            this.value = value;
        }

        public Class<T> getKey() {
            return key;
        }

        public Consumer<T> getValue() {
            return value;
        }
    }
}
