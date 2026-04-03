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
package org.jkiss.sqbase.ext.duckdb.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.ext.duckdb.model.DuckDBSequence;
import org.jkiss.sqbase.ext.generic.edit.GenericSequenceManager;
import org.jkiss.sqbase.ext.generic.model.GenericSequence;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DuckDBSequenceManager extends GenericSequenceManager {

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        // TODO: We need to add treeInjection in the plugin.xml to work with this
        //return DBWorkbench.getPlatform().getWorkspace().hasRealmPermission(RMConstants.PERMISSION_METADATA_EDITOR);
        return false;
    }

    @Override
    protected GenericSequence createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        Object container,
        Object copyFrom,
        @NotNull Map<String, Object> options
    ) {
        GenericStructContainer structContainer = (GenericStructContainer) container;
        DuckDBSequence sequence = new DuckDBSequence(structContainer, getBaseObjectName().toLowerCase(Locale.ROOT));
        setNewObjectName(monitor, structContainer, sequence);
        return sequence;
    }

    @Override
    protected void addObjectCreateActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull SQLObjectEditor<GenericSequence, GenericStructContainer>.ObjectCreateCommand command,
        @NotNull Map<String, Object> options
    ) {
        DuckDBSequence sequence = (DuckDBSequence) command.getObject();
        actions.add(new SQLDatabasePersistAction("Create sequence", sequence.getObjectDefinitionText(monitor, options)));
    }
}
