package org.openflexo.wkf.controller.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddToConsultedRole;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddToConsultedRoleInitializer extends AddToXRoleInitializer<AddToConsultedRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public AddToConsultedRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddToConsultedRole.actionType, actionInitializer);
	}

	@Override
	public Vector<Role> getAvailableRoles(AbstractActivityNode activity) {
		return activity.getAvailableConsultedRoles();
	}


}
