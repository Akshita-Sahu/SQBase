/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2026 SQBase Corp and others
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
package org.jkiss.sqbase.ui;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.model.access.DBAPermissionRealm;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.ui.preferences.PrefPageGlobalProjectNetworkProfiles;
import org.jkiss.sqbase.ui.preferences.PrefPageProjectNetworkProfiles;
import org.jkiss.sqbase.ui.workbench.WorkbenchUtils;

public class UIEditorsWorkbenchInitializer implements IWorkbenchWindowInitializer {

    public static final String MAIN_PAGE = "org.jkiss.sqbase.preferences.main.connections";

    @Override
    public void initializeWorkbenchWindow(@NotNull IWorkbenchWindowConfigurer configurer) {
        if (!DBWorkbench.getPlatform().getWorkspace().hasRealmPermission(DBAPermissionRealm.PERMISSION_ADMIN)) {
            WorkbenchUtils.removePreferencePages(MAIN_PAGE + "/" + PrefPageGlobalProjectNetworkProfiles.PAGE_ID);
            WorkbenchUtils.removePropertyPages(PrefPageProjectNetworkProfiles.PAGE_ID);
        }
    }
}