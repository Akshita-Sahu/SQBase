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
package org.jkiss.sqbase.ui.editors.sql.terminal;


import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.console.MessageConsole;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.preferences.DBPPreferenceStore;
import org.jkiss.sqbase.model.sql.SQLQueryResult;
import org.jkiss.sqbase.ui.SQBaseIcons;
import org.jkiss.sqbase.ui.UIIcon;
import org.jkiss.sqbase.ui.controls.resultset.ResultSetModel;
import org.jkiss.sqbase.ui.controls.resultset.plaintext.PlainTextFormatter;
import org.jkiss.sqbase.ui.editors.sql.SQLEditorOutputConsoleViewer;
import org.jkiss.sqbase.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;


public class SQLTerminalView extends SQLEditorOutputConsoleViewer {

    public SQLTerminalView(@NotNull IWorkbenchPartSite site, @NotNull CTabFolder tabsContainer, int styles) {
        super(site, tabsContainer, new MessageConsole("sql-data-log-output", SQBaseIcons.getImageDescriptor(UIIcon.SQL_CONSOLE)));
    }

    public void printQueryData(@NotNull DBPPreferenceStore prefs, @NotNull ResultSetModel model, @Nullable String name) {
        PlainTextFormatter formatter = new PlainTextFormatter(prefs);
        StringBuilder grid = new StringBuilder();
        if (prefs.getBoolean(SQLTerminalPreferencesConstants.SHOW_QUERY_TEXT)) {
            formatter.printQueryName(grid, name);
            grid.append("\n");
        }
        
        int totalRows = formatter.printGrid(grid, model);
        this.getOutputWriter().append("\n");
        this.getOutputWriter().append(grid.toString()).append("\n\n");
        this.getOutputWriter().append(String.valueOf(totalRows)).append(" row(s) fetched.\n\n");
        this.getOutputWriter().flush();
        this.scrollToEnd();
    }

    public void printQueryResult(@NotNull DBPPreferenceStore prefs, @NotNull SQLQueryResult result) {
        boolean hasUpdateCount = result.getExecuteResults().stream().anyMatch(r -> r.getUpdateCount() != null);
        Throwable error = result.getError();
        
        if (hasUpdateCount || error != null) {
            PlainTextFormatter formatter = new PlainTextFormatter(prefs);
            StringBuilder grid = new StringBuilder();
            if (prefs.getBoolean(SQLTerminalPreferencesConstants.SHOW_QUERY_TEXT)) {
                formatter.printQueryName(grid, result.getStatement().getText());
                grid.append("\n");
            }
            
            if (hasUpdateCount) {
                long updateCount = result.getExecuteResults().stream().mapToLong(r -> CommonUtils.notNull(r.getUpdateCount(), 0L)).sum();
                grid.append(updateCount).append(" row(s) modified.\n\n");
            }            
            if (error != null) {
                grid.append(GeneralUtils.getFirstMessage(error)).append("\n\n");
            }
            
            this.getOutputWriter().append(grid.toString());
            this.getOutputWriter().flush();
            this.scrollToEnd();
        }
    }
}
