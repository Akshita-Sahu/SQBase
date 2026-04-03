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
package org.jkiss.sqbase.ext.wmi;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.wmi.model.WMIDataSource;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.DBPDataSourceProvider;
import org.jkiss.sqbase.model.app.DBPPlatform;
import org.jkiss.sqbase.model.connection.DBPConnectionConfiguration;
import org.jkiss.sqbase.model.connection.DBPDriver;
import org.jkiss.sqbase.model.connection.DBPDriverLibrary;
import org.jkiss.sqbase.model.preferences.DBPPropertyDescriptor;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.wmi.service.WMIService;

import java.nio.file.Path;

public class WMIDataSourceProvider implements DBPDataSourceProvider {


    private boolean libLoaded = false;

    @Override
    public void init(@NotNull DBPPlatform platform)
    {
    }

    @Override
    public long getFeatures()
    {
        return FEATURE_SCHEMAS;
    }

    @Override
    public DBPPropertyDescriptor[] getConnectionProperties(
        DBRProgressMonitor monitor,
        DBPDriver driver,
        DBPConnectionConfiguration connectionInfo) throws DBException
    {
        return null;
    }

    @Override
    public String getConnectionURL(DBPDriver driver, DBPConnectionConfiguration connectionInfo)
    {
        return
            "wmi://" + connectionInfo.getServerName() +
                "/" + connectionInfo.getHostName() +
                "/" + connectionInfo.getDatabaseName();
    }

    @NotNull
    @Override
    public DBPDataSource openDataSource(@NotNull DBRProgressMonitor monitor, @NotNull DBPDataSourceContainer container) throws DBException
    {
        if (!libLoaded) {
            DBPDriver driver = container.getDriver();
            driver.getDriverLoader(container).loadDriver(monitor);
            loadNativeLib(driver);
            libLoaded = true;
        }
        return new WMIDataSource(container);
    }

    private void loadNativeLib(DBPDriver driver) throws DBException {
        for (DBPDriverLibrary libFile : driver.getDriverLibraries()) {
            if (libFile.matchesCurrentPlatform() && libFile.getType() == DBPDriverLibrary.FileType.lib) {
                Path localFile = libFile.getLocalFile();
                try {
                    if (localFile != null) {
                        WMIService.linkNative(localFile.toAbsolutePath().toString());
                    } else {
                        // Load dll from any accessible location
                        WMIService.linkNative();
                    }
                } catch (UnsatisfiedLinkError e) {
                    throw new DBException("Can't load native library '" + libFile.getDisplayName() + "'", e);
                }
            }
        }
    }

}
