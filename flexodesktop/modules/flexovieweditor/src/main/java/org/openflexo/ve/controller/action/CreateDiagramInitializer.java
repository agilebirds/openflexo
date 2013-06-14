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
package org.openflexo.ve.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.action.CreateDiagram;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.VECst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateDiagramInitializer extends ActionInitializer<CreateDiagram, View, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDiagramInitializer(VEControllerActionInitializer actionInitializer) {
		super(CreateDiagram.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	private Status chooseVirtualModel(CreateDiagram action) {
		return instanciateShowDialogAndReturnStatus(action, VECst.CREATE_DIAGRAM_DIALOG_FIB);
	}

	private Status chooseAndConfigureCreationScheme(CreateDiagram action) {
		return instanciateShowDialogAndReturnStatus(action, VECst.CHOOSE_AND_CONFIGURE_CREATION_SCHEME_DIALOG_FIB);
	}

	private Status configureModelSlot(CreateDiagram action, ModelSlot configuredModelSlot) {
		return instanciateShowDialogAndReturnStatus(action.getModelSlotInstanceConfiguration(configuredModelSlot),
				VECst.CONFIGURE_MODEL_SLOT_INSTANCE_DIALOG_FIB);
	}

	@Override
	protected FlexoActionInitializer<CreateDiagram> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDiagram>() {
			@Override
			public boolean run(EventObject e, CreateDiagram action) {
				if (action.skipChoosePopup) {
					return true;
				} else {
					int step = 0;
					boolean shouldContinue = true;
					while (shouldContinue) {
						Status result;
						if (step == 0) {
							result = chooseVirtualModel(action);
						} else if (step == action.getStepsNumber() - 1 && action.getDiagramSpecification() != null
								&& action.getDiagramSpecification().hasCreationScheme()) {
							result = chooseAndConfigureCreationScheme(action);
						} else {
							ModelSlot configuredModelSlot = action.getVirtualModel().getModelSlots().get(step - 1);
							result = configureModelSlot(action, configuredModelSlot);
						}
						if (result == Status.CANCELED) {
							return false;
						} else if (result == Status.VALIDATED) {
							return true;
						} else if (result == Status.NEXT && step + 1 < action.getStepsNumber()) {
							step = step + 1;
						} else if (result == Status.BACK && step - 1 >= 0) {
							step = step - 1;
						}
					}

					return instanciateAndShowDialog(action, VECst.CREATE_VIRTUAL_MODEL_INSTANCE_DIALOG_FIB);
				}

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDiagram> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDiagram>() {
			@Override
			public boolean run(EventObject e, CreateDiagram action) {
				// getController().setCurrentEditedObjectAsModuleView(action.getNewDiagram());
				getController().selectAndFocusObject(action.getNewDiagram());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateDiagram> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateDiagram>() {
			@Override
			public boolean handleException(FlexoException exception, CreateDiagram action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_diagram_with_this_name_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VEIconLibrary.DIAGRAM_ICON;
	}

}
