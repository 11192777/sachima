package custom;

import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlToOracleOutputVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/12/9 16:37
 */
public class MySqlToOracleOVNclob extends MySqlToOracleOutputVisitor {


    public MySqlToOracleOVNclob(Appendable appender) {
        super(appender);
    }

    public MySqlToOracleOVNclob(Appendable appender, boolean printPostSemi) {
        super(appender, printPostSemi);
    }

    @Override
    public Map<String, String> isNclobType() {
        HashMap<String, String> map = new HashMap<>();
        map.put("user", "name");
        return map;
    }
}
