package org.zaizai.sachima.sql.adapter;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.exception.SQLTranslateException;
import org.zaizai.sachima.sql.adapter.event.ColumnAlterEvent;
import org.zaizai.sachima.sql.adapter.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.DataTypeMappingHandler;
import org.zaizai.sachima.sql.adapter.handler.NonNullTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.PrimaryKeyHandler;
import org.zaizai.sachima.sql.adapter.visitor.MySQLToOracleAdaptVisitor;
import org.zaizai.sachima.sql.adapter.visitor.LiquibaseAdaptVisitor;
import org.zaizai.sachima.sql.adapter.visitor.MyBatisPlusVisitor;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.sql.visitor.SQLASTOutputVisitor;
import org.zaizai.sachima.sql.visitor.VisitorFeature;
import org.zaizai.sachima.util.ArrayUtil;
import org.zaizai.sachima.util.SQLUtils;

import java.util.*;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/1 00:21
 */
public class MySqlToOracleAdapterImpl implements MySqlToOracleAdapter {


    private static final Log LOG = LogFactory.getLog(MySqlToOracleAdapterImpl.class);

    private ColumnTypeHandler columnTypeHandler;
    private PrimaryKeyHandler primaryKeyHandler;
    private NonNullTypeHandler nonNullTypeHandler;
    private final Set<ColumnAlterEvent> handlerChain;

    private DataTypeMappingHandler dataTypeMappingHandler;

    public MySqlToOracleAdapterImpl() {
        this.handlerChain = new HashSet<>();
        this.dataTypeMappingHandler = new DataTypeMappingHandler();
    }

    public void setColumnTypeHandler(ColumnTypeHandler columnTypeHandler) {
        this.columnTypeHandler = columnTypeHandler;
        if (Objects.nonNull(columnTypeHandler)) {
            this.handlerChain.add(columnTypeHandler);
        }
    }

    public void setPrimaryKeyHandler(PrimaryKeyHandler primaryKeyHandler) {
        this.primaryKeyHandler = primaryKeyHandler;
        if (Objects.nonNull(primaryKeyHandler)) {
            this.handlerChain.add(primaryKeyHandler);
        }
    }

    public void setNonNullTypeHandler(NonNullTypeHandler nonNullTypeHandler) {
        this.nonNullTypeHandler = nonNullTypeHandler;
        if (Objects.nonNull(nonNullTypeHandler)) {
            this.handlerChain.add(nonNullTypeHandler);
        }
    }

    public void setDataTypeMappingHandler(DataTypeMappingHandler dataTypeMappingHandler) {
        this.dataTypeMappingHandler = dataTypeMappingHandler;
    }

    /**
     * <H2>not format</H2>
     *
     * @param sql      MySQL sql
     * @param features f
     * @return {@link java.lang.String} Oracle sql
     * @author Qingyu.Meng
     * @since 2022/11/18
     */
    public String translateMysqlToOracle(String sql, VisitorFeature... features) throws SQLTranslateException {
        return translateMysqlToOracle(sql, null, features);
    }

    /**
     * <H2>not format</H2>
     *
     * @param sql      MySQL sql
     * @param visitor  default: {@link MySQLToOracleAdaptVisitor}
     * @param features f
     * @return {@link java.lang.String} Oracle sql
     * @author Qingyu.Meng
     * @since 2022/11/18
     */
    public String translateMysqlToOracle(String sql, SQLASTOutputVisitor visitor, VisitorFeature... features) throws SQLTranslateException {
        LOG.debug("Original SQL:" + sql);
        if (Objects.isNull(visitor)) {
            visitor = new MySQLToOracleAdaptVisitor(new StringBuilder(), this);
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
    private String handleVisitor(SQLStatement statement, SQLASTOutputVisitor visitor, VisitorFeature... features) throws SQLTranslateException {
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
    @Override
    public String translateMysqlToOracleOnLiquibase(String sql) {
        LOG.debug("Liquibase original SQL:" + sql);
        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.mysql);
        String adaptSql = handleVisitor(statement, new LiquibaseAdaptVisitor(new StringBuilder(), this, sql));
        LOG.debug("Liquibase adapted SQL:" + adaptSql);
        return adaptSql;
    }

    /**
     * Environment: MyBatis plus
     */
    private String translateMysqlToOracleOnMyBaitsPlus(String sql, PluginUtils.MPStatementHandler mpSH) {
        sql = SQLUtils.removeLastSeparator(sql, DbType.mysql);
        LOG.debug("IBatis original SQL:" + sql);
        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.mysql);
        String adaptSql = handleVisitor(statement, new MyBatisPlusVisitor(new StringBuilder(), this, mpSH));
        LOG.debug("IBatis adapted SQL:" + adaptSql);
        return adaptSql;
    }

    /**
     * Environment: MyBatis plus
     */
    @Override
    public String translateMysqlToOracleOnMyBaitsPlus(String sql) {
        return this.translateMysqlToOracleOnMyBaitsPlus(sql, null);
    }


    /**
     * Environment: MyBatis plus
     */
    @Override
    public void translateMysqlToOracleOnMyBaitsPlus(StatementHandler sh) {
        PluginUtils.MPStatementHandler mpSH = PluginUtils.mpStatementHandler(sh);
        this.translateMysqlToOracleOnMyBaitsPlus(mpSH.boundSql().getSql(), mpSH);
    }

    /**
     * @return table primary key name.
     */
    public String getTablePrimaryKey(String tableName) {
        return Objects.isNull(primaryKeyHandler) ? null : primaryKeyHandler.getTablePrimaryKey(tableName);
    }

    /**
     * <p>Check table field type is exists.</p>
     *
     * @param dataType {@link TokenFnvConstants}
     */
    public boolean verifyDataType(String tableName, String columnName, long dataType) {
        return Objects.nonNull(columnTypeHandler) && columnTypeHandler.contains(tableName, columnName, dataType);
    }

    /**
     * @see ColumnTypeHandler#contains(String, String, long)
     */
    public boolean verifyDataTypeAny(String tableName, String columnName, Long... dataTypes) {
        return Objects.nonNull(columnTypeHandler) && columnTypeHandler.containsAny(tableName, columnName, dataTypes);
    }

    /**
     * @see NonNullTypeHandler#contains(String, String)
     */
    public boolean verifyNonnullHasDefault(String tableName, String columnName) {
        return Objects.nonNull(nonNullTypeHandler) && nonNullTypeHandler.contains(tableName, columnName);
    }

    public String generateId() {
        return Objects.isNull(primaryKeyHandler) ? null : primaryKeyHandler.generateId();
    }

    public void onEvent(SQLStatement statement) {
        this.handlerChain.forEach(h -> h.onStatement(statement));
    }

    /**
     * mapping target data type to oracle data type;
     * @param targetDataType such as bigint、 varchar()、 timestamp
     * @return oracle data type: Number、 Nvarchar()、 Date
     */
    public String getMappingDataType(String targetDataType) {
        String mappingValue = this.dataTypeMappingHandler.getMappingValue(targetDataType);
        return Objects.isNull(mappingValue) ? targetDataType : mappingValue;
    }
}
