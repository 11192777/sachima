package util;

import org.junit.Test;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.util.JDBCUtils;

import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/10 11:30
 */
public class JDBCUtilsTest {

    @Test
    public void case1() {
        String url = "jdbc:mysql://192.168.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&rewriteBatchedStatements=true";
        String owner = JDBCUtils.getOwner(url);
        Assert.isTrue(Objects.equals(owner, "test"));
    }

    @Test
    public void case2() {
        String url = "jdbc:mysql://192.168.0.1:3306/test";
        String owner = JDBCUtils.getOwner(url);
        Assert.isTrue(Objects.equals(owner, "test"));
    }

    @Test
    public void case3() {
        String url = "jdbc:oracle:thin:@192.168.0.1:23232/ORACLE_DB";
        String owner = JDBCUtils.getOwner(url);
        Assert.isTrue(Objects.equals(owner, "ORACLE_DB"));
    }
}
