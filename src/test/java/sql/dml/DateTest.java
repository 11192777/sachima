package sql.dml;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/21 11:45
 */
public class DateTest extends TestHelper {

    @Test   //TODO not adapt
    public void case0() {
        String sql = "select * from EA_FORM where CREATED_DATE = '2022-12-22 11:11:11';";
        eq(sql, "select * from EA_FORM where CREATED_DATE = '2022-12-22 11:11:11';");
    }

    @Test   //NOW() -> SYSDATE
    public void case1() {
        String sql = "insert into TEST (created_date) VALUES (now());";
        eq(sql, "insert into TEST (created_date) values (SYSDATE);");
    }

    @Test
    public void case2() {
        String sql = "select DATE_FORMAT(created_date, '%Y-%m-%d %H:%i:%s') from user;";
        eq(sql, "select TO_CHAR(created_date, 'yyyy-mm-dd hh24:mi:ss') from USER;");
    }

    @Test   //Oracle not adapt. even if : DATE_FORMAT(concat('', concat(created_date, '')), '%Y-%m-%d')
    public void case3() {
        String sql = "select DATE_FORMAT(concat(created_date, '', ''), '%Y-%m-%d') from user;";
        eq(sql, "select TO_CHAR((created_date||null||null), 'yyyy-mm-dd') from USER;");

    }

    @Test
    public void case4() {

    }
}
