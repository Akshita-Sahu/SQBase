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
package org.jkiss.sqbase.ext.mysql.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.mysql.model.MySQLCatalog;
import org.jkiss.sqbase.ext.mysql.model.MySQLEvent;
import org.jkiss.sqbase.ext.mysql.model.MySQLExecutionContext;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBPScriptObject;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MySQLEventManager extends SQLObjectEditor<MySQLEvent, MySQLCatalog> {

    public DBSObjectCache<MySQLCatalog, MySQLEvent> getObjectsCache(MySQLEvent object) {
        return object.getCatalog().getEventCache();
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_EDITOR_ON_CREATE;
    }

    @Nullable
    @Override
    protected MySQLEvent createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options) {
        return new MySQLEvent((MySQLCatalog) container, "NewEvent");
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options) {
        final MySQLEvent event = command.getObject();
        final StringBuilder script = new StringBuilder();
        try {
            script.append(event.getObjectDefinitionText(monitor, options));
        } catch (DBException e) {
            log.error(e);
        }

        makeEventActions(actions, executionContext, event, false, script.toString());
    }

    @Override
    protected void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList, @NotNull ObjectChangeCommand command, @NotNull Map<String, Object> options) {
        final MySQLEvent event = command.getObject();
        final StringBuilder script = new StringBuilder();
        options = new LinkedHashMap<>(options);
        options.put(DBPScriptObject.OPTION_OBJECT_ALTER, true);
        try {
            script.append(event.getObjectDefinitionText(monitor, options));
        } catch (DBException e) {
            log.error(e);
        }
        String ddlText = script.toString();
        if (ddlText.startsWith("CREATE ") || ddlText.startsWith("create ")) {
            ddlText = "ALTER " + ddlText.substring(7);
        }

        makeEventActions(actionList, executionContext, event, true, ddlText);
    }

    private void makeEventActions(List<DBEPersistAction> actionList, DBCExecutionContext executionContext, MySQLEvent event, boolean alter, String ddlText) {
        MySQLCatalog curCatalog = ((MySQLExecutionContext)executionContext).getDefaultCatalog();
        if (curCatalog != event.getCatalog()) {
            actionList.add(new SQLDatabasePersistAction("Set current schema ", "USE " + DBUtils.getQuotedIdentifier(event.getCatalog()), false)); //$NON-NLS-2$
        }
        actionList.add(new SQLDatabasePersistAction(alter ? "Alter event" : "Create event", ddlText)); // $NON-NLS-2$
        if (curCatalog != null && curCatalog != event.getCatalog()) {
            actionList.add(new SQLDatabasePersistAction("Set current schema ", "USE " + DBUtils.getQuotedIdentifier(curCatalog), false)); //$NON-NLS-2$
        }
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
        actions.add(new SQLDatabasePersistAction("Drop event", "DROP EVENT " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL)));
    }

}
