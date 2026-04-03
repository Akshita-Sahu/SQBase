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
package org.jkiss.sqbase.registry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.DBException;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.ModelPreferences;
import org.jkiss.sqbase.model.DBConfigurationController;
import org.jkiss.sqbase.model.DBFileController;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.app.*;
import org.jkiss.sqbase.model.connection.DBPDataSourceProviderRegistry;
import org.jkiss.sqbase.model.data.DBDRegistry;
import org.jkiss.sqbase.model.edit.DBERegistry;
import org.jkiss.sqbase.model.fs.DBFRegistry;
import org.jkiss.sqbase.model.impl.preferences.AbstractPreferenceStore;
import org.jkiss.sqbase.model.navigator.DBNModel;
import org.jkiss.sqbase.model.net.DBWHandlerRegistry;
import org.jkiss.sqbase.model.preferences.DBPPreferenceStore;
import org.jkiss.sqbase.model.runtime.DBRProgressMonitor;
import org.jkiss.sqbase.model.runtime.OSDescriptor;
import org.jkiss.sqbase.model.sql.SQLDialectMetadataRegistry;
import org.jkiss.sqbase.model.task.DBTTaskController;
import org.jkiss.sqbase.registry.datatype.DataTypeProviderRegistry;
import org.jkiss.sqbase.registry.formatter.DataFormatterRegistry;
import org.jkiss.sqbase.registry.fs.FileSystemProviderRegistry;
import org.jkiss.sqbase.registry.language.PlatformLanguageRegistry;
import org.jkiss.sqbase.registry.network.NetworkHandlerRegistry;
import org.jkiss.sqbase.registry.settings.GlobalSettings;
import org.jkiss.sqbase.runtime.IPluginService;
import org.jkiss.sqbase.runtime.jobs.DataSourceMonitorJob;
import org.jkiss.sqbase.utils.*;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.StandardConstants;
import org.osgi.framework.Bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * BaseWorkspaceImpl.
 * Base implementation of SQBase platform for all products
 */
public abstract class BasePlatformImpl implements DBPPlatform, DBPApplicationConfigurator, DBPPlatformLanguageManager {

    private static final Log log = Log.getLog(BasePlatformImpl.class);

    public static final String DBEAVER_DATA_DIR = "SQBaseData";

    private static final String APP_CONFIG_FILE = "sqbase.ini";
    private static final String ECLIPSE_CONFIG_FILE = "eclipse.ini";
    private static final String TEMP_PROJECT_NAME = ".sqbase-temp"; //$NON-NLS-1$

    public static final String CONFIG_FOLDER = ".config";
    public static final String FILES_FOLDER = ".files";

    private static final String DBEAVER_PROP_LANGUAGE = "nl";

    protected OSDescriptor localSystem;

    private DBNModel navigatorModel;
    private final List<IPluginService> activatedServices = new ArrayList<>();
    private DBFileController localFileController;
    private DBTTaskController localTaskController;
    
    private DBConfigurationController defaultConfigurationController;
    private final Map<Bundle, DBConfigurationController> configurationControllerByPlugin = new HashMap<>();

    private SQLDialectMetadataRegistry sqlDialectRegistry;

    private DBPPlatformLanguage platformLanguage;

    protected Path tempFolder;

    protected void initialize() {
        log.debug("Initialize base platform...");

        DBPPreferenceStore prefStore = getPreferenceStore();
        // Global pref events forwarder
        prefStore.addPropertyChangeListener(event -> {
            // Forward event to all data source preferences
            for (DBPDataSourceContainer ds : DataSourceRegistry.getAllDataSources()) {
                ((AbstractPreferenceStore)ds.getPreferenceStore()).firePropertyChangeEvent(prefStore, event.getProperty(), event.getOldValue(), event.getNewValue());
            }
        });

        {
            this.platformLanguage = PlatformLanguageRegistry.getInstance().getLanguage(Locale.getDefault());
            if (this.platformLanguage == null) {
                log.debug("Language for locale '" + Locale.getDefault() + "' not found. Use default.");
                this.platformLanguage = PlatformLanguageRegistry.getInstance().getLanguage(Locale.ENGLISH);
            }
        }

        // Navigator model
        this.navigatorModel = createNavigatorModel();
        this.navigatorModel.setModelAuthContext(getWorkspace().getAuthContext());
        this.navigatorModel.initialize();

        if (!getApplication().isExclusiveMode()) {
            // Activate plugin services
            activatePluginServices();

            if (!getApplication().isMultiuser()) {
                // Connections monitoring job
                new DataSourceMonitorJob(this).scheduleMonitor();
            }
        }
    }

    protected DBNModel createNavigatorModel() {
        return new DBNModel(this, null);
    }

    protected void activatePluginServices() {
        for (IPluginService pluginService : PluginServiceRegistry.getInstance().getServices()) {
            try {
                pluginService.activateService();
                activatedServices.add(pluginService);
            } catch (Throwable e) {
                log.error("Error activating plugin service", e);
            }
        }
    }

