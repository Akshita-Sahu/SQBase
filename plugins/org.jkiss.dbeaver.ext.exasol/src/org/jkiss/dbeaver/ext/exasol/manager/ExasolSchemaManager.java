/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2017 Karl Griesser (fullref@gmail.com)
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
import org.jkiss.sqbase.ext.exasol.ExasolMessages;
import org.jkiss.sqbase.ext.exasol.model.ExasolDataSource;
import org.jkiss.sqbase.ext.exasol.model.ExasolSchema;
import org.jkiss.sqbase.ext.exasol.model.ExasolVirtualSchema;
import org.jkiss.sqbase.ext.exasol.tools.ExasolUtils;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectManager;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.exec.DBCFeatureNotSupportedException;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.navigator.DBNDatabaseFolder;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class ExasolSchemaManager
    extends SQLObjectEditor<ExasolSchema, ExasolDataSource> implements DBEObjectRenamer<ExasolSchema> {


    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Override
    public DBSObjectCache<? extends DBSObject, ExasolSchema> getObjectsCache(
        ExasolSchema object) {
        ExasolDataSource source = object.getDataSource();
        return source.getSchemaCache();
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return super.canCreateObject(container);
    }

    @Override
    protected ExasolSchema createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBCException {
        Object navContainer = options.get(DBEObjectManager.OPTION_CONTAINER);
        boolean virtSchema = navContainer instanceof DBNDatabaseFolder && ((DBNDatabaseFolder) navContainer).getChildrenClass() == ExasolVirtualSchema.class;
        if (virtSchema) {
            throw new DBCFeatureNotSupportedException();
        }
        return new ExasolSchema((ExasolDataSource) container, "NEW_SCHEMA", "");
    }

    private void changeLimit(List<DBEPersistAction> actions, ExasolSchema schema, BigDecimal limit) {
        String script = String.format("ALTER SCHEMA %s SET RAW_SIZE_LIMIT = %d", DBUtils.getQuotedIdentifier(schema), limit.longValue());
        actions.add(
            new SQLDatabasePersistAction(ExasolMessages.manager_schema_raw_limit, script)
        );
    }

    private void changeOwner(List<DBEPersistAction> actions, ExasolSchema schema, String owner) {
        String script = "ALTER SCHEMA " + DBUtils.getQuotedIdentifier(schema) + " CHANGE OWNER  " + owner;
        actions.add(
            new SQLDatabasePersistAction(ExasolMessages.manager_schema_owner, script)
        );

    }


    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options) {
        final ExasolSchema schema = command.getObject();

        String script = "CREATE SCHEMA " + DBUtils.getQuotedIdentifier(schema);

        actions.add(
            new SQLDatabasePersistAction(ExasolMessages.manager_schema_create, script)
        );
        String owner = schema.getOwner();
        if (owner != null) {
            changeOwner(actions, schema, owner);
        }

        if (schema.getRawObjectSizeLimit() != null) {
            changeLimit(actions, schema, schema.getRawObjectSizeLimit());
        }
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
    	if (command.getObject() instanceof ExasolVirtualSchema)
    	{
            actions.add(
                    new SQLDatabasePersistAction("Drop schema", "DROP VIRTUAL SCHEMA " + DBUtils.getQuotedIdentifier(command.getObject()) + " CASCADE") //$NON-NLS-2$
                );
    	} else {
            actions.add(
                    new SQLDatabasePersistAction("Drop schema", "DROP SCHEMA " + DBUtils.getQuotedIdentifier(command.getObject()) + " CASCADE") //$NON-NLS-2$
                );
    		
    	}
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        ExasolSchema obj = command.getObject();
        actions.add(
            new SQLDatabasePersistAction(
                "Rename Schema",
                "RENAME SCHEMA " + DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getOldName()) + " to " +
                    DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getNewName()))
        );
    }

    @Override
    public void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList, @NotNull ObjectChangeCommand command, @NotNull Map<String, Object> options) {
        ExasolSchema schema = command.getObject();

        if (command.getProperties().size() >= 1) {
            if (command.getProperties().containsKey(DBConstants.PROP_ID_DESCRIPTION)) {
                String script = "COMMENT ON SCHEMA " + DBUtils.getQuotedIdentifier(schema) + " IS '" + ExasolUtils.quoteString(CommonUtils.notNull(schema.getDescription(), "")) + "'";
                actionList.add(
                    new SQLDatabasePersistAction("Change comment on Schema", script)
                );
            }
            if (command.getProperties().containsKey("owner")) {
                changeOwner(actionList, schema, schema.getOwner());
            }

            if (command.getProperties().containsKey("rawObjectSizeLimit")) {
                changeLimit(actionList, schema, schema.getRawObjectSizeLimit());
            }

        }
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext,
                             @NotNull ExasolSchema object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }


}
