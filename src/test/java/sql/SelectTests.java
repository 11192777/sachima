package sql;

import org.zaizai.sachima.sql.ast.SQLStatement;
import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 20:05
 */
public class SelectTests extends TestHelper {

    @Test   //select ... from common params
    public void case0() {
        String sql = "select * from ea_user where id in (?, ?, ?)";
        println(mysqlToOracle(sql));
    }

    @Test   //select
    public void case1() {
        String sql = "select * from ea_user where id in (1, 2, 3) and name in ('张三', '李四') limit 100, 10";
        println(mysqlToOracle(sql));
    }

    @Test
    public void case2() {
        String sql = "select * from user where id in (1, 2, 3, 4, 5) and name in ('张三', '李四') limit 100, 10";
        println(sql);
        println(mysqlToOracle(sql));
    }

    @Test
    public void case3() {
        String sql = "select * from user where id in ('z', 'x') and name in ('张三', '李四') limit 100, 10";
        println(sql);
        println(mysqlToOracle(sql));
    }

    @Test
    public void case4() {
        String sql = "select id , name from user where id in (select id from other where id in (2, 3, 4)) and name in ('zhangsan', 'wangwu')";
        print(mysqlToOracle(sql));
    }

    @Test
    public void case5() {
        String sql = "Select Id , name from User As u inner join Dogs as d ON u.id = d.user_id where u.id in ('1', 2, '3', '4')";
        println(mysqlToOracle(sql));
    }


    @Test
    public void case6() {
        String sql = "select * from user as u where u.id in (?, ?, ?)";
        println(mysqlToOracle(sql));
    }


    @Test
    public void case7() {
        String sql = "select * from user as u inner join dog as d on u.id = d.user_id and d.id in (1, 2, 3, 4) where u.id in (?, ?, ?)";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }

}
