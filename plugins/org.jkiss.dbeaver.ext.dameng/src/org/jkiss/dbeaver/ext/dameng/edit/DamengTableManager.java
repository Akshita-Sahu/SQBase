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

package org.jkiss.sqbase.ext.dameng.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.generic.edit.GenericTableManager;
import org.jkiss.sqbase.ext.generic.model.GenericDataSource;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.util.List;
import java.util.Map;

/**
 * @author Shengkai Bai
 */
public class DamengTableManager extends GenericTableManager implements DBEObjectRenamer<GenericTableBase> {

    @Override
    public boolean canEditObject(@NotNull GenericTableBase object) {
        return true;
    }

    @Override
    protected boolean isIncludeDropInDDL(GenericTableBase table) {
        return false;
    }

    @Override
    public void renameObject(DBECommandContext commandContext, GenericTableBase object, Map<String, Object> options, String newName) throws DBException {
        if (object.isView()) {
            throw new DBException("View rename is not supported");
        }
        processObjectRename(commandContext, object, options, newName);
    }

    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull SQLObjectEditor<GenericTableBase, GenericStructContainer>.ObjectRenameCommand command, @NotNull Map<String, Object> options) {
        final GenericDataSource dataSource = command.getObject().getDataSource();
        actions.add(
                new SQLDatabasePersistAction(
                        "Rename table",
                        "ALTER TABLE " + (command.getObject().getSchema() != null ?
                                DBUtils.getQuotedIdentifier(dataSource, command.getObject().getSchema().getName())
                                        + "." : "") + DBUtils.getQuotedIdentifier(dataSource, command.getOldName()) +
                                " RENAME TO " + DBUtils.getQuotedIdentifier(dataSource, command.getNewName()))
        );
    }
}
