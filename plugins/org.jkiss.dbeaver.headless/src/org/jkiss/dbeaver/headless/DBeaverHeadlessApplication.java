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
package org.jkiss.sqbase.headless;

import org.eclipse.equinox.app.IApplicationContext;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ModelPreferences;
import org.jkiss.sqbase.model.app.DBPApplication;
import org.jkiss.sqbase.model.app.DBPPlatform;
import org.jkiss.sqbase.model.preferences.DBPPreferenceStore;
import org.jkiss.sqbase.model.rcp.DesktopApplicationImpl;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.runtime.ui.DBPPlatformUI;
import org.jkiss.sqbase.utils.GeneralUtils;
import org.jkiss.sqbase.utils.RuntimeUtils;

import java.nio.file.Path;

/**
 * Headless application
 */
public class SQBaseHeadlessApplication extends DesktopApplicationImpl {

    private static final Log log = Log.getLog(SQBaseHeadlessApplication.class);

    public SQBaseHeadlessApplication() {
        // Initialize platform
        initializeApplicationServices();
    }

    @NotNull
    @Override
    public Object start(IApplicationContext context) {
        DBPPlatform platform = DBWorkbench.getPlatform();
        DBPApplication application = platform.getApplication();
        if (RuntimeUtils.isWindows()
            && platform.getPreferenceStore().getBoolean(ModelPreferences.PROP_USE_WIN_TRUST_STORE_TYPE)
        ) {
            System.setProperty(GeneralUtils.PROP_TRUST_STORE_TYPE, GeneralUtils.VALUE_TRUST_STORE_TYPE_WINDOWS);
        }
        log.debug("Starting headless test application " + application.getClass().getName());

        return EXIT_OK;
    }

    @Override
    public void stop() {
        log.debug("Starting headless test application");
        super.stop();
    }

    @Override
    public @Nullable Path getDefaultWorkingFolder() {
        return null;
    }

    @NotNull
    @Override
    public Class<? extends DBPPlatform> getPlatformClass() {
        return SQBaseTestPlatform.class;
    }

    @Override
    public Class<? extends DBPPlatformUI> getPlatformUIClass() {
        return SQBaseTestPlatformUI.class;
    }

    @Nullable
    @Override
    public String getDefaultProjectName() {
        return "SQBaseTests";
    }

    @Override
    public boolean isHeadlessMode() {
        return true;
    }

    @NotNull
    @Override
    public DBPPreferenceStore getPreferenceStore() {
        return SQBaseTestActivator.getInstance().getPreferences();
    }
}
