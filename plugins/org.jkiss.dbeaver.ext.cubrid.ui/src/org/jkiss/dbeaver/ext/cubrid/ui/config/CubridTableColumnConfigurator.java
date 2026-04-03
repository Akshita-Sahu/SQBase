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

import java.lang.reflect.Field;
import java.util.Map;

import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ext.cubrid.model.CubridTableColumn;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.edit.DBEObjectConfigurator;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSEntity;
import org.jkiss.sqbase.ui.UITask;
import org.jkiss.sqbase.ui.editors.object.struct.ConstraintNameGenerator;
import org.jkiss.sqbase.ui.editors.object.struct.EditAttributePage;

public class CubridTableColumnConfigurator implements DBEObjectConfigurator<CubridTableColumn>{
    private static final Log log = Log.getLog(CubridTableColumnConfigurator.class);

    @Override
    public CubridTableColumn configureObject(DBRProgressMonitor monitor, DBECommandContext commandContext, Object container, CubridTableColumn column, Map<String, Object> options) {
        return new UITask<CubridTableColumn>() {
            @Override
            protected CubridTableColumn runTask() {
                final EditAttributePage page = new EditAttributePage(commandContext, column, options);
                try {
                    Field nameGenField = EditAttributePage.class.getDeclaredField("constraintNameGenerator");
                    nameGenField.setAccessible(true);
                    nameGenField.set(page, new CubridConstraintNameGenerator(
                        column.getParentObject(),
                        column.isPersisted() ? column.getName() : null
                    ));
                } catch (Exception e) {
                    log.error(e);
                }

                if (!page.edit()) {
                    return null;
                }
                return column;
            }
        }.execute();
    }

    class CubridConstraintNameGenerator extends ConstraintNameGenerator {

        public CubridConstraintNameGenerator(DBSEntity entity, String constraintName) {
            super(entity, constraintName);
        }

        @Override
        public String getConstraintName() {
            return super.getConstraintName() != null ? super.getConstraintName().toLowerCase() : null;
        }
    }
}
