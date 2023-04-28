package org.zaizai.sachima.sql.dialect.oracle.visitor;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.expr.SQLInListExpr;
import org.zaizai.sachima.sql.ast.expr.SQLPropertyExpr;
import org.zaizai.sachima.sql.ast.expr.SQLTempExpr;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;
import org.zaizai.sachima.util.CollectionUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;


/**
 * <H1>java.sql.SQLException: ORA-01795: 列表中的最大表达式数为 1000</H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/15 18:21
 */
public class InLimit1000Visitor implements SQLASTVisitor {

    protected static InLimit1000Visitor INSTANCE = null;

    public static InLimit1000Visitor getInstance() {
        return INSTANCE == null ? new InLimit1000Visitor() : INSTANCE;
    }

    @Override
    public void endVisit(SQLInListExpr x) {
        if (Objects.isNull(x.getTargetList()) || CollectionUtils.sizeOf(x.getTargetList()) <= this.getInItemLimit()) {
            return;
        }
        ArrayList<SQLExpr> formatTargetList = new ArrayList<>(x.getTargetList().size());
        for (SQLExpr sqlExpr : x.getTargetList()) {
            String formatItem = StringUtils.format(this.getItemFormat(), sqlExpr.toString());
            formatTargetList.add(new SQLTempExpr(formatItem));
        }
        x.setTargetList(formatTargetList);

        if (x.getExpr() instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) x.getExpr();
            sqlIdentifierExpr.setName(StringUtils.format(this.getItemFormat(), sqlIdentifierExpr.getName()));
        } else if (x.getExpr() instanceof SQLPropertyExpr) {
            x.setExpr(new SQLIdentifierExpr(StringUtils.format(this.getItemFormat(), ((SQLPropertyExpr) x.getExpr()).getFullName())));
        }
    }

    public int getInItemLimit() {
        return 1000;
    }

    public String getItemFormat() {
        return "(1, {})";
    }
}
