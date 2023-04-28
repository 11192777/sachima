package sql.dml;

import custom.TestHelper;
import org.junit.Test;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 19:32
 */
public class TokenTest extends TestHelper {

    @Test   //remove `
    public void case0() {
        String sql = "SELECT `id`, `name` from `user` where `id` = '1'";
        eq(sql, "select id, name from user where id = '1'");
    }

    @Test   //remove `
    public void case1() {
        String sql = "select ef.id, ef.`name`, ef.type, ef.`code`, ef.is_enabled , ef.is_deleted, ef.tenant_id, ef.created_by, ef.created_date, ef.last_modified_by , ef.last_modified_date, ef.parent_id, ef.business_type, ef.sort_number, ef.attach_type_code , ef.retention_period, ef.encoding_field_id, ef.remark, ef.security_level_code, ef.is_paper from ea_form ef where ef.is_deleted = 0 and ef.parent_id in (?, ?, ?, ?) and ef.tenant_id = 1534382837723181057";
        eq(sql, "select ef.id, ef.name, ef.type, ef.code, ef.is_enabled , ef.is_deleted, ef.tenant_id, ef.created_by, ef.created_date, ef.last_modified_by , ef.last_modified_date, ef.parent_id, ef.business_type, ef.sort_number, ef.attach_type_code , ef.retention_period, ef.encoding_field_id, ef.remark, ef.security_level_code, ef.is_paper from ea_form ef where ef.is_deleted = 0 and ef.parent_id in (?, ?, ?, ?) and ef.tenant_id = 1534382837723181057");
    }

    @Test   //empty char
    public void case2() {
        String sql = "select * from user where name = ''";
        eq(sql, "SELECT *\n" +
                "FROM USER\n" +
                "WHERE name IS NULL");
    }

    @Test
    public void case3() {

    }

    @Test
    public void case4() {

    }

}
