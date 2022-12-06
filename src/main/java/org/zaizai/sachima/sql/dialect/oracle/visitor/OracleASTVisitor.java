/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") {
        return true;
    }
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
package org.zaizai.sachima.sql.dialect.oracle.visitor;

import org.zaizai.sachima.sql.ast.statement.SQLCheck;
import org.zaizai.sachima.sql.ast.statement.SQLCreateIndexStatement;
import org.zaizai.sachima.sql.ast.statement.SQLCreateTableStatement;
import org.zaizai.sachima.sql.ast.statement.SQLDeleteStatement;
import org.zaizai.sachima.sql.ast.statement.SQLForeignKeyImpl;
import org.zaizai.sachima.sql.ast.statement.SQLInsertStatement;
import org.zaizai.sachima.sql.ast.statement.SQLSelectQueryBlock;
import org.zaizai.sachima.sql.ast.statement.SQLUnique;
import org.zaizai.sachima.sql.ast.statement.SQLUpdateStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import org.zaizai.sachima.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.CycleClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.ModelClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.OracleReturningClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.OracleStorageClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.SampleClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.clause.SearchClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleAnalytic;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleIsOfTypeExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleRangeExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.expr.OracleTreatExpr;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCheck;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleContinueStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCreatePackageStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCreateSynonymStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleCreateTypeStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleExecuteImmediateStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleForStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OraclePipeRowStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleRaiseStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleRunStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleUnique;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import org.zaizai.sachima.sql.dialect.oracle.ast.stmt.OracleXmlColumnProperties;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public interface OracleASTVisitor extends SQLASTVisitor {


    default void endVisit(OracleAnalytic x) {

    }

    default void endVisit(OracleAnalyticWindowing x) {

    }

    default void endVisit(OracleDeleteStatement x) {
        endVisit((SQLDeleteStatement) x);
    }

    default void endVisit(OracleIntervalExpr x) {

    }

    default void endVisit(OracleOuterExpr x) {

    }

    default void endVisit(OracleSelectJoin x) {

    }

    default void endVisit(OracleSelectPivot x) {

    }

    default void endVisit(OracleSelectPivot.Item x) {

    }

    default void endVisit(OracleSelectRestriction.CheckOption x) {

    }

    default void endVisit(OracleSelectRestriction.ReadOnly x) {

    }

    default void endVisit(OracleSelectSubqueryTableSource x) {

    }

    default void endVisit(OracleSelectUnPivot x) {

    }

    default void endVisit(OracleUpdateStatement x) {

    }

    default boolean visit(OracleAnalytic x) {
        return true;
    }

    default boolean visit(OracleAnalyticWindowing x) {
        return true;
    }

    default boolean visit(OracleDeleteStatement x) {
        return visit((SQLDeleteStatement) x);
    }

    default boolean visit(OracleIntervalExpr x) {
        return true;
    }

    default boolean visit(OracleOuterExpr x) {
        return true;
    }

    default boolean visit(OracleSelectJoin x) {
        return true;
    }

    default boolean visit(OracleSelectPivot x) {
        return true;
    }

    default boolean visit(OracleSelectPivot.Item x) {
        return true;
    }

    default boolean visit(OracleSelectRestriction.CheckOption x) {
        return true;
    }

    default boolean visit(OracleSelectRestriction.ReadOnly x) {
        return true;
    }

    default boolean visit(OracleSelectSubqueryTableSource x) {
        return true;
    }

    default boolean visit(OracleSelectUnPivot x) {
        return true;
    }

    default boolean visit(OracleUpdateStatement x) {
        return visit((SQLUpdateStatement) x);
    }

    default boolean visit(SampleClause x) {
        return true;
    }

    default void endVisit(SampleClause x) {

    }

    default boolean visit(OracleSelectTableReference x) {
        return true;
    }

    default void endVisit(OracleSelectTableReference x) {

    }

    default boolean visit(PartitionExtensionClause x) {
        return true;
    }

    default void endVisit(PartitionExtensionClause x) {

    }

    default boolean visit(OracleWithSubqueryEntry x) {
        return true;
    }

    default void endVisit(OracleWithSubqueryEntry x) {

    }

    default boolean visit(SearchClause x) {
        return true;
    }

    default void endVisit(SearchClause x) {

    }

    default boolean visit(CycleClause x) {
        return true;
    }

    default void endVisit(CycleClause x) {

    }

    default boolean visit(OracleBinaryFloatExpr x) {
        return true;
    }

    default void endVisit(OracleBinaryFloatExpr x) {

    }

    default boolean visit(OracleBinaryDoubleExpr x) {
        return true;
    }

    default void endVisit(OracleBinaryDoubleExpr x) {

    }

    default boolean visit(OracleCursorExpr x) {
        return true;
    }

    default void endVisit(OracleCursorExpr x) {

    }

    default boolean visit(OracleIsSetExpr x) {
        return true;
    }

    default void endVisit(OracleIsSetExpr x) {

    }

    default boolean visit(ModelClause.ReturnRowsClause x) {
        return true;
    }

    default void endVisit(ModelClause.ReturnRowsClause x) {

    }

    default boolean visit(ModelClause.MainModelClause x) {
        return true;
    }

    default void endVisit(ModelClause.MainModelClause x) {

    }

    default boolean visit(ModelClause.ModelColumnClause x) {
        return true;
    }

    default void endVisit(ModelClause.ModelColumnClause x) {

    }

    default boolean visit(ModelClause.QueryPartitionClause x) {
        return true;
    }

    default void endVisit(ModelClause.QueryPartitionClause x) {

    }

    default boolean visit(ModelClause.ModelColumn x) {
        return true;
    }

    default void endVisit(ModelClause.ModelColumn x) {

    }

    default boolean visit(ModelClause.ModelRulesClause x) {
        return true;
    }

    default void endVisit(ModelClause.ModelRulesClause x) {

    }

    default boolean visit(ModelClause.CellAssignmentItem x) {
        return true;
    }

    default void endVisit(ModelClause.CellAssignmentItem x) {

    }

    default boolean visit(ModelClause.CellAssignment x) {
        return true;
    }

    default void endVisit(ModelClause.CellAssignment x) {

    }

    default boolean visit(ModelClause x) {
        return true;
    }

    default void endVisit(ModelClause x) {

    }

    default boolean visit(OracleReturningClause x) {
        return true;
    }

    default void endVisit(OracleReturningClause x) {

    }

    default boolean visit(OracleInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    default void endVisit(OracleInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    default boolean visit(InsertIntoClause x) {
        return true;
    }

    default void endVisit(InsertIntoClause x) {

    }

    default boolean visit(OracleMultiInsertStatement x) {
        return true;
    }

    default void endVisit(OracleMultiInsertStatement x) {

    }

    default boolean visit(ConditionalInsertClause x) {
        return true;
    }

    default void endVisit(ConditionalInsertClause x) {

    }

    default boolean visit(ConditionalInsertClauseItem x) {
        return true;
    }

    default void endVisit(ConditionalInsertClauseItem x) {

    }

    default boolean visit(OracleSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(OracleSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(OracleLockTableStatement x) {
        return true;
    }

    default void endVisit(OracleLockTableStatement x) {

    }

    default boolean visit(OracleAlterSessionStatement x) {
        return true;
    }

    default void endVisit(OracleAlterSessionStatement x) {

    }

    default boolean visit(OracleDatetimeExpr x) {
        return true;
    }

    default void endVisit(OracleDatetimeExpr x) {

    }

    default boolean visit(OracleSysdateExpr x) {
        return true;
    }

    default void endVisit(OracleSysdateExpr x) {

    }

    default boolean visit(OracleExceptionStatement x) {
        return true;
    }

    default void endVisit(OracleExceptionStatement x) {

    }

    default boolean visit(OracleExceptionStatement.Item x) {
        return true;
    }

    default void endVisit(OracleExceptionStatement.Item x) {

    }

    default boolean visit(OracleArgumentExpr x) {
        return true;
    }

    default void endVisit(OracleArgumentExpr x) {

    }

    default boolean visit(OracleSetTransactionStatement x) {
        return true;
    }

    default void endVisit(OracleSetTransactionStatement x) {

    }

    default boolean visit(OracleExplainStatement x) {
        return true;
    }

    default void endVisit(OracleExplainStatement x) {

    }

    default boolean visit(OracleAlterTableDropPartition x) {
        return true;
    }

    default void endVisit(OracleAlterTableDropPartition x) {

    }

    default boolean visit(OracleAlterTableTruncatePartition x) {
        return true;
    }

    default void endVisit(OracleAlterTableTruncatePartition x) {

    }

    default boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return true;
    }

    default void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x) {

    }

    default boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return true;
    }

    default void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {

    }

    default boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return true;
    }

    default void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {

    }

    default boolean visit(OracleAlterTableSplitPartition x) {
        return true;
    }

    default void endVisit(OracleAlterTableSplitPartition x) {

    }

    default boolean visit(OracleAlterTableModify x) {
        return true;
    }

    default void endVisit(OracleAlterTableModify x) {

    }

    default boolean visit(OracleCreateIndexStatement x) {
        return visit((SQLCreateIndexStatement) x);
    }

    default void endVisit(OracleCreateIndexStatement x) {
        endVisit((SQLCreateIndexStatement) x);
    }

    default boolean visit(OracleForStatement x) {
        return true;
    }

    default void endVisit(OracleForStatement x) {

    }

    default boolean visit(OracleRangeExpr x) {
        return true;
    }

    default void endVisit(OracleRangeExpr x) {

    }

    default boolean visit(OraclePrimaryKey x) {
        return true;
    }

    default void endVisit(OraclePrimaryKey x) {

    }

    default boolean visit(OracleCreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    default void endVisit(OracleCreateTableStatement x) {
        endVisit((SQLCreateTableStatement) x);
    }

    default boolean visit(OracleStorageClause x) {
        return true;
    }

    default void endVisit(OracleStorageClause x) {

    }

    default boolean visit(OracleGotoStatement x) {
        return true;
    }

    default void endVisit(OracleGotoStatement x) {

    }

    default boolean visit(OracleLabelStatement x) {
        return true;
    }

    default void endVisit(OracleLabelStatement x) {

    }

    default boolean visit(OracleAlterTriggerStatement x) {
        return true;
    }

    default void endVisit(OracleAlterTriggerStatement x) {

    }

    default boolean visit(OracleAlterSynonymStatement x) {
        return true;
    }

    default void endVisit(OracleAlterSynonymStatement x) {

    }

    default boolean visit(OracleAlterViewStatement x) {
        return true;
    }

    default void endVisit(OracleAlterViewStatement x) {

    }

    default boolean visit(OracleAlterTableMoveTablespace x) {
        return true;
    }

    default void endVisit(OracleAlterTableMoveTablespace x) {

    }

    default boolean visit(OracleFileSpecification x) {
        return true;
    }

    default void endVisit(OracleFileSpecification x) {

    }

    default boolean visit(OracleAlterTablespaceAddDataFile x) {
        return true;
    }

    default void endVisit(OracleAlterTablespaceAddDataFile x) {

    }

    default boolean visit(OracleAlterTablespaceStatement x) {
        return true;
    }

    default void endVisit(OracleAlterTablespaceStatement x) {

    }

    default boolean visit(OracleExitStatement x) {
        return true;
    }

    default void endVisit(OracleExitStatement x) {

    }

    default boolean visit(OracleContinueStatement x) {
        return true;
    }

    default void endVisit(OracleContinueStatement x) {

    }

    default boolean visit(OracleRaiseStatement x) {
        return true;
    }

    default void endVisit(OracleRaiseStatement x) {

    }

    default boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return true;
    }

    default void endVisit(OracleCreateDatabaseDbLinkStatement x) {

    }

    default boolean visit(OracleDropDbLinkStatement x) {
        return true;
    }

    default void endVisit(OracleDropDbLinkStatement x) {

    }

    default boolean visit(OracleDataTypeIntervalYear x) {
        return true;
    }

    default void endVisit(OracleDataTypeIntervalYear x) {

    }

    default boolean visit(OracleDataTypeIntervalDay x) {
        return true;
    }

    default void endVisit(OracleDataTypeIntervalDay x) {

    }

    default boolean visit(OracleUsingIndexClause x) {
        return true;
    }

    default void endVisit(OracleUsingIndexClause x) {

    }

    default boolean visit(OracleLobStorageClause x) {
        return true;
    }

    default void endVisit(OracleLobStorageClause x) {

    }

    default boolean visit(OracleUnique x) {
        return visit((SQLUnique) x);
    }

    default void endVisit(OracleUnique x) {
        endVisit((SQLUnique) x);
    }

    default boolean visit(OracleForeignKey x) {
        return visit((SQLForeignKeyImpl) x);
    }

    default void endVisit(OracleForeignKey x) {
        endVisit((SQLForeignKeyImpl) x);
    }

    default boolean visit(OracleCheck x) {
        return visit((SQLCheck) x);
    }

    default void endVisit(OracleCheck x) {
        endVisit((SQLCheck) x);
    }

    default boolean visit(OracleSupplementalIdKey x) {
        return true;
    }

    default void endVisit(OracleSupplementalIdKey x) {

    }

    default boolean visit(OracleSupplementalLogGrp x) {
        return true;
    }

    default void endVisit(OracleSupplementalLogGrp x) {

    }

    default boolean visit(OracleCreateTableStatement.Organization x) {
        return true;
    }

    default void endVisit(OracleCreateTableStatement.Organization x) {

    }

    default boolean visit(OracleCreateTableStatement.OIDIndex x) {
        return true;
    }

    default void endVisit(OracleCreateTableStatement.OIDIndex x) {

    }

    default boolean visit(OracleCreatePackageStatement x) {
        return true;
    }

    default void endVisit(OracleCreatePackageStatement x) {

    }

    default boolean visit(OracleExecuteImmediateStatement x) {
        return true;
    }

    default void endVisit(OracleExecuteImmediateStatement x) {

    }

    default boolean visit(OracleTreatExpr x) {
        return true;
    }

    default void endVisit(OracleTreatExpr x) {

    }

    default boolean visit(OracleCreateSynonymStatement x) {
        return true;
    }

    default void endVisit(OracleCreateSynonymStatement x) {

    }

    default boolean visit(OracleCreateTypeStatement x) {
        return true;
    }

    default void endVisit(OracleCreateTypeStatement x) {

    }

    default boolean visit(OraclePipeRowStatement x) {
        return true;
    }

    default void endVisit(OraclePipeRowStatement x) {

    }

    default boolean visit(OracleIsOfTypeExpr x) {
        return true;
    }

    default void endVisit(OracleIsOfTypeExpr x) {

    }

    default boolean visit(OracleRunStatement x) {
        return true;
    }

    default void endVisit(OracleRunStatement x) {

    }

    default boolean visit(OracleXmlColumnProperties x) {
        return true;
    }

    default void endVisit(OracleXmlColumnProperties x) {

    }

    default boolean visit(OracleXmlColumnProperties.OracleXMLTypeStorage x) {
        return true;
    }

    default void endVisit(OracleXmlColumnProperties.OracleXMLTypeStorage x) {

    }
}
