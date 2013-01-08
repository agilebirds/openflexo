package org.openflexo.wkf.controller.action;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddToAccountableRole;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddToAccountableRoleInitializer extends AddToXRoleInitializer<AddToAccountableRole> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public AddToAccountableRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddToAccountableRole.actionType, actionInitializer);
	}

	@Override
	public List<Role> getAvailableRoles(AbstractActivityNode activity) {
		return activity.getAvailableAccountableRoles();
	}

}
