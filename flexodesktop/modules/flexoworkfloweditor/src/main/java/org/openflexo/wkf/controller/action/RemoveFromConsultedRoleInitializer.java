package org.openflexo.wkf.controller.action;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.action.RemoveFromConsultedRole;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RemoveFromConsultedRoleInitializer extends RemoveFromXRoleInitializer<RemoveFromConsultedRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public RemoveFromConsultedRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(RemoveFromConsultedRole.actionType, actionInitializer);
	}

}
