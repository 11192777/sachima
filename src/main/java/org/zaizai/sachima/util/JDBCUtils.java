package org.zaizai.sachima.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.constant.StrPool;
import org.zaizai.sachima.enums.DbType;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * JDBC 工具类
 *
 * @author nieqiurong
 * @since 2016-12-05
 */
public class JDBCUtils {

    private static final Log LOG = LogFactory.getLog(JDBCUtils.class);


    /**
     * <H2>获取数据库类型</H2>
     *
     * @param jdbcUrl
     * @return  {@link org.zaizai.sachima.enums.DbType}
     * @author Qingyu.Meng
     * @since 2023/1/10
     */
    public static DbType getDbType(String jdbcUrl) {
        if (StringUtils.isEmpty(jdbcUrl)) {
            return null;
        }
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.mysql;
        } else if (url.contains(":oracle:")) {
            return DbType.oracle;
        } else {
            LOG.warn("Unrecognized database type. JDBC url is: " + jdbcUrl);
            return DbType.other;
        }
    }

    /**
     * <H2>获取数据库分区</H2>
     *
     * @param jdbcUrl
     * @return  {@link java.lang.String}
     * @author Qingyu.Meng
     * @since 2023/1/10
     */
    public static String getOwner(String jdbcUrl) {
        if (StringUtils.isEmpty(jdbcUrl)) {
            return null;
        }
        int lastSlashIndex = jdbcUrl.contains("?") ? jdbcUrl.split("\\?")[0].lastIndexOf(StrPool.SLASH) : jdbcUrl.lastIndexOf(StrPool.SLASH);
        int ownerEndIndex = jdbcUrl.contains("?") ? jdbcUrl.indexOf("?") : jdbcUrl.length();
        return jdbcUrl.substring(lastSlashIndex + 1, ownerEndIndex);
    }


    public static String getOwner(String jdbcUrl, String username) {
        if (StringUtils.isEmpty(jdbcUrl)) {
            return null;
        }
        switch (Objects.requireNonNull(getDbType(jdbcUrl))) {
            case oracle:
                return username.toUpperCase();
            default:
                return getOwner(jdbcUrl);
        }

    }

}
