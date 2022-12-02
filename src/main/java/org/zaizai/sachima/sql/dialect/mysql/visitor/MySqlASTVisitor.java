/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" ) {
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
package org.zaizai.sachima.sql.dialect.mysql.visitor;

import org.zaizai.sachima.sql.ast.statement.SQLForeignKeyImpl;
import org.zaizai.sachima.sql.ast.statement.SQLSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlForceIndexHint;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlPrimaryKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlUnique;
import org.zaizai.sachima.sql.dialect.mysql.ast.MySqlUseIndexHint;
import org.zaizai.sachima.sql.dialect.mysql.ast.MysqlForeignKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import org.zaizai.sachima.sql.dialect.mysql.ast.expr.MySqlJSONTableExpr;
import org.zaizai.sachima.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import org.zaizai.sachima.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import org.zaizai.sachima.sql.dialect.mysql.ast.expr.MySqlUserName;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.CobarShowStatus;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsBaselineStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsCancelDDLJob;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsChangeDDLJob;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsClearDDLJobCache;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsInspectDDLJobCache;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsRecoverDDLJob;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsRemoveDDLJob;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsRollbackDDLJob;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsShowDDLJobs;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsShowGlobalIndex;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.DrdsShowMetadataLock;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterDatabaseKillJob;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterDatabaseSetOption;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterEventStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterLogFileGroupStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterServerStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterFullTextIndex;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableForce;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableLock;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableOrderBy;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTableValidation;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterTablespaceStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCheckTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlChecksumTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlClearPlanCacheStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateAddLogFileGroupStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateEventStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateExternalCatalogStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateServerStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableSpaceStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlDisabledPlanCacheStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlEventSchedule;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlExecuteForAdsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlExplainPlanCacheStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlExtPartition;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlFlashbackStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlFlushStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlManageInstanceGroupStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlMigrateStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlRaftLeaderTransferStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlRaftMemberChangeStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlRenameSequenceStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowBroadcastsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowClusterNameStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowConfigStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowDatabaseStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowDatasourcesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowDdlStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowDsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowHMSMetaStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowHelpStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowJobStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowMigrateTaskStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowNodeStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowPartitionsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowPhysicalProcesslistStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowPlanCacheStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowPlanCacheStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowRuleStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowRuleStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowSequencesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowSlowStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowTopologyStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowTraceStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlSubPartitionByValue;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlUpdatePlanCacheStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlAlterFullTextStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlAlterTableAlterCheck;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlCreateFullTextAnalyzerStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlCreateFullTextCharFilterStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlCreateFullTextDictionaryStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlCreateFullTextTokenFilterStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlCreateFullTextTokenizerStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlDropFullTextStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlShowCreateFullTextStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlShowDbLockStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlShowFullTextStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlShowHtcStatement;
import org.zaizai.sachima.sql.dialect.mysql.ast.statement.MysqlShowStcStatement;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

public interface MySqlASTVisitor extends SQLASTVisitor {
    default boolean visit(MySqlTableIndex x) {
        return true;
    }

    default void endVisit(MySqlTableIndex x) {

    }

    default boolean visit(MySqlKey x) {
        return true;
    }

    default void endVisit(MySqlKey x) {

    }

    default boolean visit(MySqlPrimaryKey x) {
        return true;
    }

    default void endVisit(MySqlPrimaryKey x) {

    }

    default boolean visit(MySqlUnique x) {
        return true;
    }

    default void endVisit(MySqlUnique x) {

    }

    default boolean visit(MysqlForeignKey x) {
        return visit((SQLForeignKeyImpl) x);
    }

    default void endVisit(MysqlForeignKey x) {
        endVisit((SQLForeignKeyImpl) x);
    }

    default void endVisit(MySqlPrepareStatement x) {

    }

    default boolean visit(MySqlPrepareStatement x) {
        return true;
    }

    default void endVisit(MySqlExecuteStatement x) {

    }

