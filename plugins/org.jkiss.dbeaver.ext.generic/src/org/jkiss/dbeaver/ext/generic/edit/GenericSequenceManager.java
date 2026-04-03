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
import org.jkiss.sqbase.ext.generic.model.GenericSequence;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.exec.DBCFeatureNotSupportedException;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLUtils;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

public class GenericSequenceManager extends SQLObjectEditor<GenericSequence, GenericStructContainer> {
    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return false;
    }

    @Override
    protected String getBaseObjectName() {
        return "NEW_SEQUENCE";
    }

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, GenericSequence> getObjectsCache(GenericSequence object) {
        DBSObject parentObject = object.getParentObject();
        if (parentObject instanceof GenericObjectContainer container) {
            return container.getSequenceCache();
        }
        return null;
    }

    @Override
    protected GenericSequence createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        Object container,
        Object copyFrom,
        @NotNull Map<String, Object> options
    ) throws DBException {
        throw new DBCFeatureNotSupportedException();
    }

    @Override
    protected void addObjectCreateActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull SQLObjectEditor<GenericSequence, GenericStructContainer>.ObjectCreateCommand command,
        @NotNull Map<String, Object> options
    ) throws DBException {
        throw new DBCFeatureNotSupportedException();
    }

    @Override
    protected void addObjectDeleteActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull SQLObjectEditor<GenericSequence, GenericStructContainer>.ObjectDeleteCommand command,
        @NotNull Map<String, Object> options
    ) throws DBException {
        actions.add(
            new SQLDatabasePersistAction(
                "Drop sequence",
                "DROP SEQUENCE " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL)) //$NON-NLS-1$
        );
    }

    @Override
    protected void addObjectExtraActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull NestedObjectCommand<GenericSequence, SQLObjectEditor<GenericSequence, GenericStructContainer>.PropertyHandler> command,
        @NotNull Map<String, Object> options
    ) throws DBException {
        GenericSequence sequence = command.getObject();
        if (command.hasProperty(DBConstants.PROP_ID_DESCRIPTION)) {
            actions.add(new SQLDatabasePersistAction(
                "Comment sequence",
                "COMMENT ON SEQUENCE " + sequence.getFullyQualifiedName(DBPEvaluationContext.DDL) +
                    " IS " + SQLUtils.quoteString(sequence, CommonUtils.notEmpty(sequence.getDescription()))));
        }
    }
}
