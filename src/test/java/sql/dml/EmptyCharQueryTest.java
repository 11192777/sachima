package sql.dml;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/21 11:43
 */
public class EmptyCharQueryTest extends TestHelper {


    @Test   // = '' -> IS NULL
    public void case0() {
        String sql = "SELECT * FROM user WHERE id = ''";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case1() {
        String sql = "SELECT * FROM user WHERE id != ''";
        eq(sql, "select * from \"USER\" where id is not null");
    }

    @Test
    public void case2() {
        String sql = "SELECT * FROM user WHERE id < ''";
        eq(sql, "select * from \"USER\" where id < null");
    }

    @Test
    public void case3() {
        String sql = "SELECT * FROM user WHERE id = \"\"";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case4() {
        String sql = "select * from user where id = ''";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case5() {
        String sql = "select * from user where id != ''";
        eq(sql, "select * from \"USER\" where id is not null");
    }

    @Test
    public void case6() {
        String sql = "select * from user where id = null";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case7() {
        String sql = "select * from user where id != null";
        eq(sql, "select * from \"USER\" where id is not null");
    }

    @Test
    public void case8() {
        String sql = "select * from user where id = ' '";
        eq(sql, "select * from \"USER\" where id = ' '");
    }

    @Test
    public void case9() {
        String sql = "select * from user where id != \"\"";
        eq(sql, "select * from \"USER\" where id is not null");
    }

    @Test
    public void case10() {
        String sql = "select * from user where id in (NULL)";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case11() {
        String sql = "select * from user where id not in (NULL)";

        eq(sql, "select * from \"USER\" where id is not null");
    }

    @Test
    public void case12() {
        String sql = "select * from user where id in (NULL)";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case13() {
        String sql = "select * from user where id in ('')";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case14() {
        String sql = "select * from user where id not in ('')";
        eq(sql, "select * from \"USER\" where id is not null");
    }

    @Test
    public void case15() {
        String sql = "select * from user where id in (\"\")";
        eq(sql, "select * from \"USER\" where id is null");
    }

    @Test
    public void case16() {
        String sql = "select * from user where id not in (\"\")";
        eq(sql, "select * from \"USER\" where id is not null");
    }
}
