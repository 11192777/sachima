package custom;


import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.util.SQLAdaptHelper;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 18:04
 */
public class TestHelper {

    public static void println(Object o, Object... params) {
        print(String.valueOf(o).concat("\n"), params);
    }

    public static void print(Object o, Object... params) {
        System.out.print(StringUtils.format(String.valueOf(o), params));
    }

    public static String mysqlToOracle(String sql) {
        return SQLAdaptHelper.translateMysqlToOracle(sql);
    }

    public static SQLStatement getStatement(String sql) {
        return SQLUtils.parseSingleStatement(sql, DbType.mysql);
    }

    public static List<SQLStatement> listStatements(String sql) {
        return SQLUtils.parseStatements(sql, DbType.mysql);
    }

}
