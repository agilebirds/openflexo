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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.dm.DuplicateEntityName;
import org.openflexo.foundation.ie.cl.DuplicateComponentName;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.popups.AskNewComponentDialog;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddComponentInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddComponentInitializer(IEControllerActionInitializer actionInitializer) {
		super(AddComponent.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddComponent> getDefaultInitializer() {
		return new FlexoActionInitializer<AddComponent>() {
			@Override
			public boolean run(ActionEvent e, AddComponent action) {
				if (action.getNewComponentName() != null && action.getComponentType() != null && action.getFolder() != null) {
					return true;
				}

				FlexoComponentFolder folder = action.getFolder();
				if (folder != null) {
					AskNewComponentDialog dialog = new AskNewComponentDialog(folder.getProject(), folder);
					if (dialog.hasBeenValidated()) {
						action.setNewComponentName(dialog.getNewComponentName());
						action.setComponentType(dialog.getComponentType());
						if (action.getComponentType() == AddComponent.ComponentType.DATA_COMPONENT) {
							action.setDataComponentEntity(dialog.getDataComponentEntity());
						} else if (action.getComponentType() == AddComponent.ComponentType.MONITORING_SCREEN) {
							action.setRelatedProcess(dialog.getMonitoringScreenProcess());
						} else if (action.getComponentType() == AddComponent.ComponentType.MONITORING_COMPONENT) {
							action.setRelatedProcess(dialog.getMonitoringComponentProcess());
						}
						return true;
					}
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddComponent> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddComponent>() {
			@Override
			public boolean run(ActionEvent e, AddComponent action) {
				if (action.getNewComponent() != null) {
                    ExternalIEModule ieModule = null;
                    try {
                        ieModule = getModuleLoader().getIEModule(getProject());
                    } catch (ModuleLoadingException e1) {
                        logger.warning("Cannot load IE module."+e1.getMessage());
                    }
                    if (ieModule == null) {
						return false;
					}
					ieModule.focusOn();
					ieModule.showScreenInterface(action.getNewComponent().getDummyComponentInstance());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddComponent> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddComponent>() {
			@Override
			public boolean handleException(FlexoException exception, AddComponent action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_component_with_this_name_already_exists"));
					return true;
				}
				if (exception instanceof DuplicateComponentName) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_component_with_this_name_already_exists"));
					return true;
				}
				if (exception instanceof DuplicateEntityName) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_an_entity_with_this_name_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SEIconLibrary.OPERATION_COMPONENT_ICON;
	}

}
