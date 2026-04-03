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
package org.jkiss.sqbase.ext.mssql.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.jkiss.sqbase.ext.mssql.model.SQLServerDataSource;
import org.jkiss.sqbase.ext.mssql.model.session.SQLServerSession;
import org.jkiss.sqbase.ext.mssql.model.session.SQLServerSessionManager;
import org.jkiss.sqbase.model.admin.sessions.DBAServerSession;
import org.jkiss.sqbase.model.admin.sessions.DBAServerSessionManager;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.ui.ActionUtils;
import org.jkiss.sqbase.ui.SQBaseIcons;
import org.jkiss.sqbase.ui.UIIcon;
import org.jkiss.sqbase.ui.UIUtils;
import org.jkiss.sqbase.ui.views.session.AbstractSessionEditor;
import org.jkiss.sqbase.ui.views.session.SessionManagerViewer;
import org.jkiss.utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQLServerSessionEditor
 */
public class SQLServerSessionEditor extends AbstractSessionEditor
{
    private KillSessionAction terminateQueryAction;
    private boolean showOnlyConnections = true;

    @Override
    public void createEditorControl(Composite parent) {
        terminateQueryAction = new KillSessionAction();
        super.createEditorControl(parent);
    }

    @Override
    protected SessionManagerViewer createSessionViewer(DBCExecutionContext executionContext, Composite parent) {
        return new SessionManagerViewer<SQLServerSession>(this, parent, new SQLServerSessionManager((SQLServerDataSource) executionContext.getDataSource())) {
            @Override
            protected void contributeToToolbar(DBAServerSessionManager sessionManager, IContributionManager contributionManager)
            {
                contributionManager.add(ActionUtils.makeActionContribution(
                    new Action("Only connections", Action.AS_CHECK_BOX) {
                        {
                            setImageDescriptor(SQBaseIcons.getImageDescriptor(UIIcon.CONFIGURATION));
                            setToolTipText("Show only physical connections");
                            setChecked(showOnlyConnections);
                        }
                        @Override
                        public void run() {
                            showOnlyConnections = isChecked();
                            refreshPart(SQLServerSessionEditor.this, true);
                        }
                    }, true));
                contributionManager.add(new Separator());
                contributionManager.add(terminateQueryAction);
                contributionManager.add(new Separator());
            }

            @Override
            protected void onSessionSelect(DBAServerSession session)
            {
                super.onSessionSelect(session);
                terminateQueryAction.setEnabled(session != null);
            }

            @Override
            protected void loadSettings(IDialogSettings settings) {
                showOnlyConnections = CommonUtils.getBoolean(settings.get("showOnlyConnections"), true);
                super.loadSettings(settings);
            }

            @Override
            protected void saveSettings(IDialogSettings settings) {
                super.saveSettings(settings);
                settings.put("showOnlyConnections", showOnlyConnections);
            }

            @Override
            public Map<String, Object> getSessionOptions() {
                Map<String, Object> options = new HashMap<>();
                if (showOnlyConnections) {
                    options.put(SQLServerSessionManager.OPTION_SHOW_ONLY_CONNECTIONS, true);
                }
                return options;
            }

        };
    }


    private class KillSessionAction extends Action {
        KillSessionAction()
        {
            super(
                "Terminate",
                UIUtils.getShardImageDescriptor(ISharedImages.IMG_ELCL_STOP));
        }

        @Override
        public void run()
        {
            final List<DBAServerSession> sessions = getSessionsViewer().getSelectedSessions();
            if (sessions != null && UIUtils.confirmAction(
                getSite().getShell(),
                this.getText(),
                NLS.bind("Terminate session {0}?", sessions)))
            {
                getSessionsViewer().alterSessions(
                    sessions,
                    null);
            }
        }
    }

}