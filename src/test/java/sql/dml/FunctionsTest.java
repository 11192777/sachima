package sql.dml;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/17 19:12
 */
public class FunctionsTest extends TestHelper {


    @Test   //CHAR_LENGTH -> LENGTH
    public void case0() {
        String sql = "SELECT CHAR_LENGTH(name) FROM form WHERE id = 1;\n";
        eq(sql, "select LENGTH(name) from form where id = 1;");
    }


    @Test   //源码: CONCAT -> NVL ??? TODO mgb 瞎搞(NVL仅支持两个参数) CONCAT -> ||  更新后: CONCAT(?, ?, ?) -> (?||?||?)
    public void case1() {
        String sql = "SELECT CONCAT(`name`, '???', id) FROM ea_form WHERE id = 1;\n";
        eq(sql, "select (name||'???'||id) from ea_form where id = 1;");
    }


    @Test   //CHARACTER_LENGTH -> LENGTH
    public void case2() {
        String sql = "SELECT CHARACTER_LENGTH(name) FROM ea_form WHERE id = 1;\n";
        eq(sql, "select LENGTH(name) from ea_form where id = 1;");
    }


    @Test   //IFNULL(field, 'var') -> VAL(field, 'var')
    public void case3() {
        String sql = "select IFNULL(parent_id, 'dododo') from ea_form;\n";
        eq(sql, "select NVL(parent_id, 'dododo') from ea_form;");
    }


    @Test   //order by field -> order by decode
    public void case4() {
        String sql = "select * from user where id in (1, 2, 3) order by field(id, 3, 1, 2)";
        eq(sql, "select * from USER where id in (1, 2, 3) order by DECODE(id, 3, 1, 1, 3, 2, 5)");
    }


    @Test   //remove binary function
    public void case5() {
        String sql = "select * from user where name =  binary('zhanSan')";
        eq(sql, "select * from USER where name = 'zhanSan'");
    }


    @Test
    public void case6() {
        String sql = "select * from user where name =  binary('zhanSan') and id = 123 and code = 'zzzz'";
        eq(sql, "select * from USER where name = 'zhanSan' and id = 123 and code = 'zzzz'");
    }


    @Test   //left -> substr
    public void case7() {
        String sql = "select left(name, 10) from user;";
        eq(sql, "select SUBSTR(name, 0, 10) from USER;");
    }


    @Test
    public void case8() {
        String sql = "select concat(left(name, 10), ',' ,name) from user;";
        eq(sql, "select (SUBSTR(name, 0, 10)||','||name) from USER;");
    }


    @Test   //year -> to_char(?, 'yyyy')
    public void case9() {
        String sql = "select year(date) from user";
        eq(sql, "select TO_CHAR(date, 'yyyy') from USER");
    }


    @Test //month -> to_char(?, 'MM')
    public void case10() {
        String sql = "select year(date) from user";
        eq(sql, "select TO_CHAR(date, 'yyyy') from USER");
    }


    @Test
    public void case11() {
        String sql = "select sum(eds.statistic_count) as total, concat(year(eds.statistic_date), '-' , LPAD(month(eds.statistic_date), 2, '0')) AS res_date\n" +
                "from document_statistic eds\n" +
                "where eds.tenant_id = ?\n" +
                "  and eds.statistic_date >= ?\n" +
                "  and eds.statistic_date <= ?\n" +
                "  and eds.form_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n" +
                "  and eds.company_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n" +
                "  and eds.statistic_type in (?)\n" +
                "  and eds.tenant_id = 1534382837723181057\n" +
                "group by (year(eds.statistic_date) , '-', LPAD(month(statistic_date), 2, '0'))";
        eq(sql, "select sum(eds.statistic_count) as total , (TO_CHAR(eds.statistic_date, 'yyyy')||'-'||LPAD(TO_CHAR(eds.statistic_date, 'MM'), 2, '0')) as res_date from document_statistic eds where eds.tenant_id = ? and eds.statistic_date >= ? and eds.statistic_date <= ? and eds.form_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) and eds.company_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) and eds.statistic_type in (?) and eds.tenant_id = 1534382837723181057 group by (TO_CHAR(eds.statistic_date, 'yyyy'), '-', LPAD(TO_CHAR(statistic_date, 'MM'), 2, '0'))");
    }


    @Test
    public void case12() {
        String sql = "select * from user where id in (1, 3, 4, 2) order by field(id, 4, 3, 2, 1)";
        eq(sql, "select * from USER where id in (1, 3, 4, 2) order by DECODE(id, 4, 1, 3, 3, 2, 5, 1, 7)");
    }


    @Test
    public void case13() {
        String sql = "select concat(name, '-', code, concat('haha', id, 'zaizai')) from ea_form;";
        eq(sql, "SELECT (name||'-'||code||('haha'||id||'zaizai'))\n" +
                "FROM ea_form;");
    }


    @Test
    public void case14() {

    }


    @Test
    public void case15() {

    }


    @Test
    public void case16() {

    }


    @Test
    public void case17() {

    }


    @Test
    public void case18() {

    }


    @Test
    public void case19() {

    }

}
