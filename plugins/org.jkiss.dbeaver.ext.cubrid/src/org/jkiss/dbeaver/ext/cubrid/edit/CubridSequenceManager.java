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
package org.jkiss.sqbase.ext.cubrid.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.cubrid.model.CubridDataSource;
import org.jkiss.sqbase.ext.cubrid.model.CubridSequence;
import org.jkiss.sqbase.ext.cubrid.model.CubridUser;
import org.jkiss.sqbase.ext.generic.edit.GenericSequenceManager;
import org.jkiss.sqbase.ext.generic.model.GenericSequence;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLUtils;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

public class CubridSequenceManager extends GenericSequenceManager {

    public static final String BASE_SERIAL_NAME = "new_serial";

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        CubridUser user = (CubridUser) container;
        CubridDataSource dataSource = (CubridDataSource) user.getDataSource();
        boolean supportsMultiSchema = dataSource.getSupportMultiSchema();
        boolean isCurrentUser = user.getName().equalsIgnoreCase(dataSource.getCurrentUser());
        return supportsMultiSchema || isCurrentUser || !dataSource.isShard();
    }

    @Override
    public boolean canEditObject(GenericSequence object) {
        return !((CubridDataSource) object.getDataSource()).isShard();
    }

    @Override
    public boolean canDeleteObject(GenericSequence object) {
        return !((CubridDataSource) object.getDataSource()).isShard();
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_EDITOR_ON_CREATE;
    }

    @NotNull
    @Override
    protected GenericSequence createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        @Nullable Object container,
        @Nullable Object copyFrom,
        @NotNull Map<String, Object> options
    ) {
        return new CubridSequence((GenericStructContainer) container, BASE_SERIAL_NAME);
    }

    @NotNull
    public String buildStatement(@NotNull CubridSequence sequence, boolean forUpdate, boolean hasComment) {
        StringBuilder sb = new StringBuilder();
        if (forUpdate) {
            sb.append("ALTER SERIAL ");
        } else {
            sb.append("CREATE SERIAL ");
        }
        sb.append(sequence.getFullyQualifiedName(DBPEvaluationContext.DDL));
        buildBody(sequence, sb);
        buildOtherValue(sequence, sb, hasComment);
        return sb.toString();
    }

    public void buildBody(@NotNull CubridSequence sequence, @NotNull StringBuilder sb) {
        if (sequence.getIncrementBy() != null) {
            sb.append(" INCREMENT BY ").append(sequence.getIncrementBy());
        }
        if (sequence.getStartValue() != null) {
            sb.append(" START WITH ").append(sequence.getStartValue());
        }
        if (sequence.getMaxValue() != null) {
            sb.append(" MAXVALUE ").append(sequence.getMaxValue());
        }
        if (sequence.getMinValue() != null) {
            sb.append(" MINVALUE ").append(sequence.getMinValue());
        }
    }

    public void buildOtherValue(@NotNull CubridSequence sequence, @NotNull StringBuilder sb, boolean hasComment) {
        if (sequence.getCycle()) {
            sb.append(" CYCLE");
        } else {
            sb.append(" NOCYCLE");
        }
        if (sequence.getCachedNum() != 0) {
            sb.append(" CACHE ").append(sequence.getCachedNum());
        }
        if (hasComment || sequence.getDescription() != null) {
            sb.append(" COMMENT ").append(SQLUtils.quoteString(sequence, CommonUtils.notEmpty(sequence.getDescription())));
        }
    }

    @Override
    protected void addObjectCreateActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull ObjectCreateCommand command,
        @NotNull Map<String, Object> options
    ) {
        CubridSequence sequence = (CubridSequence) command.getObject();
        boolean hasComment = command.hasProperty(DBConstants.PROP_ID_DESCRIPTION);
        actions.add(new SQLDatabasePersistAction("Create Serial", buildStatement(sequence, false, hasComment)));
    }

    @Override
    protected void addObjectModifyActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actionList,
        @NotNull ObjectChangeCommand command,
        @NotNull Map<String, Object> options
    ) {
        CubridSequence sequence = (CubridSequence) command.getObject();
        boolean hasComment = command.hasProperty(DBConstants.PROP_ID_DESCRIPTION);
        actionList.add(new SQLDatabasePersistAction("Alter Serial", buildStatement(sequence, true, hasComment)));
    }

    @Override
    protected void addObjectDeleteActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull SQLObjectEditor<GenericSequence, GenericStructContainer>.ObjectDeleteCommand command,
        @NotNull Map<String, Object> options
    ) {
        actions.add(new SQLDatabasePersistAction(
            "Drop Serial",
            "DROP SERIAL " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL)
        ));
    }

    @Override
    protected void addObjectExtraActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull NestedObjectCommand<GenericSequence, SQLObjectEditor<GenericSequence, GenericStructContainer>.PropertyHandler> command,
        @NotNull Map<String, Object> options
    ) {
        /* This body intentionally empty. */
    }
}
