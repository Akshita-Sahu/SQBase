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
package org.jkiss.sqbase.ui.app.standalone;


import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.Log;
import org.jkiss.sqbase.model.cli.ApplicationCommandLine;
import org.jkiss.sqbase.model.cli.CLIContextImpl;
import org.jkiss.sqbase.model.cli.CLIRunMeta;
import org.jkiss.sqbase.model.cli.registry.CLICommandDescriptor;
import org.jkiss.sqbase.ui.app.standalone.cli.SQBaseMixin;
import org.jkiss.sqbase.ui.app.standalone.rpc.IInstanceController;
import picocli.CommandLine;

/**
 * Command line processing.
 * Note:
 * there are two modes of command line processing:
 * 1. On SQBase start. It tries to find already running SQBase instance (thru REST API) and make it execute passed commands
 *    If SQBase will execute at least one command using remote invocation then application won't start.
 *    Otherwise it will start normally (and then will try to process commands in UI)
 * 2. After SQBase UI start. It will execute commands directly
 */
public class SQBaseCommandLine extends ApplicationCommandLine<IInstanceController> {
    private static final Log log = Log.getLog(SQBaseCommandLine.class);

    private static SQBaseCommandLine INSTANCE = null;

    private SQBaseCommandLine() {
        super();
    }

    @Override
    protected SQBaseTopLevelCommand createTopLevelCommand(
        @Nullable IInstanceController applicationInstanceController,
        @NotNull CLIContextImpl context,
        @NotNull CLIRunMeta runMeta
    ) {
        return new SQBaseTopLevelCommand(applicationInstanceController, context, runMeta);
    }

    public synchronized static SQBaseCommandLine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SQBaseCommandLine();
        }
        return INSTANCE;
    }

    @Override
    protected void preprocessCommandLineParameter(
        @NotNull CLICommandDescriptor descriptor,
        @NotNull CommandLine.ParseResult cliCommand,
        @NotNull CLIContextImpl context,
        boolean uiActivated
    ) {
        super.preprocessCommandLineParameter(descriptor, cliCommand, context, uiActivated);
        if (!uiActivated && descriptor.isExclusiveMode()) {
            if (SQBaseApplication.instance != null) {
                SQBaseApplication.instance.setExclusiveMode(true);
            }
        }
    }

    @NotNull
    @Override
    protected CommandLine initCommandLine(
        @Nullable IInstanceController applicationInstanceController,
        @NotNull CLIContextImpl context,
        @NotNull CLIRunMeta runMeta
    ) {
        CommandLine cmd = super.initCommandLine(applicationInstanceController, context, runMeta);
        cmd.addMixin("sqbase", new SQBaseMixin());
        return cmd;
    }
}
