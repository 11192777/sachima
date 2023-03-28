# Sachima

<hr/>

## 适配日志
### 定义表
```sql
CREATE TABLE sachima
(
    id           bigint                    AUTO_INCREMENT PRIMARY KEY,
    name         varchar(255)              NULL,
    is_enabled   bit                       NULL,
    created_date timestamp                 NULL,
    number       int                       NULL,
    size         int                       NULL,
    sex          varchar(20) DEFAULT 'man' NOT NULL,
    remark       longtext                  NULL
);
```

### Keyword Adapt

- LIMIT ?, ? -> OFFSET ? ROWS FETCH FIRST ? ROWS ONLY

```sql
MySQL:  SELECT * FROM sachima LIMIT 1;
Oracle: SELECT * FROM sachima FETCH FIRST 1 ROWS ONLY;
```

```sql
MySQL:  SELECT * FROM sachima LIMIT 500, 10;
Oracle: SELECT * FROM sachima OFFSET 500 ROWS FETCH FIRST 10 ROWS ONLY;
```

- '[keyword]' -> "[keyword]"

```sql
MySQL:  SELECT number, size FROM sachima;
Oracle: SELECT "NUMBER", "SIZE" FROM sachima;
```

```sql
MySQL:  SELECT id AS number FROM sachima;
Oracle: SELECT id AS "NUMBER" FROM sachima;
```

### Token Adapt

- " -> '

```sql
MySQL:  SELECT * FROM sachima WHERE id IN ("1", "2", "3");
Oracle: SELECT * FROM sachima WHERE id IN ('1', '2', '3');
```

