package sql.dml;

import custom.TestHelper;
import org.junit.Test;


/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/15 19:18
 */
public class SelectBooleanValueTest extends TestHelper {


    @Test   //boolean adapter... md
    public void case0() {
        String sql = "select * from user where is_enabled = false;";
        eq(sql, "select * from USER where is_enabled = 0;");
    }

    @Test   //boolean adapter... md
    public void case1() {
        String sql = "select * from user where is_enabled != false;";
        eq(sql, "select * from USER where is_enabled != 0;");
    }

    @Test   //boolean adapter... md
    public void case2() {
        String sql = "select * from user where is_enabled = true;";
        eq(sql, "select * from USER where is_enabled = 1;");
    }

    @Test   //boolean adapter... md
    public void case3() {
        String sql = "select * from user where is_enabled != true;";
        eq(sql, "select * from USER where is_enabled != 1;");
    }
}
