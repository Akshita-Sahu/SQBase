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
package org.jkiss.sqbase.model.impl.data;

import org.jkiss.code.NotNull;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.model.data.DBDContentStorage;
import org.jkiss.sqbase.model.data.DBDDisplayFormat;
import org.jkiss.sqbase.model.data.storage.StringContentStorage;
import org.jkiss.sqbase.model.exec.DBCException;
import org.jkiss.sqbase.model.exec.DBCExecutionContext;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.utils.ContentUtils;
import org.jkiss.sqbase.utils.MimeTypes;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

/**
 * StringContent
 *
 * @author Serge Rider
 */
public class StringContent extends AbstractContent {

    private StringContentStorage storage;

    public StringContent(DBCExecutionContext executionContext, String data) {
        super(executionContext);
        this.storage = new StringContentStorage(data);
    }

    @Override
    public long getContentLength() throws DBCException {
        return storage.getContentLength();
    }

    @NotNull
    @Override
    public String getContentType() {
        return MimeTypes.TEXT_PLAIN;
    }

    @Override
    public String getDisplayString(@NotNull DBDDisplayFormat format) {
        return storage.getCachedValue();
    }

    @Override
    public DBDContentStorage getContents(@NotNull DBRProgressMonitor monitor) throws DBCException {
        return storage;
    }

    @Override
    public boolean updateContents(@NotNull DBRProgressMonitor monitor, @NotNull DBDContentStorage storage) throws DBException {
        try {
            try (Reader reader = storage.getContentReader()) {
                StringWriter sw = new StringWriter((int)storage.getContentLength());
                ContentUtils.copyStreams(reader, storage.getContentLength(), sw, monitor);
                this.storage = new StringContentStorage(sw.toString());
            }
        }
        catch (IOException e) {
            throw new DBCException("IO error while reading content", e);
        }
        return true;
    }

    @Override
    public String getRawValue() {
        return storage.getCachedValue();
    }

    @Override
    public boolean isNull() {
        return storage.getCachedValue() == null;
    }

    @Override
    public void release() {

    }
}
