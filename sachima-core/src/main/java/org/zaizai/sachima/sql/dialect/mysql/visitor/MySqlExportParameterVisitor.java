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
package org.zaizai.sachima.sql.dialect.mysql.visitor;

import org.zaizai.sachima.sql.ast.SQLLimit;
import org.zaizai.sachima.sql.ast.SQLOrderBy;
import org.zaizai.sachima.sql.ast.expr.SQLBetweenExpr;
import org.zaizai.sachima.sql.ast.expr.SQLBinaryOpExpr;
import org.zaizai.sachima.sql.ast.expr.SQLInListExpr;
import org.zaizai.sachima.sql.ast.expr.SQLMethodInvokeExpr;
import org.zaizai.sachima.sql.ast.statement.SQLSelectGroupByClause;
import org.zaizai.sachima.sql.ast.statement.SQLSelectItem;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlFlushStatement;
import org.zaizai.sachima.sql.visitor.ExportParameterVisitor;
import org.zaizai.sachima.sql.visitor.ExportParameterVisitorUtils;

import java.util.ArrayList;
import java.util.List;

public class MySqlExportParameterVisitor extends MySqlOutputVisitor implements ExportParameterVisitor {

    /**
     * true= if require parameterized sql output
     */
    private boolean requireParameterizedOutput;


    public MySqlExportParameterVisitor(List<Object> parameters, Appendable appender, boolean wantParameterizedOutput){
        super(appender, true);
        this.parameters = parameters;
        this.requireParameterizedOutput = wantParameterizedOutput;
    }

    public MySqlExportParameterVisitor() {
        this(new ArrayList<>());
    }

    public MySqlExportParameterVisitor(List<Object> parameters) {
        this(parameters, null, false);
    }

    public MySqlExportParameterVisitor(final Appendable appender) {
        this(new ArrayList<>(),appender, true);
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public boolean visit(final SQLSelectItem x) {
        if(requireParameterizedOutput){
            return super.visit(x);
        }
        return true;
    }

    @Override
    public boolean visit(SQLLimit x) {
        if(requireParameterizedOutput){
            return super.visit(x);
        }

        return true;
    }

    @Override
    public boolean visit(SQLOrderBy x) {
        if(requireParameterizedOutput){
            return super.visit(x);
        }
        return false;
    }

    @Override
    public boolean visit(SQLSelectGroupByClause x) {
        if(requireParameterizedOutput){
            return super.visit(x);
        }
        return false;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        if(requireParameterizedOutput){
           return super.visit(x);
        }
        
        ExportParameterVisitorUtils.exportParamterAndAccept(this.parameters, x.getArguments());
        return true;
    }

    @Override
    public boolean visit(SQLInListExpr x) {
        if(requireParameterizedOutput){
            return super.visit(x);
         }
        ExportParameterVisitorUtils.exportParamterAndAccept(this.parameters, x.getTargetList());

        return true;
    }

    @Override
    public boolean visit(SQLBetweenExpr x) {
        if(requireParameterizedOutput){
            return super.visit(x);
         }
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        if(requireParameterizedOutput){
            return super.visit(x);
         }
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
        return true;
    }

    @Override
    public boolean visit(MySqlFlushStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlFlushStatement x) {

    }
}