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
package org.jkiss.sqbase.runtime.jobs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPMessageType;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.exec.DBCExecutionPurpose;
import org.jkiss.sqbase.model.exec.DBCSession;
import org.jkiss.sqbase.model.exec.DBCTransactionManager;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.runtime.SQBaseNotifications;
import org.jkiss.sqbase.runtime.ui.UIServiceConnections;

import java.util.Map;

/**
 * EndIdleTransactionsJob
 */
class EndIdleTransactionsJob extends DataSourceUpdaterJob {
    private static final Log log = Log.getLog(EndIdleTransactionsJob.class);

    private static final Object CONFIRM_SYNC = new Object();

    private final DBPDataSource dataSource;
    private final Map<DBCExecutionContext, DBCTransactionManager> txnToEnd;

    EndIdleTransactionsJob(DBPDataSource dataSource, Map<DBCExecutionContext, DBCTransactionManager> txnToEnd) {
        super("End idle transaction for (" + dataSource.getContainer().getName() + ")");
        setUser(false);
        setSystem(true);
        this.dataSource = dataSource;
        this.txnToEnd = txnToEnd;
    }

    @Override
    public DBPDataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected IStatus updateDataSource(DBRProgressMonitor monitor) {
        UIServiceConnections serviceConnections = DBWorkbench.getService(UIServiceConnections.class);
        if (serviceConnections != null) {
            synchronized (CONFIRM_SYNC) {
                if (!serviceConnections.confirmTransactionsClose(txnToEnd.keySet().toArray(new DBCExecutionContext[0]))) {
                    return Status.CANCEL_STATUS;
                }
            }
        }
        log.debug("End idle " + txnToEnd.size() + " transactions for " + dataSource.getContainer().getId());
        for (Map.Entry<DBCExecutionContext, DBCTransactionManager> tee : txnToEnd.entrySet()) {
            try (DBCSession session = tee.getKey().openSession(monitor, DBCExecutionPurpose.UTIL, "End idle transaction")) {
                tee.getValue().rollback(session, null);
            } catch (DBException e) {
                log.error("Error ending idle transaction", e);
            }
        }
        SQBaseNotifications.showNotification(
            dataSource,
            SQBaseNotifications.NT_ROLLBACK_IDLE,
            "Transactions have been rolled back after long idle period (" + dataSource.getContainer().getName() + ")",
            DBPMessageType.ERROR);

        return Status.OK_STATUS;
    }

}
