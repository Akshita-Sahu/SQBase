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

package org.jkiss.sqbase.ui.controls.resultset;

/**
 * Result set command constants
 */
public interface IResultSetCommands {

    String CMD_TOGGLE_PANELS = "org.jkiss.sqbase.core.resultset.grid.togglePreview";
    String CMD_ACTIVATE_PANELS = "org.jkiss.sqbase.core.resultset.grid.activatePreview";
    String CMD_TOGGLE_MAXIMIZE = "org.jkiss.sqbase.core.resultset.grid.togglePanelMaximize";
    String CMD_TOGGLE_LAYOUT = "org.jkiss.sqbase.core.resultset.grid.toggleLayout";
    String CMD_TOGGLE_MODE = "org.jkiss.sqbase.core.resultset.toggleMode";
    String CMD_FOCUS_FILTER = "org.jkiss.sqbase.core.resultset.focus.filter";
    String CMD_SWITCH_PRESENTATION = "org.jkiss.sqbase.core.resultset.switchPresentation";
    String CMD_ROW_FIRST = "org.jkiss.sqbase.core.resultset.row.first";
    String CMD_ROW_PREVIOUS = "org.jkiss.sqbase.core.resultset.row.previous";
    String CMD_ROW_NEXT = "org.jkiss.sqbase.core.resultset.row.next";
    String CMD_ROW_LAST = "org.jkiss.sqbase.core.resultset.row.last";
    String CMD_FETCH_PAGE = "org.jkiss.sqbase.core.resultset.fetch.page";
    String CMD_FETCH_ALL = "org.jkiss.sqbase.core.resultset.fetch.all";
    String CMD_COUNT = "org.jkiss.sqbase.core.resultset.count";
    String CMD_ROW_EDIT = "org.jkiss.sqbase.core.resultset.row.edit";
    String CMD_ROW_EDIT_INLINE = "org.jkiss.sqbase.core.resultset.row.edit.inline";
    String CMD_ROW_ADD = "org.jkiss.sqbase.core.resultset.row.add";
    String CMD_ROW_COPY = "org.jkiss.sqbase.core.resultset.row.copy";
    String CMD_ROW_COPY_FROM_ABOVE = "org.jkiss.sqbase.core.resultset.row.copy.from.above";
    String CMD_ROW_COPY_FROM_BELOW = "org.jkiss.sqbase.core.resultset.row.copy.from.below";
    String CMD_ROW_DELETE = "org.jkiss.sqbase.core.resultset.row.delete";
    String CMD_CELL_SET_NULL = "org.jkiss.sqbase.core.resultset.cell.setNull";
    String CMD_CELL_SET_DEFAULT = "org.jkiss.sqbase.core.resultset.cell.setDefault";
    String CMD_CELL_RESET = "org.jkiss.sqbase.core.resultset.cell.reset";
    String CMD_APPLY_CHANGES = "org.jkiss.sqbase.core.resultset.applyChanges";
    String CMD_APPLY_AND_COMMIT_CHANGES = "org.jkiss.sqbase.core.resultset.applyAndCommitChanges";
    String CMD_REJECT_CHANGES = "org.jkiss.sqbase.core.resultset.rejectChanges";
    String CMD_GENERATE_SCRIPT = "org.jkiss.sqbase.core.resultset.generateScript";
    String CMD_TOGGLE_CONFIRM_SAVE = "org.jkiss.sqbase.core.resultset.toggleConfirmSave";
    String CMD_NAVIGATE_LINK = "org.jkiss.sqbase.core.resultset.navigateLink";
    String CMD_FILTER_MENU = "org.jkiss.sqbase.core.resultset.filterMenu";
    String CMD_FILTER_MENU_DISTINCT = "org.jkiss.sqbase.core.resultset.filterMenu.distinct";
    String CMD_FILTER_EDIT_SETTINGS = "org.jkiss.sqbase.core.resultset.filterSettings";
    String CMD_FILTER_SAVE_SETTING = "org.jkiss.sqbase.core.resultset.filterSave";
    String CMD_FILTER_CLEAR_SETTING = "org.jkiss.sqbase.core.resultset.filterClear";
    String CMD_REFERENCES_MENU = "org.jkiss.sqbase.core.resultset.referencesMenu";
    String CMD_COPY_COLUMN_NAMES = "org.jkiss.sqbase.core.resultset.grid.copyColumnNames";
    String CMD_COPY_ROW_NAMES = "org.jkiss.sqbase.core.resultset.grid.copyRowNames";
    String CMD_EXPORT = "org.jkiss.sqbase.core.resultset.export";
    String CMD_ZOOM_IN = "org.jkiss.sqbase.core.resultset.zoomIn";
    String CMD_ZOOM_OUT = "org.jkiss.sqbase.core.resultset.zoomOut";
    String CMD_TOGGLE_ORDER = "org.jkiss.sqbase.core.resultset.toggleOrder";
    String CMD_SELECT_ROW_COLOR = "org.jkiss.sqbase.core.resultset.grid.selectRowColor";
    String CMD_GO_TO_COLUMN = "org.jkiss.sqbase.core.resultset.grid.gotoColumn";
    String CMD_GO_TO_ROW = "org.jkiss.sqbase.core.resultset.grid.gotoRow";
    String PARAM_EXPORT_WITH_PARAM = "exportWithParameter";
}
