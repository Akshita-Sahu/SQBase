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
package org.jkiss.sqbase.ui.navigator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.sqbase.model.DBPDataSourceFolder;
import org.jkiss.sqbase.model.app.DBPDataSourceRegistry;
import org.jkiss.sqbase.model.navigator.DBNDataSource;
import org.jkiss.sqbase.model.navigator.DBNLocalFolder;
import org.jkiss.sqbase.model.navigator.DBNModel;
import org.jkiss.sqbase.model.navigator.DBNProjectDatabases;
import org.jkiss.sqbase.ui.UIUtils;
import org.jkiss.sqbase.ui.dialogs.EnterNameDialog;
import org.jkiss.sqbase.ui.internal.UINavigatorMessages;
import org.jkiss.sqbase.ui.navigator.database.NavigatorViewBase;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class NavigatorHandlerLocalFolderCreate extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        final ISelection selection = HandlerUtil.getCurrentSelection(event);

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structSelection = (IStructuredSelection) selection;
            List<DBNDataSource> dataSources = new ArrayList<>();
            DBNProjectDatabases databasesNode = null;
            DBNLocalFolder parentFolder = null;
            for (Iterator iter = structSelection.iterator(); iter.hasNext(); ) {
                Object element = iter.next();
                if (element instanceof DBNDataSource) {
                    dataSources.add((DBNDataSource) element);
                    element = ((DBNDataSource) element).getParentNode();
                }
                if (element instanceof DBNLocalFolder) {
                    parentFolder = (DBNLocalFolder) element;
                    databasesNode = parentFolder.getParentNode();
                } else if (element instanceof DBNProjectDatabases) {
                    databasesNode = (DBNProjectDatabases) element;
                }
            }
            if (databasesNode != null) {
                createFolder(HandlerUtil.getActiveWorkbenchWindow(event), activePart, databasesNode, parentFolder, dataSources, null);
            }
        }
        return null;
    }

    public static boolean createFolder(IWorkbenchWindow workbenchWindow, IWorkbenchPart activePart, DBNProjectDatabases databases, final DBNLocalFolder parentFolder, final Collection<DBNDataSource> nodes, String newName)
    {
        if (newName == null) {
            newName = EnterNameDialog.chooseName(workbenchWindow.getShell(), UINavigatorMessages.actions_navigator_folder_name);
        }
        if (CommonUtils.isEmpty(newName)) {
            return false;
        }
        // Create folder and refresh databases root
        // DS container will reload folders on refresh
        final DBPDataSourceRegistry dsRegistry = databases.getDataSourceRegistry();
        DBPDataSourceFolder folder = dsRegistry.addFolder(parentFolder == null ? null : parentFolder.getFolder(), newName);
        for (DBNDataSource node : nodes) {
            node.moveToFolder(node.getOwnerProject(), folder);
        }
        if (parentFolder != null && activePart instanceof NavigatorViewBase) {
            final TreeViewer viewer = ((NavigatorViewBase) activePart).getNavigatorViewer();
            if (viewer != null) {
                UIUtils.asyncExec(() -> viewer.expandToLevel(parentFolder, 1));
            }
        }
        DBNModel.updateConfigAndRefreshDatabases(databases);

        return true;
    }
}
