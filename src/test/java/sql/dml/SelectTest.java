package sql.dml;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.util.SQLAdaptHelper;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 20:05
 */
public class SelectTest extends TestHelper {

    @Test   //select ... from common params
    public void case0() {
        String sql = "select * from ea_user where id in (?, ?, ?)";
        eq(toOracleLimit3(sql), "SELECT *\n" +
                "FROM ea_user\n" +
                "WHERE (1, id) IN ((1, ?), (1, ?), (1, ?))", true);
    }

    @Test   //select
    public void case1() {
        String sql = "select * from ea_user where id in (1, 2, 3) and name in ('张三', '李四') limit 100, 10";
        eq(toOracleLimit3(sql), "SELECT *\n" +
                "FROM ea_user\n" +
                "WHERE (1, id) IN ((1, 1), (1, 2), (1, 3))\n" +
                "\tAND name IN ('张三', '李四')\n" +
                "OFFSET 100 ROWS FETCH FIRST 10 ROWS ONLY", true);
    }

    @Test
    public void case2() {
        String sql = "select * from user where id in (1, 2, 3, 4, 5) and name in ('张三', '李四') limit 100, 10";
        eq(toOracleLimit3(sql), "SELECT *\n" +
                "FROM \"USER\"\n" +
                "WHERE (1, id) IN ((1, 1), (1, 2), (1, 3), (1, 4), (1, 5))\n" +
                "\tAND name IN ('张三', '李四')\n" +
                "OFFSET 100 ROWS FETCH FIRST 10 ROWS ONLY", true);
    }

    @Test
    public void case3() {
        String sql = "select * from user where id in ('z', 'x') and name in ('张三', '李四') limit 100, 10";
        eq(toOracleLimit3(sql), "SELECT *\n" +
                "FROM \"USER\"\n" +
                "WHERE id IN ('z', 'x')\n" +
                "\tAND name IN ('张三', '李四')\n" +
                "OFFSET 100 ROWS FETCH FIRST 10 ROWS ONLY", true);
    }

    @Test
    public void case4() {
        String sql = "select id , name from user where id in (select id from other where id in (2, 3, 4)) and name in ('zhangsan', 'wangwu')";
        eq(toOracleLimit3(sql), "SELECT id, name\n" +
                "FROM \"USER\"\n" +
                "WHERE id IN (\n" +
                "\t\tSELECT id\n" +
                "\t\tFROM other\n" +
                "\t\tWHERE (1, id) IN ((1, 2), (1, 3), (1, 4))\n" +
                "\t)\n" +
                "\tAND name IN ('zhangsan', 'wangwu')", true);
    }

    @Test
    public void case5() {
        String sql = "Select Id , name from User As u inner join Dogs as d ON u.id = d.user_id where u.id in ('1', 2, '3', '4')";
        eq(toOracleLimit3(sql), "SELECT Id, name\n" +
                "FROM \"USER\" u\n" +
                "\tINNER JOIN Dogs d ON u.id = d.user_id\n" +
                "WHERE (1, u.id) IN ((1, '1'), (1, 2), (1, '3'), (1, '4'))", true);
    }


    @Test
    public void case6() {
        String sql = "select * from user as u where u.id in (?, ?, ?)";
        eq(toOracleLimit3(sql), "SELECT *\n" +
                "FROM \"USER\" u\n" +
                "WHERE (1, u.id) IN ((1, ?), (1, ?), (1, ?))", true);
    }


    @Test
    public void case7() {
        String sql = "select * from user as u inner join dog as d on u.id = d.user_id and d.id in (1, 2, 3, 4) where u.id in (?, ?, ?)";
        eq(toOracleLimit3(sql), "select * from \"USER\" u inner join dog d on u.id = d.user_id and (1, d.id) in ((1, 1), (1, 2), (1, 3), (1, 4)) where (1, u.id) in ((1, ?), (1, ?), (1, ?))");
    }

    @Test
    public void case8() {
        String sql = "SELECT etsj.* FROM ea_tenant_syn_job AS etsj WHERE etsj.form_code IS not NULL;";
        eq("SELECT etsj.*\n" +
                "FROM ea_tenant_syn_job etsj\n" +
                "WHERE etsj.form_code IS NOT NULL", mysqlToOracle(sql, SQLAdaptHelper::translateMysqlToOracleOnLiquibaseAndMyBaitsPlus));
    }

    @Test
    public void case9() {
        String sql = "select * from ea_form_field WHERE widget_type_property LIKE ?;";
        eq("SELECT *\n" +
                "FROM ea_form_field\n" +
                "WHERE widget_type_property LIKE ?", mysqlToOracle(sql, SQLAdaptHelper::translateMysqlToOracleOnLiquibaseAndMyBaitsPlus));
    }

    @Test
    public void case10() {
        String sql = "SELECT id, is_enabled, imap_host, imap_port, is_enabled_ssl, user_name, password, created_by, created_date, last_modified_by, last_modified_date, tenant_id FROM ea_mail_collect_task WHERE tenant_id = 1534382837723181057";
        eq(sql, "SELECT id, is_enabled, imap_host, imap_port, is_enabled_ssl\n" +
                "\t, user_name, \"PASSWORD\", created_by, created_date, last_modified_by\n" +
                "\t, last_modified_date, tenant_id\n" +
                "FROM ea_mail_collect_task\n" +
                "WHERE tenant_id = 1534382837723181057");
    }

}
