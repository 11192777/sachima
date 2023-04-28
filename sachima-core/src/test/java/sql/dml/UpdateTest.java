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
        eq(sql, "update USER u set u.name = 'zhangsan' where u.id in (1, 2, 3, 4, 5) and name in ('zhangsan', 'wangwu');");
    }

    @Test
    public void case2() {
        String sql  ="UPDATE\n" +
                "    sachima AS sa\n" +
                "    INNER JOIN user AS us ON sa.name = us.name\n" +
                "SET\n" +
                "    sa.sex    = us.sex,\n" +
                "    sa.remark = 'Update from user'\n" +
                "WHERE\n" +
                "    sa.id = us.id;";
        eq(sql, "UPDATE (\n" +
                "    SELECT\n" +
                "        sa.sex sa__sex, us.sex us__sex, sa.remark sa__remark\n" +
                "    FROM sachima sa\n" +
                "    INNER JOIN USER us ON sa.name = us.name\n" +
                "    WHERE sa.id = us.id\n" +
                ")\n" +
                "SET sa__sex = us__sex, sa__remark = 'Update from user'\n" +
                "WHERE 1=1;");
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
