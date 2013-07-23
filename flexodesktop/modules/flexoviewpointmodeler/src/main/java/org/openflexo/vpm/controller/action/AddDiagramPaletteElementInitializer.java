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

import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.action.AddDiagramPaletteElement;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.diagrampalette.DiagramPaletteElementGR;

public class AddDiagramPaletteElementInitializer extends ActionInitializer<AddDiagramPaletteElement, DiagramPalette, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddDiagramPaletteElementInitializer(VPMControllerActionInitializer actionInitializer) {
		super(AddDiagramPaletteElement.actionType, actionInitializer);
	}

	@Override
	protected VPMControllerActionInitializer getControllerActionInitializer() {
		return (VPMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddDiagramPaletteElement> getDefaultInitializer() {
		return new FlexoActionInitializer<AddDiagramPaletteElement>() {
			@Override
			public boolean run(EventObject e, AddDiagramPaletteElement action) {
				/*if (action.getNewElementName() != null && (action.getFocusedObject() != null))
					return true;*/

				action.setNewElementName(FlexoController.askForString(FlexoLocalization.localizedForKey("name_for_new_element")));
				if (action.getGraphicalRepresentation() == null) {
					action.setGraphicalRepresentation(makePaletteElementGraphicalRepresentation(ShapeType.RECTANGLE));
				}
				return true;
			}
		};
	}

	protected DiagramPaletteElementGR makePaletteElementGraphicalRepresentation(ShapeType st) {
		final DiagramPaletteElementGR gr = new DiagramPaletteElementGR(null, null);
		gr.setShapeType(st);
		gr.setX(100);
		gr.setY(100);
		gr.setWidth(50);
		gr.setHeight(50);
		gr.setIsVisible(true);
		gr.setIsFloatingLabel(false);
		gr.setIsSelectable(true);
		gr.setIsFocusable(true);
		gr.setIsReadOnly(false);
		gr.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		gr.setLayer(1);
		return gr;
	}

	@Override
	protected FlexoActionFinalizer<AddDiagramPaletteElement> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddDiagramPaletteElement>() {
			@Override
			public boolean run(EventObject e, AddDiagramPaletteElement action) {
				((VPMController) getController()).getSelectionManager().setSelectedObject(action.getNewElement());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VEIconLibrary.SHAPE_ICON;
	}

}
