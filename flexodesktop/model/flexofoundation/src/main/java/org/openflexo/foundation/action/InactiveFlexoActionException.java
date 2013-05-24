package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;

public class InactiveFlexoActionException extends FlexoException {

	private final FlexoActionType<?, ?, ?> actionType;
	private final FlexoModelObject focusedObject;
	private final Vector<? extends FlexoModelObject> globalSelection;

	public InactiveFlexoActionException(FlexoActionType<?, ?, ?> actionType, FlexoModelObject focusedObject,
			Vector<? extends FlexoModelObject> globalSelection) {
		super("Action " + actionType + " is not active for " + focusedObject + " and " + globalSelection);
		this.actionType = actionType;
		this.focusedObject = focusedObject;
		this.globalSelection = globalSelection;
	}

	public FlexoActionType<?, ?, ?> getActionType() {
		return actionType;
	}

	public FlexoModelObject getFocusedObject() {
		return focusedObject;
	}

	public Vector<? extends FlexoModelObject> getGlobalSelection() {
		return globalSelection;
	}

}
