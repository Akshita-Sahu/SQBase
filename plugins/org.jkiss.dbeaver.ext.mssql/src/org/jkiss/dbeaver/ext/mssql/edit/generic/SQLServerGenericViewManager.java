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
package org.jkiss.sqbase.ext.mssql.edit.generic;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.ext.generic.edit.GenericViewManager;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.ext.mssql.SQLServerUtils;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.util.List;
import java.util.Map;

public class SQLServerGenericViewManager extends GenericViewManager {

    @Override
    protected void addObjectModifyActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actionList,
        @NotNull SQLObjectEditor<GenericTableBase, GenericStructContainer>.ObjectChangeCommand command,
        @NotNull Map<String, Object> options
    ) {
        actionList.add(new SQLDatabasePersistAction(
            "Create view",
            SQLServerUtils.changeCreateToCreateOrReplace(command.getObject().getDDL())));
    }
}
