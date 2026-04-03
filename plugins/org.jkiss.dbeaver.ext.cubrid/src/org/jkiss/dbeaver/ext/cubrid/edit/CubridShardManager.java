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
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.cubrid.model.CubridDataSource;
import org.jkiss.sqbase.ext.cubrid.model.CubridShard;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.connection.DBPConnectionConfiguration;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

public class CubridShardManager extends SQLObjectEditor<CubridShard, GenericStructContainer> {

    private static final String PROP_SHARD_TYPE = "shardType";
    private static final String PROP_SHARD_VALUE = "shardValue";
    private static final String SHARD_TYPE_ID = "SHARD ID";

    @Override
    protected void addObjectModifyActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull ObjectChangeCommand command,
        @NotNull Map<String, Object> options
    ) {
        CubridShard shard = command.getObject();
        CubridDataSource dataSource = shard.getDataSource();
        DBPConnectionConfiguration connectionInfo = dataSource.getContainer().getConnectionConfiguration();

        int shardValue;
        try {
            shardValue = Integer.parseInt(shard.getValue());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Shard value must be a numeric value.");
        }

        if (SHARD_TYPE_ID.equals(shard.getName()) && (shardValue < 0 || shardValue > 1)) {
            throw new IllegalArgumentException("The maximum allowed Shard ID is 1. Please enter a valid value.");
        }

        connectionInfo.setProperty(PROP_SHARD_TYPE, shard.getName());
        connectionInfo.setProperty(PROP_SHARD_VALUE, shard.getValue());
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return false;
    }

    @Override
    public boolean canDeleteObject(@NotNull CubridShard object) {
        return false;
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return 0;
    }

    @Override
    public DBSObjectCache<? extends DBSObject, CubridShard> getObjectsCache(CubridShard object) {
        return null;
    }

    @Override
    protected CubridShard createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        Object container,
        Object copyFrom,
        @NotNull Map<String, Object> options
    ) throws DBException {
        return null;
    }

    @Override
    protected void addObjectCreateActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull SQLObjectEditor<CubridShard, GenericStructContainer>.ObjectCreateCommand command,
        @NotNull Map<String, Object> options
    ) {
        /* This body intentionally empty. */
    }

    @Override
    protected void addObjectDeleteActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull SQLObjectEditor<CubridShard, GenericStructContainer>.ObjectDeleteCommand command,
        @NotNull Map<String, Object> options
    ) {
        /* This body intentionally empty. */
    }
}
