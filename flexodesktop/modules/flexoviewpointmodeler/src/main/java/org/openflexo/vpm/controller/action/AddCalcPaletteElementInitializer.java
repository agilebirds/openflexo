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

import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.vpm.controller.CEDController;
import org.openflexo.vpm.palette.PaletteElementGR;


import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.action.AddCalcPaletteElement;

public class AddCalcPaletteElementInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddCalcPaletteElementInitializer(CEDControllerActionInitializer actionInitializer)
	{
		super(AddCalcPaletteElement.actionType,actionInitializer);
	}

	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() 
	{
		return (CEDControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddCalcPaletteElement> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddCalcPaletteElement>() {
			@Override
			public boolean run(ActionEvent e, AddCalcPaletteElement action)
			{
				/*if (action.getNewElementName() != null && (action.getFocusedObject() != null))
					return true;*/

				action.setNewElementName(FlexoController.askForString(FlexoLocalization.localizedForKey("name_for_new_element")));
				if (action.getGraphicalRepresentation() == null)
					action.setGraphicalRepresentation(makePaletteElementGraphicalRepresentation(ShapeType.RECTANGLE));
				return true;
			}
		};
	}
	
	protected PaletteElementGR makePaletteElementGraphicalRepresentation(ShapeType st)
	{
		final PaletteElementGR gr 
		= new PaletteElementGR(null,null);
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
	protected FlexoActionFinalizer<AddCalcPaletteElement> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddCalcPaletteElement>() {
			@Override
			public boolean run(ActionEvent e, AddCalcPaletteElement action)
			{
				((CEDController)getController()).getSelectionManager().setSelectedObject(action.getNewElement());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return VEIconLibrary.SHAPE_ICON;
	}


}
