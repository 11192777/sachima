package sql.ddl;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/10 19:46
 */
public class AlterColumnTest extends TestHelper {


    @Test
    public void case1() {
        String sql = "alter table ea_document modify original_number varchar(60) default '' null";
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case2() {
        String sql = "alter table ea_document modify original_number varchar(60) default '' null comment '原始资料号'";
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case3() {
        String sql = "comment on column EA_DOCUMENT.CREATED_BY is '创建人'";
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case4() {
        String sql = "        alter table ea_document modify document_type_id bigint null comment '资料类型id'";
        System.out.println(mysqlToOracle(sql));
    }

    @Test
    public void case5() {
    }
}
