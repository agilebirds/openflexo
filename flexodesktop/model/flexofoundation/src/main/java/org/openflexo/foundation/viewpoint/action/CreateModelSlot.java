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
package org.openflexo.foundation.viewpoint.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.VirtualModelResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TypeSafeModelSlot;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateModelSlot extends FlexoAction<CreateModelSlot, VirtualModel, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateModelSlot.class.getPackage().getName());

	public static FlexoActionType<CreateModelSlot, VirtualModel, ViewPointObject> actionType = new FlexoActionType<CreateModelSlot, VirtualModel, ViewPointObject>(
			"create_model_slot", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateModelSlot makeNewAction(VirtualModel focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreateModelSlot(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<ViewPointObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<ViewPointObject> globalSelection) {
			return true;
		}

	};

	static {
		// FlexoModelObject.addActionForClass(CreateModelSlot.actionType, ViewPoint.class);
		FlexoModelObject.addActionForClass(CreateModelSlot.actionType, VirtualModel.class);
	}

	public String modelSlotName;
	public String description;
	public TechnologyAdapter technologyAdapter;
	public FlexoMetaModelResource<?, ?> mmRes;
	public VirtualModelResource vmRes;
	public boolean required = true;
	public boolean readOnly = false;
	private Class<? extends ModelSlot<?>> modelSlotClass;

	private ModelSlot newModelSlot;

	CreateModelSlot(VirtualModel focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add model slot");

		if (technologyAdapter != null && getModelSlotClass() != null) {
			// if (getFocusedObject() instanceof VirtualModel) {
			newModelSlot = technologyAdapter.makeModelSlot(getModelSlotClass(), getFocusedObject());
			/*} else if (getFocusedObject() instanceof ViewPoint) {
				newModelSlot = technologyAdapter.createNewModelSlot((ViewPoint) getFocusedObject());
			}*/
			newModelSlot.setName(modelSlotName);
			if (newModelSlot instanceof VirtualModelModelSlot) {
				((VirtualModelModelSlot<?, ?>) newModelSlot).setVirtualModelResource(vmRes);
			} else if (newModelSlot instanceof TypeSafeModelSlot) {
				((TypeSafeModelSlot) newModelSlot).setMetaModelResource(mmRes);
			}
			newModelSlot.setIsRequired(required);
			newModelSlot.setIsReadOnly(readOnly);
			newModelSlot.setDescription(description);
			// if (getFocusedObject() instanceof VirtualModel) {
			getFocusedObject().addToModelSlots(newModelSlot);
			/*} else if (getFocusedObject() instanceof ViewPoint) {
				((ViewPoint) getFocusedObject()).addToModelSlots(newModelSlot);
			}*/
		}

	}

	public ModelSlot getNewModelSlot() {
		return newModelSlot;
	}

	private String validityMessage = EMPTY_NAME;

	private static final String DUPLICATED_NAME = FlexoLocalization.localizedForKey("this_name_is_already_used_please_choose_an_other_one");
	private static final String EMPTY_NAME = FlexoLocalization.localizedForKey("model_slot_must_have_an_non_empty_and_unique_name");
	private static final String NO_TECHNOLOGY_ADAPTER = FlexoLocalization.localizedForKey("please_choose_a_technology_adapter");
	private static final String NO_MODEL_SLOT_TYPE = FlexoLocalization.localizedForKey("please_choose_a_model_slot_type");
	private static final String NO_META_MODEL = FlexoLocalization.localizedForKey("please_choose_a_valid_metamodel");

	public String getValidityMessage() {
		return validityMessage;
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isEmpty(modelSlotName)) {
			validityMessage = EMPTY_NAME;
			return false;
		} else if (getFocusedObject() instanceof VirtualModel && getFocusedObject().getModelSlot(modelSlotName) != null) {
			validityMessage = DUPLICATED_NAME;
			return false;
		} /*else if (getFocusedObject() instanceof ViewPoint && ((ViewPoint) getFocusedObject()).getModelSlot(modelSlotName) != null) {
			validityMessage = DUPLICATED_NAME;
			return false;
			}*/else if (technologyAdapter == null) {
			validityMessage = NO_TECHNOLOGY_ADAPTER;
			return false;
		} else if (getModelSlotClass() == null) {
			validityMessage = NO_MODEL_SLOT_TYPE;
			return false;
		} else if (mmRes == null && TypeSafeModelSlot.class.isAssignableFrom(getModelSlotClass())) {
			validityMessage = NO_META_MODEL;
			return true;
		} else {
			validityMessage = "";
			return true;
		}
	}

	public Class<? extends ModelSlot<?>> getModelSlotClass() {
		if (modelSlotClass == null && technologyAdapter != null && technologyAdapter.getAvailableModelSlotTypes().size() > 0) {
			return technologyAdapter.getAvailableModelSlotTypes().get(0);
		}
		return modelSlotClass;
	}

	public void setModelSlotClass(Class<? extends ModelSlot<?>> modelSlotClass) {
		this.modelSlotClass = modelSlotClass;
	}
}