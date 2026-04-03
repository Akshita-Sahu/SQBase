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

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.model.access.DBAPrivilege;
import org.jkiss.sqbase.model.access.DBAPrivilegeOwner;
import org.jkiss.sqbase.model.access.DBAPrivilegeType;
import org.jkiss.sqbase.model.edit.DBECommand;
import org.jkiss.sqbase.model.edit.DBEPersistAction;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.impl.edit.DBECommandAbstract;
import org.jkiss.sqbase.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.ui.editors.acl.internal.ACLMessages;

import java.util.Map;

/**
 * Grant/Revoke privilege command
 */
public class ACLCommandChangePrivilege extends DBECommandAbstract<DBAPrivilegeOwner> {

    private ObjectACLManager aclManager;
    private boolean grant;
    private DBAPrivilege privilege;
    private DBAPrivilegeType[] privilegeTypes;

    public ACLCommandChangePrivilege(ObjectACLManager aclManager, DBAPrivilegeOwner user, boolean grant, DBAPrivilege privilege, DBAPrivilegeType[] privilegeTypes)
    {
        super(user, grant ? ACLMessages.edit_command_grant_privilege_action_grant_privilege : ACLMessages.edit_command_grant_privilege_action_revoke_privilege);
        this.aclManager = aclManager;
        this.grant = grant;
        this.privilege = privilege;
        this.privilegeTypes = privilegeTypes;
    }

    @Override
    public void updateModel()
    {
        //getObject().clearGrantsCache();
    }

    @NotNull
    @Override
    public DBEPersistAction[] getPersistActions(@NotNull DBRProgressMonitor monitor, @NotNull DBCExecutionContext executionContext, @NotNull Map<String, Object> options)
    {
        DBAPrivilegeOwner object = getObject();

        String grantScript = aclManager.generatePermissionChangeScript(monitor, object, grant, privilege, privilegeTypes, options);
        return new DBEPersistAction[] {
            new SQLDatabasePersistAction(
                ACLMessages.edit_command_grant_privilege_action_grant_privilege,
                grantScript)
        };
    }

    @NotNull
    @Override
    public DBECommand<?> merge(@NotNull DBECommand<?> prevCommand, @NotNull Map<Object, Object> userParams)
    {
        if (prevCommand instanceof ACLCommandChangePrivilege) {
            ACLCommandChangePrivilege prevGrant = (ACLCommandChangePrivilege) prevCommand;
            if (prevGrant.privilege == privilege && prevGrant.privilegeTypes == privilegeTypes) {
                if (prevGrant.grant == grant) {
                    return prevCommand;
                } else {
                    return null;
                }
            }
        }
        return super.merge(prevCommand, userParams);
    }

}
