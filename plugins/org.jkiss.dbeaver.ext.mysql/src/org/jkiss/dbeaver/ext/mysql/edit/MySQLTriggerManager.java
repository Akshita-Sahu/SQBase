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

package org.jkiss.sqbase.ext.mysql.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.mysql.model.MySQLCatalog;
import org.jkiss.sqbase.ext.mysql.model.MySQLExecutionContext;
import org.jkiss.sqbase.ext.mysql.model.MySQLTable;
import org.jkiss.sqbase.ext.mysql.model.MySQLTrigger;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.struct.SQLTriggerManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

/**
 * MySQLTriggerManager
 */
public class MySQLTriggerManager extends SQLTriggerManager<MySQLTrigger, MySQLTable> {

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, MySQLTrigger> getObjectsCache(MySQLTrigger object)
    {
        return object.getCatalog().getTriggerCache();
    }

    @Override
    protected MySQLTrigger createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, final Object container, Object copyFrom, @NotNull Map<String, Object> options)
    {
        MySQLTable table = (MySQLTable) container;
        return new MySQLTrigger(table.getContainer(), table, "NewTrigger");
    }

    protected void createOrReplaceTriggerQuery(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull MySQLTrigger trigger, boolean create) {
        if (trigger.isPersisted()) {
            actions.add(
                new SQLDatabasePersistAction("Drop trigger",
                    "DROP TRIGGER IF EXISTS " + trigger.getFullyQualifiedName(DBPEvaluationContext.DDL))
            );
        }
        MySQLCatalog curCatalog = ((MySQLExecutionContext)executionContext).getDefaultCatalog();
        if (curCatalog != trigger.getCatalog()) {
            actions.add(new SQLDatabasePersistAction("Set current schema ", "USE " + DBUtils.getQuotedIdentifier(trigger.getCatalog()), false)); //$NON-NLS-2$
        }

        actions.add(new SQLDatabasePersistAction("Create trigger", trigger.getBody(), true)); //$NON-NLS-2$

        if (curCatalog != null && curCatalog != trigger.getCatalog()) {
            actions.add(new SQLDatabasePersistAction("Set current schema ", "USE " + DBUtils.getQuotedIdentifier(curCatalog), false)); //$NON-NLS-2$
        }
    }

}

