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
package org.jkiss.sqbase.model.impl.struct;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBPNamedObject2;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.meta.Property;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSEntity;
import org.jkiss.sqbase.model.struct.DBSEntityType;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.rdb.DBSTable;
import org.jkiss.sqbase.model.struct.rdb.DBSTrigger;

import java.util.List;

/**
 * AbstractTable
 */
public abstract class AbstractTable<
    DATASOURCE extends DBPDataSource,
    CONTAINER extends DBSObject>
    implements DBSTable, DBPNamedObject2
{
    private CONTAINER container;
    private String tableName;

    protected AbstractTable(CONTAINER container)
    {
        this.container = container;
        this.tableName = "";
    }

    // Copy constructor
    protected AbstractTable(CONTAINER container, DBSEntity source)
    {
        this(container);
        this.tableName = source.getName();
    }

    protected AbstractTable(CONTAINER container, String tableName)
    {
        this(container);
        this.tableName = tableName;
    }

    public CONTAINER getContainer()
    {
        return container;
    }

    @NotNull
    @Override
    public DBSEntityType getEntityType()
    {
        return DBUtils.isView(this) ? DBSEntityType.VIEW : DBSEntityType.TABLE;
    }

    @NotNull
    @Override
    @Property(viewable = true, editable = true, order = 1)
    public String getName()
    {
        return tableName;
    }

    @Override
    public void setName(@NotNull String tableName)
    {
        this.tableName = tableName;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public DATASOURCE getDataSource()
    {
        return (DATASOURCE) container.getDataSource();
    }

    @Override
    public boolean isPersisted()
    {
        return true;
    }

    @Override
    public CONTAINER getParentObject()
    {
        return container;
    }

    public String toString()
    {
        return getFullyQualifiedName(DBPEvaluationContext.UI);
    }

    @Nullable
    @Override
    public List<? extends DBSTrigger> getTriggers(@NotNull DBRProgressMonitor monitor) throws DBException {
        return null;
    }
}
