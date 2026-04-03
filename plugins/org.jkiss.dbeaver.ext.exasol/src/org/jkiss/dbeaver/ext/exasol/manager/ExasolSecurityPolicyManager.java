/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2017 Karl Griesser (fullref@gmail.com)
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
package org.jkiss.sqbase.ext.exasol.manager;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.exasol.model.ExasolDataSource;
import org.jkiss.sqbase.ext.exasol.model.security.ExasolSecurityPolicy;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.exec.DBCFeatureNotSupportedException;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;


public class ExasolSecurityPolicyManager
        extends SQLObjectEditor<ExasolSecurityPolicy, ExasolDataSource>  {
    
    @Override
    public long getMakerOptions(@NotNull DBPDataSource dataSource)
    {
        return FEATURE_SAVE_IMMEDIATELY;
    }
    
    
    @Override
    public DBSObjectCache<ExasolDataSource, ExasolSecurityPolicy> getObjectsCache(
            ExasolSecurityPolicy object)
    {
        ExasolDataSource source = (ExasolDataSource) object.getDataSource();
        return source.getSecurityPolicyCache();
    }
    
    @Override
    protected ExasolSecurityPolicy createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context, Object container, Object copyFrom, @NotNull Map<String, Object> options)
        throws DBException
    {
        throw new DBCFeatureNotSupportedException();
    }
    
    
    @Override
    protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options)
    {
        ExasolSecurityPolicy obj = command.getObject();
        actions.add(
            new SQLDatabasePersistAction(
                "Rename Connection",
                "RENAME CONNECTION " +  DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getOldName()) + " to " +
                    DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getNewName()))
        );
    }
    
    @Override
    protected void addObjectDeleteActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectDeleteCommand command, @NotNull Map<String, Object> options)
    {
        final ExasolSecurityPolicy con = command.getObject();
        actions.add(
                new SQLDatabasePersistAction("Drop Connection","DROP CONNECTION " + DBUtils.getQuotedIdentifier(con))
                );
    }
    
    @Override
    protected void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList,
                                          @NotNull ObjectChangeCommand command, @NotNull Map<String, Object> options)
    {
    	
    	ExasolSecurityPolicy policy = command.getObject();
    	
    	if (policy.getEnabled())
    	{
    		String script = String.format("ALTER SYSTEM SET PASSWORD_SECURITY_POLICY='%s'", policy.getSecurityString());
    		actionList.add(new SQLDatabasePersistAction(script));
    	} else {
    		String script = String.format("ALTER SYSTEM SET PASSWORD_SECURITY_POLICY='OFF'", policy.getSecurityString());
    		actionList.add(new SQLDatabasePersistAction(script));
    	}

    }
    
    @Override
    public boolean canDeleteObject(@NotNull ExasolSecurityPolicy object) {
    	return false;
    }

    @Override
    public boolean canCreateObject(@NotNull Object container) {
    	return false;
    }


	@Override
	protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
                                          @NotNull ObjectCreateCommand command,
                                          @NotNull Map<String, Object> options) {
	}
    
    
    

}
