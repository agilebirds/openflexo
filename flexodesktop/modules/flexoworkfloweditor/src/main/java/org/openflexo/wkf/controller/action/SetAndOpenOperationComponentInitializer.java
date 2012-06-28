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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFExceptionHandler;
import org.openflexo.wkf.view.popups.AskComponentNameDialog;

public class SetAndOpenOperationComponentInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	SetAndOpenOperationComponentInitializer(WKFControllerActionInitializer actionInitializer) {
		super(SetAndOpenOperationComponent.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<SetAndOpenOperationComponent> getDefaultInitializer() {
		return new FlexoActionInitializer<SetAndOpenOperationComponent>() {
			@Override
			public boolean run(ActionEvent e, SetAndOpenOperationComponent action) {
				if (action.getNewComponentName() != null) {
					return true;
				}

				OperationNode operationNode = action.getFocusedObject();
				if (operationNode.isBeginNode() || operationNode.isEndNode()) {
					FlexoController.showError(FlexoLocalization.localizedForKey("cannot_associate_a_component_with_this_node"),
							FlexoLocalization.localizedForKey("begin_and_end_node_cannot_be_associated_with_a_component"));
					return false;
				}
				if (operationNode.getComponentInstance() == null) {
					AskComponentNameDialog newTabDialog = new AskComponentNameDialog(operationNode.getProject().getFlexoComponentLibrary());

					int dialogExitStatus = newTabDialog.getStatus();
					if (dialogExitStatus == AskComponentNameDialog.CANCEL) {
						return false;
					}
					boolean isWOComponentNameValid = IERegExp.JAVA_CLASS_NAME_PATTERN.matcher(newTabDialog.getWOComponentName()).matches();
					while (dialogExitStatus == AskComponentNameDialog.VALIDATE_NEW_COMPONENT && !isWOComponentNameValid) {
						FlexoController.showError(FlexoLocalization
								.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
						newTabDialog.setVisible(true);
						dialogExitStatus = newTabDialog.getStatus();
						if (dialogExitStatus == AskComponentNameDialog.CANCEL) {
							return false;
						}
						isWOComponentNameValid = IERegExp.JAVA_CLASS_NAME_PATTERN.matcher(newTabDialog.getWOComponentName()).matches();
					}
					String woComponentName = newTabDialog.getWOComponentName();

					if (newTabDialog.getStatus() == AskComponentNameDialog.VALIDATE_NEW_COMPONENT) {
						woComponentName = newTabDialog.getWOComponentName();// FlexoController.askForString(FlexoLocalization.localizedForKey("enter_a_component_name_for_the_new_tab"));
						if (!IERegExp.JAVA_CLASS_NAME_PATTERN.matcher(woComponentName).matches()) {
							FlexoController.showError(FlexoLocalization
									.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
							return false;
						}
						action.setNewComponentName(woComponentName);
						return true;
					} else if (newTabDialog.getStatus() == AskComponentNameDialog.VALIDATE_EXISTING_COMPONENT) {
						action.setNewComponentName(newTabDialog.getWOComponentName());
						return true;
					} else if (newTabDialog.getStatus() == AskComponentNameDialog.CANCEL) {
						return false;
					}
				}
				return false;

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SetAndOpenOperationComponent> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SetAndOpenOperationComponent>() {
			@Override
			public boolean run(ActionEvent e, SetAndOpenOperationComponent action) {
				OperationNode operationNode = action.getFocusedObject();
				if (operationNode.getComponentInstance() == null) {
					return false;
				}
				if (operationNode.getComponentInstance().getComponentDefinition() != null && action.hasCreatedComponent()) {
					ExternalIEModule ieModule = null;
					try {
						ieModule = getModuleLoader().getIEModule(getProject());
					} catch (ModuleLoadingException e1) {
						FlexoController.notify("Cannot load Screen Editor. Exception : " + e1.getMessage());
						e1.printStackTrace();
					}
					if (ieModule == null) {
						return false;
					}
					ieModule.focusOn();
					ieModule.showScreenInterface(operationNode.getComponentInstance());
					if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
						SwingUtilities.invokeLater(new SwitchToIEJob());
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<SetAndOpenOperationComponent> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<SetAndOpenOperationComponent>() {
			@Override
			public boolean handleException(FlexoException exception, SetAndOpenOperationComponent action) {
				if (exception instanceof DuplicateResourceException) {
					FlexoController.showError(FlexoLocalization
							.localizedForKey("a_component_with_this_name_already_exists_choose_an_other_one"));
					return true;
				}
				if (exception instanceof OperationAssociatedWithComponentSuccessfully) {
					WKFExceptionHandler.handleAssociation((OperationAssociatedWithComponentSuccessfully) exception,
							getControllerActionInitializer().getWKFController());
					return true;
				}
				return false;
			}
		};
	}

	private class SwitchToIEJob implements Runnable {

		SwitchToIEJob() {
		}

		@Override
		public void run() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Switching to IE");
			}
			try {
				getModuleLoader().switchToModule(Module.IE_MODULE, getController().getProject());
			} catch (ModuleLoadingException e) {
				FlexoController.notify("Cannot load Screen Editor. Exception : " + e.getMessage());
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Switched to IE done!");
			}
		}
	}
}