    default boolean visit(MysqlDeallocatePrepareStatement x) {
        return true;
    }

    default void endVisit(MysqlDeallocatePrepareStatement x) {

    }

    default boolean visit(MySqlExecuteStatement x) {
        return true;
    }

    default void endVisit(MySqlDeleteStatement x) {

    }

    default boolean visit(MySqlDeleteStatement x) {
        return true;
    }

    default void endVisit(MySqlInsertStatement x) {

    }

    default boolean visit(MySqlInsertStatement x) {
        return true;
    }

    default void endVisit(MySqlLoadDataInFileStatement x) {

    }

    default boolean visit(MySqlLoadDataInFileStatement x) {
        return true;
    }

    default void endVisit(MySqlLoadXmlStatement x) {

    }

    default boolean visit(MySqlLoadXmlStatement x) {
        return true;
    }

    default void endVisit(MySqlShowWarningsStatement x) {

    }

    default boolean visit(MySqlShowWarningsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowStatusStatement x) {

    }

    default boolean visit(MySqlShowStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowAuthorsStatement x) {

    }

    default boolean visit(MySqlShowAuthorsStatement x) {
        return true;
    }

    default void endVisit(MysqlShowHtcStatement x) {

    }

    default boolean visit(MysqlShowHtcStatement x) {
        return true;
    }

    default void endVisit(MysqlShowStcStatement x) {

    }

    default boolean visit(MysqlShowStcStatement x) {
        return true;
    }

    default void endVisit(CobarShowStatus x) {

    }

    default boolean visit(CobarShowStatus x) {
        return true;
    }

    default void endVisit(DrdsShowDDLJobs x) {

    }

    default boolean visit(DrdsShowDDLJobs x) {
        return true;
    }

    default void endVisit(DrdsCancelDDLJob x) {

    }

    default boolean visit(DrdsCancelDDLJob x) {
        return true;
    }

    default void endVisit(DrdsRecoverDDLJob x) {

    }

    default boolean visit(DrdsRecoverDDLJob x) {
        return true;
    }

    default void endVisit(DrdsRollbackDDLJob x) {

    }

    default boolean visit(DrdsRollbackDDLJob x) {
        return true;
    }

    default void endVisit(DrdsRemoveDDLJob x) {

    }

    default boolean visit(DrdsRemoveDDLJob x) {
        return true;
    }

    default void endVisit(DrdsInspectDDLJobCache x) {

    }

    default boolean visit(DrdsInspectDDLJobCache x) {
        return true;
    }

    default void endVisit(DrdsClearDDLJobCache x) {

    }

    default boolean visit(DrdsClearDDLJobCache x) {
        return true;
    }

    default void endVisit(DrdsChangeDDLJob x) {

    }

    default boolean visit(DrdsChangeDDLJob x) {
        return true;
    }

    default void endVisit(DrdsBaselineStatement x) {

    }

    default boolean visit(DrdsBaselineStatement x) {
        return true;
    }

    default void endVisit(DrdsShowGlobalIndex x) {

    }

    default boolean visit(DrdsShowGlobalIndex x) {
        return true;
    }

    default void endVisit(DrdsShowMetadataLock x) {

    }

    default boolean visit(DrdsShowMetadataLock x) {
        return true;
    }

    default void endVisit(MySqlBinlogStatement x) {

    }

    default boolean visit(MySqlBinlogStatement x) {
        return true;
    }

    default void endVisit(MySqlResetStatement x) {

    }

    default boolean visit(MySqlResetStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateUserStatement x) {

    }

    default boolean visit(MySqlCreateUserStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateUserStatement.UserSpecification x) {

    }

    default boolean visit(MySqlCreateUserStatement.UserSpecification x) {
        return true;
    }

    default void endVisit(MySqlPartitionByKey x) {

    }

    default boolean visit(MySqlPartitionByKey x) {
        return true;
    }

    default void endVisit(MySqlUpdatePlanCacheStatement x) {

    }

    default boolean visit(MySqlUpdatePlanCacheStatement x) {
        return true;
    }

