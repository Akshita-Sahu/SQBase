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
package org.jkiss.sqbase.ext.denodo;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ext.denodo.model.DenodoDataSource;
import org.jkiss.sqbase.ext.denodo.model.DenodoMetaModel;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.connection.DBPConnectionConfiguration;
import org.jkiss.sqbase.model.connection.DBPDriver;
import org.jkiss.sqbase.model.impl.jdbc.JDBCDataSourceProvider;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;

public class DenodoDataSourceProvider extends JDBCDataSourceProvider {

    private static final Log log = Log.getLog(DenodoDataSourceProvider.class);

    @Override
    public long getFeatures()
    {
        return FEATURE_SCHEMAS;
    }

    @NotNull
    @Override
    public String getConnectionURL(@NotNull DBPDriver driver, @NotNull DBPConnectionConfiguration connectionInfo)
    {
        StringBuilder url = new StringBuilder();

        url.append("jdbc:denodo://");

        url.append(connectionInfo.getHostName());
        if (!CommonUtils.isEmpty(connectionInfo.getHostPort())) {
            url.append(":").append(connectionInfo.getHostPort());
        }

        url.append("/");

        url.append(connectionInfo.getDatabaseName());

        log.debug("getConnectionURL" + url.toString());

        return url.toString();
    }

    @NotNull
    @Override
    public DBPDataSource openDataSource(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBPDataSourceContainer container)
        throws DBException
    {
        return new DenodoDataSource(monitor, container, new DenodoMetaModel());
    }

}
