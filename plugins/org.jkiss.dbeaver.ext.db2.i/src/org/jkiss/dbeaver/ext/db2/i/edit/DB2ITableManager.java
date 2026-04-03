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
package org.jkiss.sqbase.ext.db2.i.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.db2.i.model.DB2IConstraint;
import org.jkiss.sqbase.ext.generic.edit.GenericTableManager;
import org.jkiss.sqbase.ext.generic.model.*;
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

public class DB2ITableManager extends GenericTableManager implements DBEObjectRenamer<GenericTableBase> {

    private static final Class<? extends DBSObject>[] CHILD_TYPES = CommonUtils.array(
        GenericTableColumn.class,
        DB2IConstraint.class,
        GenericTableForeignKey.class,
        GenericTableIndex.class
    );

    @NotNull
    @Override
    public Class<? extends DBSObject>[] getChildTypes() {
        return CHILD_TYPES;
    }

    @Override
    public Collection<? extends DBSObject> getChildObjects(DBRProgressMonitor monitor, GenericTableBase object, Class<? extends DBSObject> childType) throws DBException {
        if (childType == DB2IConstraint.class) {
            return object.getConstraints(monitor);
        }
        return super.getChildObjects(monitor, object, childType);
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext, @NotNull GenericTableBase object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        GenericTableBase tableBase = command.getObject();
        GenericDataSource dataSource = tableBase.getDataSource();
        GenericStructContainer container = tableBase.getParentObject();
        if (container != null) {
            actions.add(
                new SQLDatabasePersistAction(
                    "Rename table",
                    "RENAME TABLE " + DBUtils.getFullyQualifiedName(dataSource, container.getName(), command.getOldName()) + //$NON-NLS-1$
                        " TO " + DBUtils.getQuotedIdentifier(dataSource, command.getNewName())) //$NON-NLS-1$
            );
        }
    }
}
