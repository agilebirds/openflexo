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
package org.openflexo.foundation.ie.menu.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class AddMenu extends FlexoAction {

	private FlexoItemMenu _newMenu;

	protected static final Logger logger = FlexoLogger.getLogger(AddMenu.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("add_menu", FlexoActionType.newMenu, FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new AddMenu(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return object != null && object instanceof FlexoItemMenu;
		}

	};

	private String menuLabel;

	private FlexoItemMenu father;

	protected AddMenu(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (menuLabel == null) {
			return;
		}
		_newMenu = FlexoItemMenu.createNewMenu(father.getProject().getFlexoNavigationMenu(), father, menuLabel);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Added a new subMenuItem to folder " + father.getMenuLabel());
		}
	}

	public FlexoItemMenu getNewMenu() {
		return _newMenu;
	}

	public FlexoItemMenu getFather() {
		return father;
	}

	public void setFather(FlexoItemMenu father) {
		this.father = father;
	}

	public String getMenuLabel() {
		return menuLabel;
	}

	public void setMenuLabel(String menuLabel) {
		this.menuLabel = menuLabel;
	}

}
