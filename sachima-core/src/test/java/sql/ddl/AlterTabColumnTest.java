package sql.ddl;

import custom.TestHelper;
import org.junit.Test;
import org.zaizai.sachima.sql.ast.SQLStatement;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/10 19:46
 */
public class AlterTabColumnTest extends TestHelper {


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
        String sql = "alter table sachima add column code varchar(100) not null;\n";
        SQLStatement statement = getStatement(sql);
        System.out.println(statement);
    }

    @Test
    public void case6() {
        String sql = "ALTER TABLE sachima DROP code;";
        SQLStatement statement = getStatement(sql);
        System.out.println(statement);
    }

    @Test
    public void case7() {
        String sql = "ALTER TABLE sachima CHANGE ori new varchar(200) NULL COMMENT 'com';";
        SQLStatement statement = getStatement(sql);
        System.out.println(statement);
    }
}
