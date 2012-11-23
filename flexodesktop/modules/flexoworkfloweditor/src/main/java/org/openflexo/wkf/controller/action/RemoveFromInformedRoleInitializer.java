package org.openflexo.wkf.controller.action;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.action.RemoveFromInformedRole;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RemoveFromInformedRoleInitializer extends RemoveFromXRoleInitializer<RemoveFromInformedRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public RemoveFromInformedRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(RemoveFromInformedRole.actionType, actionInitializer);
	}

}