    public synchronized void dispose() {
        // Deactivate plugin services
        for (IPluginService pluginService : activatedServices) {
            try {
                pluginService.deactivateService();
            } catch (Exception e) {
                log.error("Error deactivating plugin service", e);
            }
        }
        activatedServices.clear();

        // Remove temp folder
        if (tempFolder != null) {
            if (!ContentUtils.deleteFileRecursive(tempFolder)) {
                log.warn("Can not delete temp folder '" + tempFolder + "'");
            }
            tempFolder = null;
        }
        // Dispose navigator model first
        // It is a part of UI
        disposeNavigatorModel();
    }

    public void disposeNavigatorModel() {
        if (this.navigatorModel != null && this.navigatorModel.getRoot() != null) {
            log.debug("Dispose navigator model");
            this.navigatorModel.dispose();
            //this.navigatorModel = null;
        }
    }

    @NotNull
    @Override
    public DBDRegistry getValueHandlerRegistry() {
        return DataTypeProviderRegistry.getInstance();
    }

    @NotNull
    @Override
    public DBERegistry getEditorsRegistry() {
        return ObjectManagerRegistry.getInstance();
    }

    @NotNull
    @Override
    public DBFRegistry getFileSystemRegistry() {
        return FileSystemProviderRegistry.getInstance();
    }

    @NotNull
    @Override
    public SQLDialectMetadataRegistry getSQLDialectRegistry() {
        if (sqlDialectRegistry == null) {
            BundleServiceRef<SQLDialectMetadataRegistry> registryRef = RuntimeUtils.getBundleService(
                SQLDialectMetadataRegistry.class,
                true
            );
            sqlDialectRegistry = registryRef.service();
            if (sqlDialectRegistry == null) {
                throw new IllegalStateException("Cannot determine SQL dialect registry for " + getClass());
            }
            registryRef.initializeService();
        }
        return sqlDialectRegistry;
    }

    @NotNull
    @Override
    public DBWHandlerRegistry getNetworkHandlerRegistry() {
        return NetworkHandlerRegistry.getInstance();
    }

    @NotNull
    @Override
    public DBConfigurationController getConfigurationController() {
        return getPluginConfigurationController(null);
    }
    
    @NotNull
    @Override
    public DBConfigurationController getProductConfigurationController() {
        return getConfigurationController(getProductPlugin().getBundle());
    }
    
    @NotNull
    @Override
    public DBConfigurationController getPluginConfigurationController(@Nullable String pluginId) {
        return getConfigurationController(CommonUtils.isEmpty(pluginId) ? null : Platform.getBundle(pluginId));
    }
    
    private DBConfigurationController getConfigurationController(@Nullable Bundle bundle) {
        DBConfigurationController controller = bundle == null ? defaultConfigurationController : configurationControllerByPlugin.get(bundle);
        if (controller == null) {
            controller = createConfigurationController(bundle);
            if (bundle == null) {
                defaultConfigurationController = controller;
            } else {
                configurationControllerByPlugin.put(bundle, controller);
            }
        }
        return controller;
    }

    @NotNull
    @Override
    public DBConfigurationController createConfigurationController(@Nullable String pluginId) {
        return createConfigurationController(pluginId == null ? null : Platform.getBundle(pluginId));
    }

    @NotNull
    private DBConfigurationController createConfigurationController(@Nullable Bundle bundle) {
        DBPApplication application = getApplication();
        if (application instanceof DBPApplicationConfigurator) {
            String pluginBundleName = bundle == null ? null : bundle.getSymbolicName();
            return ((DBPApplicationConfigurator) application).createConfigurationController(pluginBundleName);
        } else if (bundle == null) {
            LocalConfigurationController controller = new LocalConfigurationController(
                getLocalWorkspaceConfigFolder()
            );
            Plugin productPlugin = getProductPlugin();
            if (productPlugin != null) {
                Path pluginStateLocation = RuntimeUtils.getPluginStateLocation(productPlugin);
                if (Files.exists(pluginStateLocation)) {
                    controller.setLegacyConfigFolder(pluginStateLocation);
                }
            }
            return controller;
        } else {
            return new LocalConfigurationController(
                Platform.getStateLocation(bundle).toFile().toPath()
            );
        }
    }

    private @NotNull Path getLocalWorkspaceConfigFolder() {
        return getWorkspace().getMetadataFolder().resolve(CONFIG_FOLDER);
    }

    @NotNull
    @Override
    public DBFileController getFileController() {
        if (localFileController == null) {
            localFileController = createFileController();
        }
        return localFileController;
    }

    @Override
    @NotNull
    public DBFileController createFileController() {
        DBPApplication application = getApplication();
        if (application instanceof DBPApplicationConfigurator) {
            return ((DBPApplicationConfigurator) application).createFileController();
        }

        return new LocalFileController(
            getWorkspace().getMetadataFolder().resolve(FILES_FOLDER)
        );
    }

    @NotNull
    @Override
    public Path getLocalConfigurationFile(String fileName) {
        Path productPluginPath = RuntimeUtils.getPluginStateLocation(getProductPlugin()).resolve(fileName);
        if (Files.exists(productPluginPath)) {
            return productPluginPath;
        }
        return getLocalWorkspaceConfigFolder().resolve(fileName);
    }

