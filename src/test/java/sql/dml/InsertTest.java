package sql.dml;

import custom.TestHelper;
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
                "\t, '2022-12-29 10:55:16', -1, 0, '7c9db1cd-83c2-4ae6-902f-88489de784b0')");
    }

    @Test
    public void case3() {
        String sql = " INSERT INTO `ea_tenant_config` (property_key, property_value, created_by, created_date, last_modified_by, last_modified_date, tenant_id, is_deleted )\n" +
                "      VALUES\n" +
                "      ( 'IS_SYN_MATCH_CONFIG', 'false', - 1, '2022-12-29 10:55:16', - 1, '2022-12-29 10:55:16', - 1, FALSE )";
        HashMap<String, String> map = new HashMap<>();
        map.put("ea_tenant_config", "id");
        PrimaryKeyHandler.apply(map, UUID::randomUUID);
        eqPrint(mysqlToOracle(sql), "INSERT INTO ea_tenant_config\n" +
                "\t(property_key, property_value, created_by, created_date, last_modified_by\n" +
                "\t, last_modified_date, tenant_id, is_deleted, id)\n" +
                "VALUES ('IS_SYN_MATCH_CONFIG', 'false', -1, TO_DATE('2022-12-29 10:55:16', 'yyyy-mm-dd hh24:mi:ss'), -1\n" +
                "\t, '2022-12-29 10:55:16', -1, 0, '7c9db1cd-83c2-4ae6-902f-88489de784b0')");
    }

    @Test
    public void case4() {
        String sql = "INSERT INTO user (is_enabled) values (0)";
        eq(sql, "INSERT INTO user (is_enabled) values (0)");
    }

    @Test
    public void case5() {
        String sql = "insert into EA_FORM (id, NAME) values (1, 'ss'), (2, 'dd'), (3, 'cc');";
        eq(sql, "INSERT ALL INTO EA_FORM (id, NAME)\n" +
                "VALUES (1, 'ss') into EA_FORM values (2, 'dd') into EA_FORM values (3, 'cc') SELECT 1 FROM DUAL ;");
    }

}
