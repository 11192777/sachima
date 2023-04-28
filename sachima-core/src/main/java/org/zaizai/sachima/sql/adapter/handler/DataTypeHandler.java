package org.zaizai.sachima.sql.adapter.handler;

import org.zaizai.sachima.util.FnvHashUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <H1>MySQL数据类型到Oracle数据类型映射</H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 11:10
 */
public class DataTypeHandler {

    private final HashMap<Long, String> mapping = new HashMap<>();
    private static DataTypeHandler instance = new DataTypeHandler();


    public DataTypeHandler() {
        //默认映射
        mapping.put(FnvHashUtils.fnv1a64lower("bigint"), "NUMBER");
    }

    private DataTypeHandler(Map<String, String> map) {
        map.forEach((key, value) -> this.mapping.put(FnvHashUtils.fnv1a64lower(key), value));
    }

    /**
     * @param map Map[MySQL data type, Oracle data type]
     */
    public static void apply(Map<String, String> map) {
        instance = new DataTypeHandler(map);
    }

    /**
     * mapping mysql data type to oracle data type;
     * @param mysqlDataType such as bigint、 varchar()、 timestamp
     * @return oracle data type: Number、 Nvarchar()、 Date
     */
    public static String getOracleDataType(String mysqlDataType) {
        String oracleDataType = instance.mapping.get(FnvHashUtils.fnv1a64lower(mysqlDataType));
        return Objects.isNull(oracleDataType) ? mysqlDataType : oracleDataType;
    }


}