    default void endVisit(MySqlShowPlanCacheStatusStatement x) {

    }

    default boolean visit(MySqlShowPlanCacheStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlClearPlanCacheStatement x) {

    }

    default boolean visit(MySqlClearPlanCacheStatement x) {
        return true;
    }

    default void endVisit(MySqlDisabledPlanCacheStatement x) {

    }

    default boolean visit(MySqlDisabledPlanCacheStatement x) {
        return true;
    }

    default void endVisit(MySqlExplainPlanCacheStatement x) {

    }

    default boolean visit(MySqlExplainPlanCacheStatement x) {
        return true;
    }

    default boolean visit(MySqlSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(MySqlSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(MySqlOutFileExpr x) {
        return true;
    }

    default void endVisit(MySqlOutFileExpr x) {

    }

    default boolean visit(MySqlExplainStatement x) {
        return true;
    }

    default void endVisit(MySqlExplainStatement x) {

    }

    default boolean visit(MySqlUpdateStatement x) {
        return true;
    }

    default void endVisit(MySqlUpdateStatement x) {

    }

    default boolean visit(MySqlSetTransactionStatement x) {
        return true;
    }

    default void endVisit(MySqlSetTransactionStatement x) {

    }

    default boolean visit(MySqlShowHMSMetaStatement x) {
        return true;
    }

    default void endVisit(MySqlShowHMSMetaStatement x) {

    }

    default boolean visit(MySqlShowBinaryLogsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowBinaryLogsStatement x) {

    }

    default boolean visit(MySqlShowMasterLogsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowMasterLogsStatement x) {

    }

    default boolean visit(MySqlShowCharacterSetStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCharacterSetStatement x) {

    }

    default boolean visit(MySqlShowCollationStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCollationStatement x) {

    }

    default boolean visit(MySqlShowBinLogEventsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowBinLogEventsStatement x) {

    }

    default boolean visit(MySqlShowContributorsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowContributorsStatement x) {

    }

    default boolean visit(MySqlShowCreateDatabaseStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCreateDatabaseStatement x) {

    }

    default boolean visit(MySqlShowCreateEventStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCreateEventStatement x) {

    }

    default boolean visit(MySqlShowCreateFunctionStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCreateFunctionStatement x) {

    }

    default boolean visit(MySqlShowCreateProcedureStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCreateProcedureStatement x) {

    }

    default boolean visit(MySqlShowCreateTriggerStatement x) {
        return true;
    }

    default void endVisit(MySqlShowCreateTriggerStatement x) {

    }

    default boolean visit(MySqlShowEngineStatement x) {
        return true;
    }

    default void endVisit(MySqlShowEngineStatement x) {

    }

    default boolean visit(MySqlShowEnginesStatement x) {
        return true;
    }

    default void endVisit(MySqlShowEnginesStatement x) {

    }

    default boolean visit(MySqlShowErrorsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowErrorsStatement x) {

    }

    default boolean visit(MySqlShowEventsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowEventsStatement x) {

    }

    default boolean visit(MySqlShowFunctionCodeStatement x) {
        return true;
    }

    default void endVisit(MySqlShowFunctionCodeStatement x) {

    }

    default boolean visit(MySqlShowFunctionStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowFunctionStatusStatement x) {

    }

    default boolean visit(MySqlShowGrantsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowGrantsStatement x) {

    }

    default boolean visit(MySqlUserName x) {
        return true;
    }

    default void endVisit(MySqlUserName x) {

    }

    default boolean visit(MySqlAlterDatabaseSetOption x) {
        return true;
    }

    default void endVisit(MySqlAlterDatabaseSetOption x) {

    }

    default boolean visit(MySqlAlterDatabaseKillJob x) {
        return true;
    }

    default void endVisit(MySqlAlterDatabaseKillJob x) {

    }

    default boolean visit(MySqlShowMasterStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowMasterStatusStatement x) {

    }

    default boolean visit(MySqlShowOpenTablesStatement x) {
        return true;
    }

    default void endVisit(MySqlShowOpenTablesStatement x) {

    }

    default boolean visit(MySqlShowPluginsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowPluginsStatement x) {

    }

    default boolean visit(MySqlShowPartitionsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowPartitionsStatement x) {

    }

    default boolean visit(MySqlShowPrivilegesStatement x) {
        return true;
    }

    default void endVisit(MySqlShowPrivilegesStatement x) {

    }

    default boolean visit(MySqlShowProcedureCodeStatement x) {
        return true;
    }

    default void endVisit(MySqlShowProcedureCodeStatement x) {

    }

    default boolean visit(MySqlShowProcedureStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowProcedureStatusStatement x) {

    }

    default boolean visit(MySqlShowProcessListStatement x) {
        return true;
    }

    default void endVisit(MySqlShowProcessListStatement x) {

    }

    default boolean visit(MySqlShowProfileStatement x) {
        return true;
    }

    default void endVisit(MySqlShowProfileStatement x) {

    }

    default boolean visit(MySqlShowProfilesStatement x) {
        return true;
    }

    default void endVisit(MySqlShowProfilesStatement x) {

    }

    default boolean visit(MySqlShowRelayLogEventsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowRelayLogEventsStatement x) {

    }

    default boolean visit(MySqlShowSlaveHostsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowSlaveHostsStatement x) {

    }

    default boolean visit(MySqlShowSequencesStatement x) {
        return true;
    }

    default void endVisit(MySqlShowSequencesStatement x) {

    }

    default boolean visit(MySqlShowSlaveStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowSlaveStatusStatement x) {

    }

    default boolean visit(MySqlShowSlowStatement x) {
        return true;
    }

    default void endVisit(MySqlShowSlowStatement x) {

    }

    default boolean visit(MySqlShowTableStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowTableStatusStatement x) {

    }

    default boolean visit(MySqlShowTriggersStatement x) {
        return true;
    }

    default void endVisit(MySqlShowTriggersStatement x) {

    }

    default boolean visit(MySqlShowVariantsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowVariantsStatement x) {

    }

    default boolean visit(MySqlShowTraceStatement x) {
        return true;
    }

    default void endVisit(MySqlShowTraceStatement x) {

    }

    default boolean visit(MySqlShowBroadcastsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowBroadcastsStatement x) {

    }

    default boolean visit(MySqlShowRuleStatement x) {
        return true;
    }

    default void endVisit(MySqlShowRuleStatement x) {

    }

    default boolean visit(MySqlShowRuleStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowRuleStatusStatement x) {

    }

    default boolean visit(MySqlShowDsStatement x) {
        return true;
    }

    default void endVisit(MySqlShowDsStatement x) {

    }

    default boolean visit(MySqlShowDdlStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowDdlStatusStatement x) {

    }

    default boolean visit(MySqlShowTopologyStatement x) {
        return true;
    }

    default void endVisit(MySqlShowTopologyStatement x) {

    }

    default boolean visit(MySqlRenameTableStatement.Item x) {
        return true;
    }

    default void endVisit(MySqlRenameTableStatement.Item x) {

    }

    default boolean visit(MySqlRenameTableStatement x) {
        return true;
    }

    default void endVisit(MySqlRenameTableStatement x) {

    }

    default boolean visit(MysqlShowDbLockStatement x) {
        return true;
    }

    default void endVisit(MysqlShowDbLockStatement x) {

    }

    default boolean visit(MySqlShowDatabaseStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowDatabaseStatusStatement x) {

    }

    default boolean visit(MySqlUseIndexHint x) {
        return true;
    }

    default void endVisit(MySqlUseIndexHint x) {

    }

    default boolean visit(MySqlIgnoreIndexHint x) {
        return true;
    }

    default void endVisit(MySqlIgnoreIndexHint x) {

    }

    default boolean visit(MySqlLockTableStatement x) {
        return true;
    }

    default void endVisit(MySqlLockTableStatement x) {

    }

    default boolean visit(MySqlLockTableStatement.Item x) {
        return true;
    }

    default void endVisit(MySqlLockTableStatement.Item x) {

    }

    default boolean visit(MySqlUnlockTablesStatement x) {
        return true;
    }

    default void endVisit(MySqlUnlockTablesStatement x) {

    }

    default boolean visit(MySqlForceIndexHint x) {
        return true;
    }

    default void endVisit(MySqlForceIndexHint x) {

    }

    default boolean visit(MySqlAlterTableChangeColumn x) {
        return true;
    }

    default void endVisit(MySqlAlterTableChangeColumn x) {

    }

    default boolean visit(MySqlAlterTableOption x) {
        return true;
    }

    default void endVisit(MySqlAlterTableOption x) {

    }

    default boolean visit(MySqlCreateTableStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateTableStatement x) {

    }

    default boolean visit(MySqlHelpStatement x) {
        return true;
    }

    default void endVisit(MySqlHelpStatement x) {

    }

    default boolean visit(MySqlCharExpr x) {
        return true;
    }

    default void endVisit(MySqlCharExpr x) {

    }

    default boolean visit(MySqlAlterTableModifyColumn x) {
        return true;
    }

    default void endVisit(MySqlAlterTableModifyColumn x) {

    }

    default boolean visit(MySqlAlterTableDiscardTablespace x) {
        return true;
    }

    default void endVisit(MySqlAlterTableDiscardTablespace x) {

    }

    default boolean visit(MySqlAlterTableImportTablespace x) {
        return true;
    }

    default void endVisit(MySqlAlterTableImportTablespace x) {

    }

    default boolean visit(MySqlCreateTableStatement.TableSpaceOption x) {
        return true;
    }

    default void endVisit(MySqlCreateTableStatement.TableSpaceOption x) {

    }

    default boolean visit(MySqlAnalyzeStatement x) {
        return true;
    }

    default void endVisit(MySqlAnalyzeStatement x) {

    }

    default boolean visit(MySqlCreateExternalCatalogStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateExternalCatalogStatement x) {

    }

    default boolean visit(MySqlAlterUserStatement x) {
        return true;
    }

    default void endVisit(MySqlAlterUserStatement x) {

    }

    default boolean visit(MySqlOptimizeStatement x) {
        return true;
    }

    default void endVisit(MySqlOptimizeStatement x) {

    }

    default boolean visit(MySqlHintStatement x) {
        return true;
    }

    default void endVisit(MySqlHintStatement x) {

    }

    default boolean visit(MySqlOrderingExpr x) {
        return true;
    }

    default void endVisit(MySqlOrderingExpr x) {

    }

    default boolean visit(MySqlCaseStatement x) {
        return true;
    }

    default void endVisit(MySqlCaseStatement x) {

    }

    default boolean visit(MySqlDeclareStatement x) {
        return true;
    }

    default void endVisit(MySqlDeclareStatement x) {

    }

    default boolean visit(MySqlSelectIntoStatement x) {
        return true;
    }

    default void endVisit(MySqlSelectIntoStatement x) {

    }

    default boolean visit(MySqlWhenStatement x) {
        return true;
    }

    default void endVisit(MySqlWhenStatement x) {

    }

    default boolean visit(MySqlLeaveStatement x) {
        return true;
    }

    default void endVisit(MySqlLeaveStatement x) {

    }

    default boolean visit(MySqlIterateStatement x) {
        return true;
    }

    default void endVisit(MySqlIterateStatement x) {

    }

    default boolean visit(MySqlRepeatStatement x) {
        return true;
    }

    default void endVisit(MySqlRepeatStatement x) {

    }

    default boolean visit(MySqlCursorDeclareStatement x) {
        return true;
    }

    default void endVisit(MySqlCursorDeclareStatement x) {

    }

    default boolean visit(MySqlUpdateTableSource x) {
        return true;
    }

    default void endVisit(MySqlUpdateTableSource x) {

    }

    default boolean visit(MySqlAlterTableAlterColumn x) {
        return true;
    }

    default void endVisit(MySqlAlterTableAlterColumn x) {

    }

    default boolean visit(MySqlAlterTableForce x) {
        return true;
    }

    default void endVisit(MySqlAlterTableForce x) {

    }

    default boolean visit(MySqlAlterTableLock x) {
        return true;
    }

    default void endVisit(MySqlAlterTableLock x) {

    }

    default boolean visit(MySqlAlterTableOrderBy x) {
        return true;
    }

    default void endVisit(MySqlAlterTableOrderBy x) {

    }

    default boolean visit(MySqlAlterTableValidation x) {
        return true;
    }

    default void endVisit(MySqlAlterTableValidation x) {

    }

    default boolean visit(MySqlSubPartitionByKey x) {
        return true;
    }

    default void endVisit(MySqlSubPartitionByKey x) {

    }

    default boolean visit(MySqlSubPartitionByList x) {
        return true;
    }

    default void endVisit(MySqlSubPartitionByList x) {

    }

    default boolean visit(MySqlDeclareHandlerStatement x) {
        return true;
    }

    default void endVisit(MySqlDeclareHandlerStatement x) {

    }

    default boolean visit(MySqlDeclareConditionStatement x) {
        return true;
    }

    default void endVisit(MySqlDeclareConditionStatement x) {

    }

    default boolean visit(MySqlFlushStatement x) {
        return true;
    }

    default void endVisit(MySqlFlushStatement x) {

    }

    default boolean visit(MySqlEventSchedule x) {
        return true;
    }

    default void endVisit(MySqlEventSchedule x) {

    }

    default boolean visit(MySqlCreateEventStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateEventStatement x) {

    }

    default boolean visit(MySqlCreateAddLogFileGroupStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateAddLogFileGroupStatement x) {

    }

    default boolean visit(MySqlCreateServerStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateServerStatement x) {

    }

    default boolean visit(MySqlCreateTableSpaceStatement x) {
        return true;
    }

    default void endVisit(MySqlCreateTableSpaceStatement x) {

    }

    default boolean visit(MySqlAlterEventStatement x) {
        return true;
    }

    default void endVisit(MySqlAlterEventStatement x) {

    }

    default boolean visit(MySqlAlterLogFileGroupStatement x) {
        return true;
    }

    default void endVisit(MySqlAlterLogFileGroupStatement x) {

    }

    default boolean visit(MySqlAlterServerStatement x) {
        return true;
    }

    default void endVisit(MySqlAlterServerStatement x) {

    }

    default boolean visit(MySqlAlterTablespaceStatement x) {
        return true;
    }

    default void endVisit(MySqlAlterTablespaceStatement x) {

    }

    default boolean visit(MySqlChecksumTableStatement x) {
        return true;
    }

    default void endVisit(MySqlChecksumTableStatement x) {

    }

    default boolean visit(MySqlShowDatasourcesStatement x) {
        return true;
    }

    default void endVisit(MySqlShowDatasourcesStatement x) {

    }

    default boolean visit(MySqlShowNodeStatement x) {
        return true;
    }

    default void endVisit(MySqlShowNodeStatement x) {

    }

    default boolean visit(MySqlShowHelpStatement x) {
        return true;
    }

    default void endVisit(MySqlShowHelpStatement x) {

    }

    default boolean visit(MySqlFlashbackStatement x) {
        return true;
    }

    default void endVisit(MySqlFlashbackStatement x) {

    }

    default boolean visit(MySqlShowConfigStatement x) {
        return true;
    }

    default void endVisit(MySqlShowConfigStatement x) {

    }

    default boolean visit(MySqlShowPlanCacheStatement x) {
        return true;
    }

    default void endVisit(MySqlShowPlanCacheStatement x) {

    }

    default boolean visit(MySqlShowPhysicalProcesslistStatement x) {
        return true;
    }

    default void endVisit(MySqlShowPhysicalProcesslistStatement x) {

    }

    default boolean visit(MySqlRenameSequenceStatement x) {
        return true;
    }

    default void endVisit(MySqlRenameSequenceStatement x) {

    }

    default boolean visit(MySqlCheckTableStatement x) {
        return true;
    }

    default void endVisit(MySqlCheckTableStatement x) {

    }

    default boolean visit(MysqlCreateFullTextCharFilterStatement x) {
        return true;
    }

    default void endVisit(MysqlCreateFullTextCharFilterStatement x) {

    }

    default boolean visit(MysqlShowFullTextStatement x) {
        return true;
    }

    default void endVisit(MysqlShowFullTextStatement x) {

    }

    default boolean visit(MysqlShowCreateFullTextStatement x) {
        return true;
    }

    default void endVisit(MysqlShowCreateFullTextStatement x) {

    }

    default boolean visit(MysqlAlterFullTextStatement x) {
        return true;
    }

    default void endVisit(MysqlAlterFullTextStatement x) {

    }

    default boolean visit(MysqlDropFullTextStatement x) {
        return true;
    }

    default void endVisit(MysqlDropFullTextStatement x) {

    }

    default boolean visit(MysqlCreateFullTextTokenizerStatement x) {
        return true;
    }

    default void endVisit(MysqlCreateFullTextTokenizerStatement x) {

    }

    default boolean visit(MysqlCreateFullTextTokenFilterStatement x) {
        return true;
    }

    default void endVisit(MysqlCreateFullTextTokenFilterStatement x) {

    }

    default boolean visit(MysqlCreateFullTextAnalyzerStatement x) {
        return true;
    }

    default void endVisit(MysqlCreateFullTextAnalyzerStatement x) {

    }

    default boolean visit(MysqlCreateFullTextDictionaryStatement x) {
        return true;
    }

    default void endVisit(MysqlCreateFullTextDictionaryStatement x) {

    }

    default boolean visit(MySqlAlterTableAlterFullTextIndex x) {
        return true;
    }

    default void endVisit(MySqlAlterTableAlterFullTextIndex x) {

    }

    default boolean visit(MySqlExecuteForAdsStatement x) {
        return true;
    }

    default void endVisit(MySqlExecuteForAdsStatement x) {

    }

    default boolean visit(MySqlManageInstanceGroupStatement x) {
        return true;
    }

    default void endVisit(MySqlManageInstanceGroupStatement x) {

    }

    default boolean visit(MySqlRaftMemberChangeStatement x) {
        return true;
    }

    default void endVisit(MySqlRaftMemberChangeStatement x) {

    }

    default boolean visit(MySqlRaftLeaderTransferStatement x) {
        return true;
    }

    default void endVisit(MySqlRaftLeaderTransferStatement x) {

    }

    default boolean visit(MySqlMigrateStatement x) {
        return true;
    }

    default void endVisit(MySqlMigrateStatement x) {

    }

    default boolean visit(MySqlShowClusterNameStatement x) {
        return true;
    }

    default void endVisit(MySqlShowClusterNameStatement x) {

    }

    default boolean visit(MySqlShowJobStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowJobStatusStatement x) {

    }

    default boolean visit(MySqlShowMigrateTaskStatusStatement x) {
        return true;
    }

    default void endVisit(MySqlShowMigrateTaskStatusStatement x) {

    }

    default boolean visit(MySqlSubPartitionByValue x) {
        return true;
    }

    default void endVisit(MySqlSubPartitionByValue x) {

    }

    default boolean visit(MySqlExtPartition x) {
        return true;
    }

    default void endVisit(MySqlExtPartition x) {

    }

    default boolean visit(MySqlExtPartition.Item x) {
        return true;
    }

    default void endVisit(MySqlExtPartition.Item x) {

    }

    default boolean visit(MySqlJSONTableExpr x) {
        return true;
    }

    default void endVisit(MySqlJSONTableExpr x) {

    }


    default boolean visit(MySqlJSONTableExpr.Column x) {
        return true;
    }

    default void endVisit(MySqlJSONTableExpr.Column x) {

    }

    default boolean visit(MysqlAlterTableAlterCheck x) {
        return true;
    }

    default void endVisit(MysqlAlterTableAlterCheck x) {

    }
} //
