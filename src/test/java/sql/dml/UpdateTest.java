package sql.dml;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;

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
        eq(sql, "update USER u set u.name = 'zhangsan' where u.id in (1, 2, 3, 4, 5) and name in ('zhangsan', 'wangwu');");
    }

    @Test
    public void case2() {
        String sql  ="UPDATE\n" +
                "            ea_user_often_field         AS euof\n" +
                "            INNER JOIN ea_form_field    AS eff ON euof.field_id = eff.id\n" +
                "        SET\n" +
                "            euof.widget_type = eff.widget_type, name = 'ZhangSan'" +
                "      WHERE\n" +
                "            euof.widget_type != ef" +
                "f.widget_type";
        System.out.println(sql);
    }

    @Test
    public void case3() {
        String sql = "update ea_archive set check_result = if(LENGTH(lack_doc_form_id)>2,1,2)";
        eq(sql, "UPDATE ea_archive\n" +
                "SET check_result = DECODE(SIGN(LENGTH(lack_doc_form_id) - 2), 1, 1, 2)");
    }

    @Test
    public void case4() {
        String sql = "update ea_archive set check_result = if(LENGTH(lack_doc_form_id)>=2,1,2)";
        eq(sql, "UPDATE ea_archive\n" +
                "SET check_result = DECODE(SIGN(LENGTH(lack_doc_form_id) - 2), -1, 2, 1)");
    }

}
