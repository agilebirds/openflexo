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
package org.openflexo.oe.controller.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.oe.controller.OEController;
import org.openflexo.oe.shema.OEShapeGR;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.action.DropSchemeAction;
import org.openflexo.foundation.ontology.shema.OEShape;


public class DropSchemeActionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DropSchemeActionInitializer(OEControllerActionInitializer actionInitializer)
	{
		super(DropSchemeAction.actionType,actionInitializer);
	}

	@Override
	protected OEControllerActionInitializer getControllerActionInitializer() 
	{
		return (OEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DropSchemeAction> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DropSchemeAction>() {
			@Override
			public boolean run(ActionEvent e, DropSchemeAction action)
			{
				return ParametersRetriever.retrieveParameters(action,action.escapeParameterRetrievingWhenValid);
				
			}
		};
	}

	
	@Override
	protected FlexoActionFinalizer<DropSchemeAction> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DropSchemeAction>() {
			@Override
			public boolean run(ActionEvent e, DropSchemeAction action)
			{
				OEShape shape = action.getNewShape();
				if (shape.getParent() != action.getParent()) {
					OEShapeGR parentGR = (OEShapeGR)shape.getParent().getGraphicalRepresentation();
					OEShapeGR expectedGR = (OEShapeGR)action.getParent().getGraphicalRepresentation();
					OEShapeGR myGR = (OEShapeGR)action.getNewShape().getGraphicalRepresentation();
					Point p = new Point((int)myGR.getX(),(int)myGR.getY());
					Point newP = GraphicalRepresentation.convertPoint(expectedGR, p, parentGR, 1.0);
					myGR.setLocation(new FGEPoint(newP.x,newP.y));
					logger.info("Shape has been relocated");
				}
				
				((OEController)getController()).getSelectionManager().setSelectedObject(action.getNewShape());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<DropSchemeAction> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<DropSchemeAction>() {
			@Override
			public boolean handleException(FlexoException exception, DropSchemeAction action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				return false;
			}
		};
	}


	@Override
	protected Icon getEnabledIcon() 
	{
		return VEIconLibrary.OE_SHAPE_ICON;
	}


}
