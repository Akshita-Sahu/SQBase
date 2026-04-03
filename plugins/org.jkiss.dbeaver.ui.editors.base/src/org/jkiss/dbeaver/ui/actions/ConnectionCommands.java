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
package org.jkiss.sqbase.ui.actions;

/**
 * Connection commands
 */
public interface ConnectionCommands {

    String CMD_CONNECT = "org.jkiss.sqbase.core.connect";
    String CMD_DISCONNECT = "org.jkiss.sqbase.core.disconnect";
    String CMD_DISCONNECT_ALL = "org.jkiss.sqbase.core.disconnectAll";
    String CMD_DISCONNECT_OTHER = "org.jkiss.sqbase.core.disconnectOther";
    String CMD_INVALIDATE = "org.jkiss.sqbase.core.invalidate";
    String CMD_READONLY = "org.jkiss.sqbase.core.connection.readonly";
    String CMD_COMMIT = "org.jkiss.sqbase.core.commit";
    String CMD_ROLLBACK = "org.jkiss.sqbase.core.rollback";
    String CMD_TOGGLE_AUTOCOMMIT = "org.jkiss.sqbase.core.txn.autocommit"; //$NON-NLS-1$
    String CMD_SELECT_CONNECTION = "org.jkiss.sqbase.ui.tools.select.connection";
    String CMD_SELECT_SCHEMA = "org.jkiss.sqbase.ui.tools.select.schema";

}
