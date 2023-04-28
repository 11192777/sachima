package org.zaizai.sachima.sql.ast;

import org.zaizai.sachima.sql.ast.statement.SQLCreateTableStatement;
import org.zaizai.sachima.sql.ast.statement.SQLTableSourceImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public class SQLAdhocTableSource extends SQLTableSourceImpl {
    private SQLCreateTableStatement definition;

    public SQLAdhocTableSource(SQLCreateTableStatement definition) {
        setDefinition(definition);
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            if (definition != null) {
                definition.accept(v);
            }
        }
        v.endVisit(this);
    }

    public SQLCreateTableStatement getDefinition() {
        return definition;
    }

    public void setDefinition(SQLCreateTableStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.definition = x;
    }
}
