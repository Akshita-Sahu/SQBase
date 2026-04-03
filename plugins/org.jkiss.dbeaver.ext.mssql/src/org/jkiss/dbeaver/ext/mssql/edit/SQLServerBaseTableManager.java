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
package org.jkiss.sqbase.ext.mssql.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ModelPreferences;
import org.jkiss.sqbase.ext.mssql.SQLServerUtils;
import org.jkiss.sqbase.ext.mssql.model.*;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBPScriptObject;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistActionComment;
import org.jkiss.sqbase.model.impl.sql.edit.struct.SQLTableManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLUtils;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * SQLServer table manager
 */
public abstract class SQLServerBaseTableManager<OBJECT extends SQLServerTableBase> extends SQLTableManager<OBJECT, SQLServerSchema> implements DBEObjectRenamer<OBJECT> {

    @Override
    public DBSObjectCache<SQLServerSchema, OBJECT> getObjectsCache(OBJECT object) {
        return (DBSObjectCache) object.getSchema().getTableCache();
    }

    @Override
    protected void addObjectExtraActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList, @NotNull NestedObjectCommand<OBJECT, PropertyHandler> command, @NotNull Map<String, Object> options) throws DBException {
        final OBJECT table = command.getObject();
        if (command.getProperty(DBConstants.PROP_ID_DESCRIPTION) != null) {
            boolean isUpdate = SQLServerUtils.isCommentSet(
                monitor,
                table.getDatabase(),
                SQLServerObjectClass.OBJECT_OR_COLUMN,
                table.getObjectId(),
                0);
            actionList.add(
                new SQLDatabasePersistAction(
                    "Add table comment",
                    "EXEC " + SQLServerUtils.getSystemTableName(table.getDatabase(), isUpdate ? "sp_updateextendedproperty" : "sp_addextendedproperty") +
                        " 'MS_Description', " + SQLUtils.quoteString(table, table.getDescription()) + "," +
                        " 'schema', " + SQLUtils.quoteString(table, table.getSchema().getName()) + "," +
                        " '" + (table.isView() ? "view" : "table") + "', " + SQLUtils.quoteString(table, table.getName())));
        }

        if (CommonUtils.getOption(options, DBPScriptObject.OPTION_INCLUDE_NESTED_OBJECTS)) {
            final Collection<SQLServerExtendedProperty> extendedProperties = new ArrayList<>(table.getExtendedProperties(monitor));
            for (SQLServerTableColumn attribute : CommonUtils.safeCollection(table.getAttributes(monitor))) {
                extendedProperties.addAll(attribute.getExtendedProperties(monitor));
            }
            if (!extendedProperties.isEmpty()) {
                if (table.getDataSource().getContainer().getPreferenceStore().getBoolean(ModelPreferences.META_EXTRA_DDL_INFO)) {
                    actionList.add(new SQLDatabasePersistActionComment(
                        table.getDataSource(),
                        "Extended properties"
                    ));
                }

                for (SQLServerExtendedProperty extendedProperty : extendedProperties) {
                    actionList.add(new SQLDatabasePersistAction(
                        "Add extended property",
                        extendedProperty.getObjectDefinitionText(monitor, DBPScriptObject.EMPTY_OPTIONS)
                    ));
                }
            }
        }
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options)
    {
        OBJECT object = command.getObject();
        actions.add(
            new SQLDatabasePersistAction(
                "Rename table",
                "EXEC " + SQLServerUtils.getSystemTableName(object.getDatabase(), "sp_rename") +
                    " N'" + object.getSchema().getFullyQualifiedName(DBPEvaluationContext.DML) + "." + DBUtils.getQuotedIdentifier(object.getDataSource(), command.getOldName()) +
                    "', " + SQLUtils.quoteString(object.getDataSource(), command.getNewName()) + ", 'OBJECT'")
        );
    }

    @Override
    public boolean canEditObject(@NotNull OBJECT object) {
        return !SQLServerUtils.isTableType(object) && super.canEditObject(object);
    }

    @Override
    public boolean canDeleteObject(@NotNull OBJECT object) {
        return !SQLServerUtils.isTableType(object) && super.canDeleteObject(object);
    }

}
