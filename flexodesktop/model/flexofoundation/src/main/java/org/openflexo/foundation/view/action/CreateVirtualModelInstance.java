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
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
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
	private VirtualModel<?> virtualModel;

	public boolean skipChoosePopup = false;

	CreateVirtualModelInstance(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		modelSlotConfigurations = new Hashtable<ModelSlot<?, ?>, ModelSlotInstanceConfiguration<?>>();
	}

	@Override
	protected void doAction(Object context) throws InvalidFileNameException, SaveResourceException {
		logger.info("Add virtual model instance in view " + getFocusedObject());

		newVirtualModelInstanceName = JavaUtils.getClassName(newVirtualModelInstanceName);

		if (StringUtils.isNotEmpty(newVirtualModelInstanceName) && StringUtils.isEmpty(newVirtualModelInstanceTitle)) {
			newVirtualModelInstanceTitle = newVirtualModelInstanceName;
		}

		if (StringUtils.isEmpty(newVirtualModelInstanceName)) {
			throw new InvalidParameterException("virtual model instance name is undefined");
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

	private String errorMessage;

	public String getErrorMessage() {
		isValid();
		// System.out.println("valid=" + isValid());
		// System.out.println("errorMessage=" + errorMessage);
		return errorMessage;
	}

	public int getStepsNumber() {
		if (virtualModel == null) {
			return 1;
		} else {
			return virtualModel.getModelSlots().size() + 1;
		}
	}

	@Override
	public boolean isValid() {
		if (virtualModel == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_virtual_model_type_selected");
			return false;
		}
		if (StringUtils.isEmpty(newVirtualModelInstanceName)) {
			errorMessage = FlexoLocalization.localizedForKey("no_virtual_model_name_defined");
			return false;
		}

		if (!newVirtualModelInstanceName.equals(JavaUtils.getClassName(newVirtualModelInstanceName))
				&& !newVirtualModelInstanceName.equals(JavaUtils.getVariableName(newVirtualModelInstanceName))) {
			errorMessage = FlexoLocalization.localizedForKey("invalid_name_for_new_virtual_model");
			return false;
		}

		if (StringUtils.isEmpty(newVirtualModelInstanceTitle)) {
			errorMessage = FlexoLocalization.localizedForKey("no_virtual_model_title_defined");
			return false;
		}
		if (getFocusedObject().getVirtualModelInstance(newVirtualModelInstanceName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_virtual_model_with_that_name_already_exists");
			return false;
		}
		return true;
	}

	public VirtualModelInstance getNewVirtualModelInstance() {
		return newVirtualModelInstance;
	}

	private Hashtable<ModelSlot<?, ?>, ModelSlotInstanceConfiguration<?>> modelSlotConfigurations;

	public VirtualModel<?> getVirtualModel() {
		return virtualModel;
	}

	public void setVirtualModel(VirtualModel<?> virtualModel) {
		if (virtualModel != this.virtualModel) {
			this.virtualModel = virtualModel;
			modelSlotConfigurations.clear();
			if (this.virtualModel != null) {
				for (ModelSlot<?, ?> ms : this.virtualModel.getModelSlots()) {
					modelSlotConfigurations.put(ms, ms.createConfiguration(this));
				}
			}
		}
	}

	public ModelSlotInstanceConfiguration<?> getModelSlotInstanceConfiguration(ModelSlot<?, ?> ms) {
		return modelSlotConfigurations.get(ms);
	}

	/**
	 * Return a boolean indicating if all options are enough to execute the action
	 * 
	 * @return
	 */
	public boolean isActionValidable() {
		if (!isValid()) {
			return false;
		}
		for (ModelSlot<?, ?> ms : virtualModel.getModelSlots()) {
			ModelSlotInstanceConfiguration<?> configuration = getModelSlotInstanceConfiguration(ms);
			if (!configuration.isValidConfiguration()) {
				return false;
			}
		}
		return true;
	}
}