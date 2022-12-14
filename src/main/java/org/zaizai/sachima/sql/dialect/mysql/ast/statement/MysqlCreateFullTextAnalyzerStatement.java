package org.zaizai.sachima.sql.dialect.mysql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.ast.expr.SQLTextLiteralExpr;
import org.zaizai.sachima.sql.ast.expr.SQLValuableExpr;
import org.zaizai.sachima.sql.ast.statement.SQLAssignItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.FullTextType;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.zaizai.sachima.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijun.cailj 2018/8/13
 */
public class MysqlCreateFullTextAnalyzerStatement extends MySqlStatementImpl {


    private SQLName    name;

    private String tokenizer;
    private List<String> charfilters = new ArrayList<>();
    private List<String> tokenizers = new ArrayList<>();

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public String getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(String tokenizer) {
        this.tokenizer = tokenizer;
    }

    public List<String> getCharfilters() {
        return charfilters;
    }

    public List<String> getTokenizers() {
        return tokenizers;
    }

}
