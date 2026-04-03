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

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.mysql.model.MySQLEvent;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectConfigurator;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSEntityType;
import org.jkiss.sqbase.ui.UITask;
import org.jkiss.sqbase.ui.editors.object.struct.EntityEditPage;

import java.util.Map;

/**
 * MySQLEventConfigurator
 */
public class MySQLEventConfigurator implements DBEObjectConfigurator<MySQLEvent> {

    @Override
    public MySQLEvent configureObject(@NotNull DBRProgressMonitor monitor, @Nullable DBECommandContext commandContext, @Nullable Object parent, @NotNull MySQLEvent event, @NotNull Map<String, Object> options) {
        return UITask.run(() -> {
            EntityEditPage editPage = new EntityEditPage(event.getDataSource(), DBSEntityType.EVENT);
            if (!editPage.edit()) {
                return null;
            }
            event.setName(editPage.getEntityName());
            event.setEventDefinition("SELECT 1");
            return event;
        });
    }

}
