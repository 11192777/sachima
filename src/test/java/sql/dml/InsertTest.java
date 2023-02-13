package sql.dml;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.PrimaryKeyHandler;

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
        SQLStatement statement = getStatement(sql);
        System.out.println(statement);
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case2() {
        String sql = " INSERT INTO ea_tenant_config (property_key, property_value, created_by, created_date, last_modified_by, last_modified_date, tenant_id, is_deleted )\n" +
                "      VALUES\n" +
                "      ( 'IS_SYN_MATCH_CONFIG', 'false', - 1, '2022-12-29 10:55:16', - 1, '2022-12-29 10:55:16', - 1, FALSE )";
        SQLStatement statement = getStatement(sql);
        HashMap<String, String> map = new HashMap<>();
        map.put("ea_tenant_config", "id");
        PrimaryKeyHandler.apply(map, UUID::randomUUID);
        ArrayList<ColumnTypeHandler.ColumnType> list = new ArrayList<>();
        list.add(new ColumnTypeHandler.ColumnType("ea_tenant_config", "created_date", "DATE"));
        ColumnTypeHandler.apply(list);
        System.out.println("ss" + mysqlToOracle(sql));
    }

    @Test
    public void case3() {
    }

    @Test
    public void case4() {

    }

}
