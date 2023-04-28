package sql.ddl;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 12:10
 */
public class DropIndexTest extends TestHelper {



    @Test
    public void case1() {
        String sql = "DROP INDEX EARCHIVESTEST.idx__document__tenant_id_created_date";
   
        System.out.println(mysqlToOracle(sql));
    }

}
