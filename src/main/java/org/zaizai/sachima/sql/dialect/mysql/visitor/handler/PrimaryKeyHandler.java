package org.zaizai.sachima.sql.dialect.mysql.visitor.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizai.sachima.lang.Assert;
import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.MapUtils;
import org.zaizai.sachima.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/11 17:42
 */
public class PrimaryKeyHandler {

    private static final Log LOG = LogFactory.getLog(PrimaryKeyHandler.class);

    private static PrimaryKeyHandler instance;
    private static IDGenerator idGenerator;
    private final Map<Long, String> tablesPrimaryKeyMap;

    private PrimaryKeyHandler(Map<Long, String> map) {
        this.tablesPrimaryKeyMap = map;
    }


    public static void apply(Map<String, String> tablesPrimaryKeyMap, IDGenerator idGenerator) {
        if (Objects.nonNull(instance)) {
            return;
        }
        if (MapUtils.isEmpty(tablesPrimaryKeyMap)) {
            LOG.warn("No Oracle primary key information was obtained.");
        }

        HashMap<Long, String> hashTablePrimaryKeyMap = new HashMap<>();
        tablesPrimaryKeyMap.forEach((k, v) -> hashTablePrimaryKeyMap.put(FnvHashUtils.fnv1a64lower(k), v));
        PrimaryKeyHandler.idGenerator = idGenerator;
        instance = new PrimaryKeyHandler(hashTablePrimaryKeyMap);
    }

    public static void apply(DataSource dataSource, String owner, IDGenerator idGenerator) throws SQLException {
        if (Objects.nonNull(instance)) {
            return;
        }
        Assert.notNull(owner, "Owner parameter is required.");
        String obtainSql = "SELECT CU.TABLE_NAME, CU.COLUMN_NAME FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU WHERE CU.constraint_name = AU.constraint_name AND AU.constraint_type = 'P' AND AU.TABLE_NAME NOT LIKE 'BIN$%' and AU.OWNER = ?";
        Map<String, String> tablesPrimaryKeyMap = new HashMap<>();
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(obtainSql)) {
            preparedStatement.setString(1, owner);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                String primaryKey = resultSet.getString(2);
                tablesPrimaryKeyMap.put(tableName, primaryKey);
                LOG.debug("Succeeded in obtaining the primary field [" + tableName + "."  + primaryKey + "]");
            }
        } catch (SQLException e) {
            LOG.error("===> SQL execute failed: [" + obtainSql + "]", e);
            throw e;
        } finally {
            if (Objects.nonNull(resultSet)) {
                resultSet.close();
            }
        }
        apply(tablesPrimaryKeyMap, idGenerator);
    }

    /**
     *
     */
    public static String getTablePrimaryKey(String tableName) {
        if (Objects.isNull(instance) || StringUtils.isEmpty(tableName)) {
            return null;
        }
        return instance.tablesPrimaryKeyMap.get(FnvHashUtils.fnv1a64lower(tableName));
    }

    public static String generateId() {
        return String.valueOf(idGenerator.generateID());
    }

}

