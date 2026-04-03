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
package org.jkiss.sqbase.model.impl.struct;

import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.DBPImage;
import org.jkiss.sqbase.model.DBPImageProvider;
import org.jkiss.sqbase.model.DBValueFormatting;
import org.jkiss.sqbase.model.struct.DBSEntityAttribute;
import org.jkiss.sqbase.model.struct.rdb.DBSTableIndexColumn;

/**
 * AbstractTableIndexColumn
 */
public abstract class AbstractTableIndexColumn implements DBSTableIndexColumn, DBPImageProvider
{

    @Override
    public boolean isPersisted()
    {
        return true;
    }

    @Nullable
    @Override
    public DBPImage getObjectImage()
    {
        return DBValueFormatting.getObjectImage(getTableColumn());
    }

    @Nullable
    @Override
    public DBSEntityAttribute getAttribute()
    {
        return getTableColumn();
    }
}
