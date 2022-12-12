package custom;

import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlToOracleOutputVisitor;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.NclobTypeHandler;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public NclobTypeHandler getNclobTypeHandler() {
        HashMap<String, Set<String>> map = new HashMap<>();
        map.put("user", Stream.of("name", "CodE").collect(Collectors.toSet()));
        return new NclobTypeHandler(map);
    }
}
