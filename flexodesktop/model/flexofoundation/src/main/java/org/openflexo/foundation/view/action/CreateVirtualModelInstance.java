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
import org.openflexo.foundation.InvalidArgumentException;
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

/**
 * This action is called to create a new {@link VirtualModelInstance} in a {@link View}
 * 
 * @author sylvain
 * 
 * @param <A>
 *            type of action, required to manage introspection for inheritance
 */
public abstract class CreateVirtualModelInstance<A extends CreateVirtualModelInstance<A>> extends FlexoAction<A, View, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(CreateVirtualModelInstance.class.getPackage().getName());

	public static class CreateConcreteVirtualModelInstance extends CreateVirtualModelInstance<CreateConcreteVirtualModelInstance> {
		CreateConcreteVirtualModelInstance(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			super(actionType, focusedObject, globalSelection, editor);
		}

		@Override
		public VirtualModelInstanceResource makeVirtualModelInstanceResource() throws InvalidFileNameException, SaveResourceException {
			return VirtualModelInstance.newVirtualModelInstance(getNewVirtualModelInstanceName(), getNewVirtualModelInstanceTitle(),
					getVirtualModel(), getFocusedObject());
		}
	}

	public static FlexoActionType<CreateConcreteVirtualModelInstance, View, FlexoModelObject> actionType = new FlexoActionType<CreateConcreteVirtualModelInstance, View, FlexoModelObject>(
			"instantiate_virtual_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateConcreteVirtualModelInstance makeNewAction(View focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new CreateConcreteVirtualModelInstance(focusedObject, globalSelection, editor);
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

	private String newVirtualModelInstanceName;
	private String newVirtualModelInstanceTitle;
	private VirtualModel<?> virtualModel;

	public boolean skipChoosePopup = false;

	public CreateVirtualModelInstance(FlexoActionType<A, View, FlexoModelObject> actionType, View focusedObject,
			Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		modelSlotConfigurations = new Hashtable<ModelSlot<?, ?>, ModelSlotInstanceConfiguration<?>>();
	}

	public abstract VirtualModelInstanceResource makeVirtualModelInstanceResource() throws InvalidFileNameException, SaveResourceException;

	@Override
	protected void doAction(Object context) throws InvalidFileNameException, SaveResourceException, InvalidArgumentException {
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

		VirtualModelInstanceResource newVirtualModelInstanceResource = makeVirtualModelInstanceResource();

		newVirtualModelInstance = newVirtualModelInstanceResource.getVirtualModelInstance();

		if (newVirtualModelInstance.isSynchronizable()) {
			newVirtualModelInstance.synchronize(null);
		}

		logger.info("Added virtual model instance " + newVirtualModelInstance + " in view " + getFocusedObject());

		System.out.println("OK, we have created the file " + newVirtualModelInstanceResource.getFile().getAbsolutePath());

		for (ModelSlot<?, ?> ms : virtualModel.getModelSlots()) {
			ModelSlotInstanceConfiguration<?> configuration = getModelSlotInstanceConfiguration(ms);
			if (configuration.isValidConfiguration()) {
				newVirtualModelInstance.addToModelSlotInstances(configuration.createModelSlotInstance(newVirtualModelInstance));
			} else {
				throw new InvalidArgumentException("Wrong configuration for model slot " + configuration.getModelSlot() + " configuration="
						+ configuration);
			}
		}

		System.out.println("Saving file again...");
		newVirtualModelInstance.save();
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
			errorMessage = noVirtualModelSelectedMessage();
			return false;
		}
		if (StringUtils.isEmpty(newVirtualModelInstanceName)) {
			errorMessage = noNameMessage();
			return false;
		}

		if (!newVirtualModelInstanceName.equals(JavaUtils.getClassName(newVirtualModelInstanceName))
				&& !newVirtualModelInstanceName.equals(JavaUtils.getVariableName(newVirtualModelInstanceName))) {
			errorMessage = invalidNameMessage();
			return false;
		}

		if (StringUtils.isEmpty(newVirtualModelInstanceTitle)) {
			errorMessage = noTitleMessage();
			return false;
		}
		if (getFocusedObject().getVirtualModelInstance(newVirtualModelInstanceName) != null) {
			errorMessage = duplicatedNameMessage();
			return false;
		}
		return true;
	}

	public String noVirtualModelSelectedMessage() {
		return FlexoLocalization.localizedForKey("no_virtual_model_type_selected");
	}

	public String noTitleMessage() {
		return FlexoLocalization.localizedForKey("no_virtual_model_title_defined");
	}

	public String noNameMessage() {
		return FlexoLocalization.localizedForKey("no_virtual_model_name_defined");
	}

	public String invalidNameMessage() {
		return FlexoLocalization.localizedForKey("invalid_name_for_new_virtual_model");
	}

	public String duplicatedNameMessage() {
		return FlexoLocalization.localizedForKey("a_virtual_model_with_that_name_already_exists");
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

	public String getNewVirtualModelInstanceName() {
		return newVirtualModelInstanceName;
	}

	public void setNewVirtualModelInstanceName(String newVirtualModelInstanceName) {
		this.newVirtualModelInstanceName = newVirtualModelInstanceName;
	}

	public String getNewVirtualModelInstanceTitle() {
		return newVirtualModelInstanceTitle;
	}

	public void setNewVirtualModelInstanceTitle(String newVirtualModelInstanceTitle) {
		this.newVirtualModelInstanceTitle = newVirtualModelInstanceTitle;
	}
}