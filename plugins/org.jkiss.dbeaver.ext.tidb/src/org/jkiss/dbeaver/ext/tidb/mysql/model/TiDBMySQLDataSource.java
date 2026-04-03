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

package org.jkiss.sqbase.ext.tidb.mysql.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ext.mysql.MySQLConstants;
import org.jkiss.sqbase.ext.mysql.model.MySQLCatalog;
import org.jkiss.sqbase.ext.mysql.model.MySQLDataSource;
import org.jkiss.sqbase.ext.mysql.model.MySQLEngine;
import org.jkiss.sqbase.ext.mysql.model.MySQLPrivilege;
import org.jkiss.sqbase.ext.tidb.model.plan.TiDBPlanAnalyzer;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.DBPDataSourceInfo;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.connection.DBPConnectionConfiguration;
import org.jkiss.sqbase.model.connection.DBPDriver;
import org.jkiss.sqbase.model.data.DBDValueHandlerProvider;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.jdbc.JDBCDatabaseMetaData;
import org.jkiss.sqbase.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.sqbase.model.exec.jdbc.JDBCResultSet;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.exec.plan.DBCQueryPlanner;
import org.jkiss.sqbase.model.impl.jdbc.JDBCExecutionContext;
import org.jkiss.sqbase.model.impl.jdbc.JDBCUtils;
import org.jkiss.sqbase.model.impl.jdbc.data.handlers.JDBCStandardValueHandlerProvider;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;
import org.osgi.framework.Version;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class TiDBMySQLDataSource extends MySQLDataSource {
    private static final Log log = Log.getLog(MySQLDataSource.class);
    private static final String CONN_ATTR_NAME = "connectionAttributes";
    private static final String PROP_APPLICATION_NAME = "program_name";

    private String tidbVersion = "";

    public String getServerVersion() {
        return this.tidbVersion;
    }

    public TiDBMySQLDataSource(DBRProgressMonitor monitor, DBPDataSourceContainer container) throws DBException {
        super(monitor, container, new TiDBDialect());
    }

    @Override
    public void initialize(@NotNull DBRProgressMonitor monitor) throws DBException {
        super.initialize(monitor);

        try (JDBCSession session = DBUtils.openMetaSession(monitor, this, "TiDB version fetch")) {
            try (JDBCPreparedStatement dbStat = session.prepareStatement("SELECT VERSION() AS VERSION")) {
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    if (dbResult.next()) {
                        this.tidbVersion = JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_VERSION);
                    }
                }
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    @Override
    public <T> T getAdapter(@NotNull Class<T> adapter) {
        if (adapter == DBCQueryPlanner.class) {
            return adapter.cast(new TiDBPlanAnalyzer(this));
        } else if (adapter == DBDValueHandlerProvider.class) {
            return adapter.cast(new JDBCStandardValueHandlerProvider());
        } else {
            return super.getAdapter(adapter);
        }
    }

    @Override
    public MySQLEngine getEngine(String name) {
        return DBUtils.findObject(getEngines(), name, true);
    }

    @Override
    public MySQLEngine getDefaultEngine() {
        return this.getEngine("tidb");
    }

    @Override
    public MySQLPrivilege getPrivilege(DBRProgressMonitor monitor, String name) throws DBException {
        if (name.equalsIgnoreCase("SHOW DB")) {
            return DBUtils.findObject(getPrivileges(monitor), "Show databases", true);
        }
        return DBUtils.findObject(getPrivileges(monitor), name, true);
    }

    @Override
    public Collection<? extends MySQLCatalog> getChildren(@NotNull DBRProgressMonitor monitor) {
        return getCatalogs();
    }

    @Override
    public MySQLCatalog getChild(@NotNull DBRProgressMonitor monitor, @NotNull String childName) {
        return getCatalog(childName);
    }

    @NotNull
    @Override
    public Class<? extends MySQLCatalog> getPrimaryChildType(@Nullable DBRProgressMonitor monitor) {
        return MySQLCatalog.class;
    }

    @Override
    public boolean supportsInformationSchema() {
        return true;
    }
    
    @Override
    protected DBPDataSourceInfo createDataSourceInfo(DBRProgressMonitor monitor, @NotNull JDBCDatabaseMetaData metaData) {
        super.createDataSourceInfo(monitor, metaData);
        return new TiDBMySQLDataSourceInfo(this, metaData);
    }
    
    @Override
    public boolean supportsSequences() {
        return this.isServerVersionAtLeast(4, 0);
    }

    @Override
    public boolean isServerVersionAtLeast(int major, int minor) {
        Version tidbVersion = this.getInfo().getDatabaseVersion();
        if (tidbVersion.getMajor() < major) {
            return false;
        } else if (tidbVersion.getMajor() == major && tidbVersion.getMinor() < minor) {
            return false;
        }
        return true;
    }
    
    @Override
    protected Map<String, String> getInternalConnectionProperties(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBPDriver driver,
        @NotNull JDBCExecutionContext context,
        @NotNull String purpose,
        @NotNull DBPConnectionConfiguration connectionInfo
    ) throws DBCException {
        Map<String, String> props = super.getInternalConnectionProperties(monitor, driver, context, purpose, connectionInfo);
        // build application name
        String appName = DBUtils.getClientApplicationName(getContainer(), context, purpose);
        appName = "sqbase_tidb_plugin" + (CommonUtils.isEmpty(appName) ? "" : "(" + appName + ")");
        
        // build conAttr value
        String connAttr = props.get(CONN_ATTR_NAME);
        connAttr = PROP_APPLICATION_NAME + ":" + appName + (CommonUtils.isEmpty(connAttr) ? "" : "," + connAttr);
        
        props.put(CONN_ATTR_NAME, connAttr);
        return props;
    }
}
