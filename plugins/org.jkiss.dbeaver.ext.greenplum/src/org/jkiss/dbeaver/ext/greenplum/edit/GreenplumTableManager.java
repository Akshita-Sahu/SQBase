/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2024 SQBase Corp and others
 * Copyright (C) 2019 Dmitriy Dubson (ddubson@pivotal.io)
 * Copyright (C) 2019 Gavin Shaw (gshaw@pivotal.io)
 * Copyright (C) 2019 Zach Marcin (zmarcin@pivotal.io)
 * Copyright (C) 2019 Nikhil Pawar (npawar@pivotal.io)
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
package org.jkiss.sqbase.ext.greenplum.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.greenplum.model.GreenplumTable;
import org.jkiss.sqbase.ext.postgresql.edit.PostgreTableManager;
import org.jkiss.sqbase.ext.postgresql.model.PostgreSchema;
import org.jkiss.sqbase.ext.postgresql.model.PostgreTableBase;
import org.jkiss.sqbase.ext.postgresql.model.PostgreTableContainer;
import org.jkiss.sqbase.ext.postgresql.model.PostgreTableForeign;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.messages.ModelMessages;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

/**
 * Greenplum table manager
 */
public class GreenplumTableManager extends PostgreTableManager {
    @Override
    protected GreenplumTable createDatabaseObject(@NotNull DBRProgressMonitor monitor,
                                                  @NotNull DBECommandContext context,
                                                  Object container,
                                                  Object copyFrom, @NotNull Map<String, Object> options) {
        GreenplumTable greenplumTable = new GreenplumTable((PostgreSchema) container);
        setNewObjectName(monitor, (PostgreSchema) container, greenplumTable);

        return greenplumTable;
    }

    <T extends PostgreTableBase> SQLDatabasePersistAction createDeleteAction(T table, Map<String, Object> options) {
        StringBuilder dropTableScript = new StringBuilder("DROP ")
                .append((table instanceof PostgreTableForeign ? "FOREIGN " : ""))
                .append("TABLE ")
                .append(table.getFullyQualifiedName(DBPEvaluationContext.DDL))
                .append((CommonUtils.getOption(options, OPTION_DELETE_CASCADE) ? " CASCADE" : ""));

        return new SQLDatabasePersistAction(ModelMessages.model_jdbc_drop_table, dropTableScript.toString());
    }

    @Nullable
    @Override
    public DBSObjectCache<PostgreTableContainer, PostgreTableBase> getObjectsCache(PostgreTableBase object) {
        return object.getContainer().getSchema().getTableCache();
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectDeleteCommand command,
                                          @NotNull Map<String, Object> options) {
        actions.add(createDeleteAction(command.getObject(), options));
    }
}
