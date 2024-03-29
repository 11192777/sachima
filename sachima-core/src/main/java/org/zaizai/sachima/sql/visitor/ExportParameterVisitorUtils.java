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
package org.zaizai.sachima.sql.visitor;

import org.zaizai.sachima.sql.ast.expr.SQLTempExpr;
import org.zaizai.sachima.enums.DbType;
import org.zaizai.sachima.sql.ast.SQLExpr;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.expr.*;
import org.zaizai.sachima.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import org.zaizai.sachima.sql.dialect.oracle.visitor.OracleExportParameterVisitor;

import java.util.ArrayList;
import java.util.List;

public final class ExportParameterVisitorUtils {
    
    //private for util class not need new instance
    private ExportParameterVisitorUtils() {
        super();
    }

    public static ExportParameterVisitor createExportParameterVisitor(Appendable out, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case mysql:
                return new MySqlExportParameterVisitor(out);
            case oracle:
                return new OracleExportParameterVisitor(out);
            default:
                return new ExportParameterizedOutputVisitor(out);
        }
    }

    

    public static boolean exportParamterAndAccept(final List<Object> parameters, List<SQLExpr> list) {
        for (int i = 0, size = list.size(); i < size; ++i) {
            SQLExpr param = list.get(i);

            SQLExpr result = exportParameter(parameters, param);
            if (result != param) {
                list.set(i, result);
            }
        }

        return false;
    }

    public static SQLExpr exportParameter(final List<Object> parameters, final SQLExpr param) {
        Object value = null;
        boolean replace = false;

        if (param instanceof SQLCharExpr) {
            value = ((SQLCharExpr) param).getText();
            replace = true;
        } else if ( param instanceof SQLNCharExpr) {
            value = ((SQLNCharExpr) param).getText();
            replace = true;
        } else if (param instanceof SQLBooleanExpr) {
            value = ((SQLBooleanExpr) param).getBooleanValue();
            replace = true;
        } else if (param instanceof SQLNumericLiteralExpr) {
            value = ((SQLNumericLiteralExpr) param).getNumber();
            replace = true;
        } else if (param instanceof SQLHexExpr) {
            value = ((SQLHexExpr) param).toBytes();
            replace = true;
        } else if (param instanceof SQLTimestampExpr) {
            value = ((SQLTimestampExpr) param).getValue();
            replace = true;
        } else if (param instanceof SQLDateExpr) {
            value = ((SQLDateExpr) param).getValue();
            replace = true;
        } else if (param instanceof SQLTimeExpr) {
            value = ((SQLTimeExpr) param).getValue();
            replace = true;
        } else if (param instanceof SQLTempExpr) {
            value = ((SQLTempExpr) param).getValue();
            replace = true;
        } else if (param instanceof SQLListExpr) {
            SQLListExpr list = ((SQLListExpr) param);

            List<Object> listValues = new ArrayList<>();
            for (int i = 0; i < list.getItems().size(); i++) {
                SQLExpr listItem = list.getItems().get(i);

                if (listItem instanceof SQLCharExpr) {
                    Object listValue = ((SQLCharExpr) listItem).getText();
                    listValues.add(listValue);
                } else if (listItem instanceof SQLBooleanExpr) {
                    Object listValue = ((SQLBooleanExpr) listItem).getBooleanValue();
                    listValues.add(listValue);
                } else if (listItem instanceof SQLNumericLiteralExpr) {
                    Object listValue = ((SQLNumericLiteralExpr) listItem).getNumber();
                    listValues.add(listValue);
                } else if (param instanceof SQLHexExpr) {
                    Object listValue = ((SQLHexExpr) listItem).toBytes();
                    listValues.add(listValue);
                }
            }

            if (listValues.size() == list.getItems().size()) {
                value = listValues;
                replace = true;
            }
        } else if (param instanceof SQLNullExpr) {
            replace = true;
        }

        if (replace) {
            SQLObject parent = param.getParent();
            if (parent != null) {
                List<SQLObject> mergedList = null;
                if (parent instanceof SQLBinaryOpExpr) {
                    mergedList = ((SQLBinaryOpExpr) parent).getMergedList();
                }
                if (mergedList != null) {
                    List<Object> mergedListParams = new ArrayList<>(mergedList.size() + 1);
                    for (int i = 0; i < mergedList.size(); ++i) {
                        SQLObject item = mergedList.get(i);
                        if (item instanceof SQLBinaryOpExpr) {
                            SQLBinaryOpExpr binaryOpItem = (SQLBinaryOpExpr) item;
                            exportParameter(mergedListParams, binaryOpItem.getRight());
                        }
                    }
                    if (!mergedListParams.isEmpty()) {
                        mergedListParams.add(0, value);
                        value = mergedListParams;
                    }
                }
            }

            if (parameters != null) {
                parameters.add(value);
            }

            return new SQLVariantRefExpr("?");
        }

        return param;
    }

    public static void exportParameter(final List<Object> parameters, SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr
                && x.getRight() instanceof SQLLiteralExpr
                && x.getOperator().isRelational()) {
            return;
        }

        {
            SQLExpr leftResult = ExportParameterVisitorUtils.exportParameter(parameters, x.getLeft());
            if (leftResult != x.getLeft()) {
                x.setLeft(leftResult);
            }
        }

        {
            SQLExpr rightResult = exportParameter(parameters, x.getRight());
            if (rightResult != x.getRight()) {
                x.setRight(rightResult);
            }
        }
    }

    public static void exportParameter(final List<Object> parameters, SQLBetweenExpr x) {
        {
            SQLExpr result = exportParameter(parameters, x.getBeginExpr());
            if (result != x.getBeginExpr()) {
                x.setBeginExpr(result);
            }
        }

        {
            SQLExpr result = exportParameter(parameters, x.getEndExpr());
            if (result != x.getBeginExpr()) {
                x.setEndExpr(result);
            }
        }

    }
}
