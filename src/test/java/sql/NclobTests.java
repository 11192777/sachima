package sql;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/9 16:29
 */
public class NclobTests extends TestHelper {

    @Test
    public void case0() {
        String sql = "select * from test.user as u where u.name = 'zhangSan'";
        eq(toOracleNclob(sql), "SELECT *\n" +
                "FROM test.user u\n" +
                "WHERE TO_CHAR(u.name) = 'zhangSan'", true);
    }

    @Test
    public void case1() {
        String sql = "select * from user as u where u.name = 'zhangSan'";
        eq(toOracleNclob(sql), "SELECT *\n" +
                "FROM \"USER\" u\n" +
                "WHERE TO_CHAR(u.name) = 'zhangSan'", true);
    }

    @Test
    public void case2() {
        String sql = "select * from test.user as u where name = 'zhangSan'";
        eq(toOracleNclob(sql), "SELECT *\n" +
                "FROM test.user u\n" +
                "WHERE name = 'zhangSan'", true);
    }

    @Test
    public void case3() {
        String sql = "select * from user where name = 'zhangSan'";
        eq(toOracleNclob(sql), "SELECT *\n" +
                "FROM \"USER\"\n" +
                "WHERE name = 'zhangSan'", true);
    }

    @Test
    public void case4() {

    }
}
