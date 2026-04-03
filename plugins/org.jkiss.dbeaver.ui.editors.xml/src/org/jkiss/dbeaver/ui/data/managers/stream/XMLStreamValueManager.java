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
package org.jkiss.sqbase.ui.data.managers.stream;

import org.eclipse.ui.IEditorPart;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.data.DBDContent;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSTypedObject;
import org.jkiss.sqbase.ui.data.IStreamValueEditor;
import org.jkiss.sqbase.ui.data.IStreamValueManager;
import org.jkiss.sqbase.ui.data.IValueController;
import org.jkiss.sqbase.utils.ContentUtils;

/**
 * XML editor manager
 */
public class XMLStreamValueManager implements IStreamValueManager {

    @Override
    public MatchType matchesTo(@NotNull DBRProgressMonitor monitor, @NotNull DBSTypedObject attribute, @Nullable DBDContent value) {
        // Applies to text values
        return ContentUtils.isXML(value) ? MatchType.PRIMARY : MatchType.APPLIES;
    }

    @Override
    public IStreamValueEditor createPanelEditor(@NotNull final IValueController controller)
        throws DBException
    {
        return new XMLPanelEditor();
    }

    @Override
    public IEditorPart createEditorPart(@NotNull IValueController controller) {
        return new XMLEditorPart();
    }

}
