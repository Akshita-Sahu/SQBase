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
package org.jkiss.sqbase.ext.ocient;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.generic.GenericDataSourceProvider;
import org.jkiss.sqbase.ext.ocient.model.OcientDataSource;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.connection.DBPConnectionConfiguration;
import org.jkiss.sqbase.model.connection.DBPDriver;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;

public class OcientDataSourceProvider extends GenericDataSourceProvider {

    @NotNull
    @Override
    public DBPDataSource openDataSource(@NotNull DBRProgressMonitor monitor, @NotNull DBPDataSourceContainer container)
        throws DBException {
        return new OcientDataSource(monitor, container);
    }

    @NotNull
    @Override
    public String getConnectionURL(@NotNull DBPDriver driver, @NotNull DBPConnectionConfiguration connectionInfo) {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:ocient://").append(connectionInfo.getHostName());
        if (!CommonUtils.isEmpty(connectionInfo.getHostPort())) {
            url.append(":").append(connectionInfo.getHostPort());
        }
        if (!CommonUtils.isEmpty(connectionInfo.getDatabaseName())) {
            url.append("/").append(connectionInfo.getDatabaseName());
        }
        return url.toString();
    }
}
