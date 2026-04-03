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
package org.jkiss.sqbase.ext.altibase.edit;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.generic.model.GenericPackage;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.exec.DBCFeatureNotSupportedException;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

public class AltibasePackageManager extends SQLObjectEditor<GenericPackage, GenericStructContainer> {
    
    @Override
    public boolean canCreateObject(@NotNull Object container) {
        return false;
    }

    @Override
    public boolean canDeleteObject(@NotNull GenericPackage object) {
        return true;
    }
    
    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext,
                                          @NotNull List<DBEPersistAction> actions, @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options) {
        actions.add(
            new SQLDatabasePersistAction(
                "Drop package",
                "DROP PACKAGE " + command.getObject().getFullyQualifiedName(DBPEvaluationContext.DDL))
        );
    }

    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource) {
        return 0;
    }

    @Override
    public DBSObjectCache<? extends DBSObject, GenericPackage> getObjectsCache(GenericPackage object) {
        return null;
    }

    @Override
    protected GenericPackage createDatabaseObject(@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context,
                                                  Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBException {
        throw new DBCFeatureNotSupportedException();
    }

    @Override
    protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext,
                                          @NotNull List<DBEPersistAction> actions,
                                          @NotNull SQLObjectEditor<GenericPackage, GenericStructContainer>.ObjectCreateCommand command,
                                          @NotNull Map<String, Object> options) throws DBException {
        
    }
}
