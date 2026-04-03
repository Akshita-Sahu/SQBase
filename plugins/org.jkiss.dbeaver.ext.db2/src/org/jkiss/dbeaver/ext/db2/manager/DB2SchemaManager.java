/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2013-2015 Denis Forveille (titou10.titou10@gmail.com)
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
package org.jkiss.sqbase.ext.db2.manager;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.db2.model.DB2DataSource;
import org.jkiss.sqbase.ext.db2.model.DB2Schema;
import org.jkiss.sqbase.model.DBPDataSource;
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
 * DB2 Schema Manager
 * 
 * @author Denis Forveille
 */
public class DB2SchemaManager extends SQLObjectEditor<DB2Schema, DB2DataSource> {

    private static final String SQL_CREATE_SCHEMA = "CREATE SCHEMA %s";
    private static final String SQL_DROP_SCHEMA = "DROP SCHEMA %s RESTRICT";

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource)
    {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, DB2Schema> getObjectsCache(DB2Schema object)
    {
        return object.getDataSource().getSchemaCache();
    }

    @Override
    protected DB2Schema createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context, final Object container,
                                             Object copyFrom, @NotNull Map<String, Object> options)
    {
        return new DB2Schema((DB2DataSource) container, "NEW_SCHEMA");
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options)
    {
        SQLDatabasePersistAction action = new SQLDatabasePersistAction("Create schema", String.format(SQL_CREATE_SCHEMA,
            DBUtils.getQuotedIdentifier(command.getObject())));
        actions.add(action);
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options)
    {
        String schemaName = command.getObject().getName();
        DBEPersistAction action = new SQLDatabasePersistAction("Drop schema (SQL)", String.format(SQL_DROP_SCHEMA,
            DBUtils.getQuotedIdentifier(command.getObject())));
        actions.add(action);
    }

}
