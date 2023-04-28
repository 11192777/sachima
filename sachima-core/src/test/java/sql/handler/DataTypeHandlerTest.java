package sql.handler;

import custom.TestHelper;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/3/23 11:23
 */
public class DataTypeHandlerTest extends TestHelper {

    //@Test
    //public void case0 () {
    //    System.out.println(ColumnTypeHandler.print());
    //
    //    String var0 = "CREATE TABLE sachima (" +
    //            " name varchar(20) NULL\n" +
    //            ");";
    //    ColumnTypeHandlerOld.refresh(getStatement(var0));
    //    System.out.println(ColumnTypeHandlerOld.print());
    //    assert ColumnTypeHandlerOld.contains("sachima", "name", TokenFnvConstants.VARCHAR);
    //
    //    String var1 = "ALTER TABLE sachima\n" +
    //            " CHANGE name createdDate timestamp NULL COMMENT 'com';";
    //    ColumnTypeHandlerOld.refresh(getStatement(var1));
    //    System.out.println(ColumnTypeHandlerOld.print());
    //    assert !ColumnTypeHandlerOld.contains("sachima", "name", TokenFnvConstants.VARCHAR);
    //    assert ColumnTypeHandlerOld.contains("sachima", "createdDate", TokenFnvConstants.TIMESTAMP);
    //
    //
    //    String var2 = "ALTER TABLE sachima DROP COLUMN createdDate;";
    //    ColumnTypeHandlerOld.refresh(getStatement(var2));
    //    System.out.println(ColumnTypeHandlerOld.print());
    //    assert !ColumnTypeHandlerOld.contains("sachima", "createdDate", TokenFnvConstants.TIMESTAMP);
    //}

}
