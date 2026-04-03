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
package org.jkiss.sqbase.ui.data.managers;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.data.DBDCursor;
import org.jkiss.sqbase.ui.data.IValueController;
import org.jkiss.sqbase.ui.data.IValueEditor;
import org.jkiss.sqbase.ui.data.dialogs.CursorViewDialog;
import org.jkiss.sqbase.ui.data.editors.CursorPanelEditor;

/**
 * Object value manager.
 */
public class ObjectValueManager extends StringValueManager {

    @Override
    public IValueEditor createEditor(@NotNull final IValueController controller)
        throws DBException
    {
        final Object value = controller.getValue();
        if (value instanceof DBDCursor) {
            switch (controller.getEditType()) {
                case EDITOR:
                    return new CursorViewDialog(controller);
                case PANEL:
                    return new CursorPanelEditor(controller);
            }
        }
        return super.createEditor(controller);
    }

}