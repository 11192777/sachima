package sql.dml;

import custom.TestHelper;
import org.junit.Test;

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
        String sql = "select * from dba.sachima as s where s.remark = 'zhangSan'";
        eq(sql, "SELECT *\n" +
                "FROM dba.sachima s\n" +
                "WHERE TO_CHAR(s.remark) = 'zhangSan'");
    }

    @Test
    public void case1() {
        String sql = "select * from sachima as s where s.remark = 'zhangSan'";
        eq(sql, "SELECT *\n" +
                "FROM sachima s\n" +
                "WHERE TO_CHAR(s.remark) = 'zhangSan'");
    }

    @Test
    public void case2() {
        String sql = "select * from sachima as s where remark = 'zhangSan'";
        eq(sql, "SELECT *\n" +
                "FROM sachima s\n" +
                "WHERE TO_CHAR(remark) = 'zhangSan'");
    }


    @Test
    public void case3() {
        String sql = "select * from sachima where remark = 'zhangSan'";
        eq(sql, "SELECT *\n" +
                "FROM sachima\n" +
                "WHERE TO_CHAR(remark) = 'zhangSan'");
    }

    @Test
    public void case4() {
        String sql = "select * from sachima where name = 'zhangSan'";
        eq(sql, "SELECT *\n" +
                "FROM sachima\n" +
                "WHERE name = 'zhangSan'");
    }
}
