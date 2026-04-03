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
package org.jkiss.sqbase.ext.postgresql.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.postgresql.model.PostgreDataType;
import org.jkiss.sqbase.ext.postgresql.model.PostgreSchema;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCException;
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

public class PostgreDataTypeManager extends SQLObjectEditor<PostgreDataType, PostgreSchema> implements DBEObjectRenamer<PostgreDataType> {

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return false;
    }

    @Override
    protected PostgreDataType createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBException {
        throw new DBCException("Not Implemented");
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options) throws DBException {

    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) throws DBException {
        actions.add(
                new SQLDatabasePersistAction("Drop data type", "DROP TYPE " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL)) //$NON-NLS-2$
        );
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, PostgreDataType> getObjectsCache(PostgreDataType object) {
        return object.getParentObject().getDataTypeCache();
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext, @NotNull PostgreDataType object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        PostgreDataType dataType = command.getObject();
        actions.add(
                new SQLDatabasePersistAction(
                        "Rename data type",
                        "ALTER TYPE " + dataType.getFullyQualifiedName(DBPEvaluationContext.DDL) + //$NON-NLS-1$
                                " RENAME TO " + DBUtils.getQuotedIdentifier(dataType.getDataSource(), command.getNewName())) //$NON-NLS-1$
        );
    }

    @Override
    protected void addObjectExtraActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull NestedObjectCommand<PostgreDataType, PropertyHandler> command, @NotNull Map<String, Object> options) throws DBException {
        PostgreDataType dataType = command.getObject();
        if (command.hasProperty(DBConstants.PROP_ID_DESCRIPTION)) {
            actions.add(new SQLDatabasePersistAction(
                    "Comment data type",
                    "COMMENT ON TYPE " + dataType.getFullyQualifiedName(DBPEvaluationContext.DDL) +
                            " IS " + SQLUtils.quoteString(dataType, CommonUtils.notEmpty(dataType.getDescription()))));
        }
    }
}
