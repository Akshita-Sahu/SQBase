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

package org.jkiss.sqbase.ui.actions.datasource;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.model.connection.DBPDriver;
import org.jkiss.sqbase.model.connection.DBPDriverDependencies;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.runtime.ui.UIServiceDrivers;
import org.jkiss.sqbase.ui.UITask;
import org.jkiss.sqbase.ui.dialogs.driver.DriverDownloadDialog;
import org.jkiss.utils.CommonUtils;

/**
 * UIServiceDriversImpl
 */
public class UIServiceDriversImpl implements UIServiceDrivers {

    @Override
    public boolean downloadDriverFiles(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBPDriver driver,
        @NotNull DBPDriverDependencies dependencies,
        boolean isShowExpanded
    ) {
        Boolean result = new UITask<Boolean>() {
            @Override
            protected Boolean runTask() {
                return DriverDownloadDialog.downloadDriverFiles(
                    null,
                    driver,
                    dependencies,
                    isShowExpanded
                );
            }
        }.execute();
        return CommonUtils.toBoolean(result, false);
    }

}
