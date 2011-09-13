/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.DeleteRole;
import org.openflexo.foundation.wkf.node.AbstractNode;

public class DeleteRoleInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteRoleInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(DeleteRole.actionType,actionInitializer);
	}
	
	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() 
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<DeleteRole> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DeleteRole>() {
			@Override
			public boolean run(ActionEvent e, DeleteRole action)
			{
				boolean doIt;
				Vector<Role> roleToDelete = action.getRoleToDelete();
				if (roleToDelete.size()==0) return false;
				Vector<AbstractNode> nodes = new Vector<AbstractNode>();
				for (Role r : roleToDelete) 
					nodes.addAll(r.getNodesUsingRole());
				if (nodes.size()>0) {
					StringBuilder sb = new StringBuilder();
					for (AbstractNode node : nodes) {
						sb.append("* ").append(node.getName()).append("\n");
					}
					if (roleToDelete.size() == 1)
						doIt = FlexoController.confirmWithWarning(FlexoLocalization
								.localizedForKey("would_you_like_to_delete_this_role")+"\n"+FlexoLocalization.localizedForKey("it_is_used_in:")+"\n"+sb.toString());
					else doIt = FlexoController.confirmWithWarning(FlexoLocalization
							.localizedForKey("would_you_like_to_delete_these_roles")+"\n"+FlexoLocalization.localizedForKey("they_are_used_in:")+"\n"+sb.toString());
				} else {
					if (roleToDelete.size() == 1)
						doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_role"));
					else doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_these_roles"));

				} 
				return doIt;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteRole> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DeleteRole>() {
			@Override
			public boolean run(ActionEvent e, DeleteRole action)
			{
				if (getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject()!=null && getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject().isDeleted())
					getControllerActionInitializer().getWKFController().getSelectionManager().resetSelection();
                return true;
          }
        };
	}
     
 	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.DELETE_ICON;
	}



}
