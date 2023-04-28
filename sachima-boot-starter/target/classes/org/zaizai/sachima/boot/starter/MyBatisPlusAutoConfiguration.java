package org.zaizai.sachima.boot.starter;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapter;

import java.sql.Connection;
import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/28 16:03
 */
@Configuration
@ConditionalOnProperty(prefix = "sachima", name = {"enabled", "adaptInMybatisPlus"})
public class MyBatisPlusAutoConfiguration implements InitializingBean {

    private final MySqlToOracleAdapter adapter;

    private final MybatisPlusInterceptor mybatisPlusInterceptor;


    public MyBatisPlusAutoConfiguration(ObjectProvider<SachimaAutoConfiguration> configuration,
                                        ObjectProvider<MybatisPlusInterceptor> mybatisPlusInterceptor) {
        SachimaAutoConfiguration configurationAvailable = configuration.getIfAvailable();
        if (Objects.nonNull(configurationAvailable)) {
            this.adapter = configurationAvailable.getAdapter();
        } else {
            this.adapter = null;
        }
        this.mybatisPlusInterceptor = mybatisPlusInterceptor.getIfAvailable();
    }


    @Override
    public void afterPropertiesSet() {
        if (Objects.nonNull(mybatisPlusInterceptor) && Objects.nonNull(adapter)) {
            this.mybatisPlusInterceptor.addInnerInterceptor(new InnerInterceptor() {
                @Override
                public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
                    adapter.translateMysqlToOracleOnMyBaitsPlus(sh);
                }
            });
        }
    }
}
