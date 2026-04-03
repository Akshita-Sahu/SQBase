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

package org.jkiss.sqbase.ui.editors;

import org.eclipse.swt.graphics.Color;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.DBPContextProvider;
import org.jkiss.sqbase.model.edit.DBECommandContext;
import org.jkiss.sqbase.model.navigator.DBNDatabaseNode;
import org.jkiss.sqbase.model.preferences.DBPPropertySource;
import org.jkiss.sqbase.model.struct.DBSObject;

import java.util.Collection;

/**
 * IDatabaseEditorInput
 */
public interface IDatabaseEditorInput extends INavigatorEditorInput, DBPContextProvider {

    @Nullable
    @Override
    DBNDatabaseNode getNavigatorNode();

    DBSObject getDatabaseObject();

    @Nullable
    String getNodePath();

    /**
     * Default editor page ID
     * @return page ID or null
     */
    String getDefaultPageId();

    /**
     * Default editor folder (tab) ID
     * @return folder ID or null
     */
    String getDefaultFolderId();

    /**
     * Color of the connection type used by the associated connection
     *
     * @return connection color or {@code null} if not defined
     */
    @Nullable
    Color getConnectionColor();

    /**
     * Command context
     * @return command context
     */
    @Nullable
    DBECommandContext getCommandContext();

    /**
     * Underlying object's property source
     * @return property source
     */
    DBPPropertySource getPropertySource();

    Collection<String> getAttributeNames();

    Object getAttribute(String name);

    Object setAttribute(String name, Object value);
}
