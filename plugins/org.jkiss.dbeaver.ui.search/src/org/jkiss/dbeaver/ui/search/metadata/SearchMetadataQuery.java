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
package org.jkiss.sqbase.ui.search.metadata;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.navigator.DBNModel;
import org.jkiss.sqbase.model.navigator.DBNNode;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.DBSObjectReference;
import org.jkiss.sqbase.model.struct.DBSStructureAssistant;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.ui.search.AbstractSearchResult;
import org.jkiss.sqbase.utils.GeneralUtils;
import org.jkiss.sqbase.utils.RuntimeUtils;

import java.util.Collection;
import java.util.Collections;

public class SearchMetadataQuery implements ISearchQuery {
    private static final Log log = Log.getLog(SearchMetadataQuery.class);

    private final DBSStructureAssistant structureAssistant;
    private final DBCExecutionContext executionContext;
    @NotNull
    private final DBSStructureAssistant.ObjectsSearchParams params;
    private SearchMetadataResult searchResult;

    SearchMetadataQuery(@NotNull DBPDataSource dataSource, @NotNull DBSStructureAssistant<?> structureAssistant,
                        @NotNull DBSStructureAssistant.ObjectsSearchParams params) {
        this.structureAssistant = structureAssistant;
        this.executionContext = DBUtils.getDefaultContext(dataSource, true);
        this.params = params;
    }

    @Override
    public String getLabel() {
        return params.getMask();
    }

    @Override
    public boolean canRerun() {
        return true;
    }

    @Override
    public boolean canRunInBackground() {
        return true;
    }

    @Override
    public ISearchResult getSearchResult() {
        if (searchResult == null) {
            searchResult = new SearchMetadataResult(this);
        }
        return searchResult;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        try {
            String objectNameMask = params.getMask();
            if (!params.isLikeCondition() && !objectNameMask.endsWith("%")) { //$NON-NLS-1$
                objectNameMask = objectNameMask + "%"; //$NON-NLS-1$
                params.setMask(objectNameMask);
            }
            int totalObjects = 0;
            DBNModel navigatorModel = DBWorkbench.getPlatform().getNavigatorModel();
            DBRProgressMonitor localMonitor = RuntimeUtils.makeMonitor(monitor);

            Collection<DBSObjectReference> objects = structureAssistant.findObjectsByMask(localMonitor, executionContext, params);
            for (DBSObjectReference reference : objects) {
                if (monitor.isCanceled()) {
                    break;
                }
                try {
                    DBSObject object = reference.resolveObject(localMonitor);
                    if (object != null) {
                        DBNNode node = navigatorModel.getNodeByObject(localMonitor, object, false);
                        if (node != null) {
                            searchResult.addObjects(Collections.singletonList(node));
                            totalObjects++;
                        }
                    }
                } catch (DBException e) {
                    log.error(e);
                }
            }
            searchResult.fireChange(new AbstractSearchResult.DatabaseSearchFinishEvent(searchResult, totalObjects));

            return Status.OK_STATUS;
        } catch (DBException e) {
            log.debug(e);
            return GeneralUtils.makeExceptionStatus(e);
        }
    }
}
