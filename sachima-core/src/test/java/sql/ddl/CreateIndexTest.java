package sql.ddl;

import custom.TestHelper;
import org.junit.Test;

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
   
        System.out.println(mysqlToOracle(sql));
    }

}
