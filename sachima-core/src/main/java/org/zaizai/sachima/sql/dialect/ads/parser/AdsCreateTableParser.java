package org.zaizai.sachima.sql.dialect.ads.parser;

import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.statement.*;
import org.zaizai.sachima.sql.parser.ParserException;
import org.zaizai.sachima.sql.parser.SQLCreateTableParser;
import org.zaizai.sachima.sql.parser.SQLExprParser;
import org.zaizai.sachima.sql.parser.Token;

public class AdsCreateTableParser extends SQLCreateTableParser {
    public AdsCreateTableParser(String sql) {
        super(sql);
    }

    public AdsCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        SQLCreateTableStatement stmt = newCreateStatement();

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                stmt.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (lexer.identifierEquals(TokenFnvConstants.DIMENSION)) {
            lexer.nextToken();
            stmt.setDimension(true);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            while(true) {
                Token token = lexer.token();
                if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(stmt);
                    stmt.getTableElementList().add((SQLTableElement) constraint);
                } else if (token == Token.TABLESPACE) {
                    throw new ParserException("TODO "  + lexer.info());
                } else if (lexer.token() == Token.INDEX) { //skip index
                    lexer.nextToken();
                    accept(Token.IDENTIFIER);
                    accept(Token.IDENTIFIER);
                    accept(Token.LPAREN);
                    accept(Token.IDENTIFIER);
                    while(true) {
                        if (lexer.token() == Token.COMMA) {
                            accept(Token.IDENTIFIER);
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                }
                else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.getTableElementList().add(column);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) { // compatible for sql server
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);

        }


        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setSelect(select);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.identifierEquals("PARTITION")) {
            lexer.nextToken();
            accept(Token.BY);
            acceptIdentifier("HASH");
            accept(Token.KEY);
            accept(Token.LPAREN);
            while(true) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier. " + lexer.info());
                }

                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.addPartitionColumn(column);

                if (lexer.isKeepComments() && lexer.hasComment()) {
                    column.addAfterComment(lexer.readAndResetComments());
                }

                if (lexer.token() != Token.COMMA) {
                    break;
                } else {
                    lexer.nextToken();
                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }
                }
            }
            accept(Token.RPAREN);

            acceptIdentifier("PARTITION");
            acceptIdentifier("NUM");
            accept(Token.LITERAL_INT);
        }

        if (lexer.identifierEquals(TokenFnvConstants.CLUSTERED)) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            while(true) {
                SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                stmt.addClusteredByItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(TokenFnvConstants.TABLEGROUP)) {
            lexer.nextToken();
            accept(Token.IDENTIFIER);
        }

        if (lexer.identifierEquals(TokenFnvConstants.OPTIONS)) {
            lexer.nextToken();
            accept(Token.LPAREN);

            while(true) {
                String name = lexer.stringVal();
                lexer.nextToken();
                accept(Token.EQ);
                SQLExpr value = this.exprParser.primary();
                stmt.addOption(name, value);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            accept(Token.LITERAL_CHARS);
        }


        return stmt;
    }

}
