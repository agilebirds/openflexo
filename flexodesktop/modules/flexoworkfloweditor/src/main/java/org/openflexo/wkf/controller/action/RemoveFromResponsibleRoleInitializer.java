package org.openflexo.wkf.controller.action;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.action.RemoveFromResponsibleRole;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RemoveFromResponsibleRoleInitializer extends RemoveFromXRoleInitializer<RemoveFromResponsibleRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public RemoveFromResponsibleRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(RemoveFromResponsibleRole.actionType, actionInitializer);
	}

}
