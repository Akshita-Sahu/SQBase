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
package org.jkiss.sqbase.ext.cubrid.data;

import org.jkiss.sqbase.ext.cubrid.CubridConstants;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.data.DBDFormatSettings;
import org.jkiss.sqbase.model.data.DBDValueHandler;
import org.jkiss.sqbase.model.data.DBDValueHandlerProvider;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.DBCResultSet;
import org.jkiss.sqbase.model.exec.DBCSession;
import org.jkiss.sqbase.model.impl.data.DefaultValueHandler;
import org.jkiss.sqbase.model.impl.jdbc.exec.JDBCColumnMetaData;
import org.jkiss.sqbase.model.struct.DBSTypedObject;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.utils.BeanUtils;

public class CubridValueHandlerProvider implements DBDValueHandlerProvider {

    @Override
    public DBDValueHandler getValueHandler(DBPDataSource dataSource, DBDFormatSettings preferences, DBSTypedObject typedObject) {
        boolean isEnableOID = DBWorkbench.getPlatform().getPreferenceStore().getBoolean(CubridConstants.OID_NAVIGATOR);
        if (typedObject instanceof JDBCColumnMetaData columnMeta) {
            String columnName = columnMeta.getName();
            String tableName = columnMeta.getEntityName();
            String typeName = typedObject.getTypeName();
            if (isEnableOID && CubridConstants.CLASS.equals(typeName) && columnName.equals(tableName)) {
                return new CubridOIDValueHandler();
            }
        }
        return null;
    }

    class CubridOIDValueHandler extends DefaultValueHandler {
        @Override
        public Object fetchValueObject(DBCSession session, DBCResultSet resultSet, DBSTypedObject type, int index) throws DBCException {
            String oidValue = null;
            Object originalValue = resultSet.getAttributeValue(index);
            try {
                oidValue = (String) BeanUtils.invokeObjectMethod(originalValue, "getOidString");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return oidValue;
        }
    }
}
