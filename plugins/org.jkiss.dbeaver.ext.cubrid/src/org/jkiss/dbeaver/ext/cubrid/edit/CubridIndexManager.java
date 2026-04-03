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
import org.jkiss.sqbase.ext.cubrid.model.CubridTable;
import org.jkiss.sqbase.ext.cubrid.model.CubridTableIndex;
import org.jkiss.sqbase.ext.generic.edit.GenericIndexManager;
import org.jkiss.sqbase.ext.generic.model.GenericTableIndex;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.rdb.DBSIndexType;

import java.util.List;
import java.util.Map;

public class CubridIndexManager extends GenericIndexManager {

    @Override
    protected CubridTableIndex createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        final Object container,
        Object from,
        @NotNull Map<String, Object> options
    ) {
        CubridTable table = (CubridTable) container;
        return new CubridTableIndex(table, true, null, 0, null, DBSIndexType.OTHER, false);
    }

    @Override
    protected void addObjectDeleteActions(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBCExecutionContext executionContext,
        @NotNull List<DBEPersistAction> actions,
        @NotNull ObjectDeleteCommand command,
        @NotNull Map<String, Object> options
    ) {
        GenericTableIndex index = command.getObject();
        actions.add(new SQLDatabasePersistAction(
            "Drop Index",
            "DROP INDEX " + DBUtils.getQuotedIdentifier(index.getDataSource(), index.getName()) + " ON "
            + index.getTable().getFullyQualifiedName(DBPEvaluationContext.DDL)
        ));
    }
}
