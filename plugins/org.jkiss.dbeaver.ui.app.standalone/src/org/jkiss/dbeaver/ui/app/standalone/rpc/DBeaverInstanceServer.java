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

package org.jkiss.sqbase.ui.app.standalone.rpc;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.DBPDataSourceContainer;
import org.jkiss.sqbase.model.app.DBPPlatformDesktop;
import org.jkiss.sqbase.model.app.DBPProject;
import org.jkiss.sqbase.model.cli.ApplicationInstanceServer;
import org.jkiss.sqbase.model.cli.CLIProcessResult;
import org.jkiss.sqbase.model.cli.InstanceServerProperties;
import org.jkiss.sqbase.runtime.DBWorkbench;
import org.jkiss.sqbase.ui.ActionUtils;
import org.jkiss.sqbase.ui.UIUtils;
import org.jkiss.sqbase.ui.actions.datasource.DataSourceHandler;
import org.jkiss.sqbase.ui.app.standalone.SQBaseApplication;
import org.jkiss.sqbase.ui.app.standalone.SQBaseCommandLine;
import org.jkiss.sqbase.ui.editors.EditorUtils;
import org.jkiss.sqbase.ui.editors.sql.handlers.SQLEditorHandlerOpenEditor;
import org.jkiss.sqbase.ui.editors.sql.handlers.SQLNavigatorContext;
import org.jkiss.sqbase.utils.DataSourceUtils;
import org.jkiss.sqbase.utils.GeneralUtils;
import org.jkiss.sqbase.utils.SystemVariablesResolver;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.HttpConstants;
import org.jkiss.utils.rest.RestClient;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * SQBase instance controller.
 */
public class SQBaseInstanceServer extends ApplicationInstanceServer<IInstanceController> implements IInstanceController {

    private static final Log log = Log.getLog(SQBaseInstanceServer.class);
    private DBPDataSourceContainer dataSourceContainer = null;

    private final List<File> filesToConnect = new ArrayList<>();

    private SQBaseInstanceServer() throws IOException {
        super(IInstanceController.class);
    }

    @NotNull
    public static SQBaseInstanceServer createServer() throws IOException {
        return new SQBaseInstanceServer();
    }

    @Nullable
    public static IInstanceController createClient() {
        return createClient(null);
    }

    @Nullable
    public static IInstanceController createClient(@Nullable Path workspacePath) {
        final Path path = getConfigPath(workspacePath);

        InstanceServerProperties serverProperties = deserializeProperties(path);
        if (serverProperties == null) {
            return null;
        }

        final IInstanceController instance = RestClient
            .builder(URI.create("http://localhost:" + serverProperties.port()), IInstanceController.class)
            .setSslContext(initCustomSslContext())
            .setHeaders(Map.of(HttpConstants.HEADER_AUTHORIZATION, HttpConstants.BEARER_PREFIX + serverProperties.password()))
            .create();

        try {
            final long payload = System.currentTimeMillis();
            final long response = instance.ping(payload);

            if (response != payload) {
                throw new IllegalStateException("Invalid ping response: " + response + ", was expecting " + payload);
            }
        } catch (Throwable e) {
            log.debug("Error accessing instance server: " + e.getMessage());
            return null;
        }

        return instance;
    }

    @Nullable
    private static InstanceServerProperties deserializeProperties(@NotNull Path path) {
        if (Files.notExists(path)) {
            log.trace("No instance controller is available");
            return null;
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        } catch (IOException e) {
            log.error("Error reading instance controller configuration: " + e.getMessage());
            return null;
        }

        long pid = ProcessHandle.current().pid();
        String port = properties.getProperty(InstanceServerProperties.portKey(pid));
        String password = properties.getProperty(InstanceServerProperties.passwordKey(pid));
        String startedAt = properties.getProperty(InstanceServerProperties.startedAtKey(pid), "0");

        if (CommonUtils.isEmptyTrimmed(port)) {
            log.error("No port specified for the instance controller to connect to");
            return null;
        }
        if (CommonUtils.isEmptyTrimmed(password)) {
            log.error("No password specified for the instance controller to connect to");
            return null;
        }

        try {
            return new InstanceServerProperties(Integer.parseInt(port), password, Long.parseLong(startedAt));
        } catch (NumberFormatException e) {
            log.error("Invalid instance controller configuration: " + e.getMessage());
            return null;
        }
    }

