/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2025 SQBase Corp and others
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
package org.jkiss.sqbase.ext.oracle.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.oracle.model.OracleTableBase;
import org.jkiss.sqbase.ext.oracle.model.OracleTableTrigger;
import org.jkiss.sqbase.ext.oracle.model.OracleUtils;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.struct.SQLTriggerManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * OracleTableTriggerManager
 */
public class OracleTableTriggerManager extends SQLTriggerManager<OracleTableTrigger, OracleTableBase> {

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, OracleTableTrigger> getObjectsCache(OracleTableTrigger object) {
        return object.getTable().getSchema().tableTriggerCache;
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return container instanceof OracleTableBase;
    }

    @Override
    protected OracleTableTrigger createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, final Object container, Object copyFrom, @NotNull Map<String, Object> options) {
        OracleTableBase table = (OracleTableBase) container;
        return new OracleTableTrigger(table, "NEW_TRIGGER");
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
        actions.add(
            new SQLDatabasePersistAction("Drop trigger", "DROP TRIGGER " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL)) //$NON-NLS-2$
        );
    }

    protected void createOrReplaceTriggerQuery(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull OracleTableTrigger trigger,
        boolean create
    ) {
        String source = OracleUtils.normalizeSourceName(monitor, trigger, false);
        if (source == null) {
            return;
        }
        String script = source;
        if (!script.toUpperCase(Locale.ENGLISH).trim().contains("CREATE ")) {
            script = "CREATE OR REPLACE " + script;
        }
        actions.add(new SQLDatabasePersistAction("Create trigger", script, true)); //$NON-NLS-2$
        OracleUtils.addSchemaChangeActions(executionContext, actions, trigger);
    }

}

