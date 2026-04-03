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

package org.jkiss.sqbase.ext.postgresql.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.postgresql.model.PostgreDatabase;
import org.jkiss.sqbase.ext.postgresql.model.PostgreExtension;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

public class PostgreExtensionManager extends SQLObjectEditor<PostgreExtension, PostgreDatabase>{

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Override
    public DBSObjectCache<PostgreDatabase, PostgreExtension> getObjectsCache(PostgreExtension object) {
        return object.getDatabase().extensionCache;
    }

    @Override
    protected PostgreExtension createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context,
                                                    Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBException {
        return new PostgreExtension((PostgreDatabase) container);
    }


    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectCreateCommand command,
                                          @NotNull Map<String, Object> options) {
        final PostgreExtension extension = command.getObject();

        actions.add(
            new SQLDatabasePersistAction(
                "Create extension",
                "CREATE EXTENSION " + DBUtils.getQuotedIdentifier(extension) + " SCHEMA " + extension.getSchema()) //$NON-NLS-2$
        );
    }

    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectDeleteCommand command,
                                          @NotNull Map<String, Object> options) {
        
        
        actions.add(
                new SQLDatabasePersistAction("Drop extension", "DROP EXTENSION " + DBUtils.getQuotedIdentifier(command.getObject()) + " CASCADE") //$NON-NLS-2$
            );
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
         return true;
    }

    @Override
    public boolean canDeleteObject(@NotNull PostgreExtension object) {
        return true;
    }

    @Override
    public boolean canEditObject(@NotNull PostgreExtension object) {
        return false;
    }
    
    

  
}
