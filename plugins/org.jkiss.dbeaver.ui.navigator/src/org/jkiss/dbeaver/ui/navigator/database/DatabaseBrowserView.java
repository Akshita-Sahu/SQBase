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
package org.jkiss.sqbase.ui.navigator.database;

import org.eclipse.swt.widgets.Composite;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.app.DBPProject;
import org.jkiss.sqbase.model.navigator.DBNEmptyNode;
import org.jkiss.sqbase.model.navigator.DBNModel;
import org.jkiss.sqbase.model.navigator.DBNNode;
import org.jkiss.sqbase.model.navigator.DBNProject;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.ui.SQBaseIcons;
import org.jkiss.sqbase.ui.UIExecutionQueue;
import org.jkiss.sqbase.ui.UIUtils;
import org.jkiss.sqbase.ui.navigator.INavigatorFilter;
import org.jkiss.sqbase.ui.navigator.SmartNavigatorTreeFilter;
import org.jkiss.utils.CommonUtils;

public class DatabaseBrowserView extends NavigatorViewBase {
    public static final String VIEW_ID = "org.jkiss.sqbase.core.databaseBrowser";

    private static final Log log = Log.getLog(DatabaseBrowserView.class);
    private DBNNode rootNode;

    public DatabaseBrowserView()
    {
        super();
    }

    @Override
    protected INavigatorFilter getNavigatorFilter() {
        return new SmartNavigatorTreeFilter();
    }

    @Override
    public DBNNode getRootNode() {
        if (rootNode == null) {
            String secondaryId = getViewSite().getSecondaryId();
            if (!CommonUtils.isEmpty(secondaryId)) {
                try {
                    rootNode = getNodeFromSecondaryId(secondaryId);
                } catch (DBException e) {
                    DBWorkbench.getPlatformUI().showError("Open database browser", "Can't find database navigator node", e);
                }
            }
        }
        if (rootNode != null) {
            return rootNode;
        }
        return getDefaultRootNode();
    }

    protected DBNNode getDefaultRootNode() {
        DBNProject projectNode = getGlobalNavigatorModel().getRoot().getProjectNode(DBWorkbench.getPlatform().getWorkspace().getActiveProject());
        return projectNode == null ? new DBNEmptyNode() : projectNode.getDatabases();
    }

    @Override
    public void createPartControl(Composite parent)
    {
        super.createPartControl(parent);
        getNavigatorTree().setFilterObjectType(getDefaultFilterType());

        String secondaryId = getViewSite().getSecondaryId();
        if (!CommonUtils.isEmpty(secondaryId)) {
            UIExecutionQueue.queueExec(() -> {
                try {
                    DBNNode node = getNodeFromSecondaryId(secondaryId);
                    setPartName(node.getNodeDisplayName());
                    setTitleImage(SQBaseIcons.getImage(node.getNodeIconDefault()));
                } catch (DBException e) {
                    log.error("Error creating DB browser view", e);
                }
            });
        }
    }

    @NotNull
    protected DatabaseNavigatorTreeFilterObjectType getDefaultFilterType() {
        return DatabaseNavigatorTreeFilterObjectType.table;
    }

    public static String getSecondaryIdFromNode(DBNNode node) {
        DBPProject project = null;
        for (DBNNode dn = node; dn != null; dn = dn.getParentNode()) {
            if (dn instanceof DBNProject) {
                project = ((DBNProject) dn).getProject();
                break;
            }
        }
        if (project == null) {
            throw new IllegalStateException("Navigator node " + node.getNodeUri() + " doesn't belong to a project");
        }
        // We can't use colon in secondary ID
        return project.getName() + "|" + node.getNodeUri().replace(":", "~");
    }

    public static DBNNode getNodeFromSecondaryId(String id) throws DBException {
        int divPos = id.indexOf('|');
        if (divPos == -1) {
            throw new DBException("Bad secondary ID: " + id);
        }
        String projectName = id.substring(0, divPos);
        String nodePath = id.substring(divPos + 1).replace("~", ":");
        final DBNModel navigatorModel = DBWorkbench.getPlatform().getNavigatorModel();
        DBNNode node = null;
        DBPProject projectMeta = DBWorkbench.getPlatform().getWorkspace().getProject(projectName);
        if (projectMeta != null) {
            navigatorModel.ensureProjectLoaded(projectMeta);
            node = UIUtils.runWithMonitor(monitor -> {
                monitor.beginTask("Find navigator node", 1);
                try {
                    monitor.subTask("Find node " + nodePath);
                    return navigatorModel.getNodeByPath(monitor, projectMeta, nodePath);
                } finally {
                    monitor.done();
                }
            });
        }
        if (node == null) {
            log.error("Node " + nodePath + " not found for browse view");
            node = new DBNEmptyNode();
        }
        return node;
    }

}
