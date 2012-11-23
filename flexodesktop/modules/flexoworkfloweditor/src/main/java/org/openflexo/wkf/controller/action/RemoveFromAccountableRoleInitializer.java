package org.openflexo.wkf.controller.action;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.action.RemoveFromAccountableRole;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RemoveFromAccountableRoleInitializer extends RemoveFromXRoleInitializer<RemoveFromAccountableRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public RemoveFromAccountableRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(RemoveFromAccountableRole.actionType, actionInitializer);
	}

}
