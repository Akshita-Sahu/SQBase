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
package org.jkiss.sqbase.model.sql.semantics.context;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.data.DBDPseudoAttribute;
import org.jkiss.sqbase.model.sql.semantics.SQLQuerySymbol;
import org.jkiss.sqbase.model.sql.semantics.SQLQuerySymbolClass;
import org.jkiss.sqbase.model.sql.semantics.SQLQuerySymbolDefinition;
import org.jkiss.sqbase.model.sql.semantics.model.select.SQLQueryRowsSourceModel;
import org.jkiss.sqbase.model.struct.DBSEntity;
import org.jkiss.sqbase.model.struct.DBSEntityAttribute;

public class SQLQueryResultPseudoColumn implements SQLQuerySymbolDefinition {
    @NotNull
    public final SQLQuerySymbol symbol;
    @Nullable
    public final SQLQueryRowsSourceModel source;
    @Nullable
    public final DBSEntity realSource;
    @NotNull
    public final SQLQueryExprType type;
    @NotNull
    public final DBDPseudoAttribute.PropagationPolicy propagationPolicy;
    @Nullable
    public final String description;

    public SQLQueryResultPseudoColumn(
        @NotNull SQLQuerySymbol symbol,
        @Nullable SQLQueryRowsSourceModel source,
        @Nullable DBSEntity realSource,
        @NotNull SQLQueryExprType type,
        @NotNull DBDPseudoAttribute.PropagationPolicy propagationPolicy,
        @Nullable String description
    ) {
        this.symbol = symbol;
        this.source = source;
        this.realSource = realSource;
        this.type = type;
        this.propagationPolicy = propagationPolicy;
        this.description = description;
    }

    @NotNull
    @Override
    public SQLQuerySymbolClass getSymbolClass() {
        return SQLQuerySymbolClass.COLUMN;
    }
}

