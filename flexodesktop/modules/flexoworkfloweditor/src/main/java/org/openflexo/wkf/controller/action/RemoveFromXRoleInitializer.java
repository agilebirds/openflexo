package org.openflexo.wkf.controller.action;

import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.RemoveFromXRole;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public abstract class RemoveFromXRoleInitializer<R extends RemoveFromXRole<R>> extends ActionInitializer<R, Role, AbstractActivityNode> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RemoveFromXRoleInitializer(FlexoActionType<R, Role, AbstractActivityNode> actionType, WKFControllerActionInitializer actionInitializer) {
		super(actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

}
