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
package org.jkiss.sqbase.ext.firebird.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.firebird.FireBirdUtils;
import org.jkiss.sqbase.ext.generic.model.GenericStructContainer;
import org.jkiss.sqbase.ext.generic.model.GenericTable;
import org.jkiss.sqbase.ext.generic.model.GenericTableColumn;
import org.jkiss.sqbase.model.DBPNamedObject2;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.impl.jdbc.JDBCUtils;
import org.jkiss.sqbase.model.meta.Property;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.util.*;

public class FireBirdTable extends GenericTable implements FireBirdTableBase, DBPNamedObject2 {

    private int keyLength;
    private String externalFile;
    private String ownerName;
    private Map<String, String> columnDomainTypes;

    public FireBirdTable(GenericStructContainer container, @Nullable String tableName, @Nullable String tableType, @Nullable JDBCResultSet dbResult) {
        super(container, tableName, tableType, dbResult);

        if (dbResult != null) {
            keyLength = JDBCUtils.safeGetInt(dbResult, "RDB$DBKEY_LENGTH");
            externalFile = JDBCUtils.safeGetStringTrimmed(dbResult, "RDB$EXTERNAL_FILE");
            ownerName = JDBCUtils.safeGetStringTrimmed(dbResult, "RDB$OWNER_NAME");
        }
    }

    @Property(viewable = true, order = 20)
    public String getOwnerName() {
        return ownerName;
    }

    @Property(viewable = true, order = 21)
    public int getKeyLength() {
        return keyLength;
    }

    @Property(viewable = true, order = 22)
    public String getExternalFile() {
        return externalFile;
    }

    @Override
    protected boolean isTruncateSupported() {
        return false;
    }

    @Override
    public synchronized List<FireBirdTableColumn> getAttributes(@NotNull DBRProgressMonitor monitor) throws DBException {
        Collection<? extends GenericTableColumn> childColumns = super.getAttributes(monitor);
        if (childColumns == null) {
            return Collections.emptyList();
        }
        List<FireBirdTableColumn> columns = new ArrayList<>();
        for (GenericTableColumn gtc : childColumns) {
            columns.add((FireBirdTableColumn) gtc);
        }
        columns.sort(DBUtils.orderComparator());
        return columns;
    }

    public String getColumnDomainType(DBRProgressMonitor monitor, FireBirdTableColumn column) throws DBException {
        if (columnDomainTypes == null) {
            columnDomainTypes = FireBirdUtils.readColumnDomainTypes(monitor, this);
        }
        return columnDomainTypes.get(column.getName());
    }

}
