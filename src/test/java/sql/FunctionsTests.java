package sql;

import org.zaizai.sachima.sql.ast.SQLStatement;
import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/17 19:12
 */
public class FunctionsTests extends TestHelper {


    @Test   //CHAR_LENGTH -> LENGTH
    public void case0() {
        String sql = "SELECT CHAR_LENGTH(name) FROM ea_form WHERE id = 1;\n";
        println(mysqlToOracle(sql));
    }


    @Test   //源码: CONCAT -> NVL ??? TODO mgb 瞎搞(NVL仅支持两个参数) CONCAT -> ||  更新后: CONCAT(?, ?, ?) -> (?||?||?)
    public void case1() {
        String sql = "SELECT CONCAT(`name`, '???', id) FROM ea_form WHERE id = 1;\n";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }


    @Test   //CHARACTER_LENGTH -> LENGTH
    public void case2() {
        String sql = "SELECT CHARACTER_LENGTH(name) FROM ea_form WHERE id = 1;\n";
        println(mysqlToOracle(sql));
    }


    @Test   //IFNULL(field, 'var') -> VAL(field, 'var')
    public void case3() {
        String sql = "select IFNULL(parent_id, 'dododo') from ea_form;\n";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }


    @Test   //order by field -> order by decode
    public void case4() {
        String sql = "select * from user where id in (1, 2, 3) order by field(id, 3, 1, 2)";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }


    @Test   //remove binary function
    public void case5() {
        String sql = "select * from user where name =  binary('zhanSan')";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }


    @Test
    public void case6() {
        String sql = "select * from user where name =  binary('zhanSan') and id = 123 and code = 'zzzz'";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }


    @Test
    public void case7() {

    }


    @Test
    public void case8() {

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
