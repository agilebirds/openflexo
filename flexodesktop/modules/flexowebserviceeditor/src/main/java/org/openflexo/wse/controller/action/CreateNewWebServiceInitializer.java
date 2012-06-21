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
package org.openflexo.wse.controller.action;

import java.io.File;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.FileSelectorParameter;
import org.openflexo.foundation.param.InfoLabelParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.ServiceInterfaceSelectorParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.param.WSServiceParameter;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.DefaultServiceInterface;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.InternalWSService;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wse.view.WSELibraryView;

public class CreateNewWebServiceInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateNewWebServiceInitializer(WSEControllerActionInitializer actionInitializer) {
		super(CreateNewWebService.actionType, actionInitializer);
	}

	@Override
	protected WSEControllerActionInitializer getControllerActionInitializer() {
		return (WSEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateNewWebService> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateNewWebService>() {
			@Override
			public boolean run(EventObject e, CreateNewWebService action) {
				final ParameterDefinition[] params = new ParameterDefinition[9];

				RadioButtonMode EXTERNAL_WS = new RadioButtonMode(new FileResource("Resources/WS/SmallWSIcon.gif"),
						FlexoLocalization.localizedForKey("import_external_ws"));
				RadioButtonMode INTERNAL_WS = new RadioButtonMode(new FileResource("Resources/WS/SmallWSIcon.gif"),
						FlexoLocalization.localizedForKey("export_internal_ws"));
				Vector modes = new Vector();
				modes.add(EXTERNAL_WS);
				modes.add(INTERNAL_WS);
				params[0] = new RadioButtonListParameter("mode", "select_a_choice", EXTERNAL_WS, modes);
				params[0].addParameter("displayIcon", "true");
				params[0].addParameter("icon", "iconFile");
				params[0].addParameter("format", "name");

				params[1] = new TextFieldParameter("externalWSGroupName", "ws_name_of_wsgroup", "MyService");
				params[1].setDepends("mode,selectedWSDLFile");
				params[1].setConditional("mode=" + '"' + EXTERNAL_WS + '"');

				params[2] = new FileSelectorParameter("selectedWSDLFile", "wsdl_file", ResourceLocator.getUserHomeDirectory()) {
					// override setValue to setThe wsgroupName with the name of
					// the wsdl file !
					@Override
					public void setValue(File aValue) {
						super.setValue(aValue);
						String newSelectedName = (aValue).getName();
						String newName = null;
						if (newSelectedName.indexOf(".") > 0) {
							newName = newSelectedName.substring(0, newSelectedName.indexOf("."));
						} else {
							newName = newSelectedName;
						}
						params[1].setValue(newName);
					}
				};
				params[2].setDepends("mode");
				params[2].setConditional("mode=" + '"' + EXTERNAL_WS + '"');

				String EXISTING_INTERNAL_WSGROUP = FlexoLocalization.localizedForKey("existing_internal_wsgroup");
				String NEW_INTERNAL_WSGROUP = FlexoLocalization.localizedForKey("new_internal_wsgroup");
				String[] internalModes = { EXISTING_INTERNAL_WSGROUP, NEW_INTERNAL_WSGROUP };
				params[3] = new RadioButtonListParameter<String>("internalMode", "select_add_to_wsgroup_type", NEW_INTERNAL_WSGROUP,
						internalModes);
				params[3].setDepends("mode");
				params[3].setConditional("mode=" + '"' + INTERNAL_WS + '"');

				params[4] = new TextFieldParameter("internalWSGroupName", "ws_name_of_wsgroup", "my service");
				params[4].setDepends("internalMode,mode");
				params[4].setConditional("internalMode=" + '"' + NEW_INTERNAL_WSGROUP + '"' + " AND mode=" + '"' + INTERNAL_WS + '"');

				params[5] = new WSServiceParameter("internalWSGroup", "ws_group", null);
				params[5].setDepends("mode,internalMode");
				params[5].setConditional("mode=" + '"' + INTERNAL_WS + '"' + " AND internalMode=" + '"' + EXISTING_INTERNAL_WSGROUP + '"');
				params[5].addParameter("wsGroupType", "INTERNAL");

				params[6] = new ServiceInterfaceSelectorParameter("processInterface", "ws_process_interface", null);
				params[6].setDepends("mode");
				params[6].setConditional("mode=" + '"' + INTERNAL_WS + '"');
				params[7] = new InfoLabelParameter("infoLabelExt", "description",
						FlexoLocalization.localizedForKey("ws_create_external_ws_description"));
				params[7].setDepends("mode");
				params[7].setConditional("mode=" + '"' + EXTERNAL_WS + '"');
				params[8] = new InfoLabelParameter("infoLabelInt", "description",
						FlexoLocalization.localizedForKey("ws_create_internal_ws_description"));
				params[8].setDepends("mode");
				params[8].setConditional("mode=" + '"' + INTERNAL_WS + '"');

				while (true) {
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("create_new_web_service"),
							FlexoLocalization.localizedForKey("what_would_you_like_to_do"), params);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setProject(getProject());
						if (dialog.parameterValueWithName("mode").equals(EXTERNAL_WS)) {
							String externalName = (String) dialog.parameterValueWithName("externalWSGroupName");
							File wsdlFile = (File) dialog.parameterValueWithName("selectedWSDLFile");
							action.setNewWebServiceName(externalName);
							action.setWsdlFile(wsdlFile);
							action.setWebServiceType(CreateNewWebService.EXTERNAL_WS);
							if (!wsdlFile.exists() || wsdlFile.isDirectory()) {
								FlexoController.notify(FlexoLocalization.localizedForKey("selected_file_does_not_exist"));
								continue;
							}
							if (externalName == null || externalName.trim().length() == 0) {
								FlexoController.notify(FlexoLocalization.localizedForKey("enter_a_name_for_the_new_webservice"));
								continue;
							}
							return true;
						} else if (dialog.parameterValueWithName("mode").equals(INTERNAL_WS)) {
							action.setWebServiceType(CreateNewWebService.INTERNAL_WS);
							WKFObject processInterface = (WKFObject) dialog.parameterValueWithName("processInterface");

							if (processInterface instanceof DefaultServiceInterface) {
								action.setPortRegistry(((DefaultServiceInterface) processInterface).getPortRegistery());
							} else if (processInterface instanceof PortRegistery) {
								action.setPortRegistry((PortRegistery) processInterface);
							} else if (processInterface instanceof FlexoProcess) {
								action.setPortRegistry(((FlexoProcess) processInterface).getPortRegistery());
							} else if (processInterface instanceof ServiceInterface) {
								action.setServiceInterface((ServiceInterface) processInterface);
							}

							if (dialog.parameterValueWithName("internalMode").equals(NEW_INTERNAL_WSGROUP)) {
								String internalName = (String) dialog.parameterValueWithName("internalWSGroupName");
								if (internalName == null || internalName.trim().length() == 0) {
									FlexoController.notify(FlexoLocalization.localizedForKey("enter_a_name_for_the_new_webservice_group"));
									continue;
								}
								if (logger.isLoggable(Level.INFO)) {
									logger.info("internal ws new name:" + internalName);
								}
								action.setNewWebServiceName(internalName);

							} else if (dialog.parameterValueWithName("internalMode").equals(EXISTING_INTERNAL_WSGROUP)) {
								WSService group = (WSService) dialog.parameterValueWithName("internalWSGroup");
								if (logger.isLoggable(Level.INFO)) {
									logger.info("existing internal ws name:" + group.getName());
								}
								action.setNewWebServiceName(group.getName());
							}

							return true;
						}
					}
					// CANCELLED
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateNewWebService> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateNewWebService>() {
			@Override
			public boolean run(EventObject e, CreateNewWebService action) {
				if (action.getNewWebService() == null) {
					return false;
				}
				logger.info("Finalizer for CreateNewWebService in WSLibraryView with " + action.getNewWebService());
				if (getControllerActionInitializer().getWSEController().getCurrentDisplayedObjectAsModuleView() == action
						.getNewWebService().getWSLibrary()) {
					WSELibraryView libraryView = (WSELibraryView) getControllerActionInitializer().getWSEController()
							.getCurrentModuleView();
					if (action.getNewWebService() instanceof ExternalWSService) {
						libraryView.getWSFoldersTable().selectObject(action.getNewWebService().getWSLibrary().getExternalWSFolder());
						libraryView.getWSServicesTable().selectObject(action.getNewWebService());
					} else if (action.getNewWebService() instanceof InternalWSService) {
						// do something
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateNewWebService> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateNewWebService>() {
			@Override
			public boolean handleException(FlexoException exception, CreateNewWebService action) {
				if (action instanceof CreateNewWebService && (action).getWebServiceType().equals(CreateNewWebService.EXTERNAL_WS)) {
					if (exception.getLocalizationKey() != null) {
						FlexoController.showError(FlexoLocalization.localizedForKey("ws_import_interupted") + " : "
								+ FlexoLocalization.localizedForKey((exception).getLocalizationKey()));
						return true;
					} else {
						FlexoController.showError(FlexoLocalization.localizedForKey("ws_import_interupted_exception_raised"));
						return true;
					}

				} else if (action instanceof CreateNewWebService && (action).getWebServiceType().equals(CreateNewWebService.INTERNAL_WS)) {
					if (exception.getLocalizationKey() != null) {
						FlexoController.showError(FlexoLocalization.localizedForKey("ws_add_service_not_completed") + " : "
								+ FlexoLocalization.localizedForKey((exception).getLocalizationKey()));
						return true;
					} else {
						FlexoController.showError(FlexoLocalization.localizedForKey("ws_add_service_not_completed_exception_raised"));
						return true;
					}
				}
				return false;
			}
		};
	}

	public static class RadioButtonMode extends FlexoObject {
		public File iconFile;

		public String name;

		public RadioButtonMode(File iconFile, String name) {
			this.iconFile = iconFile;
			this.name = name;
		}
	};

}
