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
package org.openflexo.foundation.view.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewDefinition.DuplicateShemaNameException;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.ViewLibraryObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class AddView extends FlexoAction<AddView, ViewLibraryObject, ViewLibraryObject> {

	private static final Logger logger = Logger.getLogger(AddView.class.getPackage().getName());

	public static FlexoActionType<AddView, ViewLibraryObject, ViewLibraryObject> actionType = new FlexoActionType<AddView, ViewLibraryObject, ViewLibraryObject>(
			"create_view", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddView makeNewAction(ViewLibraryObject focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
			return new AddView(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewLibraryObject object, Vector<ViewLibraryObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ViewLibraryObject object, Vector<ViewLibraryObject> globalSelection) {
			return (object instanceof ViewFolder || object instanceof ViewDefinition || object instanceof ViewLibrary);
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddView.actionType, ViewLibrary.class);
		FlexoModelObject.addActionForClass(AddView.actionType, ViewFolder.class);
		FlexoModelObject.addActionForClass(AddView.actionType, ViewDefinition.class);
	}

	private ViewDefinition _newShema;

	private ViewFolder _folder;

	public boolean useViewPoint = true;
	public String newViewName;
	public ViewPoint viewpoint;

	public boolean skipChoosePopup = false;

	AddView(ViewLibraryObject focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException,
			DuplicateShemaNameException {
		logger.info("Add shema");

		if (getFolder() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (StringUtils.isEmpty(newViewName)) {
			throw new InvalidParameterException("shema name is undefined");
		}
		if (getProject().getShemaLibrary().getShemaNamed(newViewName) != null) {
			throw new DuplicateShemaNameException(newViewName);
		}

		_newShema = new ViewDefinition(newViewName, getFolder().getShemaLibrary(), getFolder(), getProject(), true);
		if (useViewPoint) {
			_newShema.setCalc(viewpoint);
		}
		logger.info("Added view " + _newShema + " for project " + _newShema.getProject());
		// Creates the resource here
		_newShema.getShemaResource();
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public ViewDefinition getNewDiagram() {
		return _newShema;
	}

	public ViewLibrary getShemaLibrary() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getShemaLibrary();
		}
		return null;
	}

	public ViewFolder getFolder() {
		if (_folder == null) {
			if ((getFocusedObject() != null) && (getFocusedObject() instanceof ViewDefinition)) {
				_folder = ((ViewDefinition) getFocusedObject()).getFolder();
			} else if ((getFocusedObject() != null) && (getFocusedObject() instanceof ViewFolder)) {
				_folder = (ViewFolder) getFocusedObject();
			} else if ((getFocusedObject() != null) && (getFocusedObject() instanceof ViewLibrary)) {
				_folder = ((ViewLibrary) getFocusedObject()).getRootFolder();
			}

		}
		return _folder;
	}

	public void setFolder(ViewFolder folder) {
		_folder = folder;
	}

	public String errorMessage;

	public boolean isValid() {
		if (getFolder() == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_folder_defined");
			return false;
		} else if (viewpoint == null && useViewPoint) {
			errorMessage = FlexoLocalization.localizedForKey("no_view_point_selected");
			return false;
		}
		if (StringUtils.isEmpty(newViewName)) {
			errorMessage = FlexoLocalization.localizedForKey("no_view_name_defined");
			return false;
		}
		if (getProject().getShemaLibrary().getShemaNamed(newViewName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_view_with_that_name_already_exists");
			return false;
		}
		return true;
	}
}