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
package org.jkiss.sqbase.ui.editors.acl;

import org.jkiss.sqbase.model.DBPEvaluationContext;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.access.DBAPrivilege;
import org.jkiss.sqbase.model.access.DBAPrivilegeType;
import org.jkiss.sqbase.model.struct.DBSObject;

/**
 * PostgresRolePrivilegesEditor
 */
public abstract class BaseACLManager<PRIVILEGE extends DBAPrivilege, PRIVILEGE_TYPE extends DBAPrivilegeType> implements ObjectACLManager<PRIVILEGE, PRIVILEGE_TYPE> {

    public String getObjectUniqueName(DBSObject object) {
        return DBUtils.getObjectFullName(object, DBPEvaluationContext.DDL);
    }

}