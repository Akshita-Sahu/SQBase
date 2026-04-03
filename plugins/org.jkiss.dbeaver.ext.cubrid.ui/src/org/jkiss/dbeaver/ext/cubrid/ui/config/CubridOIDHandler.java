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
package org.jkiss.sqbase.ext.cubrid.ui.config;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.sqbase.ext.cubrid.model.CubridDataSource;
import org.jkiss.sqbase.ext.cubrid.ui.views.CubridOIDSearchDialog;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.navigator.DBNDataSource;
import org.jkiss.sqbase.model.navigator.DBNNode;
import org.jkiss.sqbase.model.runtime.VoidProgressMonitor;
import org.jkiss.sqbase.registry.DataSourceDescriptor;
import org.jkiss.sqbase.ui.navigator.NavigatorUtils;

public class CubridOIDHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final Shell activeShell = HandlerUtil.getActiveShell(event);
        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        final DBNNode node = NavigatorUtils.getSelectedNode(selection);
        if (node instanceof DBNDataSource dataSourceNode) {
            DataSourceDescriptor descriptor = (DataSourceDescriptor) dataSourceNode.getDataSourceContainer();
            DBPDataSource dataSource = descriptor.getDataSource();
            CubridDataSource cubrid = (CubridDataSource) dataSource;
            try {
                JDBCSession session = DBUtils.openMetaSession(new VoidProgressMonitor(), cubrid, "GetSession");
                CubridOIDSearchDialog dialog = new CubridOIDSearchDialog(activeShell, session);
                dialog.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