- Remove `

```sql
MySQL:  SELECT `name` FROM sachima;
Oracle: SELECT name FROM sachima;
```

- Remove AS

```sql
MySQL:  SELECT * FROM sachima AS sa;
Oracle: SELECT * FROM sachima sa;
```

### Function Adapt

- CHAR_LENGTH([column]) -> LENGTH([column])

```sql
MySQL:  SELECT CHAR_LENGTH(name) FROM sachima WHERE id = 1;
Oracle: SELECT LENGTH(name) FROM sachima WHERE id = 1;
```

- CONCAT(?, ?, ?) -> ? || ? || ?

```sql
MySQL:  SELECT CONCAT(name, CONCAT('name', 'is'), id) FROM sachima WHERE id = 1;
Oracle: SELECT (name||('name'||'is')||id) FROM sachima WHERE id = 1;
```

- IFNULL([column], ?) -> VAL([column], ?)

```sql
MySQL:  SELECT IFNULL(name, ?) FROM sachima;
Oracle: SELECT NVL(name, ?) FROM sachima;
```

- FIELD(id, ?, ?, ?) -> DECODE(id, ?, 1, ?, 3, ?, 5)

```sql
MySQL:  SELECT * FROM sachima WHERE id IN (3, 2, 1) ORDER BY FIELD(id, 3, 2, 1);
Oracle: SELECT * FROM sachima WHERE id IN (3, 2, 1) DECODE(id, 3, 1, 2, 3, 1, 5);
```

- NOW() -> SYSDATE

```sql
MySQL:  INSERT INTO sachima (created_date) VALUES (NOW());
Oracle: INSERT INTO sachima (created_date) VALUES (SYSDATE);
```

- DATE_FORMAT(created_date, '%Y-%m-%d %H:%i:%s') -> TO_CHAR(created_date, 'yyyy-mm-dd hh24:mi:ss')

```sql
MySQL:  SELECT DATE_FORMAT(created_date, '%Y-%m-%d %H:%i:%s') FROM sachima;
Oracle: SELECT TO_CHAR(created_date, 'yyyy-mm-dd hh24:mi:ss') FROM sachima;
```

```sql
MySQL:  SELECT DATE_FORMAT(created_date, '%Y-%m-%d') FROM sachima;
Oracle: SELECT TO_CHAR(created_date, 'yyyy-mm-dd') FROM sachima;
```

- Remove [BINARY] function.

```sql
MySQL:  SELECT * FROM sachima WHERE BINARY (name) = 'ZhangSan';
Oracle: SELECT * FROM sachima WHERE name = 'ZhangSan';
```

- LEFT([column], ?) -> SUBSTR([column], 0, ?)

```sql
MySQL:  SELECT LEFT(name, 10) FROM sachima;
Oracle: SELECT SUBSTR(name, 0, 10) FROM sachima;
```

- YEAR(?) -> TO_CHAR(?, 'yyyy')

```sql
MySQL:  SELECT LEFT(name, 10) FROM sachima;
Oracle: SELECT SUBSTR(name, 0, 10) FROM sachima;
```

- MONTH(?) -> TO_CHAR(?, 'MM')

```sql
MySQL:  SELECT LEFT(name, 10) FROM sachima;
Oracle: SELECT SUBSTR(name, 0, 10) FROM sachima;
```

- IF(LENGTH(name)> ?, ?, ?) ->

```sql
MySQL:  SELECT IF(LENGTH(name) > 5, 'true', 'false') FROM sachima;
Oracle: SELECT DECODE(SIGN(LENGTH(name) - 5), 1, 'true', 'false') FROM sachima;
```

```sql
MySQL:  SELECT IF(LENGTH(name) >= 5, 'true', 'false') FROM sachima;
Oracle: SELECT DECODE(SIGN(LENGTH(name) - 5), -1, 'false', 'true') FROM sachima;
```

```sql
MySQL:  SELECT IF(LENGTH(name) < 5, 'true', 'false') FROM sachima;
Oracle: SELECT DECODE(SIGN(LENGTH(name) - 5), -1, 'true', 'false') FROM sachima;
```

```sql
MySQL:  SELECT IF(LENGTH(name) <= 5, 'true', 'false') FROM sachima;
Oracle: SELECT DECODE(SIGN(LENGTH(name) - 5), 1, 'false', 'true') FROM sachima;
```

### Syntax Adapt

- fit: [42000][1795] ORA-01795: 列表中的最大表达式数为 1000

```sql
MySQL:  SELECT * FROM sachima WHERE id IN (1, 2, ... 1000, 1001);
Oracle: SELECT * FROM sachima WHERE (1, id) IN ((1, 1), (1, 2), ... (1, 1000), (1, 1001));
```

- '' -> IS NULL

```sql
MySQL:  SELECT * FROM sachima WHERE name = '';
Oracle: SELECT * FROM sachima WHERE name IS NULL;
```

```sql
MySQL:  SELECT * FROM sachima WHERE name != '';
Oracle: SELECT * FROM sachima WHERE name IS NOT NULL;
```

- IN('') -> IS NULL

```sql
MySQL:  SELECT * FROM sachima WHERE name IN ('');
Oracle: SELECT * FROM sachima WHERE name IS NULL;
```

```sql
MySQL:  SELECT * FROM sachima WHERE name NOT IN ('');
Oracle: SELECT * FROM sachima WHERE name IS NOT NULL;
```

- IN(NULL) -> IS NULL

```sql
MySQL:  SELECT * FROM sachima WHERE name IN (NULL);
Oracle: SELECT * FROM sachima WHERE name IS NULL;
```

```sql
MySQL:  SELECT * FROM sachima WHERE name NOT IN (NULL);
Oracle: SELECT * FROM sachima WHERE name IS NOT NULL;
```

- Insert values.

```sql
MySQL:
    INSERT INTO
        sachima (id, name)
    VALUES
        (1, 'ZhangSan'),
        (2, 'WangWu');
Oracle:
    INSERT ALL 
        INTO sachima (id, name) VALUES (1, 'ZhangSan')
        INTO sachima (id, name) VALUES (2, 'WangWu')
    SELECT 1 FROM DUAL;
```

- Insert [date] value.

```sql
MySQL:  INSERT INTO sachima (id, created_date) VALUES (1, '2022-12-22 12:22:12');
Oracle: INSERT INTO sachima (id, created_date) VALUES (1, TO_DATE('2022-12-22 12:22:12', 'yyyy-mm-dd hh24:mi:ss'));
```

- Insert remove NULL value item. fix:[23000][1400] ORA-01400: 无法将 NULL 插入 ("SACHIMA"."SACHIMA"."SEX")

```sql
MySQL:  INSERT INTO sachima (id, sex) VALUES (1, NULL);
Oracle: INSERT INTO sachima (id) VALUES (1);
```

```sql
MySQL:
    INSERT INTO
        sachima (id, sex)
    VALUES
        (1, NULL),
        (2, 'woman');
Oracle:
    INSERT ALL
        INTO sachima (id) VALUES (1)
        INTO sachima (id, name) VALUES (2, 'woman')
    SELECT 1 FROM DUAL;
