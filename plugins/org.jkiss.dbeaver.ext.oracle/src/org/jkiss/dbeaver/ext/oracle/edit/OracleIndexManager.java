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
package org.jkiss.sqbase.ext.oracle.edit;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.oracle.model.OracleTableBase;
import org.jkiss.sqbase.ext.oracle.model.OracleTableIndex;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.impl.sql.edit.struct.SQLIndexManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.DBSObjectCache;
import org.jkiss.sqbase.model.struct.rdb.DBSIndexType;

import java.util.Map;

/**
 * Oracle index manager
 */
public class OracleIndexManager extends SQLIndexManager<OracleTableIndex, OracleTableBase> {

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, OracleTableIndex> getObjectsCache(OracleTableIndex object) {
        return object.getParentObject().getSchema().indexCache;
    }

    @Override
    protected OracleTableIndex createDatabaseObject(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBECommandContext context,
        final Object container,
        Object from,
        @NotNull Map<String, Object> options
    ) {
        OracleTableBase table = (OracleTableBase) container;

        return new OracleTableIndex(
            table.getSchema(),
            table,
            "INDEX",
            true,
            DBSIndexType.UNKNOWN);
    }

    @Override
    protected String getDropIndexPattern(OracleTableIndex index) {
        return "DROP INDEX " + PATTERN_ITEM_INDEX; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
