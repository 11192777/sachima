package org.zaizai.sachima.sql.dialect.oracle.constant;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/17 19:42
 */
public interface FunctionConstant {

    String LENGTH = "LENGTH";

    /**
     * Synonyms For {@link #CHARACTER_LENGTH} in MySQL;
     */
    String CHAR_LENGTH = "CHAR_LENGTH";

    /**
     * Synonyms For {@link #CHAR_LENGTH} in MySQL;
     */
    String CHARACTER_LENGTH = "CHARACTER_LENGTH";

    /**
     * Oracle function. argument must be two.
     */
    String NVL = "NVL";

    String CONCAT = "CONCAT";

    /**
     * Oracle. Like CONCAT(?, ?, ?, ?) in MySQL
     */
    String ORACLE_CONCAT = "||";

    String TO_DATE = "TO_DATE";

    /**
     * MySQL function.
     */
    String IFNULL = "IFNULL";

    String FIELD = "FIELD";

    String DECODE = "DECODE";

    String BINARY = "BINARY";

    String NOW = "NOW";

    String SYSDATE = "SYSDATE";

    String DATE_FORMAT = "DATE_FORMAT";

    String TO_CHAR = "TO_CHAR";

    String LEFT = "LEFT";

    String SUBSTR = "SUBSTR";

    String YEAR = "YEAR";

    String MONTH = "MONTH";

}
