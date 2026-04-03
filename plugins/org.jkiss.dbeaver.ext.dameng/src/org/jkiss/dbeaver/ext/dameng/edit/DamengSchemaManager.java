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
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.dameng.model.DamengDataSource;
import org.jkiss.sqbase.ext.dameng.model.DamengSchema;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

/**
 * @author Shengkai Bai
 */
public class DamengSchemaManager extends SQLObjectEditor<DamengSchema, DamengDataSource> {

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return true;
    }

    @Override
    public boolean canDeleteObject(@NotNull DamengSchema object) {
        return true;
    }

    @Override
    protected String getBaseObjectName() {
        return "NEW_SCHEMA";
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Override
    public DBSObjectCache<? extends DBSObject, DamengSchema> getObjectsCache(DamengSchema object) {
        return object.getDataSource().getSchemaCache();
    }


    @Override
    protected DamengSchema createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBException {
        DamengDataSource dataSource = (DamengDataSource) container;
        DamengSchema damengSchema = new DamengSchema(dataSource, "NEW_SCHEMA", false);
        setNewObjectName(monitor, dataSource, damengSchema);
        return damengSchema;
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull SQLObjectEditor<DamengSchema, DamengDataSource>.ObjectCreateCommand command, @NotNull Map<String, Object> options) throws DBException {
        actions.add(
                new SQLDatabasePersistAction(
                        "Create schema",
                        "CREATE SCHEMA " + DBUtils.getObjectFullName(command.getObject(), DBPEvaluationContext.DDL))
        );
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull SQLObjectEditor<DamengSchema, DamengDataSource>.ObjectDeleteCommand command, @NotNull Map<String, Object> options) throws DBException {
        actions.add(
                new SQLDatabasePersistAction(
                        "Drop schema",
                        "DROP SCHEMA " + DBUtils.getObjectFullName(command.getObject(), DBPEvaluationContext.DDL) + " RESTRICT")
        );
    }

}
