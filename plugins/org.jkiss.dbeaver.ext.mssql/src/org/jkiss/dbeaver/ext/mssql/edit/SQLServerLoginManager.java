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
package org.jkiss.sqbase.ext.mssql.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.mssql.model.SQLServerDataSource;
import org.jkiss.sqbase.ext.mssql.model.SQLServerLogin;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLUtils;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

public class SQLServerLoginManager extends SQLObjectEditor<SQLServerLogin, SQLServerDataSource> implements DBEObjectRenamer<SQLServerLogin> {

    @Override
    protected SQLServerLogin createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBException {
        return new SQLServerLogin((SQLServerDataSource) container, "new_login");
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options) throws DBException {
        SQLServerLogin login = command.getObject();
        command.setDisableSessionLogging(true); // Hide password from Query Manager
        StringBuilder script = new StringBuilder(64);
        script.append("CREATE LOGIN ").append(DBUtils.getQuotedIdentifier(login.getDataSource(), login.getName()));
        if (CommonUtils.isNotEmpty(login.getPassword())) {
            script.append(" WITH PASSWORD =").append(SQLUtils.quoteString(login.getDataSource(), login.getPassword()));
        }
        actions.add(
                new SQLDatabasePersistAction("Create login", script.toString()) //$NON-NLS-2$
        );
    }

    @Override
    protected void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList, @NotNull ObjectChangeCommand command, @NotNull Map<String, Object> options) throws DBException {
        SQLServerLogin login = command.getObject();
        if (command.hasProperty("disabled")) {
            actionList.add(new SQLDatabasePersistAction(
                    "Alter login",
                    "ALTER LOGIN " + DBUtils.getQuotedIdentifier(login.getDataSource(), login.getName()) + (login.isDisabled() ? " DISABLE" : " ENABLE") //$NON-NLS-2$
            ));
        }
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) throws DBException {
        actions.add(
                new SQLDatabasePersistAction("Drop login", "DROP LOGIN " + DBUtils.getQuotedIdentifier(command.getObject())) //$NON-NLS-2$
        );
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, SQLServerLogin> getObjectsCache(SQLServerLogin object) {
        return object.getDataSource().getServerLoginCache();
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        SQLServerLogin login = command.getObject();
        SQLServerDataSource dataSource = login.getDataSource();

        actions.add(new SQLDatabasePersistAction(
                "Rename login",
                "ALTER LOGIN " + DBUtils.getQuotedIdentifier(dataSource, command.getOldName()) + " WITH NAME = " + DBUtils.getQuotedIdentifier(dataSource, command.getNewName()) //$NON-NLS-2$
        ));
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext, @NotNull SQLServerLogin object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }
}
