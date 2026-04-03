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

package org.jkiss.sqbase.ext.dameng.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.ext.dameng.model.DamengSchema;
import org.jkiss.sqbase.ext.dameng.model.DamengSequence;
import org.jkiss.sqbase.ext.generic.edit.GenericSequenceManager;
import org.jkiss.sqbase.ext.generic.model.GenericSequence;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.rm.RMConstants;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.runtime.DBWorkbench;

import java.util.List;
import java.util.Map;

/**
 * @author Shengkai Bai
 */
public class DamengSequenceManager extends GenericSequenceManager {

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return DBWorkbench.getPlatform().getWorkspace().hasRealmPermission(RMConstants.PERMISSION_METADATA_EDITOR);
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_EDITOR_ON_CREATE;
    }

    @Override
    protected DamengSequence createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        Object container,
        Object copyFrom,
        @NotNull Map<String, Object> options
    ) {
        GenericStructContainer structContainer = (GenericStructContainer) container;
        DamengSchema schema = (DamengSchema) structContainer.getSchema();
        DamengSequence sequence = new DamengSequence((GenericStructContainer) container, getBaseObjectName());
        setNewObjectName(monitor, schema, sequence);
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
        DamengSequence sequence = (DamengSequence) command.getObject();
        actions.add(new SQLDatabasePersistAction("Create sequence", sequence.buildStatement(false)));
    }

    @Override
    protected void addObjectModifyActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actionList,
        @NotNull SQLObjectEditor<GenericSequence, GenericStructContainer>.ObjectChangeCommand command,
        @NotNull Map<String, Object> options
    ) {
        DamengSequence sequence = (DamengSequence) command.getObject();
        actionList.add(new SQLDatabasePersistAction("Alter Sequence", sequence.buildStatement(true)));
    }
}
