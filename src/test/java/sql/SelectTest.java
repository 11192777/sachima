package sql;

import custom.TestHelper;
import org.junit.Test;

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

}
