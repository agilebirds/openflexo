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
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.foundation.viewpoint.VirtualModelTechnologyAdapter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateModelSlot extends FlexoAction<CreateModelSlot, ViewPointObject, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateModelSlot.class.getPackage().getName());

	public static FlexoActionType<CreateModelSlot, ViewPointObject, ViewPointObject> actionType = new FlexoActionType<CreateModelSlot, ViewPointObject, ViewPointObject>(
			"create_model_slot", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateModelSlot makeNewAction(ViewPointObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreateModelSlot(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewPointObject object, Vector<ViewPointObject> globalSelection) {
			return object instanceof VirtualModel || object instanceof ViewPoint;
		}

		@Override
		public boolean isEnabledForSelection(ViewPointObject object, Vector<ViewPointObject> globalSelection) {
			return object instanceof VirtualModel || object instanceof ViewPoint;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateModelSlot.actionType, ViewPoint.class);
		FlexoModelObject.addActionForClass(CreateModelSlot.actionType, VirtualModel.class);
	}

	public String modelSlotName;
	public String description;
	public TechnologyAdapter technologyAdapter;
	public FlexoMetaModelResource<?, ?> mmRes;
	public VirtualModelResource vmRes;
	public boolean required = true;
	public boolean readOnly = false;

	private ModelSlot newModelSlot;

	CreateModelSlot(ViewPointObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add model slot");

		if (technologyAdapter != null) {
			if (getFocusedObject() instanceof VirtualModel) {
				newModelSlot = technologyAdapter.createNewModelSlot((VirtualModel) getFocusedObject());
			} else if (getFocusedObject() instanceof ViewPoint) {
				newModelSlot = technologyAdapter.createNewModelSlot((ViewPoint) getFocusedObject());
			}
			newModelSlot.setName(modelSlotName);
			if (technologyAdapter instanceof VirtualModelTechnologyAdapter) {
				((VirtualModelModelSlot) newModelSlot).setVirtualModelResource(vmRes);
			} else {
				newModelSlot.setMetaModelResource(mmRes);
			}
			newModelSlot.setIsRequired(required);
			newModelSlot.setIsReadOnly(readOnly);
			newModelSlot.setDescription(description);
			if (getFocusedObject() instanceof VirtualModel) {
				((VirtualModel) getFocusedObject()).addToModelSlots(newModelSlot);
			} else if (getFocusedObject() instanceof ViewPoint) {
				((ViewPoint) getFocusedObject()).addToModelSlots(newModelSlot);
			}
		}

	}

	public ModelSlot getNewModelSlot() {
		return newModelSlot;
	}

	private String validityMessage = EMPTY_NAME;

	private static final String DUPLICATED_NAME = FlexoLocalization.localizedForKey("this_name_is_already_used_please_choose_an_other_one");
	private static final String EMPTY_NAME = FlexoLocalization.localizedForKey("model_slot_must_have_an_non_empty_and_unique_name");
	private static final String NO_TECHNOLOGY_ADAPTER = FlexoLocalization.localizedForKey("please_choose_a_technology_adapter");
	private static final String NO_META_MODEL = FlexoLocalization.localizedForKey("please_choose_a_valid_metamodel");

	public String getValidityMessage() {
		return validityMessage;
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isEmpty(modelSlotName)) {
			validityMessage = EMPTY_NAME;
			return false;
		} else if (getFocusedObject() instanceof VirtualModel && ((VirtualModel) getFocusedObject()).getModelSlot(modelSlotName) != null) {
			validityMessage = DUPLICATED_NAME;
			return false;
		} else if (getFocusedObject() instanceof ViewPoint && ((ViewPoint) getFocusedObject()).getModelSlot(modelSlotName) != null) {
			validityMessage = DUPLICATED_NAME;
			return false;
		} else if (technologyAdapter == null) {
			validityMessage = NO_TECHNOLOGY_ADAPTER;
			return false;
		} else if (mmRes == null) {
			validityMessage = NO_META_MODEL;
			return true;
		} else {
			validityMessage = "";
			return true;
		}
	}
}