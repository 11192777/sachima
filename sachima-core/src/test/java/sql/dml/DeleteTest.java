package sql.dml;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 20:07
 */
public class DeleteTest extends TestHelper {

    @Test   // delete in limit 1000
    public void case0() {
        String sql = "delete from user as u where u.id in (1, 2, 3, 4, 5) and name in ('zhangsan', 'wangwu')";
        eq(toOracleLimit3(sql), "DELETE FROM user u\n" +
                "WHERE (1, u.id) IN ((1, 1), (1, 2), (1, 3), (1, 4), (1, 5))\n" +
                "\tAND name IN ('zhangsan', 'wangwu')", true);
    }

    @Test
    public void case1() {
        String sql = "delete from user as u where u.id in (1, 2) and name in ('zhangsan', 'wangwu')";
        eq(toOracleLimit3(sql), "DELETE FROM user u\n" +
                "WHERE u.id IN (1, 2)\n" +
                "\tAND name IN ('zhangsan', 'wangwu')", true);
    }

    @Test
    public void case3() {
        String sql = "DELETE FROM `ea_evidence_chain_template`;\n";
        eq(sql, "DELETE FROM ea_evidence_chain_template;");
    }

}
