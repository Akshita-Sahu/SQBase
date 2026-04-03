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

package org.jkiss.sqbase.ext.mysql.ui.config;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.mysql.model.MySQLCatalog;
import org.jkiss.sqbase.ext.mysql.ui.views.MySQLCreateDatabaseDialog;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectConfigurator;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.ui.UITask;
import org.jkiss.sqbase.ui.UIUtils;

import java.util.Map;

/**
 * MySQL database configurator
 */
public class MySQLCatalogConfigurator implements DBEObjectConfigurator<MySQLCatalog> {

    @Override
    public MySQLCatalog configureObject(@NotNull DBRProgressMonitor monitor, @Nullable DBECommandContext commandContext, @Nullable Object dataSource, @NotNull MySQLCatalog database, @NotNull Map<String, Object> options) {
        return UITask.run(() -> {
            MySQLCreateDatabaseDialog dialog = new MySQLCreateDatabaseDialog(UIUtils.getActiveWorkbenchShell(), database);
            if (dialog.open() != IDialogConstants.OK_ID) {
                return null;
            }
            String schemaName = dialog.getName();
            database.setName(schemaName);
            database.getAdditionalInfo().setDefaultCharset(dialog.getCharset());
            database.getAdditionalInfo().setDefaultCollation(dialog.getCollation());
            return database;
        });
    }
}
