package org.zaizai.sachima.sql.ast;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.statement.SQLSelectOrderByItem;

import java.util.List;

public interface SQLIndex extends SQLObject {
    List<SQLName> getCovering();
    List<SQLSelectOrderByItem> getColumns();
}
