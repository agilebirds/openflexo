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
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class CreateView extends FlexoAction<CreateView, RepositoryFolder, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(CreateView.class.getPackage().getName());

	public static FlexoActionType<CreateView, RepositoryFolder, DiagramElement<?>> actionType = new FlexoActionType<CreateView, RepositoryFolder, DiagramElement<?>>(
			"create_view", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateView makeNewAction(RepositoryFolder focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
			return new CreateView(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder object, Vector<DiagramElement<?>> globalSelection) {
			return object.getResourceRepository() instanceof ViewLibrary;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder object, Vector<DiagramElement<?>> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateView.actionType, RepositoryFolder.class);
	}

	private View newView;

	public boolean useViewPoint = true;
	public String newViewName;
	public String newViewTitle;
	public ViewPointResource viewpointResource;

	public boolean skipChoosePopup = false;

	CreateView(RepositoryFolder focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws SaveResourceException, InvalidFileNameException {
		logger.info("Add view in folder " + getFolder());

		if (StringUtils.isNotEmpty(newViewTitle) && StringUtils.isEmpty(newViewName)) {
			newViewName = JavaUtils.getClassName(newViewTitle);
		}

		if (StringUtils.isNotEmpty(newViewName) && StringUtils.isEmpty(newViewTitle)) {
			newViewTitle = newViewName;
		}

		if (getFolder() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (StringUtils.isEmpty(newViewName)) {
			throw new InvalidParameterException("view name is undefined");
		}

		int index = 1;
		String baseName = newViewName;
		while (!getFolder().isValidResourceName(newViewName)) {
			newViewName = baseName + index;
			index++;
		}

		newView = View.newView(newViewName, newViewTitle, viewpointResource.getViewPoint(), getFolder(), getProject());

		logger.info("Added view " + newView + " in folder " + getFolder() + " for project " + getProject());
		// Creates the resource here
	}

	public ViewLibrary getViewLibrary() {
		if (getFocusedObject().getResourceRepository() instanceof ViewLibrary) {
			return (ViewLibrary) getFocusedObject().getResourceRepository();
		}
		return null;
	}

	public FlexoProject getProject() {
		if (getViewLibrary() != null) {
			return getViewLibrary().getProject();
		}
		return null;
	}

	public RepositoryFolder getFolder() {
		return getFocusedObject();
	}

	private String errorMessage;

	public String getErrorMessage() {
		isValid();
		// System.out.println("valid=" + isValid());
		// System.out.println("errorMessage=" + errorMessage);
		return errorMessage;
	}

	public boolean isValid() {

		// System.out.println("viewpointResource=" + viewpointResource);

		if (getFolder() == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_folder_defined");
			return false;
		} else if (viewpointResource == null && useViewPoint) {
			errorMessage = FlexoLocalization.localizedForKey("no_view_point_selected");
			return false;
		}
		if (StringUtils.isEmpty(newViewTitle)) {
			errorMessage = FlexoLocalization.localizedForKey("no_view_title_defined");
			return false;
		}

		String viewName = newViewName;
		if (StringUtils.isNotEmpty(newViewTitle) && StringUtils.isEmpty(newViewName)) {
			viewName = JavaUtils.getClassName(newViewTitle);
		}

		if (getFocusedObject().getResourceWithName(viewName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_view_with_that_name_already_exists");
			return false;
		}
		return true;
	}

	public View getNewView() {
		return newView;
	}
}