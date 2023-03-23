package org.zaizai.sachima.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.exception.SQLTranslateException;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlToOracleOutputVisitor;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.DataTypeHandler;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.PrimaryKeyHandler;
import org.zaizai.sachima.sql.visitor.SQLASTOutputVisitor;
import org.zaizai.sachima.sql.visitor.VisitorFeature;

import java.util.Objects;

/**
 * <H1>SQL适配Helper</H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/30 09:52
 */
public class SQLAdaptHelper {

    private SQLAdaptHelper() {
    }

    private static final Log LOG = LogFactory.getLog(SQLAdaptHelper.class);

    /**
     * <H2>not format</H2>
     *
     * @param sql      MySQL sql
     * @param features f
     * @return {@link java.lang.String} Oracle sql
     * @author Qingyu.Meng
     * @since 2022/11/18
     */
    public static String translateMysqlToOracle(String sql, VisitorFeature... features) throws SQLTranslateException {
        return translateMysqlToOracle(sql, null, features);
    }

    /**
     * <H2>not format</H2>
     *
     * @param sql      MySQL sql
     * @param visitor  default: {@link MySqlToOracleOutputVisitor}
     * @param features f
     * @return {@link java.lang.String} Oracle sql
     * @author Qingyu.Meng
     * @since 2022/11/18
     */
    public static String translateMysqlToOracle(String sql, SQLASTOutputVisitor visitor, VisitorFeature... features) throws SQLTranslateException {
        LOG.debug("Original SQL:" + sql);
        if (Objects.isNull(visitor)) {
            visitor = new MySqlToOracleOutputVisitor(new StringBuilder());
        }
        handleVisitor(SQLUtils.parseSingleStatement(sql, DbType.mysql), visitor, features);
        String adaptSql = visitor.getAppender().toString();
        LOG.debug("Adapted SQL:" + adaptSql);
        return adaptSql;
    }

    /**
     * <H2>handle statement</H2>
     *
     * @param statement {@link SQLStatement}
     * @param visitor   AST visitor
     * @param features  f
     * @return {@link java.lang.String}
     * @author Qingyu.Meng
     * @since 2023/1/12
     */
    private static String handleVisitor(SQLStatement statement, SQLASTOutputVisitor visitor, VisitorFeature... features) throws SQLTranslateException {
        if (ArrayUtil.isNotEmpty(features)) {
            int featuresValue = 0;
            for (VisitorFeature feature : features) {
                visitor.config(feature, true);
                featuresValue |= feature.mask;
            }
            visitor.setFeatures(featuresValue);
        }
        try {
            statement.accept(visitor);
        } catch (Exception e) {
            LOG.error("===> Handle visitor failed. SQL: " + statement, e);
            throw new SQLTranslateException();
        }
        return visitor.getAppender().toString();
    }

    /**
     * <H2>在Liquibase上适配MySQL到Oracle</H2>
     *
     * <p>对MySQL项目中Liquibase特别支持(DDL部分), 主要解决Liquibase的输出不既有MySQL SQL又有Oracle SQL问题</p>
     * <pre>
     *    Liquibase会识别数据源，会对内置的标签语言转为Oracle，例如：
     *    ```
     *    <changeSet id="xxxxxxxxx" author="author.name">
     *     		<dropIndex tableName="table_name" indexName="idx_column1_column2"/>
     *    </changeSet>
     *    ```
     *    - 在MySQL环境时解析为: drop index idx_column1_column2 on table_name;
     *    - 在Oracle环境时解析为: drop index IDX_COLUMN1_COLUMN2;
     *
     *    但是当被定义成如下同义脚本时：
     *    ```
     *    <changeSet id="xxxxxxxxx" author="author.name">
     * 		    <sql>
     *              drop index idx_column1_column2 on table_name
     * 		    </sql>
     * 	  </changeSet>
     * 	  ```
     * 	  Liquibase无法将其转换为Oracle SQL
     *
     * 	  从结果来看，@param:{sql} 即可能是MySQL SQL也可能是Oracle SQL，而我们目前没有办法
     * 	  单纯的通过SQL来判断其DBMS类型，即无法解析为正确的AST语法树，从而无法后续的SQL适配，
     * 	  所以使用此方法对Liquibase给予特别支持
     * </pre>
     *
     * <p>以下点不给予适配: </p>
     * <ol>
     *     <li>{@link MySqlCreateTableStatement} 创建表</li>
     * </ol>
     *
     * @param sql MySQL SQL
     * @return {@link java.lang.String} Oracle SQL
     * @author Qingyu.Meng
     * @since 2023/1/12
     */
    public static String translateMysqlToOracleOnLiquibase(String sql) {
        LOG.debug("Original SQL:" + sql);
        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.mysql);
        ColumnTypeHandler.refresh(statement);
        PrimaryKeyHandler.refresh(statement);
        if (statement instanceof MySqlCreateTableStatement) {
            return sql;
        }
        String adaptSql = handleVisitor(statement, new MySqlToOracleOutputVisitor(new StringBuilder()));
        LOG.debug("Adapted SQL:" + adaptSql);
        return adaptSql;
    }

    /**
     * Environment: MyBatis plus + Liquibase
     */
    public static String translateMysqlToOracleOnLiquibaseAndMyBaitsPlus(String sql) {
        sql = SQLUtils.removeLastSeparator(sql, DbType.mysql);
        return translateMysqlToOracleOnLiquibase(sql);
    }
}
