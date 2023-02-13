package sql.dml;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 20:06
 */
public class UpdateTest extends TestHelper {


    @Test
    public void case0() {
        String sql = "update user as u set u.name = 'zhangsan' where u.id in (1, 2, 3, 4, 5) and name in ('zhangsan', 'wangwu');";
        eq(sql, "update \"USER\" u set u.name = 'zhangsan' where u.id in (1, 2, 3, 4, 5) and name in ('zhangsan', 'wangwu');");
    }

    @Test
    public void case2() {
        String sql  ="UPDATE\n" +
                "            ea_user_often_field         AS euof\n" +
                "            INNER JOIN ea_form_field    AS eff ON euof.field_id = eff.id\n" +
                "        SET\n" +
                "            euof.widget_type = eff.widget_type, name = 'ZhangSan'" +
                "      WHERE\n" +
                "            euof.widget_type != eff.widget_type";
        System.out.println(mysqlToOracle(sql));
    }
}
