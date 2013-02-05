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
package org.openflexo.foundation.view.diagram.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class CreateVirtualModelInstance extends FlexoAction<CreateVirtualModelInstance, View, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(CreateVirtualModelInstance.class.getPackage().getName());

	public static FlexoActionType<CreateVirtualModelInstance, View, FlexoModelObject> actionType = new FlexoActionType<CreateVirtualModelInstance, View, FlexoModelObject>(
			"instantiate_virtual_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateVirtualModelInstance makeNewAction(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new CreateVirtualModelInstance(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(View object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(View object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateVirtualModelInstance.actionType, View.class);
	}

	private VirtualModelInstance<?, ?> newVirtualModelInstance;

	public String newVirtualModelInstanceName;
	public String newVirtualModelInstanceTitle;
	public VirtualModel<?> virtualModel;

	public boolean skipChoosePopup = false;

	CreateVirtualModelInstance(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidFileNameException, SaveResourceException {
		logger.info("Add virtual model instance in view " + getFocusedObject());

		if (StringUtils.isNotEmpty(newVirtualModelInstanceTitle) && StringUtils.isEmpty(newVirtualModelInstanceName)) {
			newVirtualModelInstanceName = JavaUtils.getClassName(newVirtualModelInstanceTitle);
		}

		if (StringUtils.isNotEmpty(newVirtualModelInstanceName) && StringUtils.isEmpty(newVirtualModelInstanceTitle)) {
			newVirtualModelInstanceTitle = newVirtualModelInstanceName;
		}

		if (StringUtils.isEmpty(newVirtualModelInstanceName)) {
			throw new InvalidParameterException("view name is undefined");
		}

		int index = 1;
		String baseName = newVirtualModelInstanceName;
		while (!getFocusedObject().isValidVirtualModelName(newVirtualModelInstanceName)) {
			newVirtualModelInstanceName = baseName + index;
			index++;
		}

		VirtualModelInstanceResource newVirtualModelInstanceResource = VirtualModelInstance.newVirtualModelInstance(
				newVirtualModelInstanceName, newVirtualModelInstanceTitle, virtualModel, getFocusedObject());
		newVirtualModelInstance = newVirtualModelInstanceResource.getVirtualModelInstance();

		logger.info("Added virtual model instance " + newVirtualModelInstance + " in view " + getFocusedObject());
		// Creates the resource here
	}

	public String errorMessage;

	public boolean isValid() {
		if (virtualModel == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_diagram_type_selected");
			return false;
		}
		if (StringUtils.isEmpty(newVirtualModelInstanceTitle)) {
			errorMessage = FlexoLocalization.localizedForKey("no_virtual_model_title_defined");
			return false;
		}

		String vmiName = newVirtualModelInstanceName;
		if (StringUtils.isNotEmpty(newVirtualModelInstanceTitle) && StringUtils.isEmpty(newVirtualModelInstanceName)) {
			vmiName = JavaUtils.getClassName(newVirtualModelInstanceTitle);
		}

		if (getFocusedObject().getVirtualModelInstance(vmiName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_virtual_model_with_that_name_already_exists");
			return false;
		}
		return true;
	}

	public VirtualModelInstance getNewVirtualModelInstance() {
		return newVirtualModelInstance;
	}
}