    /**
     * init custom ssl context to avoid default trust store initialization before an application starts
     */
    @Nullable
    private static SSLContext initCustomSslContext() {
        try {
            var factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(KeyStore.getInstance(KeyStore.getDefaultType()));
            var ssl = SSLContext.getInstance("TLS");
            ssl.init(null, factory.getTrustManagers(), null);
            return ssl;
        } catch (Exception e) {
            log.error("Error init custom ssl context: " + e.getMessage(), e);
            return null;
        }
    }

    @NotNull
    @Override
    public CLIProcessResult handleCommandLine(@NotNull String[] args) {
        try {
            return SQBaseCommandLine.getInstance().executeCommandLineCommands(
                this,
                !SQBaseApplication.getInstance().isHeadlessMode(),
                true,
                args
            );
        } catch (Exception e) {
            return new CLIProcessResult(CLIProcessResult.PostAction.ERROR, "Error executing command: " + e.getMessage());
        }
    }

    @Override
    public long ping(long payload) {
        return payload;
    }

    @Override
    public void openExternalFiles(@NotNull String[] fileNames) {
        UIUtils.asyncExec(() -> {
            List<Path> paths = EditorUtils.openExternalFiles(fileNames, dataSourceContainer);
            filesToConnect.addAll(paths.stream().map(Path::toFile).toList());
        });
    }

    @Override
    public void openDatabaseConnection(@NotNull String connectionSpec) {
        // Do not log it (#3788)
        //log.debug("Open external database connection [" + connectionSpec + "]");
        InstanceConnectionParameters instanceConParameters = new InstanceConnectionParameters();
        DBPProject activeProject = DBWorkbench.getPlatform().getWorkspace().getActiveProject();
        if (activeProject == null) {
            log.error("No active project in workspace");
            return;
        }
        dataSourceContainer = DataSourceUtils.getDataSourceBySpec(
            activeProject,
            GeneralUtils.replaceVariables(connectionSpec, SystemVariablesResolver.INSTANCE),
            instanceConParameters,
            false,
            instanceConParameters.isCreateNewConnection()
        );
        if (dataSourceContainer == null) {
            filesToConnect.clear();
            return;
        }
        if (!CommonUtils.isEmpty(filesToConnect)) {
            for (File file : filesToConnect) {
                EditorUtils.setFileDataSource(file, new SQLNavigatorContext(dataSourceContainer));
            }
        }
        if (instanceConParameters.isOpenConsole()) {
            final IWorkbenchWindow workbenchWindow = UIUtils.getActiveWorkbenchWindow();
            UIUtils.syncExec(() -> {
                SQLEditorHandlerOpenEditor.openSQLConsole(workbenchWindow, new SQLNavigatorContext(dataSourceContainer), dataSourceContainer.getName(), "");
                workbenchWindow.getShell().forceActive();

            });
        } else if (instanceConParameters.isMakeConnect()) {
            DataSourceHandler.connectToDataSource(null, dataSourceContainer, null);
        }
        filesToConnect.clear();
    }

    @Override
    public void quit() {
        log.info("Program termination requested");

        new Job("Terminate application") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                System.exit(-1);
                return Status.OK_STATUS;
            }
        }.schedule(1000);
    }

    @Override
    public void closeAllEditors() {
        log.debug("Close all open editor tabs");

        UIUtils.syncExec(() -> {
            IWorkbenchWindow window = UIUtils.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                page.closeAllEditors(false);
            }
        });
    }

    @Override
    public void executeWorkbenchCommand(@NotNull String commandId) {
        log.debug("Execute workbench command " + commandId);
        ActionUtils.runCommand(commandId, UIUtils.getActiveWorkbenchWindow());
    }

    @Override
    public void fireGlobalEvent(@NotNull String eventId, @NotNull Map<String, Object> properties) {
        DBPPlatformDesktop.getInstance().getGlobalEventManager().fireGlobalEvent(eventId, properties);
    }

    @Override
    public void bringToFront() {
        UIUtils.syncExec(() -> {
            final Shell shell = UIUtils.getActiveShell();
            if (shell != null) {
                if (!shell.getMinimized()) {
                    shell.setMinimized(true);
                }
                shell.setMinimized(false);
                shell.setActive();
            }
        });
    }
}