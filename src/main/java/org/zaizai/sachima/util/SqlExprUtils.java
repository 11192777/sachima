package org.zaizai.sachima.util;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.statement.SQLAssignItem;

import java.util.List;
import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/14 17:07
 */
public class SqlExprUtils {

    private SqlExprUtils() {
    }


    public static SQLExpr getSQLAssignItemValue(String itemName, List<SQLAssignItem> options) {
        if (Objects.isNull(itemName)) {
            return null;
        }

        long hash64 = FnvHashUtils.hashCode64(itemName);

        for (SQLAssignItem item : options) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr && ((SQLIdentifierExpr) target).hashCode64() == hash64) {
                return item.getValue();
            }
        }

        return null;
    }
}
