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
package org.jkiss.sqbase.ui.actions.datasource;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.ModelPreferences;
import org.jkiss.sqbase.model.DBPMessageType;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.exec.*;
import org.jkiss.sqbase.model.qm.QMTransactionState;
import org.jkiss.sqbase.model.qm.QMUtils;
import org.jkiss.sqbase.runtime.SQBaseNotifications;
import org.jkiss.sqbase.runtime.TasksJob;
import org.jkiss.sqbase.ui.actions.AbstractDataSourceHandler;
import org.jkiss.sqbase.ui.controls.txn.TransactionLogDialog;
import org.jkiss.sqbase.utils.RuntimeUtils;

import java.lang.reflect.InvocationTargetException;

public class DataSourceRollbackHandler extends AbstractDataSourceHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        DBCExecutionContext context = getActiveExecutionContext(event, true);
        if (context != null && context.isConnected()) {
            execute(HandlerUtil.getActiveShell(event), context);
        }
        return null;
    }

    public static void execute(@NotNull Shell shell, DBCExecutionContext context) {
        TasksJob.runTask("Rollback transaction", monitor -> {
            DBCTransactionManager txnManager = DBUtils.getTransactionManager(context);
            if (txnManager != null) {
                QMTransactionState txnInfo = QMUtils.getTransactionState(context);
                try (DBCSession session = context.openSession(monitor, DBCExecutionPurpose.UTIL, "Rollback transaction")) {
                    txnManager.rollback(session, null);
                } catch (DBCException e) {
                    throw new InvocationTargetException(e);
                }

                if (context.getDataSource().getContainer().getPreferenceStore()
                    .getBoolean(ModelPreferences.TRANSACTIONS_SHOW_NOTIFICATIONS)) {
                    SQBaseNotifications.showNotification(
                        context.getDataSource(),
                        SQBaseNotifications.NT_ROLLBACK,
                        "Transaction has been rolled back\n\n" +
                            "Query count: " + txnInfo.getUpdateCount() + "\n" +
                            "Duration: " + RuntimeUtils.formatExecutionTime(System.currentTimeMillis() - txnInfo.getTransactionStartTime())
                            + "\n",
                        DBPMessageType.ERROR,
                        () -> TransactionLogDialog.showDialog(shell, context, true)
                    );
                }
            }
        });
    }

}