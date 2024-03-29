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
package org.zaizai.sachima.sql.ast.expr;

import org.zaizai.sachima.constant.TokenFnvConstants;
import org.zaizai.sachima.exception.FastsqlException;
import org.zaizai.sachima.util.FnvHashUtils;
import org.zaizai.sachima.util.SQLUtils;
import org.zaizai.sachima.sql.ast.*;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLMethodInvokeExpr extends SQLExprImpl implements SQLReplaceable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ARGUMENTS_SEPARATOR = ", ";

    protected final List<SQLExpr> arguments = new ArrayList<>();
    protected boolean holdEmptyArgumentBracket = true;
    protected String argumentsSeparator = ARGUMENTS_SEPARATOR;
    protected String methodName;
    protected long methodNameHashCode64;
    protected SQLExpr owner;
    protected SQLExpr from;
    protected SQLExpr using;
    protected SQLExpr _for;
    protected String trimOption;
    protected transient SQLDataType resolvedReturnDataType;

    public SQLMethodInvokeExpr() {

    }

    public SQLMethodInvokeExpr(String methodName) {
        this.methodName = methodName;
    }

    public SQLMethodInvokeExpr(String methodName, long methodNameHashCode64) {
        this.methodName = methodName;
        this.methodNameHashCode64 = methodNameHashCode64;
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner) {

        this.methodName = methodName;
        setOwner(owner);
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner, SQLExpr... params) {
        this.methodName = methodName;
        setOwner(owner);
        for (SQLExpr param : params) {
            this.addArgument(param);
        }
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner, List<SQLExpr> params) {
        this.methodName = methodName;
        setOwner(owner);
        for (SQLExpr param : params) {
            this.addArgument(param);
        }
    }

    public long methodNameHashCode64() {
        if (methodNameHashCode64 == 0
                && methodName != null) {
            methodNameHashCode64 = FnvHashUtils.hashCode64(methodName);
        }
        return methodNameHashCode64;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
        this.methodNameHashCode64 = 0L;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }

    public void setArgument(int i, SQLExpr arg) {
        if (arg != null) {
            arg.setParent(this);
        }
        this.arguments.set(i, arg);
    }

    /**
     * deprecated, instead of addArgument
     *
     * @deprecated
     */
    public void addParameter(SQLExpr param) {
        if (param != null) {
            param.setParent(this);
        }
        this.arguments.add(param);
    }

    public void addArgument(SQLExpr arg) {
        if (arg != null) {
            arg.setParent(this);
        }
        this.arguments.add(arg);
    }

    public SQLExpr getOwner() {
        return this.owner;
    }

    public void setOwner(SQLExpr owner) {
        if (owner != null) {
            owner.setParent(this);
        }
        this.owner = owner;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.from = x;
    }

    @Override
    public void output(Appendable buf) {
        try {
            if (this.owner != null) {
                this.owner.output(buf);
                buf.append(".");
            }

            buf.append(this.methodName);
            buf.append("(");
            for (int i = 0, size = this.arguments.size(); i < size; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }

                this.arguments.get(i).output(buf);
            }
            buf.append(")");
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.owner != null) {
                this.owner.accept(visitor);
            }

            for (SQLExpr arg : this.arguments) {
                if (arg != null) {
                    arg.accept(visitor);
                }
            }

            if (this.from != null) {
                this.from.accept(visitor);
            }

            if (this.using != null) {
                this.using.accept(visitor);
            }

            if (this._for != null) {
                this._for.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    public List getChildren() {
        if (this.owner == null) {
            return this.arguments;
        }

        List<SQLObject> children = new ArrayList<>();
        children.add(owner);
        children.addAll(this.arguments);
        return children;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.owner != null) {
                this.owner.accept(visitor);
            }

            for (SQLExpr arg : this.arguments) {
                if (arg != null) {
                    arg.accept(visitor);
                }
            }

            if (this.from != null) {
                this.from.accept(visitor);
            }

            if (this.using != null) {
                this.using.accept(visitor);
            }

            if (this._for != null) {
                this._for.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLMethodInvokeExpr that = (SQLMethodInvokeExpr) o;

        if (methodNameHashCode64() != that.methodNameHashCode64()) {
            return false;
        }
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (!arguments.equals(that.arguments)) return false;
        return from != null ? from.equals(that.from) : that.from == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (methodNameHashCode64() ^ (methodNameHashCode64() >>> 32));
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + arguments.hashCode();
        result = 31 * result + (from != null ? from.hashCode() : 0);
        return result;
    }

    public SQLMethodInvokeExpr clone() {
        SQLMethodInvokeExpr x = new SQLMethodInvokeExpr();
        cloneTo(x);
        return x;
    }

    public void cloneTo(SQLMethodInvokeExpr x) {
        x.methodName = methodName;

        if (owner != null) {
            x.setOwner(owner.clone());
        }

        for (SQLExpr arg : arguments) {
            x.addArgument(arg.clone());
        }

        if (from != null) {
            x.setFrom(from.clone());
        }

        if (using != null) {
            x.setUsing(using.clone());
        }

        if (trimOption != null) {
            x.setTrimOption(trimOption);
        }

        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof SQLObject) {
                    value = ((SQLObject) value).clone();
                }
                x.putAttribute(key, value);
            }
        }
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (target == null) {
            return false;
        }

        for (int i = 0; i < arguments.size(); ++i) {
            if (arguments.get(i) == expr) {
                arguments.set(i, target);
                target.setParent(this);
                return true;
            }
        }

        if (from == expr) {
            setFrom(target);
            return true;
        }

        if (using == expr) {
            setUsing(target);
            return true;
        }

        if (_for == expr) {
            setFor(target);
            return true;
        }

        return false;
    }

    public boolean match(String owner, String function) {
        if (function == null) {
            return false;
        }

        if (!SQLUtils.nameEquals(function, methodName)) {
            return false;
        }

        if (owner == null && this.owner == null) {
            return true;
        }

        if (owner == null || this.owner == null) {
            return false;
        }

        if (this.owner instanceof SQLIdentifierExpr) {
            return SQLUtils.nameEquals(((SQLIdentifierExpr) this.owner).getName(), owner);
        }

        return false;
    }

    public SQLDataType computeDataType() {
        if (resolvedReturnDataType != null) {
            return resolvedReturnDataType;
        }

        long nameHash = this.methodNameHashCode64();
        if (nameHash == TokenFnvConstants.TO_DATE
                || nameHash == TokenFnvConstants.ADD_MONTHS
        ) {
            return resolvedReturnDataType = SQLDateExpr.DATA_TYPE;
        }
        if (nameHash == TokenFnvConstants.DATE_PARSE) {
            return resolvedReturnDataType = SQLTimestampExpr.DATA_TYPE;
        }
        if (nameHash == TokenFnvConstants.CURRENT_TIME
                || nameHash == TokenFnvConstants.CURTIME) {
            return resolvedReturnDataType = SQLTimeExpr.DATA_TYPE;
        }

        if (nameHash == TokenFnvConstants.BIT_COUNT
                || nameHash == TokenFnvConstants.ROW_NUMBER) {
            return resolvedReturnDataType = new SQLDataTypeImpl("BIGINT");
        }

        if (arguments.size() == 1) {
            if (nameHash == TokenFnvConstants.TRUNC) {
                return resolvedReturnDataType = arguments.get(0).computeDataType();
            }
        } else if (arguments.size() == 2) {
            SQLExpr param0 = arguments.get(0);
            SQLExpr param1 = arguments.get(1);

            if (nameHash == TokenFnvConstants.ROUND) {
                SQLDataType dataType = param0.computeDataType();
                if (dataType != null) {
                    return dataType;
                }
            } else if (nameHash == TokenFnvConstants.NVL
                    || nameHash == TokenFnvConstants.IFNULL
                    || nameHash == TokenFnvConstants.ISNULL
                    || nameHash == TokenFnvConstants.COALESCE) {
                SQLDataType dataType = param0.computeDataType();
                if (dataType != null) {
                    return dataType;
                }

                return param1.computeDataType();
            }

            if (nameHash == TokenFnvConstants.MOD) {
                return resolvedReturnDataType = SQLIntegerExpr.DATA_TYPE;
            }
        }

        if (nameHash == TokenFnvConstants.STDDEV_SAMP) {
            return resolvedReturnDataType = SQLNumberExpr.DATA_TYPE_DOUBLE;
        }

        if (nameHash == TokenFnvConstants.CONCAT
                || nameHash == TokenFnvConstants.SUBSTR
                || nameHash == TokenFnvConstants.SUBSTRING) {
            return resolvedReturnDataType = SQLCharExpr.DATA_TYPE;
        }

        if (nameHash == TokenFnvConstants.YEAR
                || nameHash == TokenFnvConstants.MONTH
                || nameHash == TokenFnvConstants.DAY
                || nameHash == TokenFnvConstants.HOUR
                || nameHash == TokenFnvConstants.MINUTE
                || nameHash == TokenFnvConstants.SECOND
                || nameHash == TokenFnvConstants.PERIOD_ADD
                || nameHash == TokenFnvConstants.PERIOD_DIFF
        ) {
            return resolvedReturnDataType = new SQLDataTypeImpl("INT");
        }

        if (nameHash == TokenFnvConstants.GROUPING) {
            return resolvedReturnDataType = new SQLDataTypeImpl("INT");
        }

        if (nameHash == TokenFnvConstants.JSON_EXTRACT_SCALAR
                || nameHash == TokenFnvConstants.FORMAT_DATETIME
                || nameHash == TokenFnvConstants.DATE_FORMAT
        ) {
            return resolvedReturnDataType = SQLCharExpr.DATA_TYPE;
        }

        if (nameHash == TokenFnvConstants.DATE_ADD
                || nameHash == TokenFnvConstants.DATE_SUB
                || nameHash == TokenFnvConstants.DATE
                || nameHash == TokenFnvConstants.STR_TO_DATE
                || nameHash == TokenFnvConstants.CURRENT_DATE) {
            return resolvedReturnDataType = SQLDateExpr.DATA_TYPE;
        }

        if (nameHash == TokenFnvConstants.UNIX_TIMESTAMP) {
            return resolvedReturnDataType = SQLIntegerExpr.DATA_TYPE;
        }

        if (nameHash == TokenFnvConstants.TIME) {
            return resolvedReturnDataType = new SQLDataTypeImpl("VARCHAR");
        }

        if (nameHash == TokenFnvConstants.SYSDATE
                || nameHash == TokenFnvConstants.CURRENT_TIMESTAMP
                || nameHash == TokenFnvConstants.SYSTIMESTAMP) {
            return resolvedReturnDataType = SQLTimestampExpr.DATA_TYPE;
        }


        return null;
    }

    public SQLExpr getUsing() {
        return using;
    }

    public void setUsing(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.using = x;
    }

    public SQLExpr getFor() {
        return _for;
    }

    public void setFor(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this._for = x;
    }

    public String getTrimOption() {
        return trimOption;
    }

    public void setTrimOption(String trimOption) {
        this.trimOption = trimOption;
    }

    public SQLDataType getResolvedReturnDataType() {
        return resolvedReturnDataType;
    }

    public void setResolvedReturnDataType(SQLDataType resolvedReturnDataType) {
        this.resolvedReturnDataType = resolvedReturnDataType;
    }

    public String getArgumentsSeparator() {
        return argumentsSeparator;
    }

    public void setArgumentsSeparator(String argumentsSeparator) {
        this.argumentsSeparator = argumentsSeparator;
    }

    public boolean getHoldEmptyArgumentBracket() {
        return holdEmptyArgumentBracket;
    }

    public void setHoldEmptyArgumentBracket(boolean holdEmptyArgumentBracket) {
        this.holdEmptyArgumentBracket = holdEmptyArgumentBracket;
    }
}
