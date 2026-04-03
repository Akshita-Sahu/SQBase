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
package org.jkiss.sqbase.ext.db2.i.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ModelPreferences;
import org.jkiss.sqbase.ext.generic.model.GenericDataSource;
import org.jkiss.sqbase.ext.generic.model.meta.GenericMetaModel;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.connection.DBPConnectionConfiguration;
import org.jkiss.sqbase.model.connection.DBPDriver;
import org.jkiss.sqbase.model.impl.jdbc.JDBCExecutionContext;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.utils.GeneralUtils;

import java.util.HashMap;
import java.util.Map;


public class DB2IDataSource extends GenericDataSource {

    private static final Log log = Log.getLog(DB2IDataSource.class);
    
    private static final String APPLICATION_NAME_PROP = "clientProgramName";

    public DB2IDataSource(DBRProgressMonitor monitor, DBPDataSourceContainer container, GenericMetaModel metaModel)
        throws DBException
    {
        super(monitor, container, metaModel, new DB2ISQLDialect());
    }
    
    @Override
    protected Map<String, String> getInternalConnectionProperties(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBPDriver driver,
        @NotNull JDBCExecutionContext context,
        @NotNull String purpose,
        @NotNull DBPConnectionConfiguration connectionInfo
    ) {
        Map<String, String> props = new HashMap<>();
        if (!getContainer().getPreferenceStore().getBoolean(ModelPreferences.META_CLIENT_NAME_DISABLE)) {
            props.put(APPLICATION_NAME_PROP, GeneralUtils.getProductName());
        }
        return props;
    }

}
