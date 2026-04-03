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
package org.jkiss.sqbase.model.sql.generator;

import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLQueryGeneratorUpdate;
import org.jkiss.sqbase.model.struct.DBSEntity;
import org.jkiss.sqbase.model.struct.DBSEntityAttribute;
import org.jkiss.utils.CommonUtils;

import java.util.Collection;

public class SQLGeneratorDelete extends SQLGeneratorTable {

    @Override
    public void generateSQL(DBRProgressMonitor monitor, StringBuilder sql, DBSEntity object) throws DBException {
        String entityName = getEntityName(object);
        if (object instanceof SQLQueryGeneratorUpdate) {
            sql.append(((SQLQueryGeneratorUpdate) object).generateTableDeleteFrom(entityName));
        } else {
            sql.append("DELETE FROM ").append(entityName);
        }
        sql.append(getLineSeparator()).append("WHERE ");
        Collection<? extends DBSEntityAttribute> keyAttributes = getKeyAttributes(monitor, object);
        if (CommonUtils.isEmpty(keyAttributes)) {
            keyAttributes = getAllAttributes(monitor, object);
        }
        boolean hasAttr = false;
        for (DBSEntityAttribute attr : keyAttributes) {
            if (hasAttr) sql.append(" AND ");
            sql.append(DBUtils.getObjectFullName(attr, DBPEvaluationContext.DML)).append("=");
            appendDefaultValue(sql, attr);
            hasAttr = true;
        }
        sql.append(";\n");
    }
}
