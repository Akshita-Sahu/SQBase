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
package org.jkiss.sqbase.model.ai;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSEntity;

public interface AISchemaGenerator {
    String generateSchema(
        @NotNull DBRProgressMonitor monitor,
        @Nullable DBCExecutionContext executionContext,
        @NotNull AISchemaGenerationOptions options,
        @NotNull DBSEntity entity
    ) throws DBException;
}
