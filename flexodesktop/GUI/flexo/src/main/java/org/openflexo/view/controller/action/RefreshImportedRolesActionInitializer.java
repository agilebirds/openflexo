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
package org.openflexo.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.imported.action.RefreshImportedProcessAction;
import org.openflexo.foundation.imported.action.RefreshImportedRoleAction;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;


public class RefreshImportedRolesActionInitializer extends ActionInitializer
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RefreshImportedProcessesActionInitializer.class.getPackage().getName());

	public RefreshImportedRolesActionInitializer(ControllerActionInitializer actionInitializer)
	{
		super(RefreshImportedRoleAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<RefreshImportedRoleAction> getDefaultInitializer()
	{
		return new FlexoActionInitializer<RefreshImportedRoleAction>()
		{
			@Override
			public boolean run(ActionEvent e, RefreshImportedRoleAction action)
			{
				
				PPMWebServiceClient client = getController().getWSClient();
				if(client!=null){
					action.setWebService(client.getWebService_PortType());
					action.setLogin(client.getLogin());
					action.setMd5Password(client.getEncriptedPWD());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<RefreshImportedProcessAction> getDefaultExceptionHandler() {
		return new FlexoWSExceptionHandler<RefreshImportedProcessAction>(getController());
	}
	
	@Override
	protected FlexoActionFinalizer<RefreshImportedRoleAction> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<RefreshImportedRoleAction>()
		{
			@Override
			public boolean run(ActionEvent e, RefreshImportedRoleAction action)
			{
				if (!action.isAutomaticAction()) {
					FlexoController.notify(action.getReport());
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon()
	{
		return IconFactory.getImageIcon(WKFIconLibrary.ROLE_ICON, new IconMarker[]{IconLibrary.IMPORT});
	}

}
