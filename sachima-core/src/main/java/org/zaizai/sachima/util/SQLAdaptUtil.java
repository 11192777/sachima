package org.zaizai.sachima.util;

import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapter;
import org.zaizai.sachima.sql.adapter.MySqlToOracleAdapterImpl;
import org.zaizai.sachima.sql.adapter.handler.ColumnTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.NonNullTypeHandler;
import org.zaizai.sachima.sql.adapter.handler.PrimaryKeyHandler;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/4 16:36
 */
public class SQLAdaptUtil {

    private SQLAdaptUtil() {
    }

    public static MySqlToOracleAdapter newInstance(DataSource dataSource, String owner) throws SQLException {
        MySqlToOracleAdapterImpl adapter = new MySqlToOracleAdapterImpl();
        adapter.setColumnTypeHandler(ColumnTypeHandler.build(dataSource, owner));
        adapter.setPrimaryKeyHandler(PrimaryKeyHandler.build(dataSource, owner));
        adapter.setNonNullTypeHandler(NonNullTypeHandler.build(dataSource, owner));
        return adapter;
    }

    public static MySqlToOracleAdapter newInstance() {
        return new MySqlToOracleAdapterImpl();
    }
}
