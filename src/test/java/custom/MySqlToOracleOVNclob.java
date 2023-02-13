package custom;

import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlToOracleOutputVisitor;
import org.zaizai.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

}
