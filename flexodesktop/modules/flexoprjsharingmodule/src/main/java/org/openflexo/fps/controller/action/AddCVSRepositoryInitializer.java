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
package org.openflexo.fps.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.param.IntegerParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.action.AddCVSRepository;
import org.openflexo.fps.action.CVSRefresh;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddCVSRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddCVSRepositoryInitializer(FPSControllerActionInitializer actionInitializer) {
		super(AddCVSRepository.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddCVSRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<AddCVSRepository>() {
			@Override
			public boolean run(ActionEvent e, AddCVSRepository action) {
				TextFieldParameter paramName = new TextFieldParameter("name", "cvs_repository_name",
						(action.getName() == null ? FlexoLocalization.localizedForKey("new_repository") : action.getName()));
				TextFieldParameter hostName = new TextFieldParameter("hostName", "host_name", (action.getHostName() == null ? ""
						: action.getHostName()));
				TextFieldParameter repository = new TextFieldParameter("cvsRepositoryPath", "cvs_repository_path",
						(action.getRepository() == null ? "" : action.getRepository()));
				EnumDropDownParameter<CVSRepository.ConnectionType> connectionTypeParam = new EnumDropDownParameter<CVSRepository.ConnectionType>(
						"connectionType", "connection_type", CVSRepository.ConnectionType.PServer, CVSRepository.ConnectionType.values());
				connectionTypeParam.setShowReset(false);
				IntegerParameter port = new IntegerParameter("port", "port_number", action.getPort());
				port.setDepends("connectionType");
				port.setConditional("connectionType=" + '"' + CVSRepository.ConnectionType.SSH.getStringRepresentation() + '"');
				TextFieldParameter userName = new TextFieldParameter("userName", "user_name", (action.getUserName() == null ? ""
						: action.getUserName()));
				TextFieldParameter passwd = new TextFieldParameter("passwd", "password", (action.getPasswd() == null ? ""
						: action.getPasswd()));
				passwd.setIsPassword(true);
				CheckboxParameter storePasswd = new CheckboxParameter("storePasswd", "store_password", action.getStorePasswd());

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("declare_new_cvs_repository"),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_cvs_repository"), paramName, hostName, port,
						repository, connectionTypeParam, userName, passwd, storePasswd);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setName(paramName.getValue());
					action.setHostName(hostName.getValue());
					action.setRepository(repository.getValue());
					action.setConnectionType(connectionTypeParam.getValue());
					action.setPort(port.getValue());
					action.setUserName(userName.getValue());
					action.setPassword(passwd.getValue());
					action.setStorePasswd(storePasswd.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddCVSRepository> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddCVSRepository>() {
			@Override
			public boolean run(ActionEvent e, AddCVSRepository action) {
				(CVSRefresh.actionType.makeNewEmbeddedAction(action.getNewCVSRepository(), null, action)).doAction();
				return true;
			}
		};
	}

	/*protected FlexoExceptionHandler<AddCVSRepository> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<AddCVSRepository>() {
			public boolean handleException(FlexoException exception, AddCVSRepository action) {
	        	if (exception instanceof FlexoAuthentificationException) {
	        		boolean tryAgain = true;
	        		while (tryAgain) {
	        			ReadOnlyTextFieldParameter userName = new ReadOnlyTextFieldParameter("userName", "user_name", 
	        					(action.getUserName() == null ? "" : action.getUserName()));
	        			TextFieldParameter passwd = new TextFieldParameter("passwd", "password", 
	        					(action.getPasswd() == null ? "" : action.getPasswd()));
	                    passwd.setIsPassword(true);
	                    CheckboxParameter storePasswd = new CheckboxParameter("storePasswd", "store_password",action.getStorePasswd()); 
	                    
	        			AskParametersDialog dialog = new AskParametersDialog(getProject(), FlexoLocalization
	        					.localizedForKey("authentification_failed"), FlexoLocalization
	        					.localizedForKey("reenter_password"), 
	        					userName,passwd,storePasswd);
	        			if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
	       					action.getNewCVSRepository().setUserName(userName.getValue());
	        				if (action.getConnectionType() == ConnectionType.PServer) {
	         					action.getNewCVSRepository().setPassword(passwd.getValue(),true);
	        				}
	        				else if (action.getConnectionType() == ConnectionType.SSH) {
	        					action.getNewCVSRepository().setPassword(passwd.getValue(),false);
	        				}
	      					action.getNewCVSRepository().setStorePassword(storePasswd.getValue());
	      					try {
	        					action.getNewCVSRepository()._retrieveModules();
	        					tryAgain = false;
	        				} catch (CommandAbortedException e) {
	        					tryAgain = false;
	        					e.printStackTrace();
	        				} catch (IOException e) {
	        					tryAgain = false;
	        					e.printStackTrace();
	        				} catch (CommandException e) {
	        					tryAgain = false;
	        					e.printStackTrace();
	        				} catch (AuthenticationException e) {
	        					tryAgain = true;
	        				}
	        			} else {
	        				tryAgain = false;
	        			}
	        		}
	        		return true;
	        	}
	        	return false;
			}
	    };
	}*/

}
