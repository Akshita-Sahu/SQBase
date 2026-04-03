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
package org.jkiss.sqbase.ext.exasol.model;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.data.gis.handlers.GISGeometryValueHandler;
import org.jkiss.sqbase.model.data.DBDDisplayFormat;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.DBCSession;
import org.jkiss.sqbase.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.gis.DBGeometry;
import org.jkiss.sqbase.model.struct.DBSTypedObject;
import org.locationtech.jts.geom.Geometry;

import java.sql.SQLException;

public class ExasolGeometryValueHandler extends GISGeometryValueHandler {
	
	@Override
	protected Object fetchColumnValue(DBCSession session, JDBCResultSet resultSet, DBSTypedObject type, int index)
			throws DBCException, SQLException {
		return resultSet.getString(index);
	}	

    protected void bindGeometryParameter(@NotNull JDBCSession session, @NotNull JDBCPreparedStatement statement, int paramIndex, @NotNull Geometry value) throws SQLException {
        statement.setString(paramIndex, value.toString()); // Just convert to string for Exasol (doesn't work with bytes)
    }

	@NotNull
	@Override
	public String getValueDisplayString(@NotNull DBSTypedObject column, Object value, @NotNull DBDDisplayFormat format) {
		if (value instanceof DBGeometry && format == DBDDisplayFormat.NATIVE) {
			return value.toString();
		}
		return super.getValueDisplayString(column, value, format);
	}
}
