/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2024 SQBase Corp and others
 * Copyright (C) 2016-2019 Karl Griesser (fullref@gmail.com)
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
package org.jkiss.sqbase.ext.exasol;

import org.jkiss.sqbase.ext.exasol.model.ExasolGeometryValueHandler;
import org.jkiss.sqbase.model.DBPDataKind;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.data.DBDFormatSettings;
import org.jkiss.sqbase.model.data.DBDValueHandler;
import org.jkiss.sqbase.model.data.DBDValueHandlerProvider;
import org.jkiss.sqbase.model.impl.jdbc.data.handlers.JDBCDateTimeValueHandler;
import org.jkiss.sqbase.model.struct.DBSTypedObject;

public class ExasolValueHandlerProvider implements DBDValueHandlerProvider {

    @Override
    public DBDValueHandler getValueHandler(DBPDataSource dataSource, DBDFormatSettings preferences,
            DBSTypedObject typedObject) {
        if (typedObject.getDataKind() == DBPDataKind.DATETIME) {
            return new JDBCDateTimeValueHandler(preferences);
        }
        String typeID = typedObject.getTypeName();

        switch (typeID) {
            case ExasolConstants.TYPE_GEOMETRY:
                return new ExasolGeometryValueHandler();
            default:
                return null;
        }
    }
}
