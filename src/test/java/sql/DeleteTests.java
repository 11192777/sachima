package sql;

import custom.TestHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 20:07
 */
public class DeleteTests extends TestHelper {

    private static final Log LOG = LogFactory.getLog(DeleteTests.class);

    @Test   // delete in limit 1000
    public void case0() {
        String sql = "delete from user as u where u.id in (1, 2, 3, 4, 5) and name in ('zhangsan', 'wangwu')";
        LOG.info(sql);
        println(mysqlToOracle(sql));
    }


    @Test
    public void case1() {
        String sql = "delete from user as u where u.id in (1, 2) and name in ('zhangsan', 'wangwu')";
        println(mysqlToOracle(sql));
    }

}
