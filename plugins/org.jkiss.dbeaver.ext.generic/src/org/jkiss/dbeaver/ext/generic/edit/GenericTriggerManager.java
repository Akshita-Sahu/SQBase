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
package org.jkiss.sqbase.ext.generic.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.ext.generic.model.GenericTrigger;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.sql.edit.struct.SQLTriggerManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

public class GenericTriggerManager<
    TRIGGER extends GenericTrigger<?>>
    extends SQLTriggerManager<TRIGGER, GenericTableBase> {

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return false;
    }

    @Override
    public boolean canDeleteObject(@NotNull TRIGGER object) {
        return true;
    }

    @Override
    protected void createOrReplaceTriggerQuery(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull TRIGGER trigger,
        boolean create
    ) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    protected TRIGGER createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        Object container,
        Object copyFrom,
        @NotNull Map<String, Object> options
    ) throws DBException {
        throw new DBException("Not Implemented");
    }

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, TRIGGER> getObjectsCache(TRIGGER object) {
        DBSObject container = object.getContainer();
        if (container instanceof GenericStructContainer structContainer) {
            return (DBSObjectCache) structContainer.getTableTriggerCache();
        } else if (container instanceof GenericTableBase tableBase) {
            return (DBSObjectCache) tableBase.getContainer().getTableTriggerCache();
        }
        return null;
    }
}
