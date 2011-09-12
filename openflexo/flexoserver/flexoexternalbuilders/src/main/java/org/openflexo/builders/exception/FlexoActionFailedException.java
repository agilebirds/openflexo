package org.openflexo.builders.exception;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;

public class FlexoActionFailedException extends Exception {

	private FlexoAction<? extends FlexoAction<?,FlexoModelObject,FlexoModelObject>, FlexoModelObject, FlexoModelObject> failedAction; 
	
	public FlexoActionFailedException(FlexoAction<? extends FlexoAction<?,FlexoModelObject,FlexoModelObject>, FlexoModelObject, FlexoModelObject> failedAction) {
		this.failedAction = failedAction;
	}

	public FlexoAction<? extends FlexoAction<?,FlexoModelObject,FlexoModelObject>, FlexoModelObject, FlexoModelObject> getFailedAction() {
		return failedAction;
	}
	
	@Override
	public String getMessage() {
		return "Action "+failedAction.getLocalizedName()+" could not be performed";
	}
	
}
