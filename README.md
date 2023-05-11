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

- CHAR_LENGTH([tabColumn]) -> LENGTH([tabColumn])

```sql
MySQL:  SELECT CHAR_LENGTH(name) FROM sachima WHERE id = 1;
Oracle: SELECT LENGTH(name) FROM sachima WHERE id = 1;
```

- CONCAT(?, ?, ?) -> ? || ? || ?

```sql
MySQL:  SELECT CONCAT(name, CONCAT('name', 'is'), id) FROM sachima WHERE id = 1;
Oracle: SELECT (name||('name'||'is')||id) FROM sachima WHERE id = 1;
```

- IFNULL([tabColumn], ?) -> VAL([tabColumn], ?)

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

- LEFT([tabColumn], ?) -> SUBSTR([tabColumn], 0, ?)

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

### 接入方案
- Springboot集成MyBatis-plus适配、Liquibase适配，application.yml配置如下

```yaml
sachima:
  enabled: true
  adaptInLiquibase: true
  adaptInMybatisPlus: true
```

完整配置参考：
```yaml
sachima:
  enabled: true
  adaptInLiquibase: false
  adaptInMybatisPlus: false
  handlerSettings:
    enabledColumnTypeHandler: true
    enabledNonNullTypeHandler: true
    enabledPrimaryKeyHandler: true
    # default: org.zaizai.sachima.sql.adapter.handler.DataTypeMappingHandler.DataTypeMappingHandler()
    dataTypeMapping: 
      # CLOB : NCLOB，ALTER TABLE sachima MODIFY name CLOB ===> ALTER TABLE sachima MODIFY name NCLOB;
      CLOB : NCLOB
      BIGINT : NUMBER
      VARCHAR : NVARCHAR
```