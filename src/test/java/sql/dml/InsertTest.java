package sql.dml;

import custom.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.PrimaryKeyHandler;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 19:32
 */
public class InsertTest extends TestHelper {

    @Test   //"insert into ... value ..." -> "insert into ... values ..."
    public void case0() {
        String sql = "INSERT INTO t_user (id, name) VALUE (2, 'zhangsan');";
        eq(sql, "insert into t_user (id, name) values (2, 'zhangsan');");
    }

    @Test
    public void case1() {
        String sql = " INSERT INTO ea_tenant_config (property_key, property_value, created_by, created_date, last_modified_by, last_modified_date, tenant_id, is_deleted )\n" +
                "      VALUES\n" +
                "      ( 'IS_SYN_MATCH_CONFIG', 'false', - 1, '2022-12-29 10:55:16', - 1, '2022-12-29 10:55:16', - 1, FALSE )";
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case2() {
        String sql = " INSERT INTO ea_tenant_config (property_key, property_value, created_by, created_date, last_modified_by, last_modified_date, tenant_id, is_deleted )\n" +
                "      VALUES\n" +
                "      ( 'IS_SYN_MATCH_CONFIG', 'false', - 1, '2022-12-29 10:55:16', - 1, '2022-12-29 10:55:16', - 1, FALSE )";
        HashMap<String, String> map = new HashMap<>();
        map.put("ea_tenant_config", "id");
        PrimaryKeyHandler.apply(map, UUID::randomUUID);
        ArrayList<ColumnTypeHandler.ColumnType> list = new ArrayList<>();
        list.add(new ColumnTypeHandler.ColumnType("ea_tenant_config", "created_date", "DATE"));
        ColumnTypeHandler.apply(list);
        eqPrint(mysqlToOracle(sql), "INSERT INTO ea_tenant_config\n" +
                "\t(property_key, property_value, created_by, created_date, last_modified_by\n" +
                "\t, last_modified_date, tenant_id, is_deleted, id)\n" +
                "VALUES ('IS_SYN_MATCH_CONFIG', 'false', -1, TO_DATE('2022-12-29 10:55:16', 'yyyy-mm-dd hh24:mi:ss'), -1\n" +
                "\t, '2022-12-29 10:55:16', -1, 0, '10000')");
    }

    @Test
    public void case3() {
        String sql = " INSERT INTO `ea_tenant_config` (property_key, property_value, created_by, created_date, last_modified_by, last_modified_date, tenant_id, is_deleted )\n" +
                "      VALUES\n" +
                "      ( 'IS_SYN_MATCH_CONFIG', 'false', - 1, '2022-12-29 10:55:16', - 1, '2022-12-29 10:55:16', - 1, FALSE )";
        eq(mysqlToOracle(sql), "INSERT INTO ea_tenant_config\n" +
                "\t(property_key, property_value, created_by, created_date, last_modified_by\n" +
                "\t, last_modified_date, tenant_id, is_deleted, id)\n" +
                "VALUES ('IS_SYN_MATCH_CONFIG', 'false', -1, TO_DATE('2022-12-29 10:55:16', 'yyyy-mm-dd hh24:mi:ss'), -1\n" +
                "\t, '2022-12-29 10:55:16', -1, 0, '10000')");
    }

    @Test
    public void case4() {
        String sql = "INSERT INTO user (is_enabled) values (0)";
        eq(sql, "INSERT INTO user (is_enabled) values (0)");
    }

    @Test
    public void case5() {
        String sql = "insert into TEST (id, NAME) values (1, 'aa'), (2, 'dd'), (3, 'cc');";
        eq(sql, "INSERT ALL \n" +
                "INTO TEST (id, NAME) VALUES (1, 'aa')\n" +
                "INTO TEST (id, NAME) VALUES (2, 'dd')\n" +
                "INTO TEST (id, NAME) VALUES (3, 'cc')\n" +
                "SELECT 1 FROM DUAL;");
    }
    
    @Test
    public void case6() {
        String sql = "insert into EA_FORM (id, NAME) values (1, 'aa'), (2, 'dd'), (3, 'cc');";
        eq(sql, "INSERT ALL \n" +
                "INTO EA_FORM (id, NAME, UID) VALUES (1, 'aa', '10000')\n" +
                "INTO EA_FORM (id, NAME, UID) VALUES (2, 'dd', '10000')\n" +
                "INTO EA_FORM (id, NAME, UID) VALUES (3, 'cc', '10000')\n" +
                "SELECT 1 FROM DUAL;");
    }

    @Test
    public void case7() {
        String sql = "insert into TIME_STAMP_TABLE (created_date) values ('2022-12-22 12:22:12')";
        eq(sql, "INSERT INTO TIME_STAMP_TABLE (created_date)\n" +
                "VALUES (TO_DATE('2022-12-22 12:22:12', 'yyyy-mm-dd hh24:mi:ss'))");
    }

    @Test
    public void case8() {
        String sql = "insert into TIME_STAMP_TABLE (`created_date`) values ('2022-12-22 12:22:12')";
        eq(sql, "INSERT INTO TIME_STAMP_TABLE (created_date)\n" +
                "VALUES (TO_DATE('2022-12-22 12:22:12', 'yyyy-mm-dd hh24:mi:ss'))");
    }

    @Test
    public void case9() {
        String sql = "insert into null_test_table (id, name) values (1, NULL)";
        eq(sql, "INSERT INTO null_test_table (id)\n" +
                "VALUES (1)");
    }

    @Test
    public void case10() {
        String sql = "INSERT INTO EXAMPLE (ID, NAME) VALUES (1000, NULL), (1001, 'ZhangSan');";
        eq(sql, "INSERT ALL \n" +
                "INTO EXAMPLE (ID) VALUES (1000)\n" +
                "INTO EXAMPLE (ID, NAME) VALUES (1001, 'ZhangSan')\n" +
                "SELECT 1 FROM DUAL;");
    }
}
