package sql.ddl;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 11:47
 */
public class CreateIndexTest extends TestHelper {


    @Test
    public void case1() {
        String sql = "CREATE INDEX EARCHIVESTEST.idx__form__parent_id ON EARCHIVESTEST.ea_form(parent_id)";
        SQLStatement statement = getStatement(sql);
        System.out.println(statement);
        System.out.println(mysqlToOracle(sql));
    }

}
