/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2024 SQBase Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.sqbase.model.sql.completion;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPImage;
import org.jkiss.sqbase.model.DBPKeywordType;
import org.jkiss.sqbase.model.DBPNamedObject;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.sql.SQLSyntaxManager;
import org.jkiss.sqbase.model.sql.parser.SQLRuleManager;

import java.util.Map;

/**
 * SQL Completion proposal
 */
public interface SQLCompletionContext {

    int PROPOSAL_CASE_DEFAULT                       = 0;
    int PROPOSAL_CASE_UPPER                         = 1;
    int PROPOSAL_CASE_LOWER                         = 2;

    DBPDataSource getDataSource();

    @Nullable
    DBCExecutionContext getExecutionContext();

    SQLSyntaxManager getSyntaxManager();

    SQLRuleManager getRuleManager();

    boolean isUseFQNames();

    boolean isReplaceWords();

    boolean isShowServerHelp();

    boolean isUseShortNames();

    int getInsertCase();

    boolean isSearchProcedures();

    boolean isSearchInsideNames();

    boolean isSortAlphabetically();

    boolean isSearchGlobally();

    boolean isHideDuplicates();

    boolean isShowValues();

    boolean isForceQualifiedColumnNames();

    SQLCompletionProposalBase createProposal(
        @NotNull SQLCompletionRequest request,
        @NotNull String displayString,
        @NotNull String replacementString,
        int cursorPosition,
        @Nullable DBPImage image,
        @NotNull DBPKeywordType proposalType,
        @Nullable String description,
        @Nullable DBPNamedObject object,
        @NotNull Map<String, Object> params);

}
