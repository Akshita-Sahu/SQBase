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
package org.jkiss.sqbase.ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.DBConstants;
import org.jkiss.sqbase.model.DBPNamedObject;
import org.jkiss.sqbase.model.DBPObject;
import org.jkiss.sqbase.model.DBUtils;
import org.jkiss.sqbase.model.data.DBDAttributeBinding;
import org.jkiss.sqbase.model.data.DBDRowIdentifier;
import org.jkiss.sqbase.model.meta.Property;
import org.jkiss.sqbase.runtime.properties.PropertyCollector;
import org.jkiss.sqbase.ui.data.IAttributeController;
import org.jkiss.sqbase.ui.data.IRowController;
import org.jkiss.sqbase.ui.data.IValueController;
import org.jkiss.sqbase.ui.editors.data.internal.DataEditorsMessages;
import org.jkiss.sqbase.ui.properties.PropertyTreeViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Column info panel.
 */
public class ColumnInfoPanel extends Composite {

    private PropertyTreeViewer propViewer;

    public ColumnInfoPanel(Composite parent, int style, IValueController valueController) {
        super(parent, style);
        if (valueController instanceof IAttributeController) {
            this.createPanel((IAttributeController) valueController);
        }
    }

    protected void createPanel(IAttributeController valueController)
    {
        PropertyCollector infoItem = new PropertyCollector(valueController.getBinding().getMetaAttribute(), false);
        infoItem.collectProperties();
        valueController.getValueManager().contributeProperties(infoItem, valueController);
        DBDRowIdentifier rowIdentifier = valueController.getRowIdentifier();
        if (rowIdentifier != null) {
            infoItem.addProperty(
                null,
                "Key",
                DataEditorsMessages.controls_column_info_panel_property_key,
                new CellKeyInfo(valueController.getRowController(), rowIdentifier)
            );
        }

        this.setLayout(new FillLayout());
        {
            propViewer = new PropertyTreeViewer(this, SWT.H_SCROLL | SWT.V_SCROLL);
            propViewer.loadProperties(infoItem);
        }
    }

    public void layoutProperties() {
        getParent().layout();
        propViewer.repackColumns();
    }

    public static class KeyColumnValue implements DBPNamedObject {
        private DBDAttributeBinding attribute;
        private Object value;
        public KeyColumnValue(DBDAttributeBinding attribute, @Nullable Object value)
        {
            this.attribute = attribute;
            this.value = value;
        }
        @NotNull
        @Override
        public String getName()
        {
            return attribute.getName();
        }
        @Override
        public String toString()
        {
            return DBUtils.isNullValue(value) ? DBConstants.NULL_VALUE_LABEL : value.toString(); //$NON-NLS-1$
        }
    }

    public static class CellKeyInfo implements DBPObject {
        @NotNull
        private final IRowController rowController;
        @NotNull
        private final DBDRowIdentifier rowIdentifier;

        public CellKeyInfo(@NotNull IRowController rowController, @NotNull DBDRowIdentifier rowIdentifier) {
            this.rowController = rowController;
            this.rowIdentifier = rowIdentifier;
        }

        @Property(viewable = true, order = 1, category = "general")
        public String getType()
        {
            return rowIdentifier.getKeyType();
        }

        @Property(viewable = true, order = 2, category = "general")
        public String getName()
        {
            return rowIdentifier.getUniqueKey().getName();
        }

        @Property(viewable = true, order = 3, category = "columns")
        public List<KeyColumnValue> getColumns()
        {
            List<DBDAttributeBinding> rowAttributes = rowController.getRowAttributes();
            List<KeyColumnValue> columns = new ArrayList<>();
            for (DBDAttributeBinding binding : rowIdentifier.getAttributes()) {
                columns.add(new KeyColumnValue(binding, rowController.getAttributeValue(binding)));
            }
            return columns;
        }

        @Override
        public String toString()
        {
            return "";//valueController.getRowIdentifier().getEntityIdentifier()
        }
    }

}
