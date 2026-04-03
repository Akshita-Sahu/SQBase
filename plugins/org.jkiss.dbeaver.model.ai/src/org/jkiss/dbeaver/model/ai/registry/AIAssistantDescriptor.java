/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2026 SQBase Corp and others
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
package org.jkiss.sqbase.model.ai.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.ai.AIAssistant;
import org.jkiss.sqbase.model.ai.AISqlFormatter;
import org.jkiss.sqbase.model.app.DBPWorkspace;
import org.jkiss.sqbase.model.impl.AbstractDescriptor;
import org.jkiss.sqbase.registry.RegistryConstants;
import org.jkiss.utils.CommonUtils;

public class AIAssistantDescriptor extends AbstractDescriptor {

    public static final String EXTENSION_ID = "com.sqbase.ai.assistant";
    private final ObjectType objectType;
    private final ObjectType formatterType;
    private final int priority;

    protected AIAssistantDescriptor(IConfigurationElement contributorConfig) {
        super(contributorConfig);
        this.objectType = new ObjectType(contributorConfig, RegistryConstants.ATTR_CLASS);
        this.formatterType = new ObjectType(contributorConfig, "sqlFormatter");
        this.priority = CommonUtils.toInt(contributorConfig.getAttribute("priority"));
    }

    @NotNull
    public AIAssistant createInstance(DBPWorkspace workspace) throws DBException {
        return objectType.createInstance(AIAssistant.class, workspace);
    }

    @NotNull
    public AISqlFormatter createSqlFormatter() throws DBException {
        return formatterType.createInstance(AISqlFormatter.class);
    }

    public int getPriority() {
        return priority;
    }
}