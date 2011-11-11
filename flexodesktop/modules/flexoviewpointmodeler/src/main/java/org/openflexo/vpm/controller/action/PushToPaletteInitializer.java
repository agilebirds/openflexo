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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.action.PushToPalette;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.controller.CEDController;

public class PushToPaletteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	PushToPaletteInitializer(CEDControllerActionInitializer actionInitializer) {
		super(PushToPalette.actionType, actionInitializer);
	}

	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() {
		return (CEDControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public CEDController getController() {
		return (CEDController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<PushToPalette> getDefaultInitializer() {
		return new FlexoActionInitializer<PushToPalette>() {
			@Override
			public boolean run(ActionEvent e, PushToPalette action) {
				FIBDialog dialog = FIBDialog.instanciateComponent(CEDCst.PUSH_TO_PALETTE_DIALOG_FIB, action, null, true);
				if (dialog.getStatus() == Status.VALIDATED) {
					GraphicalRepresentation gr = ((GraphicalRepresentation) action.getFocusedObject().getGraphicalRepresentation());
					if (gr instanceof ShapeGraphicalRepresentation) {
						action.graphicalRepresentation = new ShapeGraphicalRepresentation();
						((ShapeGraphicalRepresentation) action.graphicalRepresentation).setsWith(gr);
					} else if (gr instanceof ConnectorGraphicalRepresentation) {
						action.graphicalRepresentation = new ConnectorGraphicalRepresentation();
						((ConnectorGraphicalRepresentation) action.graphicalRepresentation).setsWith(gr);
					}
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<PushToPalette> getDefaultFinalizer() {
		return new FlexoActionFinalizer<PushToPalette>() {
			@Override
			public boolean run(ActionEvent e, PushToPalette action) {
				getController().setCurrentEditedObjectAsModuleView(action.palette, getController().VIEW_POINT_PERSPECTIVE);
				getController().getSelectionManager().setSelectedObject(action.getNewPaletteElement());
				return true;
			}
		};
	}

}
