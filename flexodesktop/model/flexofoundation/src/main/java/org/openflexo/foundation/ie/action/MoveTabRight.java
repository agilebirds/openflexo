/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.ie.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ie.widget.IETabContainerWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;

public class MoveTabRight extends FlexoUndoableAction {

	private IETabWidget selectedTab;

	public static FlexoActionType actionType = new FlexoActionType("move_tab_right", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new MoveTabRight(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return object != null && object instanceof IETabWidget && ((IETabWidget) object).getRootParent().getTabCount() > 1;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, IETabContainerWidget.class);
		FlexoModelObject.addActionForClass(actionType, IETabWidget.class);
	}

	MoveTabRight(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (selectedTab != null) {
			selectedTab.moveRight();
		}

	}

	@Override
	protected void undoAction(Object context) {
		if (selectedTab != null) {
			selectedTab.moveLeft();
		}
	}

	@Override
	protected void redoAction(Object context) {
		if (selectedTab != null) {
			selectedTab.moveRight();
		}
	}

	public IETabWidget getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(IETabWidget selected_tab) {
		this.selectedTab = selected_tab;
	}

}
