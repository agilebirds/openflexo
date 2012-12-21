package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;

public class InactiveFlexoActionException extends FlexoException {

	private final FlexoActionType<?, ?, ?> actionType;
	private final FlexoObject focusedObject;
	private final Vector<? extends FlexoObject> globalSelection;

	public InactiveFlexoActionException(FlexoActionType<?, ?, ?> actionType, FlexoObject focusedObject,
			Vector<? extends FlexoObject> globalSelection) {
		this.actionType = actionType;
		this.focusedObject = focusedObject;
		this.globalSelection = globalSelection;
	}

	public FlexoActionType<?, ?, ?> getActionType() {
		return actionType;
	}

	public FlexoObject getFocusedObject() {
		return focusedObject;
	}

	public Vector<? extends FlexoObject> getGlobalSelection() {
		return globalSelection;
	}

}
