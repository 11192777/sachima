package custom;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.exception.FastsqlColumnAmbiguousException;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.util.SQLAdaptHelper;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;
import sql.DeleteTest;

import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 18:04
 */
public class TestHelper {

    private static final Log LOG = LogFactory.getLog(DeleteTest.class);

    public static void eq(String sql, String target) {
        eq(sql, target, false);
    }

    public static void eq(String sql, String target, boolean alreadyOracleSql) {
        if (!alreadyOracleSql) {
            sql = mysqlToOracle(sql);
        }
        if (Objects.equals(sql, target)) {
            LOG.info(StringUtils.format("[YES] ====> SUCCESS"));
        } else {
            LOG.error(StringUtils.format("[NO] ====> SQL:\n{}", sql));
            String format = StringUtils.format("Not in line with expectations ==>> \n " +
                    "\t\tExpectant:{}\n \t\tResultSQL:{}\n", target, sql);
            throw new IllegalArgumentException(format);
        }
    }

    public static String mysqlToOracle(String sql) {
        return SQLAdaptHelper.translateMysqlToOracle(sql);
    }

    public static SQLStatement getStatement(String sql) {
        return SQLUtils.parseSingleStatement(sql, DbType.mysql);
    }

    public static String toOracleLimit3(String sql) {
        StringBuilder appender = new StringBuilder();
        SQLUtils.parseSingleStatement(sql, DbType.mysql).accept(new MySqlToOracleOVLimit3(appender));
        return appender.toString();
    }

    public static String toOracleNclob(String sql) {
        return SQLAdaptHelper.translateMysqlToOracle(sql, new MySqlToOracleOVNclob(new StringBuffer(), false));
    }
}
