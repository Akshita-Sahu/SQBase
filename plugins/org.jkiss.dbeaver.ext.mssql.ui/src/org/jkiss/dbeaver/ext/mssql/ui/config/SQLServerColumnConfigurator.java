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
package org.jkiss.sqbase.ext.mssql.ui.config;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.mssql.model.SQLServerTableColumn;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectConfigurator;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.ui.UITask;
import org.jkiss.sqbase.ui.editors.object.struct.EditAttributePage;

import java.util.Map;

public class SQLServerColumnConfigurator implements DBEObjectConfigurator<SQLServerTableColumn> {
    @Override
    public SQLServerTableColumn configureObject(@NotNull DBRProgressMonitor monitor, @Nullable DBECommandContext commandContext, @Nullable Object container, @NotNull SQLServerTableColumn column, @NotNull Map<String, Object> options) {
        return new UITask<SQLServerTableColumn>() {
            @Override
            protected SQLServerTableColumn runTask() {
                final EditAttributePage page = new EditAttributePage(commandContext, column, options);
                if (!page.edit()) {
                    return null;
                }
                return column;
            }
        }.execute();
    }
}
