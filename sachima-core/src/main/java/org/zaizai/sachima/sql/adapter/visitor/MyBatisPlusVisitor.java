package org.zaizai.sachima.sql.adapter.visitor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.ast.SQLCommentHint;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.expr.SQLCharExpr;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.statement.SQLInsertStatement;
import org.zaizai.sachima.sql.ast.statement.SQLWithSubqueryClause;
import org.zaizai.sachima.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/3/31 16:45
 */
public class MyBatisPlusVisitor extends MySQLToOracleAdaptVisitor {

    private final PluginUtils.MPStatementHandler mpSH;

    public MyBatisPlusVisitor(Appendable appender, MySqlToOracleAdapterImpl adapter, PluginUtils.MPStatementHandler mpSH) {
        super(appender, adapter);
        this.mpSH = mpSH;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        this.setTableName(x.getTableName().getSimpleName());

        List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            visit(with);
            println();
        }

        if (x.isUpsert()) {
            print0(ucase ? "UPSERT INTO " : "upsert into ");
        } else {
            print0(ucase ? "INSERT INTO " : "insert into ");
        }

        x.getTableSource().accept(this);

        String columnsString = x.getColumnsString();
        if (columnsString != null) {
            print0(columnsString);
        } else {
            printInsertColumns(x.getColumns());
        }

        if (!x.getValuesList().isEmpty()) {
            println();
            print0(ucase ? "VALUES " : "values ");
            printAndAccept(x.getValuesList(), ", ");
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        return false;
    }

    private String getParameterValue(int index) {
        BoundSql boundSql = mpSH.boundSql();
        Configuration configuration = mpSH.configuration();
        ParameterMapping parameterMapping = boundSql.getParameterMappings().get(index);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
            Object value;
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (boundSql.getParameterObject()== null) {
                return null;
            } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(boundSql.getParameterObject().getClass())) {
                value = boundSql.getParameterObject();
            } else {
                MetaObject metaObject = configuration.newMetaObject(boundSql.getParameterObject());
                value = metaObject.getValue(propertyName);
            }
            return Objects.isNull(value) ? null : String.valueOf(value);
        }
        return null;
    }

}
