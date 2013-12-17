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
package org.openflexo.foundation.sg.implmodel;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition.DuplicateImplementationModelNameException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateImplementationModel extends FlexoAction<CreateImplementationModel, GeneratedSources, CGObject> {

	private static final Logger logger = Logger.getLogger(CreateImplementationModel.class.getPackage().getName());

	public static FlexoActionType<CreateImplementationModel, GeneratedSources, CGObject> actionType = new FlexoActionType<CreateImplementationModel, GeneratedSources, CGObject>(
			"create_new_implementation_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateImplementationModel makeNewAction(GeneratedSources focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new CreateImplementationModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(GeneratedSources object, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(GeneratedSources object, Vector<CGObject> globalSelection) {
			return object != null;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(CreateImplementationModel.actionType, GeneratedSources.class);
	}

	private ImplementationModelDefinition _newImplementationModelDefinition;

	public String newModelName;
	public String newModelDescription;
	public boolean skipDialog = false;

	CreateImplementationModel(GeneratedSources focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException, DuplicateImplementationModelNameException,
			DuplicateResourceException {
		logger.info("Add implementation model");

		if (StringUtils.isEmpty(newModelName)) {
			throw new InvalidParametersException("model name is undefined");
		}
		if (getProject().getGeneratedSources().getImplementationModelNamed(newModelName) != null) {
			throw new ImplementationModelDefinition.DuplicateImplementationModelNameException(newModelName);
		}

		_newImplementationModelDefinition = new ImplementationModelDefinition(newModelName, getFocusedObject(), getProject(), true);
		_newImplementationModelDefinition.setDescription(newModelDescription);
		logger.info("Created implementation model " + _newImplementationModelDefinition + " for project "
				+ _newImplementationModelDefinition.getProject());
		// Creates the resource here
		_newImplementationModelDefinition.getImplementationModelResource();
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public ImplementationModelDefinition getNewImplementationModelDefinition() {
		return _newImplementationModelDefinition;
	}

	public String errorMessage;

	public boolean isValid() {
		if (StringUtils.isEmpty(newModelName)) {
			errorMessage = FlexoLocalization.localizedForKey("no_implementation_model_name_defined");
			return false;
		}
		if (getProject().getGeneratedSources().getImplementationModelDefinitionNamed(newModelName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("an_implementation_model_with_that_name_already_exists");
			return false;
		}
		return true;
	}
}