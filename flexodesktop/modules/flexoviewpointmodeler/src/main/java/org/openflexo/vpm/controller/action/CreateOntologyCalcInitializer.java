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
package org.openflexo.vpm.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.action.CreateViewPoint;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.controller.CEDController;

public class CreateOntologyCalcInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateOntologyCalcInitializer(CEDControllerActionInitializer actionInitializer) {
		super(CreateViewPoint.actionType, actionInitializer);
	}

	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() {
		return (CEDControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateViewPoint> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateViewPoint>() {
			@Override
			public boolean run(ActionEvent e, CreateViewPoint action) {
				FIBDialog dialog = FIBDialog.instanciateComponent(CEDCst.CREATE_VIEW_POINT_DIALOG_FIB, action, null, true);
				return (dialog.getStatus() == Status.VALIDATED);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateViewPoint> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateViewPoint>() {
			@Override
			public boolean run(ActionEvent e, CreateViewPoint action) {
				((CEDController) getController()).getSelectionManager().setSelectedObject(action.getNewCalc());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.CALC_ICON;
	}

}
