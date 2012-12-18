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
import org.openflexo.foundation.viewpoint.ActionContainer;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeObject;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.localization.FlexoLocalization;

public class CreateEditionAction extends FlexoAction<CreateEditionAction, EditionSchemeObject, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateEditionAction.class.getPackage().getName());

	public static FlexoActionType<CreateEditionAction, EditionSchemeObject, ViewPointObject> actionType = new FlexoActionType<CreateEditionAction, EditionSchemeObject, ViewPointObject>(
			"create_edition_action", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateEditionAction makeNewAction(EditionSchemeObject focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new CreateEditionAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(EditionSchemeObject object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(EditionSchemeObject object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateEditionAction.actionType, EditionScheme.class);
		FlexoModelObject.addActionForClass(CreateEditionAction.actionType, EditionAction.class);
	}

	public String description;
	public ModelSlot<?, ?> modelSlot;
	public Class<? extends EditionAction> editionActionClass;

	private EditionAction newEditionAction;

	CreateEditionAction(EditionSchemeObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);

	}

	public List<Class<? extends EditionAction>> getAvailableEditionActionTypes() {
		if (modelSlot != null) {
			return modelSlot.getAvailableEditionActionTypes();
		}
		return null;
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add edition action");

		if (modelSlot != null && editionActionClass != null) {
			newEditionAction = modelSlot.makeEditionAction(editionActionClass);
			newEditionAction.setModelSlot(modelSlot);
			if (getFocusedObject() instanceof ActionContainer) {
				((ActionContainer) getFocusedObject()).addToActions(newEditionAction);
			} else if (getFocusedObject() instanceof EditionAction) {
				ActionContainer container = ((EditionAction) getFocusedObject()).getActionContainer();
				int index = container.getIndex((EditionAction) getFocusedObject());
				container.insertActionAtIndex(newEditionAction, index + 1);
			}
		}

	}

	public EditionAction getNewEditionAction() {
		return newEditionAction;
	}

	private String validityMessage = NO_MODEL_SLOT;

	private static final String NO_MODEL_SLOT = FlexoLocalization.localizedForKey("please_choose_a_model_slot");

	public String getValidityMessage() {
		return validityMessage;
	}

	public boolean isValid() {
		if (modelSlot == null) {
			validityMessage = NO_MODEL_SLOT;
			return false;
		} else {
			validityMessage = "";
			return true;
		}
	}

}