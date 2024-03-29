package org.zaizai.sachima.sql.dialect.ads.parser;

import org.zaizai.sachima.sql.parser.Keywords;
import org.zaizai.sachima.sql.parser.Lexer;
import org.zaizai.sachima.sql.parser.SQLParserFeature;
import org.zaizai.sachima.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class AdsLexer extends Lexer {
    public static final Keywords DEFAULT_ADS_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("SHOW", Token.SHOW);
        map.put("ALL", Token.ALL);

        DEFAULT_ADS_KEYWORDS = new Keywords(map);
    }

    public AdsLexer(String input) {
        super(input);
        super.keywords = DEFAULT_ADS_KEYWORDS;
    }

    public AdsLexer(String input, SQLParserFeature... features){
        super(input);
        super.keywords = DEFAULT_ADS_KEYWORDS;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}
