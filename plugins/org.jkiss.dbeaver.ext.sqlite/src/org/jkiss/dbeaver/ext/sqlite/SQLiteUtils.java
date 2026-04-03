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

package org.jkiss.sqbase.ext.sqlite;

import org.jkiss.api.CompositeObjectId;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ext.generic.model.GenericSchema;
import org.jkiss.sqbase.ext.generic.model.GenericTableBase;
import org.jkiss.sqbase.ext.sqlite.model.SQLiteObjectType;
import org.jkiss.sqbase.ext.sqlite.model.SQLiteTable;
import org.jkiss.sqbase.ext.sqlite.edit.SQLiteTableManager;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBECommand;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.runtime.DBWorkbench;

import java.util.HashMap;
import java.util.Map;

/**
 * SQLiteUtils
 */
public class SQLiteUtils {

    public static final CompositeObjectId DRIVER_REFERENCE = new CompositeObjectId("sqlite", "sqlite_jdbc");
    private static final Log log = Log.getLog(SQLiteUtils.class);


    public static String readMasterDefinition(DBRProgressMonitor monitor, DBSObject sourceObject, SQLiteObjectType objectType, String sourceObjectName, GenericTableBase table) {
        try (JDBCSession session = DBUtils.openMetaSession(monitor, sourceObject, "Load SQLite description")) {
            try (JDBCPreparedStatement dbStat = session.prepareStatement(
                "SELECT sql FROM " + (sourceObject.getParentObject() instanceof GenericSchema ?
                                      DBUtils.getQuotedIdentifier(sourceObject.getParentObject()) + "." : "")
                    + "sqlite_master WHERE type=? AND tbl_name=?" + (sourceObjectName != null ? " AND name=?" : "")
                    + "\n" + "UNION ALL\n" + "SELECT sql FROM "
                    + "sqlite_temp_master WHERE type=? AND tbl_name=?" + (sourceObjectName != null ? " AND name=?" : "")
                    + "\n"))
            {
                int paramIndex = 1;
                dbStat.setString(paramIndex++, objectType.name());
                dbStat.setString(paramIndex++, table.getName());
                if (sourceObjectName != null) {
                    dbStat.setString(paramIndex++, sourceObjectName);
                }
                dbStat.setString(paramIndex++, objectType.name());
                dbStat.setString(paramIndex++, table.getName());
                if (sourceObjectName != null) {
                    dbStat.setString(paramIndex++, sourceObjectName);
                }
                try (JDBCResultSet resultSet = dbStat.executeQuery()) {
                    StringBuilder sql = new StringBuilder();
                    while (resultSet.next()) {
                        String ddl = resultSet.getString(1);
                        if (ddl != null) {
                            sql.append(ddl);
                            sql.append(";\n");
                        }
                    }
                    String ddl = sql.toString();
                    //ddl = ddl.replaceAll("(?i)CREATE VIEW", "CREATE OR REPLACE VIEW");
                    return ddl;
                }
            }
        } catch (Exception e) {
            log.debug(e);
            return null;
        }
    }

    public static void makeRecreateTableCommand(DBECommandContext commandContext, SQLiteTable table, DBECommand sourceCommand) {
        if (DBWorkbench.getPlatform().getEditorsRegistry().getObjectManager(table.getClass()) instanceof SQLiteTableManager tableManager) {
            Map<String, Object> options = new HashMap<>();
            tableManager.addRecreateCommand(commandContext, table, options, sourceCommand);
        }
    }
}
