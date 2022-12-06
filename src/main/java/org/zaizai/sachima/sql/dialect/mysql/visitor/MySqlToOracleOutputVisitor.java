package org.zaizai.sachima.sql.dialect.mysql.visitor;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.ast.statement.SQLExprTableSource;
import org.zaizai.sachima.sql.ast.statement.SQLSelectItem;
import org.zaizai.sachima.sql.dialect.oracle.constant.FunctionConstant;
import org.zaizai.sachima.sql.dialect.oracle.parser.OracleLexer;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleOutputVisitor;
import org.zaizai.sachima.sql.parser.Token;
import org.zaizai.sachima.util.CollectionUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <H1>Simple MySQL to Oracle</H1>
 * <P>Custom {@link org.zaizai.sachima.sql.dialect.mysql.visitor.impl.VisitorHandler}</P>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/18 15:18
 */
public class MySqlToOracleOutputVisitor extends OracleOutputVisitor {

    private static final int IN_ITEM_LIMIT = 1000;
    private static final String IN_ITEM_FORMAT = "(1, {})";
    private static final Number TRUE = 1;
    private static final Number FALSE = 0;

    public MySqlToOracleOutputVisitor(Appendable appender) {
        super(appender);
    }

    public MySqlToOracleOutputVisitor(Appendable appender, boolean printPostSemi) {
        super(appender, printPostSemi);
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        if (Objects.nonNull(x.getOperator()) && FunctionConstant.BINARY.equals(x.getOperator().getName())) {
            x.setOperator(null);
        }
        return true;
    }

