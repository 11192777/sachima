/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaizai.sachima.sql.repository;

import org.zaizai.sachima.sql.ast.SQLDeclareItem;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.SQLOver;
import org.zaizai.sachima.sql.ast.SQLParameter;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.ast.statement.*;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import static org.zaizai.sachima.sql.repository.SchemaResolveVisitorFactory.resolve;
import static org.zaizai.sachima.sql.repository.SchemaResolveVisitorFactory.resolveIdent;
import static org.zaizai.sachima.sql.repository.SchemaResolveVisitorFactory.resolveUnion;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 03/08/2017.
 */
public interface SchemaResolveVisitor extends SQLASTVisitor {

    boolean isEnabled(Option option);
    int getOptions();

    public static enum Option {
        ResolveAllColumn,
        ResolveIdentifierAlias,
        CheckColumnAmbiguous
        ;
        private Option() {
            mask = (1 << ordinal());
        }

        public final int mask;

        public static int of(Option... options) {
            if (options == null) {
                return 0;
            }

            int value = 0;

            for (Option option : options) {
                value |= option.mask;
            }

            return value;
        }
    }

    SchemaRepository getRepository();

    Context getContext();
    Context createContext(SQLObject object);
    void popContext();

    static class Context {
        public final Context parent;
        public final SQLObject object;
        public final int level;

        private SQLTableSource tableSource;

        private SQLTableSource from;

        private Map<Long, SQLTableSource> tableSourceMap;

        protected Map<Long, SQLDeclareItem> declares;

        public Context(SQLObject object, Context parent) {
            this.object = object;
            this.parent = parent;
            this.level = parent == null
                    ? 0
                    : parent.level + 1;
        }

        public SQLTableSource getFrom() {
            return from;
        }

        public void setFrom(SQLTableSource from) {
            this.from = from;
        }

        public SQLTableSource getTableSource() {
            return tableSource;
        }

        public void setTableSource(SQLTableSource tableSource) {
            this.tableSource = tableSource;
        }

        public void addTableSource(long alias_hash, SQLTableSource tableSource) {
            if (tableSourceMap == null) {
                tableSourceMap = new HashMap<>();
            }

            tableSourceMap.put(alias_hash, tableSource);
        }

        protected void declare(SQLDeclareItem x) {
            if (declares == null) {
                declares = new HashMap<>();
            }
            declares.put(x.getName().nameHashCode64(), x);
        }

        protected SQLDeclareItem findDeclare(long nameHash) {
            if (declares == null) {
                return null;
            }
            return declares.get(nameHash);
        }

        protected SQLTableSource findTableSource(long nameHash) {
            SQLTableSource table = null;
            if (tableSourceMap != null) {
                table = tableSourceMap.get(nameHash);
            }

            return table;
        }

        protected SQLTableSource findTableSourceRecursive(long nameHash) {
            for (Context ctx = this; ctx != null; ctx = ctx.parent) {
                if (ctx.tableSourceMap != null) {
                    SQLTableSource table = ctx.tableSourceMap.get(nameHash);
                    if (table != null) {
                        return table;
                    }
                }
            }

            return null;
        }
    }

    default boolean visit(SQLSelectStatement x) {
        resolve(this, x.getSelect());
        return false;
    }

    default boolean visit(SQLSelect x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLWithSubqueryClause x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLIfStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLCreateFunctionStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLExprTableSource x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLSelectQueryBlock x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLForeignKeyImpl x) {
        resolve(this, x);
        return false;
    }


    default boolean visit(SQLIdentifierExpr x) {
        resolveIdent(this, x);
        return true;
    }

    default boolean visit(SQLPropertyExpr x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLBinaryOpExpr x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLAllColumnExpr x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLCreateTableStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLUpdateStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLDeleteStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLAlterTableStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLInsertStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLParameter x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLDeclareItem x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLOver x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLMethodInvokeExpr x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLUnionQuery x) {
        resolveUnion(this, x);
        return false;
    }

    default boolean visit(SQLMergeStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLCreateProcedureStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLBlockStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLReplaceStatement x) {
        resolve(this, x);
        return false;
    }

    default boolean visit(SQLCastExpr x) {
        x.getExpr()
                .accept(this);
        return true;
    }

    default boolean visit(SQLFetchStatement x) {
        resolve(this, x);
        return false;
    }
}
