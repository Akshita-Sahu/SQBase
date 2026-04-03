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
import org.jkiss.sqbase.ext.generic.model.GenericObjectContainer;
import org.jkiss.sqbase.ext.generic.model.GenericProcedure;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.ext.generic.model.meta.GenericMetaModel;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.exec.DBCFeatureNotSupportedException;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.messages.ModelMessages;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.sqbase.model.struct.cache.ListCache;

import java.util.List;
import java.util.Map;

/**
 * Generic procedure manager
 */
public class GenericProcedureManager extends SQLObjectEditor<GenericProcedure, GenericStructContainer> {

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_EDITOR_ON_CREATE;
    }

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, GenericProcedure> getObjectsCache(GenericProcedure object) {
        GenericStructContainer container = object.getContainer();
        return new ListCache<>(((GenericObjectContainer) container).getProcedureCache());
    }

    @Override
    protected GenericProcedure createDatabaseObject(
        @NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, final Object container,
        Object from, @NotNull Map<String, Object> options
    ) throws DBException {
        throw new DBCFeatureNotSupportedException();
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options) throws DBCException {
        throw new DBCFeatureNotSupportedException();
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
        GenericProcedure object = command.getObject();
        String procedureName;
        GenericMetaModel metaModel = object.getDataSource().getMetaModel();
        if (metaModel.supportsOverloadedProcedureNames()) {
            try {
                procedureName = object.getProcedureSignature(monitor, metaModel.showProcedureParamNames());
            } catch (DBException e) {
                log.debug("Can't read procedure/function parameters", e);
                procedureName = object.getFullyQualifiedName(DBPEvaluationContext.DDL);
            }
        } else {
            procedureName = object.getFullyQualifiedName(DBPEvaluationContext.DDL);
        }
        actions.add(
            new SQLDatabasePersistAction(
                ModelMessages.model_jdbc_drop_table,
                "DROP " + object.getProcedureType().name() +  //$NON-NLS-2$
                    " " + procedureName)
        );
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return false;
    }

    @Override
    public boolean canDeleteObject(@NotNull GenericProcedure object) {
        return true;
    }

}
