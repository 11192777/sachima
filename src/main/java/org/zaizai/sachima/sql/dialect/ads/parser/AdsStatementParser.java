package org.zaizai.sachima.sql.dialect.ads.parser;

import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.sql.ast.SQLStatement;
import org.zaizai.sachima.sql.ast.statement.*;
import org.zaizai.sachima.sql.parser.*;

public class AdsStatementParser extends SQLStatementParser {
    public AdsStatementParser(String sql) {
        super (new AdsExprParser(sql));
    }

    public AdsStatementParser(String sql, SQLParserFeature... features) {
        super (new AdsExprParser(sql, features));
    }

    public AdsStatementParser(Lexer lexer){
        super(new AdsExprParser(lexer));
    }

    public AdsSelectParser createSQLSelectParser() {
        return new AdsSelectParser(this.exprParser, selectListCache);
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new AdsCreateTableParser(this.exprParser);
    }

    public SQLCreateTableStatement parseCreateTable() {
        AdsCreateTableParser parser = new AdsCreateTableParser(this.exprParser);
        return parser.parseCreateTable(true);
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (lexer.identifierEquals(TokenFnvConstants.DATABASES)) {
            lexer.nextToken();

            SQLShowDatabasesStatement stmt = parseShowDatabases(false);

            return stmt;
        }

        if (lexer.identifierEquals(TokenFnvConstants.TABLES)) {
            lexer.nextToken();

            SQLShowTablesStatement stmt = parseShowTables();

            return stmt;
        }

        if (lexer.identifierEquals(TokenFnvConstants.COLUMNS)) {
            lexer.nextToken();

            SQLShowColumnsStatement stmt = parseShowColumns();

            return stmt;
        }

        if (lexer.identifierEquals(TokenFnvConstants.TABLEGROUPS)) {
            lexer.nextToken();

            SQLShowTableGroupsStatement stmt = parseShowTableGroups();

            return stmt;
        }

        if (lexer.identifierEquals(TokenFnvConstants.PROCESSLIST)) {
            lexer.nextToken();

            SQLShowProcessListStatement stmt = new SQLShowProcessListStatement();
            if (lexer.identifierEquals(TokenFnvConstants.MPP)) {
                lexer.nextToken();
                stmt.setMpp(true);
            }

            return stmt;
        }

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();

            accept(Token.TABLE);

            SQLShowCreateTableStatement stmt = new SQLShowCreateTableStatement();
            stmt.setName(this.exprParser.name());
            return stmt;
        }

        if (lexer.token() == Token.ALL) {
            lexer.nextToken();
            if (lexer.token() == Token.CREATE) {
                lexer.nextToken();

                accept(Token.TABLE);

                SQLShowCreateTableStatement stmt = new SQLShowCreateTableStatement();
                stmt.setAll(true);
                stmt.setName(this.exprParser.name());
                return stmt;
            }

        }

        throw new ParserException("TODO " + lexer.info());
    }
}
