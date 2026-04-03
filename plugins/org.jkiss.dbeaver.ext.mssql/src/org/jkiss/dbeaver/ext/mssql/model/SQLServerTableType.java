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
package org.jkiss.sqbase.ext.mssql.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.mssql.SQLServerUtils;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.impl.jdbc.JDBCUtils;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSEntityAssociation;
import org.jkiss.sqbase.model.struct.DBStructUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SQLServerTableType extends SQLServerTableBase {
    private transient volatile List<SQLServerTableForeignKey> references;

    public SQLServerTableType(@NotNull SQLServerSchema catalog, @NotNull ResultSet dbResult, @NotNull String name) {
        super(catalog, dbResult, name);
    }

    @Override
    public void setObjectDefinitionText(String source) {
    }

    @NotNull
    @Override
    public String getObjectDefinitionText(@NotNull DBRProgressMonitor monitor, @NotNull Map<String, Object> options) throws DBException {
        return DBStructUtils.generateTableDDL(monitor, this, options, false);
    }

    @Override
    public boolean supportsObjectDefinitionOption(@NotNull String option) {
        return false;
    }

    @Override
    public boolean isView() {
        return false;
    }

    @Nullable
    @Override
    public Collection<SQLServerTableUniqueKey> getConstraints(@NotNull DBRProgressMonitor monitor) throws DBException {
        return getContainer().getUniqueConstraintCache().getObjects(monitor, getSchema(), this);
    }

    @Nullable
    @Override
    public Collection<SQLServerTableForeignKey> getAssociations(@NotNull DBRProgressMonitor monitor) throws DBException {
        return getSchema().getForeignKeyCache().getObjects(monitor, getSchema(), this);
    }

    @Nullable
    @Override
    public Collection<? extends DBSEntityAssociation> getReferences(@NotNull DBRProgressMonitor monitor) throws DBException {
        if (references != null || monitor == null) {
            return references;
        }
        try (JDBCSession session = DBUtils.openMetaSession(monitor, this,  "Read table references")) {
            try (JDBCPreparedStatement dbStat = session.prepareStatement(
                    "SELECT t.schema_id as schema_id,t.name as table_name,fk.name as key_name\n" +
                            "FROM " +
                            SQLServerUtils.getSystemTableName(getDatabase(), "all_objects") + " t, " +
                            SQLServerUtils.getSystemTableName(getDatabase(), "foreign_keys") + " fk, " +
                            SQLServerUtils.getSystemTableName(getDatabase(), "all_objects") + " tr\n" +
                            "WHERE t.object_id = fk.parent_object_id AND tr.object_id=fk.referenced_object_id AND fk.referenced_object_id=?\n" +
                            "ORDER BY 1,2,3")) {
                dbStat.setLong(1, getObjectId());
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    List<SQLServerTableForeignKey> result = new ArrayList<>();
                    while (dbResult.next()) {
                        long schemaId = JDBCUtils.safeGetLong(dbResult, "schema_id");
                        String tableName = JDBCUtils.safeGetString(dbResult, "table_name");
                        String fkName = JDBCUtils.safeGetString(dbResult, "key_name");

                        SQLServerSchema schema = getDatabase().getSchema(monitor, schemaId);
                        if (schema != null) {
                            SQLServerTableBase table = schema.getTable(monitor, tableName);
                            if (table != null) {
                                DBSEntityAssociation object = DBUtils.findObject(table.getAssociations(monitor), fkName);
                                if (object instanceof SQLServerTableForeignKey) {
                                    result.add((SQLServerTableForeignKey) object);
                                }
                            }
                        }
                    }
                    this.references = result;
                    return result;
                }
            } catch (SQLException e) {
                throw new DBCException(e, session.getExecutionContext());
            }
        }
    }

    @Override
    boolean supportsTriggers() {
        return false;
    }
}
