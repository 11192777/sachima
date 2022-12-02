package sql;

import org.zaizai.sachima.sql.ast.SQLStatement;
import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.util.MySqlUtils;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/21 11:45
 */
public class DateTests extends TestHelper {

    @Test   //TODO not adapt
    public void case0() {
        String sql = "select * from EA_FORM where CREATED_DATE = '2022-12-22 11:11:11';";
        print(mysqlToOracle(sql));
    }

    @Test   //NOW() -> SYSDATE
    public void case1() {
        String sql = "insert into ea_form (created_date) VALUES (now());";
        SQLStatement statement = getStatement(sql);
        print(mysqlToOracle(sql));
    }

    @Test
    public void case2() {
        String sql = "select DATE_FORMAT(created_date, '%Y-%m-%d %H:%i:%s') from user;";
        SQLStatement statement = getStatement(sql);
        print(mysqlToOracle(sql));
    }

    @Test   //hhhhhhh Oracle not adapt. even if : DATE_FORMAT(concat('', concat(created_date, '')), '%Y-%m-%d')
    public void case3() {
        String sql = "select DATE_FORMAT(concat(created_date, '', ''), '%Y-%m-%d') from user;";
        SQLStatement statement = getStatement(sql);
        boolean is = MySqlUtils.isKeyword("IS");
        print(mysqlToOracle(sql));
    }

    @Test
    public void case4() {

    }
}
