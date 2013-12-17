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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.ActionContainer;
import org.openflexo.foundation.viewpoint.AddEditionPatternInstance;
import org.openflexo.foundation.viewpoint.ConditionalAction;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeObject;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.FetchRequestIterationAction;
import org.openflexo.foundation.viewpoint.IterationAction;
import org.openflexo.foundation.viewpoint.MatchEditionPatternInstance;
import org.openflexo.foundation.viewpoint.SelectEditionPatternInstance;
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
		FlexoObject.addActionForClass(CreateEditionAction.actionType, EditionScheme.class);
		FlexoObject.addActionForClass(CreateEditionAction.actionType, EditionAction.class);
	}

	public static enum CreateEditionActionChoice {
		BuiltInAction, ModelSlotSpecificAction, RequestAction, ControlAction
	}

	public static enum LayoutChoice {
		InsertAfter, InsertBefore, InsertInside;
	}

	public String description;
	public CreateEditionActionChoice actionChoice = CreateEditionActionChoice.BuiltInAction;
	private LayoutChoice layoutChoice;
	public ModelSlot modelSlot;
	public Class<? extends EditionAction> builtInActionClass;
	public Class<? extends EditionAction> controlActionClass;
	public Class<? extends EditionAction> modelSlotSpecificActionClass;
	public Class<? extends FetchRequest> requestActionClass;

	private EditionAction newEditionAction;

	private final List<Class<? extends EditionAction>> builtInActions;
	private final List<Class<? extends EditionAction>> controlActions;

	CreateEditionAction(EditionSchemeObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);

		builtInActions = new ArrayList<Class<? extends EditionAction>>();
		builtInActions.add(org.openflexo.foundation.viewpoint.AssignationAction.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.AddToListAction.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.RemoveFromListAction.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.ExecutionAction.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.DeclarePatternRole.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.AddEditionPatternInstance.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.MatchEditionPatternInstance.class);
		builtInActions.add(org.openflexo.foundation.viewpoint.SelectEditionPatternInstance.class);
		builtInActions.add(DeleteAction.class);

		controlActions = new ArrayList<Class<? extends EditionAction>>();
		controlActions.add(ConditionalAction.class);
		controlActions.add(IterationAction.class);
		controlActions.add(FetchRequestIterationAction.class);

		// If the model slot is empty, then select the first one
		if (modelSlot == null && !focusedObject.getVirtualModel().getModelSlots().isEmpty()) {
			modelSlot = focusedObject.getVirtualModel().getModelSlots().get(0);
		}
	}

	public List<Class<? extends EditionAction>> getBuiltInActions() {
		return builtInActions;
	}

	public List<Class<? extends EditionAction>> getControlActions() {
		return controlActions;
	}

	public List<Class<? extends EditionAction>> getModelSlotSpecificActions() {
		if (modelSlot != null) {
			return modelSlot.getAvailableEditionActionTypes();
		}
		return null;
	}

	public List<Class<? extends EditionAction>> getRequestActions() {
		if (modelSlot != null) {
			return modelSlot.getAvailableFetchRequestActionTypes();
		}
		return null;
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParameterException {
		logger.info("Add edition action, modelSlot=" + modelSlot + " actionChoice=" + actionChoice);

		newEditionAction = makeEditionAction();

		if (newEditionAction != null) {
			if ((getFocusedObject() instanceof ActionContainer) && (getLayoutChoice() == LayoutChoice.InsertInside)) {
				((ActionContainer) getFocusedObject()).addToActions(newEditionAction);
			} else if (getFocusedObject() instanceof EditionAction) {
				ActionContainer container = ((EditionAction) getFocusedObject()).getActionContainer();
				int index = container.getIndex((EditionAction) getFocusedObject());
				if (getLayoutChoice() == LayoutChoice.InsertAfter) {
					container.insertActionAtIndex(newEditionAction, index + 1);
				} else {
					container.insertActionAtIndex(newEditionAction, index);
				}
			}
		}

	}

	public EditionAction getNewEditionAction() {
		return newEditionAction;
	}

	private String validityMessage = NO_ACTION_TYPE_SELECTED;

	private static final String NO_MODEL_SLOT = FlexoLocalization.localizedForKey("please_choose_a_model_slot");
	private static final String NO_ACTION_TYPE_SELECTED = FlexoLocalization.localizedForKey("please_select_an_action_type");

	public String getValidityMessage() {
		return validityMessage;
	}

	@Override
	public boolean isValid() {
		switch (actionChoice) {
		case BuiltInAction:
			if (builtInActionClass == null) {
				validityMessage = NO_ACTION_TYPE_SELECTED;
				return false;
			}
			return true;
		case ControlAction:
			if (controlActionClass == null) {
				validityMessage = NO_ACTION_TYPE_SELECTED;
				return false;
			}
			return true;
		case ModelSlotSpecificAction:
			if (modelSlot == null) {
				validityMessage = NO_MODEL_SLOT;
				return false;
			}
			if (modelSlotSpecificActionClass == null) {
				validityMessage = NO_ACTION_TYPE_SELECTED;
				return false;
			}
			return true;
		case RequestAction:
			if (modelSlot == null) {
				validityMessage = NO_MODEL_SLOT;
				return false;
			}
			if (requestActionClass == null) {
				validityMessage = NO_ACTION_TYPE_SELECTED;
				return false;
			}
			return true;

		default:
			return false;
		}

	}

	private EditionAction makeEditionAction() {
		EditionAction returned;
		switch (actionChoice) {
		case BuiltInAction:
			if (builtInActionClass == null) {
				logger.warning("Unexpected " + builtInActionClass);
				return null;
			}
			if (org.openflexo.foundation.viewpoint.AssignationAction.class.isAssignableFrom(builtInActionClass)) {
				return new org.openflexo.foundation.viewpoint.AssignationAction();
			} else if (org.openflexo.foundation.viewpoint.AddToListAction.class.isAssignableFrom(builtInActionClass)) {
				return new org.openflexo.foundation.viewpoint.AddToListAction();
			} else if (org.openflexo.foundation.viewpoint.RemoveFromListAction.class.isAssignableFrom(builtInActionClass)) {
				return new org.openflexo.foundation.viewpoint.RemoveFromListAction();
			} else if (org.openflexo.foundation.viewpoint.ExecutionAction.class.isAssignableFrom(builtInActionClass)) {
				return new org.openflexo.foundation.viewpoint.ExecutionAction();
			} else if (org.openflexo.foundation.viewpoint.DeclarePatternRole.class.isAssignableFrom(builtInActionClass)) {
				return new org.openflexo.foundation.viewpoint.DeclarePatternRole();
			} else if (AddEditionPatternInstance.class.isAssignableFrom(builtInActionClass)) {
				return new AddEditionPatternInstance();
			} else if (MatchEditionPatternInstance.class.isAssignableFrom(builtInActionClass)) {
				return new MatchEditionPatternInstance();
			} else if (SelectEditionPatternInstance.class.isAssignableFrom(builtInActionClass)) {
				return new SelectEditionPatternInstance();
			} else if (DeleteAction.class.isAssignableFrom(builtInActionClass)) {
				return new DeleteAction();
			} else {
				logger.warning("Unexpected " + builtInActionClass);
				return null;
			}
		case ControlAction:
			if (controlActionClass == null) {
				logger.warning("Unexpected " + controlActionClass);
				return null;
			}
			if (ConditionalAction.class.isAssignableFrom(controlActionClass)) {
				return new ConditionalAction();
			} else if (IterationAction.class.isAssignableFrom(controlActionClass)) {
				return new IterationAction();
			} else if (FetchRequestIterationAction.class.isAssignableFrom(controlActionClass) && requestActionClass != null
					&& modelSlot != null) {
				returned = new FetchRequestIterationAction();
				FetchRequest request = modelSlot.makeFetchRequest(requestActionClass);
				request.setModelSlot(modelSlot);
				((FetchRequestIterationAction) returned).setFetchRequest(request);
				returned.setModelSlot(modelSlot);
				return returned;
			} else {
				logger.warning("Unexpected " + controlActionClass);
				return null;
			}
		case ModelSlotSpecificAction:
			if (modelSlotSpecificActionClass != null && modelSlot != null) {
				returned = modelSlot.makeEditionAction(modelSlotSpecificActionClass);
				returned.setModelSlot(modelSlot);
				return returned;
			}
			break;
		case RequestAction:
			if (requestActionClass != null && modelSlot != null) {
				returned = modelSlot.makeFetchRequest(requestActionClass);
				returned.setModelSlot(modelSlot);
				return returned;
			}

		default:
			break;
		}

		logger.warning("Cannot build EditionAction");
		return null;

	}

	public LayoutChoice getLayoutChoice() {
		if (layoutChoice == null) {
			if (getFocusedObject() instanceof ActionContainer) {
				return LayoutChoice.InsertInside;
			}
			return LayoutChoice.InsertAfter;
		}
		return layoutChoice;
	}

	public void setLayoutChoice(LayoutChoice layoutChoice) {
		this.layoutChoice = layoutChoice;
	}

}