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
package org.jkiss.sqbase.ext.postgresql.model;

import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.postgresql.PostgreUtils;
import org.jkiss.sqbase.model.*;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.impl.jdbc.JDBCUtils;
import org.jkiss.sqbase.model.meta.Property;
import org.jkiss.sqbase.model.meta.PropertyLength;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreTableForeign
 */
public class PostgreTableForeign extends PostgreTable implements DBPForeignObject, DBPImageProvider
{
    private long foreignServerId;
    private String foreignServerName;
    private String[] foreignOptions;

    public PostgreTableForeign(PostgreSchema catalog)
    {
        super(catalog);
    }

    public PostgreTableForeign(
        PostgreSchema catalog,
        ResultSet dbResult)
    {
        super(catalog, dbResult);
    }

    @Property(viewable = false, order = 200)
    public PostgreForeignServer getForeignServer(DBRProgressMonitor monitor) throws DBException {
        readForeignInfo(monitor);
        return PostgreUtils.getObjectById(monitor, getDatabase().foreignServerCache, getDatabase(), foreignServerId);
    }

    public String getForeignServerName() {
        return foreignServerName;
    }

    public void setForeignServerName(String foreignServerName) {
        this.foreignServerName = foreignServerName;
    }

    @Override
    public String getTableTypeName() {
        return "FOREIGN TABLE";
    }

    @Property(viewable = false, length = PropertyLength.MULTILINE, order = 201)
    public String[] getForeignOptions(DBRProgressMonitor monitor) throws DBException {
        readForeignInfo(monitor);
        return foreignOptions;
    }

    public void setForeignOptions(String[] foreignOptions) {
        this.foreignOptions = foreignOptions;
    }

    private void readForeignInfo(DBRProgressMonitor monitor) throws DBException {
        if (foreignServerId > 0) {
            return;
        }
        try (JDBCSession session = DBUtils.openMetaSession(monitor, this, "Read foreign table info")) {
            try (JDBCPreparedStatement stat = session.prepareStatement("SELECT * FROM pg_catalog.pg_foreign_table WHERE ftrelid=?")) {
                stat.setLong(1, getObjectId());
                try (JDBCResultSet result = stat.executeQuery()) {
                    if (result.next()) {
                        foreignServerId = JDBCUtils.safeGetLong(result, "ftserver");
                        foreignOptions = PostgreUtils.safeGetStringArray(result, "ftoptions");
                    }
                }
            } catch (SQLException e) {
                throw new DBCException(e, session.getExecutionContext());
            }
        }
    }

    @Nullable
    @Override
    public DBPImage getObjectImage() {
        return DBIcon.TREE_TABLE_LINK;
    }

    @Override
    public boolean isForeignObject() {
        return true;
    }
}