    @NotNull
    @Override
    public DBTTaskController getTaskController() {
        if (localTaskController == null) {
            localTaskController = createTaskController();
        }
        return localTaskController;
    }

    @NotNull
    @Override
    public DBTTaskController createTaskController() {
        DBPApplication application = getApplication();
        if (application instanceof DBPApplicationConfigurator) {
            return ((DBPApplicationConfigurator) application).createTaskController();
        } else {
            return new LocalTaskController();
        }
    }

    protected abstract Plugin getProductPlugin();
    
    @NotNull
    @Override
    public Path getApplicationConfiguration() {
        Path configPath;
        try {
            configPath = RuntimeUtils.getLocalPathFromURL(Platform.getInstallLocation().getURL());
        } catch (IOException e) {
            throw new IllegalStateException("Can't detect application installation folder.", e);
        }
        Path iniFile = configPath.resolve(ECLIPSE_CONFIG_FILE);
        if (!Files.exists(iniFile)) {
            iniFile = configPath.resolve(APP_CONFIG_FILE);
        }
        return iniFile;
    }

    @NotNull
    @Override
    public DBPDataFormatterRegistry getDataFormatterRegistry() {
        return DataFormatterRegistry.getInstance();
    }

    @NotNull
    @Override
    public OSDescriptor getLocalSystem() {
        if (this.localSystem == null) {
            this.localSystem = new OSDescriptor(Platform.getOS(), Platform.getOSArch());
        }
        return this.localSystem;
    }

    @NotNull
    @Override
    public DBNModel getNavigatorModel() {
        return navigatorModel;
    }

    @NotNull
    @Override
    public DBPDataSourceProviderRegistry getDataSourceProviderRegistry() {
        return DataSourceProviderRegistry.getInstance();
    }

    @NotNull
    @Override
    public DBPPlatformLanguage getPlatformLanguage() {
        return platformLanguage;
    }

    @Override
    public void setPlatformLanguage(@NotNull DBPPlatformLanguage language) throws DBException {
        if (CommonUtils.equalObjects(language, this.platformLanguage)) {
            return;
        }

        GlobalSettings.getInstance().setGlobalProperty(DBEAVER_PROP_LANGUAGE, language.getCode());
        this.platformLanguage = language;
        // This property is fake. But we set it to trigger property change listener
        // which will ask to restart workbench.
        getPreferenceStore().setValue(ModelPreferences.PLATFORM_LANGUAGE, language.getCode());
    }

    @NotNull
    public Path getTempFolder(@NotNull DBRProgressMonitor monitor, @NotNull String name) {
        if (tempFolder == null) {
            // Make temp folder
            try {
                String tempFolderPath = System.getProperty("sqbase.io.tmpdir");
                if (!CommonUtils.isEmpty(tempFolderPath)) {
                    tempFolderPath = GeneralUtils.replaceVariables(tempFolderPath, new SystemVariablesResolver());

                    File dbTempFolder = new File(tempFolderPath);
                    if (!dbTempFolder.mkdirs()) {
                        throw new IOException("Can't create temp directory '" + dbTempFolder.getAbsolutePath() + "'");
                    }
                } else {
                    tempFolderPath = System.getProperty(StandardConstants.ENV_TMP_DIR);
                }
                monitor.subTask("Create temp folder '" + tempFolderPath + "'");
                Path tmpFolder = Paths.get(tempFolderPath);
                if (!Files.exists(tmpFolder)) {
                    log.debug("Create global temp folder '" + tmpFolder + "'");
                    Files.createDirectories(tmpFolder);
                }
                tempFolder = Files.createTempDirectory(tmpFolder, TEMP_PROJECT_NAME);
            } catch (IOException e) {
                final String sysTempFolder = System.getProperty(StandardConstants.ENV_TMP_DIR);
                if (!CommonUtils.isEmpty(sysTempFolder)) {
                    tempFolder = Path.of(sysTempFolder).resolve(TEMP_PROJECT_NAME);
                    if (!Files.exists(tempFolder)) {
                        try {
                            Files.createDirectories(tempFolder);
                        } catch (IOException ex) {
                            final String sysUserFolder = System.getProperty(StandardConstants.ENV_USER_HOME);
                            if (!CommonUtils.isEmpty(sysUserFolder)) {
                                tempFolder = Path.of(sysUserFolder).resolve(TEMP_PROJECT_NAME);
                                if (!Files.exists(tempFolder)) {
                                    try {
                                        Files.createDirectories(tempFolder);
                                    } catch (IOException exc) {
                                        tempFolder = Path.of(TEMP_PROJECT_NAME);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Path localTemp = name == null ? tempFolder : tempFolder.resolve(name);
        if (!Files.exists(localTemp)) {
            try {
                Files.createDirectories(localTemp);
            } catch (IOException e) {
                log.error("Can't create temp directory " + localTemp, e);
            }
        }
        return localTemp;
    }

}
