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
package org.jkiss.sqbase.ui.navigator;

/**
 * NavigatorCommands
 */
public class NavigatorCommands {

    public static final String GROUP_TOOLS = "tools";
    public static final String GROUP_TOOLS_END = "tools_end";
    public static final String GROUP_NAVIGATOR_ADDITIONS = "navigator_additions";
    public static final String GROUP_NAVIGATOR_ADDITIONS_END = "navigator_additions_end";

    public static final String CMD_OBJECT_OPEN = "org.jkiss.sqbase.core.object.open"; //$NON-NLS-1$
    public static final String CMD_OBJECT_CREATE = "org.jkiss.sqbase.core.object.create"; //$NON-NLS-1$
    public static final String CMD_OBJECT_DELETE = "org.jkiss.sqbase.core.object.delete"; //$NON-NLS-1$
    public static final String CMD_OBJECT_MOVE_TOP = "org.jkiss.sqbase.core.object.move.top"; //$NON-NLS-1$
    public static final String CMD_OBJECT_MOVE_BOTTOM = "org.jkiss.sqbase.core.object.move.bottom"; //$NON-NLS-1$
    public static final String CMD_OBJECT_MOVE_UP = "org.jkiss.sqbase.core.object.move.up"; //$NON-NLS-1$
    public static final String CMD_OBJECT_MOVE_DOWN = "org.jkiss.sqbase.core.object.move.down"; //$NON-NLS-1$
    public static final String CMD_OBJECT_SET_DEFAULT = "org.jkiss.sqbase.core.navigator.set.default";
    public static final String CMD_CREATE_LOCAL_FOLDER = "org.jkiss.sqbase.core.new.folder";
    public static final String CMD_CREATE_RESOURCE_FILE = "org.jkiss.sqbase.core.resource.create.file";
    public static final String CMD_CREATE_RESOURCE_FOLDER = "org.jkiss.sqbase.core.resource.create.folder";
    public static final String CMD_CREATE_FILE_LINK = "org.jkiss.sqbase.core.resource.link.file";
    public static final String CMD_CREATE_FOLDER_LINK = "org.jkiss.sqbase.core.resource.link.folder";
    public static final String CMD_CREATE_PROJECT = "org.jkiss.sqbase.core.project.create";

    public static final String CMD_FILTER_CONNECTED = "org.jkiss.sqbase.navigator.filter.connected";
    public static final String CMD_FILTER_OBJECT_TYPE = "org.jkiss.sqbase.navigator.filter.object.type";

    public static final String PARAM_OBJECT_TYPE = "org.jkiss.sqbase.core.object.type";
    public static final String PARAM_OBJECT_TYPE_NAME = "org.jkiss.sqbase.core.object.typeName";
    public static final String PARAM_OBJECT_TYPE_ICON = "org.jkiss.sqbase.core.object.typeIcon";
    public static final String PARAM_OBJECT_TYPE_FOLDER = "org.jkiss.sqbase.core.object.folder";

}
