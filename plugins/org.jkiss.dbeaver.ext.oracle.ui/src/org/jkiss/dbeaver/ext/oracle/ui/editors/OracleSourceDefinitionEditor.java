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

package org.jkiss.sqbase.ext.oracle.ui.editors;

import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.ext.oracle.model.OracleConstants;
import org.jkiss.sqbase.ext.oracle.model.source.OracleSourceObject;
import org.jkiss.sqbase.model.DBPScriptObjectExt;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.ui.editors.sql.SQLSourceViewer;

/**
 * Oracle source definition editor
 */
public class OracleSourceDefinitionEditor extends SQLSourceViewer<OracleSourceObject> {

    @Override
    protected String getCompileCommandId()
    {
        return OracleConstants.CMD_COMPILE;
    }

    @Override
    protected String getSourceText(DBRProgressMonitor monitor) throws DBException {
        return ((DBPScriptObjectExt)getSourceObject()).getExtendedDefinitionText(monitor);
    }

    @Override
    protected void setSourceText(DBRProgressMonitor monitor, String sourceText) {
        getInputPropertySource().setPropertyValue(
            monitor,
            OracleConstants.PROP_OBJECT_BODY_DEFINITION,
            sourceText);
    }

    @Override
    protected boolean isReadOnly() {
        return false;
    }
}
