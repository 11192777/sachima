package org.zaizai.sachima.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/28 10:05
 */
@ConfigurationProperties(prefix = SachimaProperties.SACHIMA)
public class SachimaProperties {

    protected static final String SACHIMA = "sachima";

    /**
     * Use sachima.
     */
    private Boolean enabled;

    /**
     * Enabled in MyBatis-plus.
     */
    private Boolean adaptInMybatisPlus;

    /**
     * Enabled in Liquibase.
     */
    private Boolean adaptInLiquibase;

    /**
     * Handler settings.
     */
    private HandlerSettings handlerSettings;

    public static class HandlerSettings {

        /**
         * Recommended use. If not, there may be adaptation issues.
         */
        private Boolean enabledColumnTypeHandler;

        /**
         * Primary keys are AutoFill.
         */
        private Boolean enabledPrimaryKeyHandler;

        /**
         * Resolves an issue where non-empty fields are displayed inserted.
         */
        private Boolean enabledNonNullTypeHandler;

        public Boolean getEnabledColumnTypeHandler() {
            return enabledColumnTypeHandler;
        }

        public void setEnabledColumnTypeHandler(Boolean enabledColumnTypeHandler) {
            this.enabledColumnTypeHandler = enabledColumnTypeHandler;
        }

        public Boolean getEnabledPrimaryKeyHandler() {
            return enabledPrimaryKeyHandler;
        }

        public void setEnabledPrimaryKeyHandler(Boolean enabledPrimaryKeyHandler) {
            this.enabledPrimaryKeyHandler = enabledPrimaryKeyHandler;
        }

        public Boolean getEnabledNonNullTypeHandler() {
            return enabledNonNullTypeHandler;
        }

        public void setEnabledNonNullTypeHandler(Boolean enabledNonNullTypeHandler) {
            this.enabledNonNullTypeHandler = enabledNonNullTypeHandler;
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAdaptInMybatisPlus() {
        return adaptInMybatisPlus;
    }

    public void setAdaptInMybatisPlus(Boolean adaptInMybatisPlus) {
        this.adaptInMybatisPlus = adaptInMybatisPlus;
    }

    public Boolean getAdaptInLiquibase() {
        return adaptInLiquibase;
    }

    public void setAdaptInLiquibase(Boolean adaptInLiquibase) {
        this.adaptInLiquibase = adaptInLiquibase;
    }

    public HandlerSettings getHandlerSettings() {
        return handlerSettings;
    }

    public void setHandlerSettings(HandlerSettings handlerSettings) {
        this.handlerSettings = handlerSettings;
    }
}
