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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.ViewDefinition.DuplicateShemaNameException;
import org.openflexo.foundation.view.action.AddView;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.VECst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddViewInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddViewInitializer(OEControllerActionInitializer actionInitializer) {
		super(AddView.actionType, actionInitializer);
	}

	@Override
	protected OEControllerActionInitializer getControllerActionInitializer() {
		return (OEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddView> getDefaultInitializer() {
		return new FlexoActionInitializer<AddView>() {
			@Override
			public boolean run(ActionEvent e, AddView action) {
				if (action.skipChoosePopup) {
					return true;
				} else {
					FIBDialog dialog = FIBDialog.instanciateComponent(VECst.ADD_VIEW_DIALOG_FIB, action, null, true,
							FlexoLocalization.getMainLocalizer());
					return (dialog.getStatus() == Status.VALIDATED);
				}

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddView> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddView>() {
			@Override
			public boolean run(ActionEvent e, AddView action) {
				getController().setCurrentEditedObjectAsModuleView(action.getNewDiagram());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddView> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddView>() {
			@Override
			public boolean handleException(FlexoException exception, AddView action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_view_with_this_name_already_exists"));
					return true;
				}
				if (exception instanceof DuplicateShemaNameException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_view_with_this_name_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VEIconLibrary.VIEW_ICON;
	}

}
