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
package org.jkiss.sqbase.model.impl.struct;

import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.struct.DBSObject;
import org.jkiss.sqbase.model.struct.DBSObjectType;

/**
 * Direct object reference
 */
public class DirectObjectReference extends AbstractObjectReference<DBSObject> {

    private final DBSObject object;

    public DirectObjectReference(DBSObject container, DBSObjectType type, DBSObject object) {
        super(object.getName(), container, object.getDescription(), object.getClass(), type);
        this.object = object;
    }

    @Override
    public DBSObject resolveObject(DBRProgressMonitor monitor) throws DBException {
        return object;
    }
}
