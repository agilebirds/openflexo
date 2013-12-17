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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.EditionPatternStructuralFacet;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreatePatternRole extends FlexoAction<CreatePatternRole, EditionPatternObject, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreatePatternRole.class.getPackage().getName());

	public static FlexoActionType<CreatePatternRole, EditionPatternObject, ViewPointObject> actionType = new FlexoActionType<CreatePatternRole, EditionPatternObject, ViewPointObject>(
			"create_pattern_role", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreatePatternRole makeNewAction(EditionPatternObject focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new CreatePatternRole(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(EditionPatternObject object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(EditionPatternObject object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObject.addActionForClass(CreatePatternRole.actionType, EditionPattern.class);
		FlexoObject.addActionForClass(CreatePatternRole.actionType, EditionPatternStructuralFacet.class);
	}

	private String patternRoleName;
	public String description;
	public ModelSlot<?> modelSlot;
	public Class<? extends PatternRole> patternRoleClass;
	public IFlexoOntologyClass individualType;
	public EditionPattern editionPatternInstanceType;

	private PatternRole newPatternRole;

	CreatePatternRole(EditionPatternObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);

	}

	public EditionPattern getEditionPattern() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getEditionPattern();
		}
		return null;
	}

	public String getPatternRoleName() {
		if (StringUtils.isEmpty(patternRoleName) && modelSlot != null && patternRoleClass != null) {
			return getEditionPattern().getAvailableRoleName(modelSlot.defaultPatternRoleName(patternRoleClass));
		}
		return patternRoleName;
	}

	public void setPatternRoleName(String patternRoleName) {
		this.patternRoleName = patternRoleName;
	}

	public List<Class<? extends PatternRole<?>>> getAvailablePatternRoleTypes() {
		if (modelSlot != null) {
			return modelSlot.getAvailablePatternRoleTypes();
		}
		return null;
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParameterException {
		logger.info("Add pattern role");

		if (modelSlot != null && patternRoleClass != null) {
			newPatternRole = modelSlot.makePatternRole(patternRoleClass);
			newPatternRole.setPatternRoleName(getPatternRoleName());
			newPatternRole.setModelSlot(modelSlot);
			newPatternRole.setDescription(description);
			if (isIndividual()) {
				((IndividualPatternRole) newPatternRole).setOntologicType(individualType);
			}
			if (isEditionPatternInstance()) {
				((EditionPatternInstancePatternRole) newPatternRole).setEditionPatternType(editionPatternInstanceType);
			}
			getEditionPattern().addToPatternRoles(newPatternRole);
		}

	}

	public PatternRole getNewPatternRole() {
		return newPatternRole;
	}

	private String validityMessage = EMPTY_NAME;

	private static final String DUPLICATED_NAME = FlexoLocalization.localizedForKey("this_name_is_already_used_please_choose_an_other_one");
	private static final String EMPTY_NAME = FlexoLocalization.localizedForKey("pattern_role_must_have_an_non_empty_and_unique_name");
	private static final String NO_MODEL_SLOT = FlexoLocalization.localizedForKey("please_choose_a_model_slot");

	public String getValidityMessage() {
		return validityMessage;
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isEmpty(getPatternRoleName())) {
			validityMessage = EMPTY_NAME;
			return false;
		} else if (getEditionPattern().getPatternRole(getPatternRoleName()) != null) {
			validityMessage = DUPLICATED_NAME;
			return false;
		} else if (modelSlot == null) {
			validityMessage = NO_MODEL_SLOT;
			return false;
		} else {
			validityMessage = "";
			return true;
		}
	}

	public boolean isIndividual() {
		if (patternRoleClass == null) {
			return false;
		}
		return IndividualPatternRole.class.isAssignableFrom(patternRoleClass);
	}

	public boolean isEditionPatternInstance() {
		if (patternRoleClass == null) {
			return false;
		}
		return EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass);
	}

	public VirtualModel getModelSlotVirtualModel() {
		if (modelSlot instanceof VirtualModelModelSlot) {
			if (((VirtualModelModelSlot) modelSlot).getVirtualModelResource() != null) {
				return ((VirtualModelModelSlot) modelSlot).getVirtualModelResource().getVirtualModel();
			}
		}
		return null;
	}

	public List<ModelSlot<?>> getAvailableModelSlots() {
		if (getFocusedObject() instanceof VirtualModel) {
			return ((VirtualModel) getFocusedObject()).getModelSlots();
		} else {
			return getFocusedObject().getVirtualModel().getModelSlots();
		}
	}

	/**
	 * Return a metamodel adressed by a model slot
	 * 
	 * @return
	 */
	public FlexoMetaModel getAdressedFlexoMetaModel() {
		if (modelSlot instanceof TypeAwareModelSlot) {
			TypeAwareModelSlot typeAwareModelSlot = (TypeAwareModelSlot) modelSlot;
			return typeAwareModelSlot.getMetaModelResource().getMetaModelData();
		}
		return null;
	}

}