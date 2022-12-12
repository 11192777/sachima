package org.zaizai.sachima.util;

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlToOracleOutputVisitor;
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

    private SQLAdaptHelper() {}

    /**
     * <H2>not format</H2>
     *
     * @param sql           MySQL sql
     * @param features      f
     * @return {@link java.lang.String} Oracle sql
     * @author Qingyu.Meng
     * @since 2022/11/18
     */
    public static String translateMysqlToOracle(String sql, VisitorFeature... features) {
        return translateMysqlToOracle(sql, null, features);
    }

    /**
     * <H2>not format</H2>
     *
     * @param sql           MySQL sql
     * @param visitor       default: {@link MySqlToOracleOutputVisitor}
     * @param features      f
     * @return {@link java.lang.String} Oracle sql
     * @author Qingyu.Meng
     * @since 2022/11/18
     */
    public static String translateMysqlToOracle(String sql, SQLASTOutputVisitor visitor, VisitorFeature... features) {
        if (Objects.isNull(visitor)) {
            visitor =  new MySqlToOracleOutputVisitor(new StringBuffer(), false);
        }
        int featuresValue = 0;
        if (ArrayUtil.isNotEmpty(features)) {
            for (VisitorFeature feature : features) {
                visitor.config(feature, true);
                featuresValue |= feature.mask;
            }
        }
        visitor.setFeatures(featuresValue);
        SQLUtils.parseSingleStatement(sql, DbType.mysql).accept(visitor);
        return visitor.getAppender().toString();
    }
}
