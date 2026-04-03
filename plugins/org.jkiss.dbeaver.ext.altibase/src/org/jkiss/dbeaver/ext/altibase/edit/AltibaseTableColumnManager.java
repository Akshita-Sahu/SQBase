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
package org.jkiss.sqbase.ext.altibase.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.generic.edit.GenericTableColumnManager;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.ext.generic.model.GenericTableColumn;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.messages.ModelMessages;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.util.List;
import java.util.Map;

public class AltibaseTableColumnManager extends GenericTableColumnManager 
    implements DBEObjectRenamer<GenericTableColumn> {

    @Override
    public boolean canRenameObject(GenericTableColumn object) {
        return true;
    }

    @Override
    public void renameObject(
        @NotNull DBECommandContext commandContext,
        @NotNull GenericTableColumn object,
        @NotNull Map<String, Object> options,
        @NotNull String newName
    ) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }

    @Override
    protected void addObjectCreateActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull ObjectCreateCommand command,
        @NotNull Map<String, Object> options
    ) {
        final GenericTableBase table = command.getObject().getParentObject();

        String sql = "ALTER TABLE " + DBUtils.getObjectFullName(table, DBPEvaluationContext.DDL) +
            " ADD COLUMN (" + getNestedDeclaration(monitor, table, command, options) + ")";
        actions.add(
            new SQLDatabasePersistAction(
                ModelMessages.model_jdbc_create_new_table_column,
                sql));
    }
    
    @Override
    protected void addObjectRenameActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull ObjectRenameCommand command,
        @NotNull Map<String, Object> options
    ) {
        final GenericTableColumn column = command.getObject();

        actions.add(
            new SQLDatabasePersistAction(
                "Rename column",
                "ALTER TABLE " + column.getTable().getFullyQualifiedName(DBPEvaluationContext.DDL) + " RENAME COLUMN " +
                    DBUtils.getQuotedIdentifier(column.getDataSource(), command.getOldName()) +
                    " TO " + DBUtils.getQuotedIdentifier(column.getDataSource(), command.getNewName())));
    }
}
