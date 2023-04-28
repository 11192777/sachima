package org.zaizai.sachima.sql.adapter.visitor;

import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlObjectImpl;

/**
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/4/4 16:28
 */
public interface AdaptVisitor {

    default void visit(MySqlObjectImpl x) {

    }
}
