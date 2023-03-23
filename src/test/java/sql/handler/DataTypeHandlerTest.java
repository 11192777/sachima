package sql.handler;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/3/23 11:23
 */
public class DataTypeHandlerTest extends TestHelper {

    @Test
    public void case0 () {
        System.out.println(ColumnTypeHandler.print());

        String var0 = "CREATE TABLE sachima (" +
                " name varchar(20) NULL\n" +
                ");";
        ColumnTypeHandler.refresh(getStatement(var0));
        System.out.println(ColumnTypeHandler.print());
        assert ColumnTypeHandler.contains("sachima", "name", TokenFnvConstants.VARCHAR);

        String var1 = "ALTER TABLE sachima\n" +
                " CHANGE name createdDate timestamp NULL COMMENT 'com';";
        ColumnTypeHandler.refresh(getStatement(var1));
        System.out.println(ColumnTypeHandler.print());
        assert !ColumnTypeHandler.contains("sachima", "name", TokenFnvConstants.VARCHAR);
        assert ColumnTypeHandler.contains("sachima", "createdDate", TokenFnvConstants.TIMESTAMP);


        String var2 = "ALTER TABLE sachima DROP COLUMN createdDate;";
        ColumnTypeHandler.refresh(getStatement(var2));
        System.out.println(ColumnTypeHandler.print());
        assert !ColumnTypeHandler.contains("sachima", "createdDate", TokenFnvConstants.TIMESTAMP);
    }

}
