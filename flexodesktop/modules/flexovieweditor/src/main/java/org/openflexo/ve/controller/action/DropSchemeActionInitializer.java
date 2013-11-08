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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.controller.VEController;
import org.openflexo.ve.diagram.DiagramModuleView;
import org.openflexo.ve.widget.ParametersRetriever;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DropSchemeActionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DropSchemeActionInitializer(VEControllerActionInitializer actionInitializer) {
		super(DropSchemeAction.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DropSchemeAction> getDefaultInitializer() {
		return new FlexoActionInitializer<DropSchemeAction>() {
			@Override
			public boolean run(EventObject e, DropSchemeAction action) {
				return ParametersRetriever.retrieveParameters(action, action.escapeParameterRetrievingWhenValid);

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DropSchemeAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DropSchemeAction>() {
			@Override
			public boolean run(EventObject e, DropSchemeAction action) {
				/*	DiagramShape shape = action.getPrimaryShape();
					logger.info("border5 = " + ((ShapeGraphicalRepresentation) shape.getGraphicalRepresentation()).getBorder());
					if (shape.getParent() != action.getParent()) {
						DiagramShapeGR parentNode = (DiagramShapeGR) shape.getParent().getGraphicalRepresentation();
						DiagramShapeGR expectedGR = (DiagramShapeGR) action.getParent().getGraphicalRepresentation();
						DiagramShapeGR myGR = (DiagramShapeGR) action.getPrimaryShape().getGraphicalRepresentation();
						Point p = new Point((int) myGR.getX(), (int) myGR.getY());
						Point newP = GraphicalRepresentation.convertPoint(expectedGR, p, parentNode, 1.0);
						myGR.setLocation(new FGEPoint(newP.x, newP.y));
						logger.info("border6 = " + myGR.getBorder());
						logger.info("ShapeSpecification has been relocated");
					}*/

				((VEController) getController()).getSelectionManager().setSelectedObject(action.getPrimaryShape());
				if (action.getPrimaryShape() != null) {
					ModuleView<?> moduleView = getController().moduleViewForObject(action.getPrimaryShape().getDiagram(), false);
					if (moduleView instanceof DiagramModuleView) {
						ShapeView<DiagramShape> shape = ((DiagramModuleView) moduleView).getController().getDrawingView()
								.shapeViewForObject(action.getPrimaryShape().getGraphicalRepresentation());
						if (shape != null) {
							if (shape.getLabelView() != null) {
								shape.getLabelView().continueEdition();
							}
						}

					}
				}

				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<DropSchemeAction> getDefaultExceptionHandler() {
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
	protected Icon getEnabledIcon() {
		return VEIconLibrary.SHAPE_ICON;
	}

}
