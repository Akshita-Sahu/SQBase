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
package org.jkiss.sqbase.ext.altibase.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.altibase.model.AltibaseSequence;
import org.jkiss.sqbase.ext.generic.edit.GenericSequenceManager;
import org.jkiss.sqbase.ext.generic.model.GenericSequence;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

public class AltibaseSequenceManager extends GenericSequenceManager {

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_EDITOR_ON_CREATE;
    }
    
    @Override
    public boolean canEditObject(@NotNull GenericSequence object) {
        return true;
    }

    @Override
    protected void addObjectCreateActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull ObjectCreateCommand command,
        @NotNull Map<String, Object> options
    ) {
        actions.add(new SQLDatabasePersistAction("Create sequence", 
                        ((AltibaseSequence) (command.getObject())).buildStatement(false)));
    }

    @Override
    protected AltibaseSequence createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        final Object container,
        Object from,
        @NotNull Map<String, Object> options
    ) {
        return new AltibaseSequence((GenericStructContainer) container, getBaseObjectName());
    }
    
    @Override
    protected void addObjectModifyActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actionList,
        @NotNull ObjectChangeCommand command,
        @NotNull Map<String, Object> options
    ) {
        actionList.add(new SQLDatabasePersistAction("Alter sequence", 
                        ((AltibaseSequence) (command.getObject())).buildStatement(true)));
    }
    
    @Override
    protected void validateObjectProperties(
        DBRProgressMonitor monitor,
        ObjectChangeCommand command,
        Map<String, Object> options
    ) throws DBException {
        if (CommonUtils.isEmpty(command.getObject().getName())) {
            throw new DBException("Sequence name cannot be empty");
        }
    }
}
