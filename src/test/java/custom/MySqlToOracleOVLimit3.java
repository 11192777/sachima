package custom;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlToOracleOutputVisitor;
import org.zaizai.sachima.util.CollectionUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/3 18:39
 */
public class MySqlToOracleOVLimit3 extends MySqlToOracleOutputVisitor {

    private static final int IN_ITEM_LIMIT = 3;
    private static final String IN_ITEM_FORMAT = "(1, {})";

    public MySqlToOracleOVLimit3(Appendable appender) {
        super(appender);
    }

    @Override
    public boolean visit(SQLInListExpr x) {
        if (Objects.isNull(x.getExpr())) {
            return super.visit(x);
        }
        int targetSize = CollectionUtils.sizeOf(x.getTargetList());
        if (targetSize == 1 && (x.getTargetList().get(0) instanceof SQLNullExpr
                || (x.getTargetList().get(0) instanceof SQLCharExpr && "".equals(((SQLCharExpr) x.getTargetList().get(0)).getValue())))) {
            return super.visit(this.getSQLNullBinaryOpExpr(x.getExpr(), x.isNot()));
        } else if (targetSize >= IN_ITEM_LIMIT) {
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
        }
        return super.visit(x);
    }
}
