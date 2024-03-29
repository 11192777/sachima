package org.zaizai.sachima.sql.ast;

import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;


import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLStructDataType extends SQLObjectImpl implements SQLDataType {
    private DbType dbType;
    private List<Field> fields = new ArrayList<>();

    public SQLStructDataType() {

    }

    public SQLStructDataType(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public String getName() {
        return "STRUCT";
    }

    @Override
    public long nameHashCode64() {
        return TokenFnvConstants.STRUCT;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SQLExpr> getArguments() {
        return Collections.emptyList();
    }

    @Override
    public Boolean getWithTimeZone() {
        return null;
    }

    @Override
    public void setWithTimeZone(Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWithLocalTimeZone() {
        return false;
    }

    @Override
    public void setWithLocalTimeZone(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, fields);
        }
        visitor.endVisit(this);
    }

    public SQLStructDataType clone() {
        SQLStructDataType x = new SQLStructDataType(dbType);

        for (Field field : fields) {
            x.addField(field.name, field.dataType.clone());
        }

        return x;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Field addField(SQLName name, SQLDataType dataType) {
        Field field = new Field(name, dataType);
        field.setParent(this);
        fields.add(field);
        return field;
    }

    public int jdbcType() {
        return Types.STRUCT;
    }

    @Override
    public boolean isInt() {
        return false;
    }
    @Override
    public boolean isNumberic() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean hasKeyLength() {
        return false;
    }

    public static class Field extends SQLObjectImpl {
        private SQLName     name;
        private SQLDataType dataType;
        private String      comment;

        public Field(SQLName name, SQLDataType dataType) {
            setName(name);
            setDataType(dataType);
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, dataType);
            }
            visitor.endVisit(this);
        }

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.name = x;
        }

        public SQLDataType getDataType() {
            return dataType;
        }

        public void setDataType(SQLDataType x) {
            if (x != null) {
                x.setParent(this);
            }
            this.dataType = x;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