```

- Nclob binary operator.

```java
    @Before
    public void initDataTypeHandler() {
        ArrayList<ColumnTypeHandler.ColumnType> list = new ArrayList<>();
        list.add(new ColumnTypeHandler.ColumnType("sachima", "remark", "NCLOB"));
        ColumnTypeHandler.apply(list);
    }
```

```sql
MySQL:  SELECT * FROM sachima WHERE remark = 'Hello word.';
Oracle: SELECT * FROM sachima WHERE TO_CHAR(remark) = 'Hello word.';
```

- Boolean value.

```sql
MySQL:  SELECT * FROM sachima WHERE is_enabled = TRUE AND is_deleted != FALSE;
Oracle: SELECT * FROM sachima WHERE is_enabled = 1 AND is_deleted != 0;
```

- Compensate for primary keys.

```java
    @Before
    public void initPrimaryTypeHandler() {
        HashMap<String, String> primaryKeyMap = new HashMap<>();
        primaryKeyMap.put("sachima", "id");
        PrimaryKeyHandler.apply(primaryKeyMap, () -> 1000L);
    }
```

```sql
MySQL:  INSERT INTO sachima (name) VALUES ('ZhangSan');
Oracle: INSERT INTO sachima (name, id) VALUES ('ZhangSan', '1000');
```

```sql
MySQL:  INSERT INTO sachima (id, name) VALUES (1, 'ZhangSan');
Oracle: INSERT INTO sachima (id, name) VALUES (1, 'ZhangSan');
```

- Update use JOIN

```sql
MySQL:
    UPDATE
        sachima AS sa
        INNER JOIN user AS us ON sa.name = us.name
    SET
      sa.sex    = us.sex,
      sa.remark = 'Update from user'
    WHERE
      sa.id = us.id;
Oracle:
    UPDATE (
        SELECT
            sa.sex sa__sex, us.sex us__sex, sa.remark sa__remark
        FROM 
            sachima sa
            INNER JOIN USER us ON sa.name = us.name
        WHERE sa.id = us.id
    )
    SET sa__sex = us__sex, sa__remark = 'Update from user'
    WHERE 1=1;
```

<hr/>

## Guides

### DML接入方案
- 定义静态常量类用于存储适配时参数

```java
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.huilianyi.earchives.utils.SpringContextUtil;
import com.huilianyi.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;
import com.huilianyi.sachima.util.JDBCUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/9/15 15:20
 */
@Slf4j
@Component
@Order(value = Integer.MIN_VALUE)
public class DBAdapterConstant implements EnvironmentAware, ApplicationRunner {

    @Getter
    private static DbType dbType;
    @Getter
    private static String dbName;
    @Getter
    private static String owner;

    @Setter(value = AccessLevel.PRIVATE)
    private static Environment currentEnvironment;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        DBAdapterConstant.setCurrentEnvironment(environment);
        DBAdapterConstant.setInfo();
    }

    private static void setInfo() {
        String jdbcUrl = currentEnvironment.getProperty("spring.datasource.url");
        dbName = currentEnvironment.getProperty("db.name");
        if (StringUtils.isNotBlank(jdbcUrl)) {
            DBAdapterConstant.dbType = JdbcUtils.getDbType(jdbcUrl);
        }
        owner = JDBCUtils.getOwner(jdbcUrl, currentEnvironment.getProperty("db.username"));
        log.info("===> The DB info is initialized: type:{} name:{} owner:{}", dbType.getDesc(), dbName, owner);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (DbType.ORACLE.equals(dbType)) {
            //使用字段类型拓展
            ColumnTypeHandler.apply(SpringContextUtil.getBean(DataSource.class), owner);
        }
    }
}
```

- 使用MyBatis-Plus 插件集成DML适配

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor();
    // DML SQL适配插件
    if (DbType.ORACLE.equals(DBAdapterConstant.getDbType())) {
        interceptor.addInnerInterceptor(new com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor() {
            @Override
            public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
                PluginUtils.MPStatementHandler mpSH = PluginUtils.mpStatementHandler(sh);
                mpSH.mPBoundSql().sql(SQLAdaptHelper.translateMysqlToOracle(mpSH.mPBoundSql().sql()));
            } 
        });
    }
    // 分页插件
    interceptor.addInnerInterceptor(new com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor(DBAdapterConstant.getDbType()));
    return interceptor;
}
```

### DML&DDL接入方案

- 注入DruidDataSource时集成Oracle适配

