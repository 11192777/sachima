package sql;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.util.StringUtils;

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
        eq(sql, "select * from \"USER\" where id in (1, 2, 3) order by DECODE(id, 3, 1, 2)");
    }


    @Test   //remove binary function
    public void case5() {
        String sql = "select * from user where name =  binary('zhanSan')";
        eq(sql, "select * from \"USER\" where name = 'zhanSan'");
    }


    @Test
    public void case6() {
        String sql = "select * from user where name =  binary('zhanSan') and id = 123 and code = 'zzzz'";
        eq(sql, "select * from \"USER\" where name = 'zhanSan' and id = 123 and code = 'zzzz'");
    }


    @Test   //left -> substr
    public void case7() {
        String sql = "select left(name, 10) from user;";
        eq(sql, "select SUBSTR(name, 0, 10) from \"USER\";");
    }


    @Test
    public void case8() {
        String sql = "select concat(left(name, 10), ',' ,name) from user;";
        eq(sql, "select (SUBSTR(name, 0, 10)||','||name) from \"USER\";");
    }


    @Test
    public void case9() {

    }


    @Test
    public void case10() {

    }


    @Test
    public void case11() {

    }


    @Test
    public void case12() {

    }


    @Test
    public void case13() {

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
