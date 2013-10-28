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

import java.util.logging.Logger;

import org.openflexo.ve.VECst;
import org.openflexo.ve.controller.VEController;
import org.openflexo.ve.controller.VESelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class VEControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public VEControllerActionInitializer(VEController controller) {
		super(controller);
	}

	protected VEController getVEController() {
		return (VEController) getController();
	}

	protected VESelectionManager getVESelectionManager() {
		return (VESelectionManager) getVEController().getSelectionManager();
	}

	@Override
	public void initializeActions() {

		super.initializeActions();

		new VESetPropertyInitializer(this);
		new VESetPropertyInitializer(this);

		// Disabled copy/paste
		if (VECst.CUT_COPY_PASTE_ENABLED) {
			new VECopyInitializer(this);
			new VECutInitializer(this);
			new VEPasteInitializer(this);
			new VESelectAllInitializer(this);
		}

		// View library perspective
		new CreateViewInitializer(this);
		new MoveViewInitializer(this);

		new DeleteViewInitializer(this);
		new CreateVirtualModelInstanceInitializer(this);
		new DeleteVirtualModelInstanceInitializer(this);

		// Diagram perspective
		new CreateDiagramInitializer(this);
		new DeleteDiagramInitializer(this);
		new AddShapeInitializer(this);
		new AddConnectorInitializer(this);
		new DeleteDiagramElementsInitializer(this);
		new DropSchemeActionInitializer(this);
		new LinkSchemeActionInitializer(this);
		new ActionSchemeActionInitializer(this);
		new SynchronizationSchemeActionInitializer(this);
		new NavigationSchemeActionInitializer(this);
		new ResetGraphicalRepresentationInitializer(this);
		new DeclareShapeInEditionPatternInitializer(this);
		new DeclareConnectorInEditionPatternInitializer(this);
		new PushToPaletteInitializer(this);
		new ExportDiagramToImageInitializer(this);

	}

}
