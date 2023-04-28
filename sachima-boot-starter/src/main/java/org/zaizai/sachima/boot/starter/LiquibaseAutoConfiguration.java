package org.zaizai.sachima.boot.starter;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zaizai.sachima.exception.SQLTranslateException;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapter;
import org.zaizai.sachima.sql.parser.ParserException;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/28 16:01
 */
@Configuration
@ConditionalOnProperty(prefix = "sachima", name = {"enabled", "adaptInLiquibase"})
public class LiquibaseAutoConfiguration {

    private final SachimaAutoConfiguration configuration;

    public LiquibaseAutoConfiguration(ObjectProvider<SachimaAutoConfiguration> configuration) {
        this.configuration = configuration.getIfAvailable();
    }

    @Bean
    @Primary
    public SpringLiquibase oracleSupportLiquibase(ObjectProvider<DataSource> dataSource,
                                                  ObjectProvider<LiquibaseProperties> liquibaseProperties) {
        Assert.notNull(this.configuration);
        OracleSupportLiquibase liquibase = OracleSupportLiquibase.build(dataSource.getIfAvailable(), this.configuration.getAdapter());
        Assert.notNull(liquibase);
        LiquibaseProperties properties = liquibaseProperties.getIfAvailable();
        Assert.notNull(properties);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setClearCheckSums(properties.isClearChecksums());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setLabels(properties.getLabels());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(properties.isTestRollbackOnUpdate());
        liquibase.setTag(properties.getTag());
        return liquibase;
    }


    public static class OracleSupportLiquibase extends SpringLiquibase {

        private static final Log LOG = LogFactory.getLog(OracleSupportLiquibase.class);

        private OracleSupportLiquibase(DruidDataSource dataSource) {
            this.dataSource = dataSource;
        }

        public static OracleSupportLiquibase build(DataSource dataSource, MySqlToOracleAdapter adapter) {
            Assert.isTrue(dataSource instanceof DruidDataSource, "Only com.alibaba.druid.pool.DruidDataSource are supported.");
            return build((DruidDataSource) dataSource, adapter);
        }

        public static OracleSupportLiquibase build(DruidDataSource druidDataSource, MySqlToOracleAdapter adapter) {
            DruidDataSource bootstrap = druidDataSource.cloneDruidDataSource();
            bootstrap.getProxyFilters().add(new SQLAdaptFilter(adapter));
            return new OracleSupportLiquibase(bootstrap);
        }

        @Override
        public void afterPropertiesSet() throws LiquibaseException {
            super.afterPropertiesSet();
            log.debug("===> Liquibase is done.");
            dataSource = null;
            log.debug("===> Leave Liquibase dataSource to JC.");
        }

        private static class SQLAdaptFilter extends FilterEventAdapter {

            private final MySqlToOracleAdapter adapter;

            public SQLAdaptFilter(MySqlToOracleAdapter adapter) {
                this.adapter = adapter;
            }

            @Override
            public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql) throws SQLException {
                return super.connection_prepareStatement(chain, connection, this.adaptSQL(sql));
            }

            @Override
            public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
                return super.statement_executeQuery(chain, statement, this.adaptSQL(sql));
            }

            @Override
            public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
                return super.statement_execute(chain, statement, this.adaptSQL(sql));
            }

            private String adaptSQL(String sql) {
                try {
                    return this.adapter.translateMysqlToOracleOnLiquibase(sql);
                } catch (SQLTranslateException | ParserException | IllegalArgumentException e) {
                    LOG.warn("===> Adapted failed. return original sql:" + sql);
                    return sql;
                }
            }
        }

    }

}
