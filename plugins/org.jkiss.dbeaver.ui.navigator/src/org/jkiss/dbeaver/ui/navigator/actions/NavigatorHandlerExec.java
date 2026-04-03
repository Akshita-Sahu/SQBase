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
package org.jkiss.sqbase.ui.navigator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.app.DBPPlatformDesktop;
import org.jkiss.sqbase.model.fs.DBFUtils;
import org.jkiss.sqbase.model.navigator.DBNNode;
import org.jkiss.sqbase.model.navigator.DBNResource;
import org.jkiss.sqbase.model.rcp.RCPProject;
import org.jkiss.sqbase.model.runtime.AbstractJob;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.DBSObjectContainer;
import org.jkiss.sqbase.model.struct.rdb.DBSCatalog;
import org.jkiss.sqbase.ui.UIUtils;
import org.jkiss.sqbase.ui.actions.exec.SQLNativeExecutorDescriptor;
import org.jkiss.sqbase.ui.actions.exec.SQLNativeExecutorRegistry;
import org.jkiss.sqbase.ui.actions.exec.SQLScriptExecutor;
import org.jkiss.sqbase.ui.editors.EditorUtils;
import org.jkiss.sqbase.ui.navigator.NavigatorUtils;

import java.nio.file.Path;
import java.util.Collection;

public class NavigatorHandlerExec extends AbstractHandler {
    private static final Log log = Log.getLog(NavigatorHandlerExec.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection.isEmpty()) {
            return null;
        }
        final DBNNode node = NavigatorUtils.getSelectedNode(selection);
        assert(node instanceof DBNResource);
        IResource resource = ((DBNResource) node).getResource();
        assert (resource instanceof IFile);
        IFile script = (IFile) resource;

        DBPDataSourceContainer container = EditorUtils.getFileDataSource(script);
        if (container == null) {
            return null;
        }
        new AbstractJob("Calling native execution") {
            @NotNull
            @Override
            protected IStatus run(@NotNull DBRProgressMonitor monitor) {
                RCPProject project = (RCPProject) DBPPlatformDesktop.getInstance().getWorkspace().getProject(script.getProject());
                String resourcePath = project.getResourcePath(script);
                String catalog = (String) project.getResourceProperty(
                    resourcePath,
                    EditorUtils.PROP_CONTEXT_DEFAULT_CATALOG
                );
                try {
                    if (!container.isConnected()) {
                        container.connect(monitor, true, false);
                    }
                    DBPDataSource dataSource = container.getDataSource();
                    DBSObject launchObject = dataSource;
                    if (catalog != null) {
                        if (dataSource instanceof DBSObjectContainer) {
                            if (DBSCatalog.class.isAssignableFrom(((DBSObjectContainer) dataSource).getPrimaryChildType(monitor))) {
                                Collection<? extends DBSObject> children = ((DBSObjectContainer) dataSource).getChildren(monitor);
                                DBSObject foundCatalog = DBUtils.findObject(children, catalog);
                                if (foundCatalog != null) {
                                    launchObject = foundCatalog;
                                }
                            }
                        }
                    }
                    SQLNativeExecutorDescriptor executorDescriptor = SQLNativeExecutorRegistry.getInstance()
                        .getExecutorDescriptor(container);
                    if (executorDescriptor != null && executorDescriptor.getNativeExecutor() != null) {
                        Path file = DBFUtils.resolvePathFromURI(monitor, project, script.getLocationURI());

                        SQLScriptExecutor<DBSObject> nativeExecutor =
                            (SQLScriptExecutor<DBSObject>) executorDescriptor.getNativeExecutor();
                        DBSObject finalLaunchObject = launchObject;
                        UIUtils.syncExec(() -> {
                            try {
                                nativeExecutor.execute(finalLaunchObject, file);
                            } catch (DBException e) {
                                log.error(e);
                            }
                        });
                    }
                } catch (Throwable exception) {
                    return Status.error("Error calling native execution" ,exception);
                }
                return Status.OK_STATUS;
            }
        }.schedule();
        return null;
    }
}