    /**
     * <ol>
     *     <li>u.`name` -> u."NAME"</li>
     * </ol>
     */
    @Override
    public boolean visit(SQLPropertyExpr x) {
        x.setName(identifierTransferredMeaning(x.getName()));
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        if (x.getExpr() instanceof SQLIdentifierExpr) {
            this.identifierTransferredMeaning((SQLIdentifierExpr) x.getExpr());
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        if (x.getExpr() instanceof SQLIdentifierExpr) {
            this.identifierTransferredMeaning((SQLIdentifierExpr) x.getExpr());
        }
        if (Objects.nonNull(x.getAlias())) {
            x.setAlias(this.keywordTransferredMeaning(x.getAliasIgnoreIdentifiers()));
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        this.identifierTransferredMeaning(x);
        return super.visit(x);
    }

    private void identifierTransferredMeaning(SQLIdentifierExpr x) {
        x.setName(this.identifierTransferredMeaning(x.getName()));
    }

    /**
     * <ol>
     *     <li>u.`name` -> u.name</li>
     * </ol>
     */
    private String identifierTransferredMeaning(String name) {
        if (name.contains("`")) {
            return name.replace("`", "");
        }
        return this.keywordTransferredMeaning(name);
    }

    /**
     * <P>关键字转义 number -> "NUMBER"</P>
     */
    private String keywordTransferredMeaning(String name) {
        Token keyword = OracleLexer.DEFAULT_ORACLE_KEYWORDS.getKeyword(name);
        return Objects.nonNull(keyword) ? StringUtils.format("\"{}\"", keyword.name) : name;
    }

    /**
     * MySQL to Oracle. function adapter.
     * <ol>
     *     <li>char_length -> length
     *     select * from ea_form WHERE char_length(name) = 1; ===> select * from ea_form WHERE length(name) = 1;
     *     </li>
     *     <li>CHARACTER_LENGTH -> length
     *     select * from ea_form WHERE CHARACTER_LENGTH(name) = 1; ===> select * from ea_form WHERE length(name) = 1;
     *     </li>
     *     <li>CONCAT(?, ?, ?) -> (?||?||?)
     *     select CONCAT(id, '666', name) FROM user; ===> select (id||'666',name) FROM user;
     *     </li> or CONCAT(?, CONCAT(?, CONCAT(field, ?)))
     *     <li>FIELD -> DECODE
     *     select * from user where id in (1, 2, 3) order by field(id, 3, 1, 2) ===> select * from "USER" where "ID" in (1, 2, 3) order by DECODE("ID", 3, 1, 2)
     *     </li>
     *     <li>NOW() -> SYSDATE
     *     insert into user (created_date) values(now()) ===> insert into user (created_date) values(SYSDATE)
     *     </li>
     *     <li>DATE_FORMAT() -> TO_CHAR
     *     select date_format(created_date, '%Y-%m-%d %H-%i-%s') from ea_form; ===> select TO_CHAR(created_date, 'yyyy-mm-dd hh24:mi:ss') from ea_form;
     *     </li>
     * </ol>
     */
    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        if (StringUtils.equalsAnyIgnoreCase(methodName, FunctionConstant.CHAR_LENGTH, FunctionConstant.CHARACTER_LENGTH)) {
            x.setMethodName(FunctionConstant.LENGTH);
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.CONCAT) && CollectionUtils.isNotEmpty(x.getArguments())) {
            //emm... Modify source code. add argument that separator field. Source yyds
            x.setMethodName("");
            x.setArgumentsSeparator(FunctionConstant.ORACLE_CONCAT);
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.IFNULL)) {
            x.setMethodName(FunctionConstant.NVL);
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.FIELD)) {
            x.setMethodName(FunctionConstant.DECODE);
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.NOW)) {
            x.setMethodName(FunctionConstant.SYSDATE);
            x.setHoldEmptyArgumentBracket(false);
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.DATE_FORMAT)) {
            x.setMethodName(FunctionConstant.TO_CHAR);
            if (Objects.isNull(x.getArguments()) || x.getArguments().size() != 2) {
                return super.visit(x);
            }
            // fun date_format of MySQL must be two args.
            if (x.getArguments().get(1) instanceof SQLCharExpr) {
                SQLCharExpr sqlCharExpr = (SQLCharExpr) x.getArguments().get(1);
                sqlCharExpr.setText(sqlCharExpr.getText().replace("%Y", "yyyy").replace("%m", "mm").replace("%d", "dd").replace("%H", "hh24").replace("%i", "mi").replace("%s", "ss"));
            }
        }
        return super.visit(x);
    }

    /**
     * Sql limit 1000 adapt.
     *
     * <p>
     * if sql in items size > 1000 {@link MySqlToOracleOutputVisitor#IN_ITEM_LIMIT}
     * then SELECT * FROM user WHERE id IN (1, 2, ...., 1001) ==> SELECT * FROM user WHERE (1, id) IN ((1, 1), (1, 2), ...., (1, 1001))
     * </p>
     *
     * <p>Special</p>
     * <pre>
     *     if field IN (NULL) then field IS NULL
     *     if field IN ('') then field IS NULL
     *     if field NOT IN (NULL) then field IS NOT NULL
     *     if field NOT IN ('') then field IS NOT NULL
     * </pre>
     */
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

    /**
     * <H2>数据空操作</H2>
     *
     * @param left  左节点
     * @param isNot 是否为空
     * @author Qingyu.Meng
     * @since 2022/12/2
     */
    protected SQLBinaryOpExpr getSQLNullBinaryOpExpr(SQLExpr left, boolean isNot) {
        return new SQLBinaryOpExpr(left, isNot? SQLBinaryOperator.IsNot : SQLBinaryOperator.Is, new SQLNullExpr());
    }

    /**
     * Boolean value
     * <p>
     * if the Oracle boolean storage type is Number(1) and MySQL is bit
     * then TRUE -> 1, FALSE ->0
     * </p>
     *
     * <p>Oracle Null</p>
     * <ol>
     *     <li>= ''     -> IS NULL</li>
     *     <li>!= ''    -> IS NOT NULL</li>
     *     <li>= null   -> IS NULL</li>
     *     <li>!= null  -> IS NOT NULL</li>
     * </ol>
     *
     * <p>if not. Define a new class and override this method. </p>
     */
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        if (Objects.isNull(x.getRight()) || Objects.isNull(x.getOperator())) {
            return super.visit(x);
        }
        if (x.getRight() instanceof SQLBooleanExpr) {
            x.setRight(new SQLNumberExpr(getBooleanNumber((SQLBooleanExpr) x.getRight())));
        } else if (x.getRight() instanceof SQLNullExpr) {
            if (SQLBinaryOperator.Equality.equals(x.getOperator())) {
                x.setOperator(SQLBinaryOperator.Is);
            } else if (SQLBinaryOperator.NotEqual.equals(x.getOperator())) {
                x.setOperator(SQLBinaryOperator.IsNot);
            }
        } else if (x.getRight() instanceof SQLCharExpr && "".equals(((SQLCharExpr) x.getRight()).getValue())) {
            x.setRight(new SQLNullExpr());
            if (SQLBinaryOperator.Equality.equals(x.getOperator())) {
                x.setOperator(SQLBinaryOperator.Is);
            } else if (SQLBinaryOperator.NotEqual.equals(x.getOperator())) {
                x.setOperator(SQLBinaryOperator.IsNot);
            }
        }
        return super.visit(x);
    }

    private static Number getBooleanNumber(SQLBooleanExpr right) {
        //How do you think it's boolean field?
        //if params has boolean field map. need cache?
        return String.valueOf(right).equalsIgnoreCase(Boolean.TRUE.toString()) ? TRUE : FALSE;
    }

}
