package org.zaizai.sachima.sql.adapter.visitor;

import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.adapter.handler.VisitorHandler;
import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.ast.*;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.ast.statement.*;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlObjectImpl;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.*;
import org.zaizai.sachima.sql.dialect.oracle.constant.FunctionConstant;
import org.zaizai.sachima.sql.dialect.oracle.parser.OracleLexer;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleOutputVisitor;
import org.zaizai.sachima.sql.parser.Token;
import org.zaizai.sachima.util.CollectionUtils;
import org.zaizai.sachima.util.OracleUtils;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.*;

/**
 * <H1>Simple MySQL to Oracle</H1>
 * <P>Custom {@link VisitorHandler}</P>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/18 15:18
 */
public class MySQLToOracleAdaptVisitor extends OracleOutputVisitor implements AdaptVisitor{

    private static final int IN_ITEM_LIMIT = 1000;
    private static final String IN_ITEM_FORMAT = "(1, {})";
    private static final Number TRUE = 1;
    private static final Number FALSE = 0;
    protected String tableName = null;
    protected MySqlToOracleAdapterImpl adapter;

    public MySQLToOracleAdaptVisitor(Appendable appender, MySqlToOracleAdapterImpl handler) {
        super(appender);
        this.adapter = handler;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        if (Objects.nonNull(x.getOperator()) && FunctionConstant.BINARY.equals(x.getOperator().getName())) {
            x.setOperator(null);
        }
        return true;
    }

    protected void setTableName(String tableName) {
        this.tableName = SQLUtils.normalize(tableName);
    }

