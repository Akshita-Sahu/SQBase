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

package org.jkiss.sqbase.ext.dameng.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.dameng.DamengConstants;
import org.jkiss.sqbase.model.DBPObjectWithLongId;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.cache.AbstractObjectCache;

import java.sql.SQLException;

/**
 * @author Shengkai Bai
 */
public class DamengUtils {

    public static String getDDL(DBRProgressMonitor monitor, DBSObject object, DamengConstants.ObjectType objectType, String schema) throws DBException {

        try (JDBCSession session = DBUtils.openMetaSession(monitor, object, "Load source code for " + objectType + " '" + object.getName() + "'")) {
            JDBCPreparedStatement dbStat = session.prepareStatement("SELECT DBMS_METADATA.GET_DDL(?,?,?)");
            dbStat.setString(1, objectType.name());
            dbStat.setString(2, object.getName());
            dbStat.setString(3, schema);
            JDBCResultSet dbResult = dbStat.executeQuery();
            if (dbResult.next()) {
                return dbResult.getString(1);
            }
        } catch (SQLException e) {
            throw new DBException("Load source code for " + objectType + " '" + object.getName() + "' failed", e);
        }
        return null;
    }

    @Nullable
    public static <OWNER extends DBSObject, OBJECT extends DBPObjectWithLongId & DBSObject> OBJECT getObjectById(
            @NotNull DBRProgressMonitor monitor,
            @NotNull AbstractObjectCache<OWNER, OBJECT> cache,
            @NotNull OWNER owner,
            long objectId)
            throws DBException {
        for (OBJECT object : cache.getAllObjects(monitor, owner)) {
            if (object.getObjectId() == objectId) {
                return object;
            }
        }
        return null;
    }

}
