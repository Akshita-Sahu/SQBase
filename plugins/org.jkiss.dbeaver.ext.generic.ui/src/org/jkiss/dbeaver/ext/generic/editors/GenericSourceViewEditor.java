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

package org.jkiss.sqbase.ext.generic.editors;

import org.jkiss.sqbase.ext.generic.model.GenericSQLDialect;
import org.jkiss.sqbase.model.DBPScriptObject;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.sql.SQLUtils;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.DBSObjectWithScript;
import org.jkiss.sqbase.ui.editors.sql.SQLSourceViewer;

/**
 * GenericSourceViewEditor
 */
public class GenericSourceViewEditor<T extends DBPScriptObject & DBSObject> extends SQLSourceViewer<T> {

    public GenericSourceViewEditor() {
    }

    @Override
    protected boolean isReadOnly() {
        return !(getSourceObject() instanceof DBSObjectWithScript);
    }

    @Override
    protected void setSourceText(DBRProgressMonitor monitor, String sourceText) {
        boolean supportsDelimitersInViews = true;
        if (getSQLDialect() instanceof GenericSQLDialect) {
            supportsDelimitersInViews = ((GenericSQLDialect) getSQLDialect()).supportsDelimiterAfterViews();
        }
        getInputPropertySource().setPropertyValue(monitor, "objectDefinitionText",
            supportsDelimitersInViews ? sourceText : SQLUtils.removeQueryDelimiter(getSQLDialect(), sourceText));
    }

}

