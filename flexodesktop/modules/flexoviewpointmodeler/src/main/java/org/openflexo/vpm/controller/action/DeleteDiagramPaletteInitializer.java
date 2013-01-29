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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.view.diagram.action.DeleteDiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.controller.VPMController;

public class DeleteDiagramPaletteInitializer extends ActionInitializer<DeleteDiagramPalette, DiagramPalette, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteDiagramPaletteInitializer(VPMControllerActionInitializer actionInitializer) {
		super(DeleteDiagramPalette.actionType, actionInitializer);
	}

	@Override
	protected VPMControllerActionInitializer getControllerActionInitializer() {
		return (VPMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

	@Override
	public VPMController getController() {
		return (VPMController) super.getController();
	}

	@Override
	protected FlexoActionFinalizer<DeleteDiagramPalette> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteDiagramPalette>() {
			@Override
			public boolean run(EventObject e, DeleteDiagramPalette action) {
				return true;
			}
		};
	}

}
