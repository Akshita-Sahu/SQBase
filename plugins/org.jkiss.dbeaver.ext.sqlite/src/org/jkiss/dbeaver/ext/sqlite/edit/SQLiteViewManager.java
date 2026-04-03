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
package org.jkiss.sqbase.ext.sqlite.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.ext.generic.edit.GenericViewManager;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.util.List;
import java.util.Map;

public class SQLiteViewManager extends GenericViewManager {
    @Override
    protected void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull SQLObjectEditor<GenericTableBase, GenericStructContainer>.ObjectChangeCommand command, @NotNull Map<String, Object> options) {
        if (!command.hasProperty(DBConstants.PROP_ID_DESCRIPTION) || command.getProperties().size() > 1) {
            actions.add(new SQLDatabasePersistAction(
                "Drop view",
                "DROP " + getViewType(command.getObject()) + " " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL)
            ));
        }
        super.addObjectModifyActions(monitor, executionContext, actions, command, options);
    }
}
