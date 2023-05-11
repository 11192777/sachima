package org.zaizai.sachima.sql.adapter.handler;

import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <H1>数据类型映射，在DDL中，会根据数据类型映射配置替换原SQL中的数据类型</H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 11:10
 */
public class DataTypeMappingHandler {

    private final HashMap<Long, String> mapping = new HashMap<>();

    public DataTypeMappingHandler() {
        //默认映射
        mapping.put(TokenFnvConstants.CLOB, "NCLOB");
    }

    private DataTypeMappingHandler(Map<String, String> map) {
        map.forEach((key, value) -> this.mapping.put(FnvHashUtils.fnv1a64lower(key), value));
    }

    /**
     * @param map Map[MySQL data type, Oracle data type]
     */
    public static DataTypeMappingHandler build(Map<String, String> map) {
        return new DataTypeMappingHandler(map);
    }

    public String getMappingValue(String key) {
        return StringUtils.isEmpty(key) ? null : mapping.get(FnvHashUtils.fnv1a64lower(key));
    }


}
