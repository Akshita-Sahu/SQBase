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
package org.jkiss.sqbase.ext.denodo.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.ext.generic.model.GenericSQLDialect;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.exec.jdbc.JDBCDatabaseMetaData;
import org.jkiss.sqbase.model.exec.jdbc.JDBCSession;
import org.jkiss.sqbase.model.impl.jdbc.JDBCDataSource;
import org.jkiss.sqbase.model.sql.parser.rules.SQLDollarQuoteRule;
import org.jkiss.sqbase.model.text.parser.TPRule;
import org.jkiss.sqbase.model.text.parser.TPRuleProvider;

import java.util.Arrays;

public class DenodoSQLDialect extends GenericSQLDialect implements TPRuleProvider {

    public DenodoSQLDialect() {
        super("Denodo", "denodo");
    }

    public void initDriverSettings(JDBCSession session, JDBCDataSource dataSource, JDBCDatabaseMetaData metaData) {
        super.initDriverSettings(session, dataSource, metaData);
        addSQLKeywords(
                Arrays.asList(
                        "VQL",
                        "DESC"
                ));
    }

    @NotNull
    @Override
    public TPRule[] extendRules(@Nullable DBPDataSourceContainer dataSource, @NotNull RulePosition position) {
        if (position == RulePosition.INITIAL || position == RulePosition.PARTITION) {
            return new TPRule[] {
                new SQLDollarQuoteRule(position == RulePosition.PARTITION, false, false, true) };
        }
        return new TPRule[0];
    }

    @Override
    public boolean supportsAliasInConditions() {
        return false;
    }
}
