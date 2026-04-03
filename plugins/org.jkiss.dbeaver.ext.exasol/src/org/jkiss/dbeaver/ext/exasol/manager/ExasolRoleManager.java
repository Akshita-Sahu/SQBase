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
package org.jkiss.sqbase.ext.exasol.manager;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.exasol.ExasolConstants;
import org.jkiss.sqbase.ext.exasol.ExasolMessages;
import org.jkiss.sqbase.ext.exasol.model.ExasolDataSource;
import org.jkiss.sqbase.ext.exasol.model.ExasolPriority;
import org.jkiss.sqbase.ext.exasol.model.security.ExasolRole;
import org.jkiss.sqbase.ext.exasol.tools.ExasolUtils;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.messages.ModelMessages;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

public class ExasolRoleManager extends SQLObjectEditor<ExasolRole, ExasolDataSource> implements DBEObjectRenamer<ExasolRole> {

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Override
    public DBSObjectCache<ExasolDataSource, ExasolRole> getObjectsCache(
        ExasolRole object) {
        ExasolDataSource ds = (ExasolDataSource) object.getDataSource();
        return ds.getRoleCache();

    }

    @Override
    protected ExasolRole createDatabaseObject(@NotNull DBRProgressMonitor monitor,
                                              @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options)
    {
        return new ExasolRole((ExasolDataSource) container, "ROLE", "");
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options) {
        ExasolRole obj = command.getObject();

        String script = "CREATE ROLE " + DBUtils.getQuotedIdentifier(obj);

        actions.add(new SQLDatabasePersistAction("Create Role", script));

        if (!CommonUtils.isEmpty(obj.getDescription())) {
            actions.add(Comment(obj));
        }

    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
        ExasolRole obj = command.getObject();
        actions.add(new SQLDatabasePersistAction("Drop Role", "DROP ROLE " + DBUtils.getQuotedIdentifier(obj)));
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext,
                             @NotNull ExasolRole object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }

    @Override
    protected void processObjectRename(DBECommandContext commandContext,
                                       ExasolRole object, Map<String, Object> options, String newName) throws DBException {
        ObjectRenameCommand command = new ObjectRenameCommand(object, ModelMessages.model_jdbc_rename_object, options, newName);
        commandContext.addCommand(command, new RenameObjectReflector(), true);
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        ExasolRole obj = command.getObject();
        actions.add(
            new SQLDatabasePersistAction(
                "Rename ROLE",
                "RENAME ROLE " + DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getOldName()) + " to " +
                    DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getNewName()))
        );
    }

    private SQLDatabasePersistAction Comment(ExasolRole obj) {
        return new SQLDatabasePersistAction("Comment on Role", "COMMENT ON ROLE " + DBUtils.getQuotedIdentifier(obj) + " IS '" + ExasolUtils.quoteString(obj.getDescription()) + "'");
    }

    @Override
    protected void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList,
                                          @NotNull ObjectChangeCommand command, @NotNull Map<String, Object> options) {
        ExasolRole obj = command.getObject();
        ExasolPriority priority = obj.getPriority();

        if (command.getProperties().containsKey("description")) {

            actionList.add(Comment(obj));
        }

        if (command.getProperties().containsKey("priority")) {
        	String script = "";
        	if (ExasolConstants.CONSUMER_GROUP_CLASS.equals(priority.getClass().getName())) { 
        		script = String.format("ALTER ROLE %s SET CONSUMER_GROUP = %s", DBUtils.getQuotedIdentifier(obj), DBUtils.getQuotedIdentifier(priority));
                actionList.add(new SQLDatabasePersistAction(ExasolMessages.manager_assign_priority_group, script));
        	}
        	else if (ExasolConstants.PRIORITY_GROUP_CLASS.equals(priority.getClass().getName())) {
        		script = String.format("GRANT PRIORITY GROUP %s to %s", DBUtils.getQuotedIdentifier(priority), DBUtils.getQuotedIdentifier(obj));
                actionList.add(new SQLDatabasePersistAction(ExasolMessages.manager_assign_priority_group, script));
			} 
        }


    }

}