/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaizai.sachima.sql.ast;

import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.sql.ast.expr.SQLIntegerExpr;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SQLDataTypeImpl extends SQLObjectImpl implements SQLDataType, SQLDbTypedObject {

    private String name;
    private long nameHashCode64;
    protected final List<SQLExpr> arguments = new ArrayList<>();
    private Boolean withTimeZone;
    private boolean withLocalTimeZone = false;
    private DbType dbType;

    private boolean unsigned;
    private boolean zerofill;

    // for oracle
    private SQLExpr indexBy;

    public SQLDataTypeImpl() {

    }

    public SQLDataTypeImpl(String name) {
        this.name = name;
    }

    public SQLDataTypeImpl(String name, int precision) {
        this(name);
        addArgument(new SQLIntegerExpr(precision));
    }

    public SQLDataTypeImpl(String name, SQLExpr arg) {
        this(name);
        addArgument(arg);
    }

    public SQLDataTypeImpl(String name, int precision, int scale) {
        this(name);
        addArgument(new SQLIntegerExpr(precision));
        addArgument(new SQLIntegerExpr(scale));
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < arguments.size(); i++) {
                SQLExpr arg = arguments.get(i);
                if (arg != null) {
                    arg.accept(visitor);
                }
            }
        }

        visitor.endVisit(this);
    }

    public String getName() {
        return this.name;
    }

    public long nameHashCode64() {
        if (nameHashCode64 == 0) {
            nameHashCode64 = FnvHashUtils.hashCode64(name);
        }
        return nameHashCode64;
    }

    public void setName(String name) {
        this.name = name;
        nameHashCode64 = 0L;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }

    public void addArgument(SQLExpr argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        this.arguments.add(argument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLDataTypeImpl dataType = (SQLDataTypeImpl) o;

        if (name != null ? !name.equals(dataType.name) : dataType.name != null) return false;
        if (!arguments.equals(dataType.arguments)) {
            return false;
        }
        return withTimeZone != null ? withTimeZone.equals(dataType.withTimeZone) : dataType.withTimeZone == null;
    }

    @Override
    public int hashCode() {
        long value = nameHashCode64();
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public Boolean getWithTimeZone() {
        return withTimeZone;
    }

    public void setWithTimeZone(Boolean withTimeZone) {
        this.withTimeZone = withTimeZone;
    }

    public boolean isWithLocalTimeZone() {
        return withLocalTimeZone;
    }

    public void setWithLocalTimeZone(boolean withLocalTimeZone) {
        this.withLocalTimeZone = withLocalTimeZone;
    }

    public DbType getDbType() {
        return dbType;
    }

    @Override
    public int jdbcType() {
        long nameNash = nameHashCode64();

        if (nameNash == TokenFnvConstants.TINYINT) {
            return Types.TINYINT;
        }

        if (nameNash == TokenFnvConstants.SMALLINT) {
            return Types.SMALLINT;
        }

        if (nameNash == TokenFnvConstants.INT || nameNash == TokenFnvConstants.INTEGER) {
            return Types.INTEGER;
        }

        if (nameNash == TokenFnvConstants.BIGINT) {
            return Types.BIGINT;
        }

        if (nameNash == TokenFnvConstants.DECIMAL) {
            return Types.DECIMAL;
        }

        if (nameNash == TokenFnvConstants.FLOAT) {
            return Types.FLOAT;
        }

        if (nameNash == TokenFnvConstants.REAL) {
            return Types.REAL;
        }

        if (nameNash == TokenFnvConstants.DOUBLE) {
            return Types.DOUBLE;
        }

        if (nameNash == TokenFnvConstants.NUMBER || nameNash == TokenFnvConstants.NUMERIC) {
            return Types.NUMERIC;
        }

        if (nameNash == TokenFnvConstants.BOOLEAN) {
            return Types.BOOLEAN;
        }

        if (nameNash == TokenFnvConstants.DATE || nameNash == TokenFnvConstants.NEWDATE) {
            return Types.DATE;
        }

        if (nameNash == TokenFnvConstants.DATETIME || nameNash == TokenFnvConstants.TIMESTAMP) {
            return Types.TIMESTAMP;
        }

        if (nameNash == TokenFnvConstants.TIME) {
            return Types.TIME;
        }

        if (nameNash == TokenFnvConstants.BLOB) {
            return Types.BLOB;
        }

        if (nameNash == TokenFnvConstants.ROWID) {
            return Types.ROWID;
        }

        if (nameNash == TokenFnvConstants.REF) {
            return Types.REF;
        }

        if (nameNash == TokenFnvConstants.TINYINT || nameNash == TokenFnvConstants.TINY) {
            return Types.TINYINT;
        }

        if (nameNash == TokenFnvConstants.SMALLINT || nameNash == TokenFnvConstants.SHORT) {
            return Types.SMALLINT;
        }

        if (nameNash == TokenFnvConstants.INT
                || nameNash == TokenFnvConstants.INT24
                || nameNash == TokenFnvConstants.INTEGER) {
            return Types.INTEGER;
        }

        if (nameNash == TokenFnvConstants.NUMBER || nameNash == TokenFnvConstants.NUMERIC) {
            return Types.NUMERIC;
        }

        if (nameNash == TokenFnvConstants.BOOLEAN) {
            return Types.BOOLEAN;
        }

        if (nameNash == TokenFnvConstants.DATE
                || nameNash == TokenFnvConstants.YEAR
                || nameNash == TokenFnvConstants.NEWDATE) {
            return Types.DATE;
        }

        if (nameNash == TokenFnvConstants.DATETIME || nameNash == TokenFnvConstants.TIMESTAMP) {
            return Types.TIMESTAMP;
        }

        if (nameNash == TokenFnvConstants.TIME) {
            return Types.TIME;
        }

        if (nameNash == TokenFnvConstants.TINYBLOB) {
            return Types.VARBINARY;
        }

        if (nameNash == TokenFnvConstants.BLOB) {
            return Types.BLOB;
        }

        if (nameNash == TokenFnvConstants.LONGBLOB) {
            return Types.LONGVARBINARY;
        }

        if (nameNash == TokenFnvConstants.ROWID) {
            return Types.ROWID;
        }

        if (nameNash == TokenFnvConstants.REF) {
            return Types.REF;
        }

        if (nameNash == TokenFnvConstants.BINARY || nameNash == TokenFnvConstants.GEOMETRY) {
            return Types.BINARY;
        }

        if (nameNash == TokenFnvConstants.SQLXML) {
            return Types.SQLXML;
        }

        if (nameNash == TokenFnvConstants.BIT) {
            return Types.BIT;
        }

        if (nameNash == TokenFnvConstants.NCHAR) {
            return Types.NCHAR;
        }

        if (nameNash == TokenFnvConstants.CHAR
                || nameNash == TokenFnvConstants.ENUM
                || nameNash == TokenFnvConstants.SET
                || nameNash == TokenFnvConstants.JSON) {
            return Types.CHAR;
        }

        if (nameNash == TokenFnvConstants.VARCHAR
                || nameNash == TokenFnvConstants.VARCHAR2
                || nameNash == TokenFnvConstants.STRING) {
            return Types.VARCHAR;
        }

        if (nameNash == TokenFnvConstants.NVARCHAR || nameNash == TokenFnvConstants.NVARCHAR2) {
            return Types.NVARCHAR;
        }

        if (nameNash == TokenFnvConstants.CLOB
                || nameNash == TokenFnvConstants.TEXT
                || nameNash == TokenFnvConstants.TINYTEXT
                || nameNash == TokenFnvConstants.MEDIUMTEXT
                || nameNash == TokenFnvConstants.LONGTEXT) {
            return Types.CLOB;
        }

        if (nameNash == TokenFnvConstants.NCLOB) {
            return Types.NCLOB;
        }


        if (nameNash == TokenFnvConstants.TINYBLOB) {
            return Types.VARBINARY;
        }

        if (nameNash == TokenFnvConstants.LONGBLOB) {
            return Types.LONGVARBINARY;
        }

        if (nameNash == TokenFnvConstants.BINARY || nameNash == TokenFnvConstants.GEOMETRY) {
            return Types.BINARY;
        }

        if (nameNash == TokenFnvConstants.SQLXML) {
            return Types.SQLXML;
        }

        //

        if (nameNash == TokenFnvConstants.NCHAR) {
            return Types.NCHAR;
        }

        if (nameNash == TokenFnvConstants.CHAR || nameNash == TokenFnvConstants.JSON) {
            return Types.CHAR;
        }

        if (nameNash == TokenFnvConstants.VARCHAR
                || nameNash == TokenFnvConstants.VARCHAR2
                || nameNash == TokenFnvConstants.STRING) {
            return Types.VARCHAR;
        }

        if (nameNash == TokenFnvConstants.NVARCHAR || nameNash == TokenFnvConstants.NVARCHAR2) {
            return Types.NVARCHAR;
        }

        if (nameNash == TokenFnvConstants.CLOB
                || nameNash == TokenFnvConstants.TEXT
                || nameNash == TokenFnvConstants.TINYTEXT
                || nameNash == TokenFnvConstants.MEDIUMTEXT
                || nameNash == TokenFnvConstants.LONGTEXT) {
            return Types.CLOB;
        }

        if (nameNash == TokenFnvConstants.NCLOB) {
            return Types.NCLOB;
        }

        return 0;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public SQLDataTypeImpl clone() {
        SQLDataTypeImpl x = new SQLDataTypeImpl();

        cloneTo(x);

        return x;
    }

    public void cloneTo(SQLDataTypeImpl x) {
        x.dbType = dbType;
        x.name = name;
        x.nameHashCode64 = nameHashCode64;

        for (SQLExpr arg : arguments) {
            x.addArgument(arg.clone());
        }

        x.withTimeZone = withTimeZone;
        x.withLocalTimeZone = withLocalTimeZone;
        x.zerofill = zerofill;
        x.unsigned = unsigned;

        if (indexBy != null) {
            x.setIndexBy(indexBy.clone());
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public boolean isZerofill() {
        return zerofill;
    }

    public void setZerofill(boolean zerofill) {
        this.zerofill = zerofill;
    }

    public SQLExpr getIndexBy() {
        return indexBy;
    }

    public void setIndexBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.indexBy = x;
    }

    public boolean isInt() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == TokenFnvConstants.BIGINT
                || hashCode64 == TokenFnvConstants.INT
                || hashCode64 == TokenFnvConstants.INT4
                || hashCode64 == TokenFnvConstants.INT24
                || hashCode64 == TokenFnvConstants.SMALLINT
                || hashCode64 == TokenFnvConstants.TINYINT
                || hashCode64 == TokenFnvConstants.INTEGER;
    }

    public boolean isNumberic() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == TokenFnvConstants.REAL
                || hashCode64 == TokenFnvConstants.FLOAT
                || hashCode64 == TokenFnvConstants.DOUBLE
                || hashCode64 == TokenFnvConstants.DOUBLE_PRECISION
                || hashCode64 == TokenFnvConstants.NUMBER
                || hashCode64 == TokenFnvConstants.DECIMAL;
    }

    public boolean isString() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == TokenFnvConstants.VARCHAR
                || hashCode64 == TokenFnvConstants.VARCHAR2
                || hashCode64 == TokenFnvConstants.CHAR
                || hashCode64 == TokenFnvConstants.NCHAR
                || hashCode64 == TokenFnvConstants.NVARCHAR
                || hashCode64 == TokenFnvConstants.NVARCHAR2
                || hashCode64 == TokenFnvConstants.TEXT
                || hashCode64 == TokenFnvConstants.TINYTEXT
                || hashCode64 == TokenFnvConstants.MEDIUMTEXT
                || hashCode64 == TokenFnvConstants.LONGTEXT
                || hashCode64 == TokenFnvConstants.CLOB
                || hashCode64 == TokenFnvConstants.NCLOB
                || hashCode64 == TokenFnvConstants.MULTIVALUE
                || hashCode64 == TokenFnvConstants.STRING;
    }

    @Override
    public boolean hasKeyLength() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == TokenFnvConstants.VARCHAR
                || hashCode64 == TokenFnvConstants.VARCHAR2
                || hashCode64 == TokenFnvConstants.CHAR
                || hashCode64 == TokenFnvConstants.NCHAR
                || hashCode64 == TokenFnvConstants.NVARCHAR
                || hashCode64 == TokenFnvConstants.NVARCHAR2
                || hashCode64 == TokenFnvConstants.TEXT
                || hashCode64 == TokenFnvConstants.TINYTEXT
                || hashCode64 == TokenFnvConstants.MEDIUMTEXT
                || hashCode64 == TokenFnvConstants.LONGTEXT
                || hashCode64 == TokenFnvConstants.CLOB
                || hashCode64 == TokenFnvConstants.NCLOB
                || hashCode64 == TokenFnvConstants.MULTIVALUE
                || hashCode64 == TokenFnvConstants.STRING
                || hashCode64 == TokenFnvConstants.BLOB
                || hashCode64 == TokenFnvConstants.TINYBLOB
                || hashCode64 == TokenFnvConstants.LONGBLOB
                || hashCode64 == TokenFnvConstants.BINARY
                || hashCode64 == TokenFnvConstants.VARBINARY;
    }
}
