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
package org.jkiss.sqbase.registry.rm;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.DBRuntimeException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.DBPDataSourceFolder;
import org.jkiss.sqbase.model.app.DBPProject;
import org.jkiss.sqbase.model.preferences.DBPPreferenceStore;
import org.jkiss.sqbase.model.rm.RMController;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.runtime.VoidProgressMonitor;
import org.jkiss.sqbase.registry.DataSourceConfigurationManagerBuffer;
import org.jkiss.sqbase.registry.DataSourceDescriptor;
import org.jkiss.sqbase.registry.DataSourceFolder;
import org.jkiss.sqbase.registry.DataSourceRegistry;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class DataSourceRegistryRM<T extends DataSourceDescriptor> extends DataSourceRegistry<T> {
    private static final Log log = Log.getLog(DataSourceRegistryRM.class);

    @NotNull
    private final RMController rmController;

    public DataSourceRegistryRM(
        @NotNull DBPProject project,
        @NotNull RMController rmController,
        @NotNull DBPPreferenceStore preferenceStore
    ) {
        super(project, new DataSourceConfigurationManagerRM(project, rmController), preferenceStore);
        this.rmController = rmController;
    }

    @Override
    protected void persistDataSourceCreate(@NotNull DBPDataSourceContainer container) {
        if (getProject().isInMemory()) {
            return;
        }
        DataSourceConfigurationManagerBuffer buffer = new DataSourceConfigurationManagerBuffer();
        saveConfigurationToManager(new VoidProgressMonitor(), buffer, dsc -> dsc.equals(container));

        try {
            rmController.createProjectDataSources(
                getRemoteProjectId(), new String(buffer.getData(), StandardCharsets.UTF_8), List.of(container.getId()));
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            log.error("Error persisting rm data source update", e);
        }
    }

    @Override
    protected void persistDataSourceUpdate(@NotNull DBPDataSourceContainer container) {
        if (getProject().isInMemory()) {
            return;
        }
        DataSourceConfigurationManagerBuffer buffer = new DataSourceConfigurationManagerBuffer();
        saveConfigurationToManager(new VoidProgressMonitor(), buffer, dsc -> dsc.equals(container));

        try {
            rmController.updateProjectDataSources(
                getRemoteProjectId(), new String(buffer.getData(), StandardCharsets.UTF_8), List.of(container.getId()));
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            log.error("Error persisting rm data source update", e);
        }
    }

    @Override
    protected void persistDataSourceDelete(@NotNull DBPDataSourceContainer container) {
        if (getProject().isInMemory()) {
            return;
        }
        try {
            rmController.deleteProjectDataSources(getRemoteProjectId(), new String[]{container.getId()});
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            log.error("Error persisting rm data source update", e);
        }
    }

    @Override
    protected void persistDataFolderDelete(@NotNull String folderPath, boolean dropContents) {
        if (getProject().isInMemory()) {
            return;
        }
        try {
            rmController.deleteProjectDataSourceFolders(getRemoteProjectId(), new String[]{folderPath}, dropContents);
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            log.error("Error persisting rm data folder delete", e);
        }
    }

    @NotNull
    @Override
    public DataSourceFolder addFolder(@Nullable DBPDataSourceFolder parent, @NotNull String name) {
        if (getProject().isInMemory()) {
            return createFolder(parent, name);
        }
        try {
            rmController.createProjectDataSourceFolder(getRemoteProjectId(), parent == null ? name : parent.getFolderPath() + "/" + name);
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            throw new DBRuntimeException("Error persisting rm data folder create", e);
        }
        return createFolder(parent, name);
    }


    @Override
    public void moveFolder(@NotNull String oldPath, @NotNull String newPath) throws DBException {
        if (getProject().isInMemory()) {
            super.moveFolder(oldPath, newPath);
            return;
        }
        try {
            rmController.moveProjectDataSourceFolder(getRemoteProjectId(), oldPath, newPath);
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            log.error("Error persisting rm data folder move", e);
            return;
        }
        super.moveFolder(oldPath, newPath);
    }

    @Override
    protected void saveDataSources(DBRProgressMonitor monitor) {
        if (getProject().isInMemory()) {
            return;
        }

        DataSourceConfigurationManagerBuffer buffer = new DataSourceConfigurationManagerBuffer();
        saveConfigurationToManager(monitor, buffer, null);

        try {
            rmController.updateProjectDataSources(
                getRemoteProjectId(), new String(buffer.getData(), StandardCharsets.UTF_8), List.of());
            lastError = null;
        } catch (DBException e) {
            lastError = e;
            log.error("Error saving data source configuration", e);
        }
    }

    @NotNull
    private String getRemoteProjectId() {
        return getProject().getId();
    }

}
