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
package org.jkiss.sqbase.registry;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.core.SQBaseActivator;
import org.jkiss.sqbase.core.DesktopPlatform;
import org.jkiss.sqbase.core.DesktopUI;
import org.jkiss.sqbase.model.app.DBPPlatform;
import org.jkiss.sqbase.model.preferences.DBPPreferenceStore;
import org.jkiss.sqbase.model.rcp.DesktopApplicationImpl;
import org.jkiss.sqbase.runtime.ui.DBPPlatformUI;

import java.nio.file.Path;

/**
 * EclipseApplicationImpl
 */
public abstract class EclipsePluginApplicationImpl extends DesktopApplicationImpl {

    public EclipsePluginApplicationImpl() {
        initializeApplicationServices();
    }

    @Override
    public boolean isStandalone() {
        return false;
    }

    @Override
    public boolean isPrimaryInstance() {
        return false;
    }

    @Override
    public boolean isHeadlessMode() {
        return false;
    }

    @Override
    public String getInfoDetails() {
        return "Eclipse";
    }

    @Nullable
    @Override
    public String getDefaultProjectName() {
        return "SQBase";
    }

    @Nullable
    @Override
    public Path getDefaultWorkingFolder() {
        return null;
    }

    @NotNull
    @Override
    public Class<? extends DBPPlatform> getPlatformClass() {
        return DesktopPlatform.class;
    }

    @Nullable
    @Override
    public Class<? extends DBPPlatformUI> getPlatformUIClass() {
        return DesktopUI.class;
    }

    @NotNull
    @Override
    public DBPPreferenceStore getPreferenceStore() {
        return SQBaseActivator.getInstance().getPreferences();

    }

}
