package org.zaizai.sachima.sql.adapter;

import org.apache.ibatis.executor.statement.StatementHandler;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/1 17:06
 */
public interface MySqlToOracleAdapter {

    String translateMysqlToOracleOnLiquibase(String sql);

    String translateMysqlToOracleOnMyBaitsPlus(String sql);

    void translateMysqlToOracleOnMyBaitsPlus(StatementHandler sh);

}
