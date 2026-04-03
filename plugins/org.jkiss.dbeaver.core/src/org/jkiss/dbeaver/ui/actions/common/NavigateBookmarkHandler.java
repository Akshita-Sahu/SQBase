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
package org.jkiss.sqbase.ui.actions.common;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.navigator.DBNDataSource;
import org.jkiss.sqbase.model.navigator.DBNNode;
import org.jkiss.sqbase.model.navigator.DBNUtils;
import org.jkiss.sqbase.ui.UIUtils;
import org.jkiss.sqbase.ui.navigator.NavigatorUtils;
import org.jkiss.sqbase.ui.navigator.actions.NavigatorHandlerObjectBase;
import org.jkiss.sqbase.ui.navigator.database.DatabaseNavigatorView;
import org.jkiss.sqbase.ui.navigator.database.NavigatorViewBase;
import org.jkiss.sqbase.ui.navigator.project.ProjectExplorerView;
import org.jkiss.sqbase.ui.resources.bookmarks.BookmarkStorage;
import org.jkiss.sqbase.ui.resources.bookmarks.BookmarksHandlerImpl;
import org.jkiss.sqbase.ui.resources.bookmarks.DBNBookmark;

public class NavigateBookmarkHandler extends NavigatorHandlerObjectBase {

    private static final Log log = Log.getLog(NavigateBookmarkHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        NavigatorViewBase activeNavigatorView = NavigatorUtils.getActiveNavigatorView(event);

        if (activeNavigatorView != null) {
            DBNNode selectedNode = NavigatorUtils.getSelectedNode(HandlerUtil.getCurrentSelection(event));
            if (selectedNode instanceof DBNBookmark) {
                BookmarkStorage storage = ((DBNBookmark) selectedNode).getStorage();

                final DBPDataSourceContainer dataSourceContainer = DBUtils.findDataSource(storage.getDataSourceId());
                if (dataSourceContainer == null) {
                    log.debug("Can't find datasource '" + storage.getDataSourceId() + "'"); //$NON-NLS-2$
                    return null;
                }
                final DBNDataSource dsNode = (DBNDataSource) DBNUtils.getNodeByObject(dataSourceContainer);
                if (dsNode == null) {
                    log.error("Can't find datasource node for '" + dataSourceContainer.getName() + "'"); //$NON-NLS-2$
                    return null;
                }

                if (activeNavigatorView instanceof ProjectExplorerView) {
                    // Show in DB navigator is we are currently in project explorer
                    final IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
                    IViewPart dbNavigatorPart = activePage.findView(DatabaseNavigatorView.VIEW_ID);
                    if (dbNavigatorPart instanceof NavigatorViewBase) {
                        activeNavigatorView = (NavigatorViewBase) dbNavigatorPart;
                        try {
                            activePage.showView(DatabaseNavigatorView.VIEW_ID);
                        } catch (PartInitException e) {
                            log.debug(e);
                        }
                    }
                }

                NavigatorViewBase navigatorView = activeNavigatorView;
                try {
                    dsNode.initializeNode(null, status -> {
                        if (status.isOK()) {
                            UIUtils.syncExec(() -> BookmarksHandlerImpl.navigateNodeByPath(navigatorView, dsNode, storage));
                        }
                    });
                } catch (DBException e) {
                    log.error(e);
                }
            }
        }

        return null;
    }

}