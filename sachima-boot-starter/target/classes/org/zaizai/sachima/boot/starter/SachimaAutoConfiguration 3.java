package org.zaizai.sachima.boot.starter;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.adapter.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.NonNullTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.PrimaryKeyHandler;
import org.zaizai.sachima.util.JDBCUtils;

import javax.sql.DataSource;
import java.util.Objects;


/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/28 09:41
 */
@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties({SachimaProperties.class})
@ConditionalOnProperty(prefix = "sachima", name = "enabled")
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SachimaAutoConfiguration implements InitializingBean {


    private final SachimaProperties sachimaProperties;
    private final DataSource dataSource;
    private final DataSourceProperties dataSourceProperties;

    private MySqlToOracleAdapterImpl adapter;

    public SachimaAutoConfiguration(SachimaProperties sachimaProperties,
                                    ObjectProvider<DataSource> dataSource,
                                    ObjectProvider<DataSourceProperties> dataSourceProperties) {
        this.sachimaProperties = sachimaProperties;
        this.dataSource = dataSource.getIfAvailable();
        this.dataSourceProperties = dataSourceProperties.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Boolean.TRUE.equals(this.sachimaProperties.getEnabled())) {
            this.adapter = new MySqlToOracleAdapterImpl();
        }
        if (Objects.isNull(this.sachimaProperties.getHandlerSettings())) {
            return;
        }
        String owner = JDBCUtils.getOwner(this.dataSourceProperties.getUrl(), this.dataSourceProperties.getUsername());
        if (Boolean.TRUE.equals(this.sachimaProperties.getHandlerSettings().getEnabledColumnTypeHandler())) {
            this.adapter.setColumnTypeHandler(ColumnTypeHandler.build(dataSource, owner));
        }
        if (Boolean.TRUE.equals(this.sachimaProperties.getHandlerSettings().getEnabledNonNullTypeHandler())) {
            this.adapter.setNonNullTypeHandler(NonNullTypeHandler.build(dataSource, owner));
        }
        if (Boolean.TRUE.equals(this.sachimaProperties.getHandlerSettings().getEnabledPrimaryKeyHandler())) {
            this.adapter.setPrimaryKeyHandler(PrimaryKeyHandler.build(dataSource, owner));
        }
    }

    public MySqlToOracleAdapterImpl getAdapter() {
        return adapter;
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }
}
