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

import org.flexo.model.TestModelObject;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.viewpoint.CreationScheme;
import org.openflexo.foundation.viewpoint.ViewPoint;
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
public abstract class CreateVirtualModelInstance<A extends CreateVirtualModelInstance<A>> extends FlexoAction<A, View, TestModelObject>
		implements FlexoObserver {

	private static final Logger logger = Logger.getLogger(CreateVirtualModelInstance.class.getPackage().getName());

	public static class CreateConcreteVirtualModelInstance extends CreateVirtualModelInstance<CreateConcreteVirtualModelInstance> {
		CreateConcreteVirtualModelInstance(View focusedObject, Vector<TestModelObject> globalSelection, FlexoEditor editor) {
			super(actionType, focusedObject, globalSelection, editor);
		}

		@Override
		public VirtualModelInstanceResource makeVirtualModelInstanceResource() throws InvalidFileNameException, SaveResourceException {
			return VirtualModelInstance.newVirtualModelInstance(getNewVirtualModelInstanceName(), getNewVirtualModelInstanceTitle(),
					getVirtualModel(), getFocusedObject());
		}
	}

	public static FlexoActionType<CreateConcreteVirtualModelInstance, View, TestModelObject> actionType = new FlexoActionType<CreateConcreteVirtualModelInstance, View, TestModelObject>(
			"instantiate_virtual_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateConcreteVirtualModelInstance makeNewAction(View focusedObject, Vector<TestModelObject> globalSelection,
				FlexoEditor editor) {
			return new CreateConcreteVirtualModelInstance(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(View object, Vector<TestModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(View object, Vector<TestModelObject> globalSelection) {
			return object != null;
		}

	};

	static {
		TestModelObject.addActionForClass(CreateVirtualModelInstance.actionType, View.class);
	}

	private VirtualModelInstance newVirtualModelInstance;

	private String newVirtualModelInstanceName;
	private String newVirtualModelInstanceTitle;
	private VirtualModel virtualModel;
	private CreationScheme creationScheme;

	public boolean skipChoosePopup = false;

	public CreateVirtualModelInstance(FlexoActionType<A, View, TestModelObject> actionType, View focusedObject,
			Vector<TestModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		modelSlotConfigurations = new Hashtable<ModelSlot<?>, ModelSlotInstanceConfiguration<?, ?>>();
	}

	public abstract VirtualModelInstanceResource makeVirtualModelInstanceResource() throws InvalidFileNameException, SaveResourceException;

	@Override
	protected void doAction(Object context) throws InvalidFileNameException, SaveResourceException, InvalidArgumentException {
		logger.info("Add virtual model instance in view " + getFocusedObject() + " creationSchemeAction=" + creationSchemeAction);

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

		logger.info("Added virtual model instance " + newVirtualModelInstance + " in view " + getFocusedObject());

		System.out.println("OK, we have created the file " + newVirtualModelInstanceResource.getFile().getAbsolutePath());

		for (ModelSlot ms : virtualModel.getModelSlots()) {
			ModelSlotInstanceConfiguration<?, ?> configuration = getModelSlotInstanceConfiguration(ms);
			if (configuration.isValidConfiguration()) {
				newVirtualModelInstance.addToModelSlotInstances(configuration.createModelSlotInstance(newVirtualModelInstance));
			} else {
				throw new InvalidArgumentException("Wrong configuration for model slot " + configuration.getModelSlot() + " configuration="
						+ configuration);
			}
		}

		if (creationSchemeAction != null) {
			creationSchemeAction.initWithEditionPatternInstance(newVirtualModelInstance);
			creationSchemeAction.doAction();
		}

		System.out.println("Now, we try to synchronize the new virtual model instance");

		if (newVirtualModelInstance.isSynchronizable()) {
			System.out.println("Go for it");
			newVirtualModelInstance.synchronize(null);
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
		} else if (!getVirtualModel().hasCreationScheme()) {
			return virtualModel.getModelSlots().size() + 1;
		} else {
			return virtualModel.getModelSlots().size() + 2;
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

	private final Hashtable<ModelSlot<?>, ModelSlotInstanceConfiguration<?, ?>> modelSlotConfigurations;

	public VirtualModel getVirtualModel() {
		return virtualModel;
	}

	public void setVirtualModel(VirtualModel virtualModel) {
		if (virtualModel != this.virtualModel) {
			this.virtualModel = virtualModel;
			modelSlotConfigurations.clear();
			if (this.virtualModel != null) {
				for (ModelSlot<?> ms : this.virtualModel.getModelSlots()) {
					modelSlotConfigurations.put(ms, ms.createConfiguration(this));
				}
			}
		}
	}

	/*public DiagramSpecification getDiagramSpecification() {
		if (virtualModel instanceof DiagramSpecification) {
			return (DiagramSpecification) virtualModel;
		} else {
			return null;
		}
	}

	public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
		if (diagramSpecification != this.virtualModel) {
			this.virtualModel = diagramSpecification;
			modelSlotConfigurations.clear();
			if (this.virtualModel != null) {
				for (ModelSlot<?> ms : this.virtualModel.getModelSlots()) {
					modelSlotConfigurations.put(ms, ms.createConfiguration(this));
				}
			}
		}
	}*/

	public ModelSlotInstanceConfiguration<?, ?> getModelSlotInstanceConfiguration(ModelSlot ms) {
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
		if (getVirtualModel() == null) {
			return false;
		}
		for (ModelSlot ms : virtualModel.getModelSlots()) {
			ModelSlotInstanceConfiguration<?, ?> configuration = getModelSlotInstanceConfiguration(ms);
			if (!configuration.isValidConfiguration()) {
				return false;
			}
		}
		if (getVirtualModel().hasCreationScheme()) {
			if (getCreationScheme() == null) {
				return false;
			}
			if (getCreationSchemeAction() == null) {
				return false;
			}
			if (!getCreationSchemeAction().areRequiredParametersSetAndValid()) {
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

	public CreationScheme getCreationScheme() {
		return creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		this.creationScheme = creationScheme;
		if (creationScheme != null) {
			creationSchemeAction = CreationSchemeAction.actionType.makeNewEmbeddedAction(null, null, this);
			creationSchemeAction.setCreationScheme(creationScheme);
			creationSchemeAction.addObserver(this);
		} else {
			creationSchemeAction = null;
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification.propertyName().equals(EditionSchemeAction.PARAMETER_VALUE_CHANGED)) {
			setChanged();
			notifyObservers(new DataModification("isActionValidable", false, true));
		}
	}

	private CreationSchemeAction creationSchemeAction;

	public CreationSchemeAction getCreationSchemeAction() {
		return creationSchemeAction;
	}

	public ViewPoint getViewpoint() {
		return getFocusedObject().getViewPoint();
	}
}