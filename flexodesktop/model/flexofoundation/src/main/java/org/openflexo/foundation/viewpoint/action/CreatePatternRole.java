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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreatePatternRole extends FlexoAction<CreatePatternRole, EditionPattern, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreatePatternRole.class.getPackage().getName());

	public static FlexoActionType<CreatePatternRole, EditionPattern, ViewPointObject> actionType = new FlexoActionType<CreatePatternRole, EditionPattern, ViewPointObject>(
			"create_pattern_role", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreatePatternRole makeNewAction(EditionPattern focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreatePatternRole(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(EditionPattern object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(EditionPattern object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreatePatternRole.actionType, EditionPattern.class);
	}

	private String patternRoleName;
	public String description;
	public ModelSlot<?, ?> modelSlot;
	public Class<? extends PatternRole> patternRoleClass;

	private PatternRole newPatternRole;

	CreatePatternRole(EditionPattern focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);

	}

	public String getPatternRoleName() {
		if (StringUtils.isEmpty(patternRoleName) && modelSlot != null && patternRoleClass != null) {
			return getFocusedObject().getAvailableRoleName(modelSlot.defaultPatternRoleName(patternRoleClass));
		}
		return patternRoleName;
	}

	public void setPatternRoleName(String patternRoleName) {
		this.patternRoleName = patternRoleName;
	}

	public List<Class<? extends PatternRole>> getAvailablePatternRoleTypes() {
		if (modelSlot != null) {
			return modelSlot.getAvailablePatternRoleTypes();
		}
		return null;
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add pattern role");

		if (modelSlot != null && patternRoleClass != null) {
			newPatternRole = modelSlot.makePatternRole(patternRoleClass);
			newPatternRole.setPatternRoleName(getPatternRoleName());
			newPatternRole.setModelSlot(modelSlot);
			newPatternRole.setDescription(description);
			getFocusedObject().addToPatternRoles(newPatternRole);
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

	public boolean isValid() {
		if (StringUtils.isEmpty(getPatternRoleName())) {
			validityMessage = EMPTY_NAME;
			return false;
		} else if (getFocusedObject().getPatternRole(getPatternRoleName()) != null) {
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

}