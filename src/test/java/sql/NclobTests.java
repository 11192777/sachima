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
        eq(toOracleNclob(sql), "select * from test.user u where TO_CHAR(u.name) = 'zhangSan'", true);
    }

    @Test
    public void case1() {
        String sql = "select * from user as u where u.name = 'zhangSan'";
        eq(toOracleNclob(sql), "select * from \"USER\" u where TO_CHAR(u.name) = 'zhangSan'", true);
    }

    @Test
    public void case2() {
        String sql = "select * from test.user as u where name = 'zhangSan'";
        eq(toOracleNclob(sql), "select * from test.user u where TO_CHAR(name) = 'zhangSan'", true);
    }

    @Test
    public void case3() {
        String sql = "select * from user where name = 'zhangSan'";
        eq(toOracleNclob(sql), "select * from \"USER\" where TO_CHAR(name) = 'zhangSan'", true);
    }

    @Test
    public void case4() {
        String sql = "select * from user where code = 'zhangSan'";
        eq(toOracleNclob(sql), "select * from \"USER\" where TO_CHAR(code) = 'zhangSan'", true);
    }
}
