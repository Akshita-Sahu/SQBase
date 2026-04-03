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

package org.jkiss.sqbase.ui.actions.datasource;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.jkiss.sqbase.SQBasePreferences;
import org.jkiss.sqbase.model.DBPMessageType;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.runtime.SQBaseNotifications;
import org.jkiss.sqbase.runtime.ui.UIServiceSystemAgent;
import org.jkiss.sqbase.ui.TrayIconHandler;
import org.jkiss.sqbase.ui.UIUtils;

/**
 * UIServiceDriversImpl
 */
public class UIServiceSystemAgentImpl implements UIServiceSystemAgent {

    private TrayIconHandler trayItem;

    public UIServiceSystemAgentImpl() {
        this.trayItem = new TrayIconHandler();
    }

    @Override
    public long getLongOperationTimeout() {
        return DBWorkbench.getPlatform().getPreferenceStore().getLong(SQBasePreferences.AGENT_LONG_OPERATION_TIMEOUT);
    }

    @Override
    public void notifyAgent(String message, int status) {
        if (!DBWorkbench.getPlatform().getPreferenceStore().getBoolean(SQBasePreferences.AGENT_LONG_OPERATION_NOTIFY)) {
            // Notifications disabled
            return;
        }
        if (TrayIconHandler.isSupported()) {
            UIUtils.syncExec(() -> Display.getCurrent().beep());
            trayItem.notify(message, status);
        } else {
            SQBaseNotifications.showNotification(
                "agent.notify",
                "Agent Notification",
                message,
                status == IStatus.INFO ? DBPMessageType.INFORMATION :
                    (status == IStatus.ERROR ? DBPMessageType.ERROR : DBPMessageType.WARNING),
                null);
        }
    }


}