    /**
     * <ol>
     *     <li>u.`name` -> u."NAME"</li>
     * </ol>
     */
    @Override
    public boolean visit(SQLPropertyExpr x) {
        x.setName(this.identifierTransferredMeaning(x.getName()));
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        this.setTableName(x.getTableName());
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
     *     select * from user where id in (1, 2, 3) order by field(id, 3, 1, 2) ===> select * from "USER" where "ID" in (1, 2, 3) order by DECODE("ID", 3 , 1, 1, 3, 2, 5)
     *     </li>
     *     <li>NOW() -> SYSDATE
     *     insert into user (created_date) values(now()) ===> insert into user (created_date) values(SYSDATE)
     *     </li>
     *     <li>DATE_FORMAT() -> TO_CHAR
     *     select date_format(created_date, '%Y-%m-%d %H-%i-%s') from ea_form; ===> select TO_CHAR(created_date, 'yyyy-mm-dd hh24:mi:ss') from ea_form;
     *     </li>
     *     <li>left([value], [num]) -> SUBSTR([value] , 0, [num])
     *     select left(name, 2) from user ===> select substr(name, 0, 2) from user;
     *     </li>
     *     <li>if(length(name) > 10, 'y', 'n') -> DECODE(SIGN(LENGTH(name) - 10, 1, 'y', 'n'))
     *     select if(length(name) > 10, 'y', 'n') from dual; ===> select DECODE(SIGN(LENGTH(name) - 10, 1, 'y', 'n')) from dual;
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
            for (int i = 1; i < x.getArguments().size(); i += 2) {
                x.getArguments().add(i + 1, new SQLNumberExpr(i));
            }
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
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.LEFT)) {
            x.setMethodName(FunctionConstant.SUBSTR);
            x.getArguments().add(x.getArguments().get(1).clone());
            x.getArguments().set(1, new SQLIntegerExpr(0));
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.YEAR)) {
            x.setMethodName(FunctionConstant.TO_CHAR);
            x.getArguments().add(new SQLCharExpr("yyyy"));
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.MONTH)) {
            x.setMethodName(FunctionConstant.TO_CHAR);
            x.getArguments().add(new SQLCharExpr("MM"));
        } else if (StringUtils.equalsIgnoreCase(methodName, FunctionConstant.IF)) {
            x.setMethodName(FunctionConstant.DECODE);
            if (x.getArguments().get(0) instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) x.getArguments().get(0);
                List<SQLExpr> args = OracleUtils.listMySqlIfMethodInvokeArgs(sqlBinaryOpExpr.getOperator(), x.getArguments().get(1), x.getArguments().get(2));
                sqlBinaryOpExpr.setOperator(SQLBinaryOperator.Subtract);
                //build a SIGN method invoke expr.
                SQLMethodInvokeExpr signMethodInvokeExpr = new SQLMethodInvokeExpr(FunctionConstant.SIGN);
                signMethodInvokeExpr.addArgument(sqlBinaryOpExpr);
                x.getArguments().clear();
                x.getArguments().add(signMethodInvokeExpr);
                x.getArguments().addAll(args);
            }
        }
        return super.visit(x);
    }

    /**
     * Sql limit 1000 adapt.
     *
     * <p>
     * if sql in items size > 1000 {@link MySQLToOracleAdaptVisitor#IN_ITEM_LIMIT}
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
        return new SQLBinaryOpExpr(left, isNot ? SQLBinaryOperator.IsNot : SQLBinaryOperator.Is, new SQLNullExpr());
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
     *
     * <pre>NCLOB type. example: file_url(NCLOB)
     *
     *  MySQL:  select id from file where file_url = 'https://....'
     *  Oracle: select id from file where TO_CHAR(file_url) = 'https://....'
     * </pre>
     */
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        if (Objects.isNull(x.getRight()) || Objects.isNull(x.getOperator())) {
            return super.visit(x);
        }
        //if (x.getRight() instanceof SQLBooleanExpr) {
        //    x.setRight(new SQLNumberExpr(getBooleanNumber((SQLBooleanExpr) x.getRight())));
        //} else
        if (x.getRight() instanceof SQLNullExpr) {
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

        if (x.getLeft() instanceof SQLName && Objects.nonNull(tableName)) {
            SQLName filed = (SQLName) x.getLeft();
            if (adapter.verifyDataType(tableName, filed.getSimpleName(), TokenFnvConstants.NCLOB)) {
                x.setLeft(new SQLMethodInvokeExpr(FunctionConstant.TO_CHAR, null, Collections.singletonList(filed)));
            }
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLBooleanExpr x) {
        return super.visit(new SQLNumberExpr(getBooleanNumber(x)));
    }

    /**
     * Oracle adapt.
     * TRUE -> 1, FALSE -> 0
     */
    private static Number getBooleanNumber(SQLBooleanExpr x) {
        //TODO use ColmnTypeHandler to check is boolean field
        return String.valueOf(x).equalsIgnoreCase(Boolean.TRUE.toString()) ? TRUE : FALSE;
    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        if (x.getOn().getExpr() instanceof SQLPropertyExpr) {
            SQLPropertyExpr expr = (SQLPropertyExpr) x.getOn().getExpr();
            expr.setName(this.identifierTransferredMeaning(expr.getName()));
        }
        return super.visit(x);
    }


    @Override
    public boolean visit(SQLAlterTableStatement x) {
        return super.visit(x);
    }

    /**
     * Nonsupport comment.
     * <p>
     * such as: alter table [table_name] add [column_name] [column_type] DEFAULT NULL COMMENT '[comment]';
     * result is: alter table [table_name] add [column_name] [column_type] DEFAULT NULL;
     */
    public void visit(MySqlObjectImpl x) {
        if (x instanceof MySqlAlterTableModifyColumn) {
            print0("MODIFY ");
            SQLColumnDefinition columnDefinition = ((MySqlAlterTableModifyColumn) x).getNewColumnDefinition();
            SQLExpr comment = columnDefinition.getComment();
            if (Objects.nonNull(comment)) {
                columnDefinition.setComment(null);
                columnDefinition.getConstraints().clear();
            }
            super.visit(columnDefinition);
            //支持拆成两条SQL，但是JDBC不支持
            //if (Objects.nonNull(comment)) {
            //    print0("\n/\n");
            //    SQLCommentStatement sqlCommentStatement = new SQLCommentStatement();
            //    sqlCommentStatement.setOn(new SQLExprTableSource(new SQLPropertyExpr(tableName, columnDefinition.getColumnName())));
            //    sqlCommentStatement.setType(SQLCommentStatement.Type.COLUMN);
            //    sqlCommentStatement.setComment(comment);
            //    super.visit(sqlCommentStatement);
            //}
        }
    }

    /**
     * MySQL data type mapping to Oracle data type.
     * <p>
     * such as : bigint to Number
     */
    @Override
    public boolean visit(SQLDataType x) {
        //x.setName(DataTypeMappingHandler.getOracleDataType(x.getName()));
        return super.visit(x);
    }

    /**
     * MySQL create table adapt.
     */
    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        //SQLName indexName = x.getIndexDefinition().getName();
        //SQLTableSource table = x.getIndexDefinition().getTable();
        //if (indexName instanceof SQLPropertyExpr && table instanceof SQLExprTableSource) {
        //    SQLPropertyExpr indexNameProperty = (SQLPropertyExpr) indexName;
        //    indexNameProperty.setName(((SQLExprTableSource) table).getTableName().concat("_").concat(indexNameProperty.getName()));
        //}
        return super.visit(x);
    }

    /**
     * MySQL insert adapt.
     *
     * <p>Adapt point:</p>
     * <ol>
     *     <li>ID compensate</li>
     *     <li>Insert columns to_char format</li>
     *     <li>Remove Null insert item</li>
     * </ol>
     * <p>
     * Oracle nonsupport [''] & [""] value.
     *
     * <pre>
     *     围绕NAME列在Oracle的定义举例子，从而对删除空值的插入说明：
     *     example  :  INSERT INTO EXAMPLE (ID, NAME) VALUES (1000, [NULL | '' | ""])
     *     adapt    :  INSERT INTO EXAMPLE (ID) VALUES (1000);
     *
     *     <ol>
     *         <li>当NAME列，定义可为空时，@example 和 @adapt 效果一样</li>
     *         <li>当NAME列，定义不为空时，@example 和 @adapt 无论在MySQL和Oracle都会失败</li>
     *         <li>当NAME列，定义不为空时且存在默认值，@example 在Oracle因为显示插入NULL而失败，适配后解决</li>
     *     </ol>
     *
     *     Batch情况适配：
     *     example  :  INSERT INTO EXAMPLE (ID, NAME) VALUES (1000, NULL), (1001, 'ZhangSan');
     *     adapt    :  INSERT ALL
     *                    INTO EXAMPLE (ID) VALUES (1000)
     *                    INTO EXAMPLE (ID, NAME) VALUES (1001, 'ZhangSan')
     *                 SELECT 1 FROM DUAL;
     * </pre>
     */
    @Override
    public boolean visit(SQLInsertStatement x) {
        this.setTableName(x.getTableName().getSimpleName());

        String tablePrimaryKey = adapter.getTablePrimaryKey(this.tableName);
        if (Objects.nonNull(tablePrimaryKey)) {
            boolean existPrimaryField = false;
            for (SQLExpr column : x.getColumns()) {
                if (column instanceof SQLIdentifierExpr && ((SQLIdentifierExpr) column).getName().equalsIgnoreCase(tablePrimaryKey)) {
                    existPrimaryField = true;
                    break;
                }
            }
            if (!existPrimaryField) {
                x.getColumns().add(new SQLIdentifierExpr(tablePrimaryKey));
                x.getValuesList().forEach(item -> item.addValue(new SQLCharExpr(this.adapter.generateId())));
            }
        }

        if (CollectionUtils.isNotEmpty(x.getValuesList())) {
            x.getValuesList().forEach(item -> this.handleInsertValueItem(x.getColumns(), item));
        }

        if (x.getValuesList().size() <= 1) {
            for (int index = x.getValuesList().get(0).getValues().size() - 1; index >= 0; index--) {
                if (this.isSQLNullExpr(x.getValuesList().get(0).getValues().get(index))) {
                    x.getColumns().remove(index);
                }
            }
            return super.visit(x);
        }

        print0(ucase ? "INSERT ALL " : "insert all ");
        println();
        for (SQLInsertStatement.ValuesClause valuesClause : x.getValuesList()) {
            print(ucase ? "INTO " : "into ");
            x.getTableSource().accept(this);
            String columnsString = x.getColumnsString();
            if (columnsString != null) {
                print0(columnsString);
            } else {
                printInsertColumns(this.listNonnullExprColumns(x.getColumns(), valuesClause));
            }
            print0(ucase ? " VALUES " : " values ");
            valuesClause.accept(this);
            println();
        }
        print0("SELECT 1 FROM DUAL");
        return false;
    }

    /**
     * <H2>过滤掉为空的列</H2>
     *
     * @param columns      insert columns
     * @param valuesClause insert column values
     * @return {@link java.util.List<org.zaizai.sachima.sql.ast.SQLExpr>}  filter NullExpr column items.
     * @author Qingyu.Meng
     * @since 2023/3/22
     */
    protected List<SQLExpr> listNonnullExprColumns(List<SQLExpr> columns, SQLInsertStatement.ValuesClause valuesClause) {
        ArrayList<SQLExpr> targetList = new ArrayList<>(columns.size());
        for (int index = 0; index < valuesClause.getValues().size(); index++) {
            if (!this.isSQLNullExpr(valuesClause.getValues().get(index))) {
                targetList.add(columns.get(index));
            }
        }
        return targetList;
    }


    /**
     * <p>[NULL] & [''] & [""]</p>
     * <p>
     * Oracle nonsupport '' value.
     */
    protected boolean isSQLNullExpr(SQLExpr x) {
        return x instanceof SQLNullExpr || (x instanceof SQLCharExpr && ((SQLCharExpr) x).getValue().equals(""));
    }

    /**
     * <H2>过滤掉为空的值</H2>
     *
     * @param x values of Sql insert.
     * @return {@link boolean}
     * @author Qingyu.Meng
     * @since 2023/3/22
     */
    @Override
    public boolean visit(SQLInsertStatement.ValuesClause x) {
        x.getValues().removeIf(this::isSQLNullExpr);
        return super.visit(x);
    }


    /**
     * <H2>处理Insert操作</H2>
     *
     * <pre>
     *     1.如果存在字段为DATE类型，则to_date包裹一下
     * </pre>
     *
     * @param columns    插入列
     * @param valuesItem 插入值
     * @author Qingyu.Meng
     * @since 2023/3/14
     */
    protected void handleInsertValueItem(List<SQLExpr> columns, SQLInsertStatement.ValuesClause valuesItem) {
        for (int i = 0; i < columns.size(); i++) {
            SQLExpr column = columns.get(i);
            if (column instanceof SQLIdentifierExpr) {
                String columnName = ((SQLIdentifierExpr) column).getName();
                if (adapter.verifyDataTypeAny(this.tableName, columnName, TokenFnvConstants.DATE, TokenFnvConstants.TIMESTAMP)) {
                    SQLExpr sqlExpr = valuesItem.getValues().get(i);
                    if (sqlExpr instanceof SQLCharExpr) {
                        SQLMethodInvokeExpr sqlMethodInvokeExpr = new SQLMethodInvokeExpr(FunctionConstant.TO_DATE);
                        sqlMethodInvokeExpr.getArguments().add(valuesItem.getValues().get(i));
                        sqlMethodInvokeExpr.getArguments().add(new SQLCharExpr(this.getOracleDateFormat(((SQLCharExpr) sqlExpr).getText())));
                        valuesItem.getValues().set(i, sqlMethodInvokeExpr);
                    }
                }
            }
        }

    }

    /**
     * Get oracle to_char format.
     * <p>
     * such as : @param: "2022-12-22 01:01:01" @result: "yyyy-mm-dd hh24:mi:ss"
     *
     * @param date 日期串
     * @return Oracle日期format串
     */
    public String getOracleDateFormat(String date) {
        Assert.notNull(date, "Inaccurate date value: {}", date);
        String[] formatItems = new String[]{"yyyy", "mm", "dd", "hh24", "mi", "ss"};
        int formatItemIndex = 0;
        boolean appendFlag = true;
        char[] dateItems = date.toCharArray();
        StringBuilder appender = new StringBuilder();
        for (char dateItem : dateItems) {
            if (dateItem >= '0' && dateItem <= '9') {
                if (appendFlag) {
                    appender.append(formatItems[formatItemIndex++]);
                    appendFlag = false;
                }
            } else {
                appendFlag = true;
                appender.append(dateItem);
            }
        }
        return appender.toString();
    }

    /**
     * <H2>MySQL JOIN update adapt.</H2>
     *
     * @param x MySQL update statement.
     * @return {@link boolean}
     * @author Qingyu.Meng
     * @since 2023/2/13
     */
    @Override
    public boolean visit(SQLUpdateStatement x) {
        if (x instanceof MySqlUpdateStatement && x.getTableSource() instanceof SQLJoinTableSource) {
            print0(ucase ? "UPDATE " : "update ");
            print0("(\n");
            print0(ucase ? "SELECT " : "select ");
            boolean sepFlag = false;
            for (SQLExpr expr : this.getSQLUpdatePropertyItems(x.getItems())) {
                if (!(expr instanceof SQLPropertyExpr) && !(expr instanceof SQLIdentifierExpr)) {
                    continue;
                }
                if (sepFlag) {
                    print0(", ");
                }
                sepFlag = true;
                if (expr instanceof SQLPropertyExpr) {
                    printExpr(expr);
                    print0(" ");
                    print0(((SQLPropertyExpr) expr).getFullName().replace(".", "__"));
                } else {
                    printExpr(expr);
                    print0(" ");
                }
            }
            println();
            print0(ucase ? "FROM " : "from ");
            printTableSource(x.getTableSource());
            println();
            SQLExpr where = x.getWhere();
            if (Objects.nonNull(where)) {
                indentCount++;
                print0(ucase ? "WHERE " : "where");
                printExpr(where, parameterized);
                indentCount--;
            }
            print0("\n)\n");
            print0(ucase ? "SET " : "set ");
            for (int i = 0; i < x.getItems().size(); i++) {
                if (i != 0) {
                    print0(", ");
                }
                SQLUpdateSetItem sqlUpdateSetItem = x.getItems().get(i);
                this.visitUpdateTableAlias(sqlUpdateSetItem);
            }
            println();
            print0(ucase ? "WHERE 1=1" : "where 1=1");
            return false;
        } else {
            return super.visit(x);
        }
    }

    /**
     * MySQL JOIN update adapt.
     *
     * @param x SQL update items.
     */
    public void visitUpdateTableAlias(SQLUpdateSetItem x) {
        if (x.getColumn() instanceof SQLPropertyExpr) {
            print0(((SQLPropertyExpr) x.getColumn()).getFullName().replace(".", "__"));
        } else {
            printExpr(x.getColumn(), parameterized);
        }
        print0(" = ");
        if (x.getValue() instanceof SQLPropertyExpr) {
            print0(((SQLPropertyExpr) x.getValue()).getFullName().replace(".", "__"));
        } else {
            printExpr(x.getValue(), parameterized);
        }
    }

    /**
     * get update properties.
     */
    private List<SQLExpr> getSQLUpdatePropertyItems(List<SQLUpdateSetItem> items) {
        List<SQLExpr> targets = new ArrayList<>(items.size() * 2);
        for (SQLUpdateSetItem item : items) {
            targets.add(item.getColumn());
            targets.add(item.getValue());
        }
        return targets;
    }

    /**
     * MySQL Binary Expr adapt.
     * <p>
     * MySQL:  SELECT * FROM user WHERE is_enabled = b'1'
     * Oracle: SELECT * FROM user WHERE is_enabled = 1
     */
    @Override
    public boolean visit(SQLBinaryExpr x) {
        print0(x.getText());
        return false;
    }

}
