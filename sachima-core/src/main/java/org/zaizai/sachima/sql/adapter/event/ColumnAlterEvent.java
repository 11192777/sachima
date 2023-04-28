package org.zaizai.sachima.sql.adapter.event;

import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.statement.SQLAlterTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/1 15:25
 */
public interface ColumnAlterEvent extends Event{

    void onStatement(SQLStatement x);

    void onStatement(SQLAlterTableStatement x);

    void onStatement(MySqlCreateTableStatement x);

}
