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
package org.jkiss.sqbase.registry.data.hints;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSource;
import org.jkiss.sqbase.model.data.DBDAttributeBinding;
import org.jkiss.sqbase.model.data.hints.DBDValueHintContext;
import org.jkiss.sqbase.model.data.hints.DBDValueHintProvider;
import org.jkiss.sqbase.model.struct.DBSDataContainer;
import org.jkiss.sqbase.model.struct.DBSEntity;
import org.jkiss.sqbase.model.struct.DBSTypedObject;
import org.jkiss.utils.CommonUtils;

/**
 * ValueHintProviderDescriptor
 */
public class ValueHintProviderDescriptor extends AbstractValueBindingDescriptor<DBDValueHintProvider, DBDValueHintContext> {
    private static final Log log = Log.getLog(ValueHintProviderDescriptor.class);

    public static final String EXTENSION_ID = "org.jkiss.sqbase.dataHintProvider"; //$NON-NLS-1$
    public static final String TAG_HINT_PROVIDER = "hintProvider"; //$NON-NLS-1$

    @NotNull
    private final DBDValueHintProvider.HintObject forObject;
    private final boolean visibleByDefault;
    private final boolean association;
    @NotNull
    private final String label;

    public ValueHintProviderDescriptor(IConfigurationElement config) {
        super(config);
        String forAttr = config.getAttribute("for");
        if (forAttr != null) forAttr = forAttr.toUpperCase();
        this.forObject = CommonUtils.valueOf(
            DBDValueHintProvider.HintObject.class,
            forAttr,
            DBDValueHintProvider.HintObject.CELL);
        this.visibleByDefault = CommonUtils.getBoolean(config.getAttribute("visibleByDefault"), true);
        this.association = CommonUtils.getBoolean(config.getAttribute("association"));
        this.label = config.getAttribute("label");
    }

    @Override
    protected Class<DBDValueHintProvider> getImplClass() {
        return DBDValueHintProvider.class;
    }

    @NotNull
    public DBDValueHintProvider.HintObject getForObject() {
        return forObject;
    }

    @NotNull
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isEnabled(DBSTypedObject typedObject, DBDValueHintContext context, boolean checkConfigDisable) {
        if (checkConfigDisable) {
            DBSDataContainer dataContainer = context.getDataContainer();
            DBPDataSource dataSource = dataContainer == null ? null : dataContainer.getDataSource();
            DBSEntity contextEntity = dataSource == null ? null : context.getContextEntity();
            if (!ValueHintRegistry.getInstance().isHintEnabled(
                this,
                dataSource == null ? null : dataSource.getContainer(),
                contextEntity)
            ) {
                return false;
            }
        }
        if (association && typedObject != null) {
            return typedObject instanceof DBDAttributeBinding binding &&
               !CommonUtils.isEmpty(binding.getReferrers());
        }
        return true;
    }

    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

}