```java
@Bean
public DataSource getDataSource(DataSourceProperties dataSourceProperties) {
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
    dataSource.setUrl(dataSourceProperties.getUrl());
    dataSource.setUsername(dataSourceProperties.getUsername());
    dataSource.setPassword(new String(Base64.getDecoder().decode(dataSourceProperties.getPassword())));

    //Oracle适配
    if (com.huilianyi.sachima.enums.DbType.isOracleDbType(com.huilianyi.sachima.util.JDBCUtils.getDbType(dataSourceProperties.getUrl()))) {
        dataSource.getProxyFilters().add(MySqlToOracleAdaptFilter.getInstance(dataSource.cloneDruidDataSource()));
    }
    return dataSource;
}
```

- Oracle适配依赖的Druid Filter

```java
package com.huilianyi.earchives.config.druid;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import com.huilianyi.sachima.exception.SQLTranslateException;
import com.huilianyi.sachima.sql.dialect.mysql.visitor.handler.ColumnTypeHandler;
import com.huilianyi.sachima.sql.dialect.mysql.visitor.handler.PrimaryKeyHandler;
import com.huilianyi.sachima.util.JDBCUtils;
import com.huilianyi.sachima.util.SQLAdaptHelper;

import java.sql.SQLException;

/**
 * <H1>集成Oracle适配到DruidDataSource</H1>
 *
 * <P>主要集成点: </P>
 * <ol>
 *     <li>Liquibase SQL</li>
 *     <li>MyBatis SQL</li>
 * </ol>
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2023/1/10 10:18
 */
@Slf4j
public class MySqlToOracleAdaptFilter extends FilterEventAdapter {

    public static MySqlToOracleAdaptFilter getInstance(DruidDataSource dataSource) {
        String owner = JDBCUtils.getOwner(dataSource.getUrl(), dataSource.getUsername());
        try {
            //集成字段类型
            ColumnTypeHandler.apply(dataSource, owner);
            //集成com.baomidou.mybatisplus.core.toolkit.IdWorker#getId()
            PrimaryKeyHandler.apply(dataSource, owner, IdWorker::getId);
        } catch (Exception e) {
            throw new RuntimeException("Initialize MySqlToOracleAdaptFilter failed.", e);
        }
        return new MySqlToOracleAdaptFilter();
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql) throws SQLException {
        return super.connection_prepareStatement(chain, connection, this.adaptSQL(sql));
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        return super.statement_executeQuery(chain, statement, this.adaptSQL(sql));
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        return super.statement_execute(chain, statement, this.adaptSQL(sql));
    }

    /**
     * <H2>转换sql为Oracle sql</H2>
     *
     * @param sql Liquibase SQL & MyBaits SQL
     * @return  {@link java.lang.String}    adapt sql;
     * @author Qingyu.Meng
     * @since 2023/1/12
     */
    private String adaptSQL(String sql) {
        try {
            return SQLAdaptHelper.translateMysqlToOracleOnLiquibase(sql);
        } catch (SQLTranslateException e) {
            log.warn("===> Adapted failed. return original sql:{}", sql);
            return sql;
        }
    }
}
```

### 范围限制

- DML 方案
  - 依赖 MyBatis-Plus
- DML & DDL 方案
  - 需要 USER_CONS_COLUMNS、USER_CONSTRAINTS、ALL_TAB_COLUMNS表权限
  
    ```sql
      GRANT SELECT ON USER_CONS_COLUMNS TO <owner>;
      GRANT SELECT ON USER_CONSTRAINTS TO <owner>;
      GRANT SELECT ON ALL_TAB_COLUMNS TO <owner>;
    ```
  - 依赖 MyBatis-Plus
  - 依赖 Druid
  - 依赖 Liquibase，其中Liquibase集成限制如下：
    - DDL语法需要使用原生标签语言实现，DML不做限制，如下所示：
    
      ```xml
      <changeSet id="xxxxxx" author="author.name">
        <sql>
          CREATE TABLE ea_form_field
          (
            id      bigint        AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
            name    varchar(100)  NOT NULL COMMENT 'mame'
           ) COMMENT '完整性资料类型维度规则配置';
        </sql>
      </changeSet>
      ```
    - 创建索引需要加表名，如下所示：
    
      ```xml
      <changeSet id="xxxxxxxxx" author="author.name">
        <createIndex tableName="table_name" indexName="idx_tableName_column1_column2">
          <column name="column1"/>
          <column name="column2(250)"/>
        </createIndex>
      </changeSet>
      ```