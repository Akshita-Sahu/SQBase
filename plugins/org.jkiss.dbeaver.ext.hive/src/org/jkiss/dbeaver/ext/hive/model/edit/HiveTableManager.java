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
package org.jkiss.sqbase.ext.hive.model.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.generic.edit.GenericTableManager;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.ext.generic.model.GenericTableForeignKey;
import org.jkiss.sqbase.ext.generic.model.GenericUniqueKey;
import org.jkiss.sqbase.ext.hive.model.HiveIndex;
import org.jkiss.sqbase.ext.hive.model.HiveTable;
import org.jkiss.sqbase.ext.hive.model.HiveTableColumn;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.utils.CommonUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HiveTableManager extends GenericTableManager implements DBEObjectRenamer<GenericTableBase> {

    private static final Class<? extends DBSObject>[] CHILD_TYPES = CommonUtils.array(
            HiveTableColumn.class,
            GenericUniqueKey.class,
            GenericTableForeignKey.class,
            HiveIndex.class
    );

    @NotNull
    @Override
    public Class<? extends DBSObject>[] getChildTypes() {
        return CHILD_TYPES;
    }

    @Override
    public Collection<? extends DBSObject> getChildObjects(DBRProgressMonitor monitor, GenericTableBase object, Class<? extends DBSObject> childType) throws DBException {
        if (childType == HiveTableColumn.class) {
            return object.getAttributes(monitor);
        } else if (childType == HiveIndex.class) {
            return object.getIndexes(monitor);
        }
        return super.getChildObjects(monitor, object, childType);
    }

    @Override
    public boolean canDeleteObject(@NotNull GenericTableBase object) {
        return !((HiveTable)object).isIndexTable();
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        actions.add(
                new SQLDatabasePersistAction(
                        "Rename table",
                        "ALTER TABLE " + DBUtils.getQuotedIdentifier(command.getObject().getSchema()) + "." + DBUtils.getQuotedIdentifier(command.getObject().getDataSource(), command.getOldName()) + //$NON-NLS-1$
                                " RENAME TO " + DBUtils.getQuotedIdentifier(command.getObject().getDataSource(), command.getNewName())) //$NON-NLS-1$
        );
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
        GenericTableBase table = command.getObject();
        if (table.getTableType().equals("INDEX_TABLE")) {
            actions.add(
                    new SQLDatabasePersistAction("Drop index table", "DROP INDEX " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL))); //$NON-NLS-2$
            return;
        }
        super.addObjectDeleteActions(monitor, executionContext, actions, command, options);
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext, @NotNull GenericTableBase object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }
}
