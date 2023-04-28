package util;

import org.junit.Assert;
import org.junit.Test;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.util.SQLUtils;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/2/15 22:22
 */
public class SQLUtilsTest {

    @Test
    public void case1() {
        String sql = "select * from mysql where user = 'admin';";
        Assert.assertEquals("select * from mysql where user = 'admin' ", SQLUtils.removeLastSeparator(sql, DbType.mysql));
    }

    @Test
    public void case2() {
        String sql = "select * from mysql where user = 'admin';\n\t  ";
        Assert.assertEquals("select * from mysql where user = 'admin' \n\t  ", SQLUtils.removeLastSeparator(sql, DbType.mysql));
    }

    @Test
    public void case3() {
        String sql = "select * from mysql where user = 'admin'; 1=1";
        Assert.assertEquals("select * from mysql where user = 'admin'; 1=1", SQLUtils.removeLastSeparator(sql, DbType.mysql));
    }

}
