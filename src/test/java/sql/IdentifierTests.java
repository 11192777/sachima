package sql;

import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.parser.Keywords;
import org.zaizai.sachima.sql.parser.Token;
import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/21 11:40
 */
public class IdentifierTests extends TestHelper {


    @Test   //" -> '
    public void case0() {
        String sql = "SELECT * FROM user WHERE id IN (\"1\", \"2\", \"3\")";
        print(mysqlToOracle(sql));
    }

    @Test   //select number, size -> select "ID", "NUMBER", "SIZE" from "USER"
    public void case1() {
        String sql = "select id, number, size from user";
        SQLStatement statement = getStatement(sql);
        print(mysqlToOracle(sql));
    }

    @Test   //out -> select u."ID", u."NUMBER", u."SIZE" from "USER" u
    public void case2() {
        String sql = "select u.id, u.number, u.size from user as u";
        SQLStatement statement = getStatement(sql);
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case3() {
        String sql = "select u.`id`, u.`number`, u.size from user as u";
        SQLStatement statement = getStatement(sql);
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case4() {
        String sql = "SELECT\n" +
                "            id\n" +
                "        FROM\n" +
                "            ea_initialization_change_log\n" +
                "        WHERE\n" +
                "            id = ?";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }

    @Test   //as `key` -> as key
    public void case5() {
        String sql = "select ea.id as `key`, count(ead.id) as `value` from ea_archive ea inner join ea_archive_document ead on ea.id = ead.archive_id and ead.tenant_id = 1534382837723181057 inner join ea_document ed on ed.id = ead.document_id and ed.is_deleted = 0 and ed.tenant_id = 1534382837723181057 inner join ea_document_attachment eda on ed.id = eda.document_id and eda.is_deleted = 0 and eda.tenant_id = 1534382837723181057 where ea.id in (?, ?, ?) and ea.is_deleted = 0 and ea.tenant_id = 1534382837723181057 group by ea.id\n";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }

    @Test   // as `number` -> as "NUMBER"
    public void case6() {
        String sql = "select ea.id as `number`, count(ead.id) as `value` from ea_archive ea inner join ea_archive_document ead on ea.id = ead.archive_id and ead.tenant_id = 1534382837723181057 inner join ea_document ed on ed.id = ead.document_id and ed.is_deleted = 0 and ed.tenant_id = 1534382837723181057 inner join ea_document_attachment eda on ed.id = eda.document_id and eda.is_deleted = 0 and eda.tenant_id = 1534382837723181057 where ea.id in (?, ?, ?) and ea.is_deleted = 0 and ea.tenant_id = 1534382837723181057 group by ea.id\n";
        SQLStatement statement = getStatement(sql);
        println(mysqlToOracle(sql));
    }
}
