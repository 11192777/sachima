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

import org.zaizai.sachima.exception.FastsqlException;
import org.zaizai.sachima.sql.ast.SQLExprImpl;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;
import org.zaizai.sachima.util.HexBin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SQLHexExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr {

    private final String hex;

    public SQLHexExpr(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public void output(Appendable buf) {
        try {
            buf.append("0x");
            buf.append(this.hex);

            String charset = (String) getAttribute("USING");
            if (charset != null) {
                buf.append(" USING ");
                buf.append(charset);
            }
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hex == null) ? 0 : hex.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLHexExpr other = (SQLHexExpr) obj;
        if (hex == null) {
            return Objects.isNull(other.hex);
        } else {
            return hex.equals(other.hex);
        }
    }

    public byte[] toBytes() {
        return HexBin.decode(this.hex);
    }

    public SQLHexExpr clone() {
        return new SQLHexExpr(hex);
    }

    public byte[] getValue() {
        return toBytes();
    }


    public SQLCharExpr toCharExpr() {
        byte[] bytes = toBytes();
        if (bytes == null) {
            return null;
        }
        String str = new String(bytes, StandardCharsets.UTF_8);
        return new SQLCharExpr(str);
    }
}
