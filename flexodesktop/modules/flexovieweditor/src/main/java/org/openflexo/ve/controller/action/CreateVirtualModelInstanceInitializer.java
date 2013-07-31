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

import java.io.File;
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
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance.CreateConcreteVirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.VECst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateVirtualModelInstanceInitializer extends ActionInitializer<CreateConcreteVirtualModelInstance, View, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateVirtualModelInstanceInitializer(VEControllerActionInitializer actionInitializer) {
		super(CreateVirtualModelInstance.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	private Status chooseVirtualModel(CreateConcreteVirtualModelInstance action) {
		return instanciateShowDialogAndReturnStatus(action, VECst.CREATE_VIRTUAL_MODEL_INSTANCE_DIALOG_FIB);
	}

	@Override
	protected FlexoActionInitializer<CreateConcreteVirtualModelInstance> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateConcreteVirtualModelInstance>() {
			@Override
			public boolean run(EventObject e, CreateConcreteVirtualModelInstance action) {
				if (action.skipChoosePopup) {
					return true;
				} else {
					int step = 0;
					boolean shouldContinue = true;
					while (shouldContinue) {
						Status result;
						if (step == 0) {
							result = instanciateShowDialogAndReturnStatus(action, VECst.CREATE_VIRTUAL_MODEL_INSTANCE_DIALOG_FIB);
						} else {
							ModelSlot configuredModelSlot = action.getVirtualModel().getModelSlots().get(step - 1);
							result = instanciateShowDialogAndReturnStatus(action.getModelSlotInstanceConfiguration(configuredModelSlot),
									getModelSlotInstanceConfigurationFIB(configuredModelSlot.getClass()));
						}
						if (result == Status.CANCELED) {
							return false;
						} else if (result == Status.VALIDATED) {
							return true;
						} else if (result == Status.NEXT && step + 1 <= action.getVirtualModel().getModelSlots().size()) {
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
	protected FlexoActionFinalizer<CreateConcreteVirtualModelInstance> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateConcreteVirtualModelInstance>() {
			@Override
			public boolean run(EventObject e, CreateConcreteVirtualModelInstance action) {
				// getController().setCurrentEditedObjectAsModuleView(action.getNewVirtualModelInstance());
				getController().selectAndFocusObject(action.getNewVirtualModelInstance());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateConcreteVirtualModelInstance> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateConcreteVirtualModelInstance>() {
			@Override
			public boolean handleException(FlexoException exception, CreateConcreteVirtualModelInstance action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_virtual_model_with_this_name_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VEIconLibrary.VIRTUAL_MODEL_INSTANCE_ICON;
	}

	/**
	 * @author Vincent This method has to be removed as soon as we will have a real Wizard Management. Its purpose is to handle the
	 *         separation of FIBs for Model Slot Configurations.
	 * @return File that correspond to the FIB
	 */
	private File getModelSlotInstanceConfigurationFIB(Class modelSlotClass) {
		if (TypeAwareModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return VECst.CONFIGURE_TYPESAFE_MODEL_SLOT_INSTANCE_DIALOG_FIB;
		}
		if (FreeModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return VECst.CONFIGURE_FREE_MODEL_SLOT_INSTANCE_DIALOG_FIB;
		}
		if (VirtualModelModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return VECst.CONFIGURE_VIRTUAL_MODEL_SLOT_INSTANCE_DIALOG_FIB;
		}
		return null;
	}
}
