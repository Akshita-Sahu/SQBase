/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2017-2017 Karl Griesser (fullref@gmail.com)
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
import org.jkiss.sqbase.ext.exasol.ExasolConstants;
import org.jkiss.sqbase.ext.exasol.model.ExasolTable;
import org.jkiss.sqbase.ext.exasol.model.ExasolTableUniqueKey;
import org.jkiss.sqbase.ext.exasol.tools.ExasolUtils;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectRenamer;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.impl.sql.edit.struct.SQLConstraintManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSEntityConstraintType;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;

import java.util.List;
import java.util.Map;

public class ExasolPrimaryKeyManager
		extends SQLConstraintManager<ExasolTableUniqueKey, ExasolTable> 
		implements DBEObjectRenamer<ExasolTableUniqueKey>{

	@Override
	public DBSObjectCache<? extends DBSObject, ExasolTableUniqueKey> getObjectsCache(
			ExasolTableUniqueKey object)
	{
		return object.getTable().getSchema().getConstraintCache();
	}

	@Override
	protected ExasolTableUniqueKey createDatabaseObject(
		@NotNull DBRProgressMonitor monitor, @NotNull DBECommandContext context,
		Object container, Object copyFrom, @NotNull Map<String, Object> options) throws DBException
	{
		return new ExasolTableUniqueKey(
			(ExasolTable) container,
			DBSEntityConstraintType.PRIMARY_KEY,
			true,
			"CONSTRAINT"
		);		
	}
	
	@Override
	protected String getDropConstraintPattern(ExasolTableUniqueKey constraint)
	{
		return "ALTER TABLE " + constraint.getTable().getFullyQualifiedName(DBPEvaluationContext.DDL) + " DROP PRIMARY KEY";
	}
	
	@Override
	protected void addObjectCreateActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
										  @NotNull ObjectCreateCommand command, @NotNull Map<String, Object> options)
	{
		ExasolTableUniqueKey obj = (ExasolTableUniqueKey) command.getObject();
		try {
			actions.add(new SQLDatabasePersistAction("Create PK", ExasolUtils.getPKDdl(obj, monitor)));
		} catch (DBException e) {
			log.error("Could not generated DDL for PK");
		}
	}

	@Override
	public void renameObject(@NotNull DBECommandContext commandContext,
							 @NotNull ExasolTableUniqueKey object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException
	{
		processObjectRename(commandContext, object, options, newName);
		
	}
	
	@Override
	protected void addObjectModifyActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actionList,
										  @NotNull ObjectChangeCommand command, @NotNull Map<String, Object> options)
	{
		final ExasolTableUniqueKey constraint = command.getObject();
		
		if (command.getProperties().containsKey(DBConstants.PROP_ID_ENABLED))
		{
			actionList.add(
					new SQLDatabasePersistAction("Alter PK",
							"ALTER TABLE " + constraint.getTable().getFullyQualifiedName(DBPEvaluationContext.DDL) + 
							" MODIFY CONSTRAINT " + constraint.getName() + " " +
							(constraint.getEnabled() ? ExasolConstants.KEYWORD_ENABLE : ExasolConstants.KEYWORD_DISABLE)
							)
					);
		}
	}
	
	
	@Override
	protected void addObjectRenameActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull List<DBEPersistAction> actions,
										  @NotNull ObjectRenameCommand command, @NotNull Map<String, Object> options)
	{
		final ExasolTableUniqueKey key = command.getObject();
		actions.add(
				new SQLDatabasePersistAction(
					"Rename PK", 
					"ALTER TABLE " + DBUtils.getObjectFullName(key.getTable(),DBPEvaluationContext.DDL) + " RENAME CONSTRAINT " 
					+ DBUtils.getQuotedIdentifier(key.getDataSource(),command.getOldName()) + " to " +
					DBUtils.getQuotedIdentifier(key.getDataSource(), command.getNewName())
				)
		);

	}
	

}
