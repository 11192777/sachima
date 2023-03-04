package custom;


import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;
import org.zaizai.sachima.util.SQLAdaptHelper;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sql.dml.DeleteTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 18:04
 */
public class TestHelper {

    private static final Log LOG = LogFactory.getLog(DeleteTest.class);

    public static void eqPrint(String sql, String target) {
        try {
            eq(sql, target);
        } catch (Exception e) {
            //skip
        }
    }

    public static void eq(String sql, String target) {
        eq(sql, target, false);
    }

    public static void eq(String sql, String target, boolean alreadyOracleSql) {
        if (!alreadyOracleSql) {
            sql = mysqlToOracle(sql);
        }
        if (sqlEquals(sql, target)) {
            LOG.info(StringUtils.format("[YES] ====> SUCCESS"));
        } else {
            LOG.error(StringUtils.format("[NO] ====> SQL:\n{}", sql));
            String format = StringUtils.format("Not in line with expectations ==>> \n " +
                    "\t\tExpectant:{}\n \t\tResultSQL:{}\n", target, sql);
            throw new IllegalArgumentException(format);
        }
    }

    public static boolean sqlEquals(String arg1, String arg2) {
        if (StringUtils.isEmpty(arg1)) {
            return false;
        }
        arg1 = arg1.replaceAll("\\s+", " ");
        arg2 = arg2.replaceAll("\\s+", " ");
        return StringUtils.equalsIgnoreCase(arg1, arg2);
    }

    public static String mysqlToOracle(String sql) {
        return mysqlToOracle(sql, SQLAdaptHelper::translateMysqlToOracle);
    }

    public static String mysqlToOracle(String sql, Function<String, String> fun) {
        return fun.apply(sql);
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
        HashMap<String, List<String>> tableColumnMap = new HashMap<>();
        tableColumnMap.put("user", Stream.of("name", "code").collect(Collectors.toList()));

        Map<String, Map<String, List<String>>> dataTypeMap = new HashMap<>();
        dataTypeMap.put("NCLOB", tableColumnMap);

        ColumnTypeHandler.apply(dataTypeMap);
        return SQLAdaptHelper.translateMysqlToOracle(sql, new MySqlToOracleOVNclob(new StringBuffer()));
    }
}
