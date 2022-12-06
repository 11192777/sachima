package sql;

import custom.TestHelper;
import org.junit.Test;

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

    }

    @Test
    public void case2() {

    }

    @Test
    public void case3() {

    }

    @Test
    public void case4() {

    }

}
