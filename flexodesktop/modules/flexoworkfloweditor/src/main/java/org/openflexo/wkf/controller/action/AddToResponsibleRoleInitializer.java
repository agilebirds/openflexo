package org.openflexo.wkf.controller.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddToResponsibleRole;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddToResponsibleRoleInitializer extends AddToXRoleInitializer<AddToResponsibleRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public AddToResponsibleRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddToResponsibleRole.actionType, actionInitializer);
	}

	@Override
	public Vector<Role> getAvailableRoles(AbstractActivityNode activity) {
		return activity.getAvailableResponsibleRoles();
	}

}
