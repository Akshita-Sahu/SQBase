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

package org.jkiss.sqbase.model.app;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.DBConfigurationController;
import org.jkiss.sqbase.model.DBFileController;
import org.jkiss.sqbase.model.connection.DBPDataSourceProviderRegistry;
import org.jkiss.sqbase.model.data.DBDRegistry;
import org.jkiss.sqbase.model.edit.DBERegistry;
import org.jkiss.sqbase.model.fs.DBFRegistry;
import org.jkiss.sqbase.model.navigator.DBNModel;
import org.jkiss.sqbase.model.net.DBWHandlerRegistry;
import org.jkiss.sqbase.model.preferences.DBPPreferenceStore;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.runtime.OSDescriptor;
import org.jkiss.sqbase.model.sql.SQLDialectMetadataRegistry;
import org.jkiss.sqbase.model.task.DBTTaskController;

import java.io.IOException;
import java.nio.file.Path;

/**
 * DBPPlatform
 */
public interface DBPPlatform {

    @NotNull
    DBPApplication getApplication();

    @NotNull
    DBPWorkspace getWorkspace();

    @Deprecated // use navigator model from DBPProject
    @NotNull
    DBNModel getNavigatorModel();

    @NotNull
    DBPDataSourceProviderRegistry getDataSourceProviderRegistry();

    @NotNull
    OSDescriptor getLocalSystem();

    @NotNull
    DBDRegistry getValueHandlerRegistry();

    @NotNull
    DBERegistry getEditorsRegistry();

    @NotNull
    DBFRegistry getFileSystemRegistry();

    @NotNull
    SQLDialectMetadataRegistry getSQLDialectRegistry();

    @NotNull
    DBWHandlerRegistry getNetworkHandlerRegistry();

    @NotNull
    DBPPreferenceStore getPreferenceStore();

    @NotNull
    DBACertificateStorage getCertificateStorage();

    @NotNull
    Path getTempFolder(@NotNull DBRProgressMonitor monitor, @NotNull String name) throws IOException;

    /**
     * Returns platform configuration controller,
     * which keeps configuration which can be shared with other users.
     */
    @NotNull
    DBConfigurationController getConfigurationController();
    
    /**
     * Returns configuration controller,
     * which keeps product configuration which can be shared with other users.
     */
    @NotNull
    DBConfigurationController getProductConfigurationController();
    
    /**
     * Returns configuration controller,
     * which keeps plugin configuration which can be shared with other users.
     */
    @NotNull
    DBConfigurationController getPluginConfigurationController(@Nullable String pluginId);

    /**
     * Local config files are used to store some configuration specific to local machine only.
     */
    @NotNull
    Path getLocalConfigurationFile(String fileName);

    /**
     * File controller allows to read/write binary files (e.g. custom driver libraries)
     */
    @NotNull
    DBFileController getFileController();

    /**
     * Task controller can read and change tasks configuration file
     */
    @NotNull
    DBTTaskController getTaskController();

    @Deprecated
    @NotNull
    Path getApplicationConfiguration();


    @NotNull
    DBPDataFormatterRegistry getDataFormatterRegistry();

    boolean isShuttingDown();

    default boolean isUnitTestMode() {
        return false;
    }
}
