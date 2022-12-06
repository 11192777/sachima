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
package org.zaizai.sachima.sql.parser;

import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.statement.SQLSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.mysql.parser.MySqlExprParser;
import org.zaizai.sachima.sql.dialect.mysql.parser.MySqlLexer;
import org.zaizai.sachima.sql.dialect.mysql.parser.MySqlStatementParser;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.oracle.parser.OracleExprParser;
import org.zaizai.sachima.sql.dialect.oracle.parser.OracleLexer;
import org.zaizai.sachima.sql.dialect.oracle.parser.OracleStatementParser;


public class SQLParserUtils {

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType) {
        SQLParserFeature[] features;
        if (DbType.mysql == dbType) {
            features = new SQLParserFeature[] {SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[] {};
        }
        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, boolean keepComments) {
        SQLParserFeature[] features;
        if (keepComments) {
            features = new SQLParserFeature[] {SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[] {};
        }

        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, SQLParserFeature... features) {
        return createSQLStatementParser(sql, dbType == null ? null : DbType.valueOf(dbType), features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
            case oceanbase_oracle:
                return new OracleStatementParser(sql, features);
            case mysql:
                return new MySqlStatementParser(sql, features);
            default:
                return new SQLStatementParser(sql, dbType);
        }
    }

    public static SQLExprParser createExprParser(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
                return new OracleExprParser(sql, features);
            case mysql:
                return new MySqlExprParser(sql, features);
            default:
                return new SQLExprParser(sql, dbType, features);
        }
    }

    public static Lexer createLexer(String sql, DbType dbType) {
        return createLexer(sql, dbType, new SQLParserFeature[0]);
    }

    public static Lexer createLexer(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
                return new OracleLexer(sql);
            case mysql:
                return new MySqlLexer(sql);
            default:
                return new Lexer(sql, null, dbType);
        }
    }

    public static SQLSelectQueryBlock createSelectQueryBlock(DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case mysql:
                return new MySqlSelectQueryBlock();
            case oracle:
                return new OracleSelectQueryBlock();
            default:
                return new SQLSelectQueryBlock(dbType);
        }
     }


}
