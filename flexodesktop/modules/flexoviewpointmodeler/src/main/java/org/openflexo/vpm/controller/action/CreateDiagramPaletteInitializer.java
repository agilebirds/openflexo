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

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.view.diagram.viewpoint.action.CreateDiagramPalette;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.VPMCst;
import org.openflexo.vpm.controller.VPMController;

public class CreateDiagramPaletteInitializer extends ActionInitializer<CreateDiagramPalette, DiagramSpecification, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDiagramPaletteInitializer(VPMControllerActionInitializer actionInitializer) {
		super(CreateDiagramPalette.actionType, actionInitializer);
	}

	@Override
	protected VPMControllerActionInitializer getControllerActionInitializer() {
		return (VPMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public VPMController getController() {
		return (VPMController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<CreateDiagramPalette> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDiagramPalette>() {
			@Override
			public boolean run(EventObject e, CreateDiagramPalette action) {
				FGEModelFactory factory;
				try {
					factory = new FGEModelFactoryImpl();
					action.graphicalRepresentation = makePaletteGraphicalRepresentation(factory);
					return instanciateAndShowDialog(action, VPMCst.CREATE_PALETTE_DIALOG_FIB);
				} catch (ModelDefinitionException e1) {
					e1.printStackTrace();
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDiagramPalette> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDiagramPalette>() {
			@Override
			public boolean run(EventObject e, CreateDiagramPalette action) {
				getController().setCurrentEditedObjectAsModuleView(action.getNewPalette(), getController().VIEW_POINT_PERSPECTIVE);
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.VIEWPOINT_ICON;
	}

	protected DrawingGraphicalRepresentation makePaletteGraphicalRepresentation(FGEModelFactory factory) {
		DrawingGraphicalRepresentation gr = factory.makeDrawingGraphicalRepresentation();
		gr.setDrawWorkingArea(true);
		gr.setWidth(260);
		gr.setHeight(300);
		gr.setIsVisible(true);
		return gr;
	}

}
