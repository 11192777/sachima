package custom;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.adapter.bean.TabColumn;
import org.zaizai.sachima.sql.adapter.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.PrimaryKeyHandler;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.util.StringUtils;
import sql.dml.DeleteTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 18:04
 */
public class TestHelper {

    protected final MySqlToOracleAdapterImpl handler = new MySqlToOracleAdapterImpl();

    @Before
    public void initPrimaryTypeHandler() {
        HashMap<String, String> primaryKeyMap = new HashMap<>();
        primaryKeyMap.put("EA_FORM", "UID");
        primaryKeyMap.put("ea_tenant_config", "id");
        primaryKeyMap.put("sachima", "id");
        PrimaryKeyHandler primaryKeyHandler = PrimaryKeyHandler.build(primaryKeyMap);
        primaryKeyHandler.setIdGenerator(() -> 10000L);
        handler.setPrimaryKeyHandler(primaryKeyHandler);
    }

    @Before
    public void initDataTypeHandler() {
        ArrayList<TabColumn> list = new ArrayList<>();
        list.add(new TabColumn("ea_tenant_config", "created_date", "DATE"));
        list.add(new TabColumn("TIME_STAMP_TABLE", "created_date", "TIMESTAMP"));
        list.add(new TabColumn("sachima", "remark", "NCLOB"));
        handler.setColumnTypeHandler(ColumnTypeHandler.build(list));
    }


    private static final Log LOG = LogFactory.getLog(DeleteTest.class);

    public void eq(String sql, String target) {
        eq(sql, target, false);
    }

    public void eq(String sql, String target, boolean alreadyOracleSql) {
        if (!alreadyOracleSql) {
            sql = this.mysqlToOracle(sql);
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

    public String mysqlToOracle(String sql) {
        return this.mysqlToOracle(sql, handler::translateMysqlToOracle);
    }

    public String mysqlToOracle(String sql, Function<String, String> fun) {
        return fun.apply(sql);
    }

    public SQLStatement getStatement(String sql) {
        return SQLUtils.parseSingleStatement(sql, DbType.mysql);
    }

    public String toOracleLimit3(String sql) {
        StringBuilder appender = new StringBuilder();
        SQLUtils.parseSingleStatement(sql, DbType.mysql).accept(new MySqlToOracleOVLimit3(appender));
        return appender.toString();
    }
}
