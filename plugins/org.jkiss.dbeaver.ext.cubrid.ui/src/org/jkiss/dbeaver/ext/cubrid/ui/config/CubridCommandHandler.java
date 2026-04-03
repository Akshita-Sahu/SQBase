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
package org.jkiss.sqbase.ext.cubrid.ui.config;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.ext.cubrid.model.CubridPrivilage;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.edit.prop.DBECommandComposite;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CubridCommandHandler extends DBECommandComposite<CubridPrivilage, CubridPrivilageHandler>
{

    protected CubridCommandHandler(CubridPrivilage object) {
        super(object, "Update User");
    }

    @NotNull
    @Override
    public DBEPersistAction[] getPersistActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull Map<String, Object> options) {
        List<DBEPersistAction> actions = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (getObject().isPersisted()) {
            builder.append("ALTER USER ");
            builder.append(DBUtils.getQuotedIdentifier(getObject()));
        }
        buildBody(builder);
        actions.add(new SQLDatabasePersistAction("Update User", builder.toString()));
        return actions.toArray(new DBEPersistAction[0]);

    }

    private void buildBody(StringBuilder builder) {
        for (Object key : getProperties().keySet()) {
            switch (key.toString()) {
                case "PASSWORD":
                    builder.append(" PASSWORD ").append(SQLUtils.quoteString(getObject(), getProperty(key).toString()));
                    break;
                case "DESCRIPTION":
                    builder.append(" COMMENT ").append(SQLUtils.quoteString(getObject(), getProperty(key).toString()));
                default:
                    break;
            }
        }
    }
}