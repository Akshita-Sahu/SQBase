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

package org.jkiss.sqbase.ext.dameng.model;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.access.DBAPrivilege;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.impl.jdbc.JDBCUtils;
import org.jkiss.sqbase.model.meta.Property;
import org.jkiss.sqbase.model.struct.DBSObject;

/**
 * @author Shengkai Bai
 */
public class DamengPrivSystem implements DBAPrivilege {

    private DamengRole damengRole;

    private String name;

    private Boolean grantable;

    public DamengPrivSystem(DamengRole damengRole, JDBCResultSet resultSet) {
        this.damengRole = damengRole;
        this.name = JDBCUtils.safeGetString(resultSet, "PRIV");
        this.grantable = "Y".equals(JDBCUtils.safeGetString(resultSet, "GRANTABLE"));
    }

    @NotNull
    @Override
    @Property(viewable = true, order = 1)
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isPersisted() {
        return true;
    }

    @Override
    public DBSObject getParentObject() {
        return damengRole;
    }

    @Override
    public DBPDataSource getDataSource() {
        return damengRole.getDataSource();
    }

    @Property(viewable = true, order = 2)
    public Boolean getGrantable() {
        return grantable;
    }
}
