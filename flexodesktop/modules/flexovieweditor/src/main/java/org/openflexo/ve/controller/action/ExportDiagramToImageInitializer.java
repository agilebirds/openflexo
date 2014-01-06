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

import java.awt.image.BufferedImage;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.view.FGELayeredView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.diagram.model.action.ExportDiagramToImageAction;
import org.openflexo.ve.controller.VEController;
import org.openflexo.ve.diagram.DiagramController;
import org.openflexo.ve.diagram.DiagramGR;
import org.openflexo.ve.diagram.DiagramModuleView;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class ExportDiagramToImageInitializer extends ActionInitializer<ExportDiagramToImageAction, DiagramElement<?>, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ExportDiagramToImageInitializer(VEControllerActionInitializer actionInitializer) {
		super(ExportDiagramToImageAction.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public VEController getController() {
		return (VEController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<ExportDiagramToImageAction> getDefaultInitializer() {
		return new FlexoActionInitializer<ExportDiagramToImageAction>() {
			@Override
			public boolean run(EventObject e, ExportDiagramToImageAction action) {
				
				if (getController().getCurrentModuleView() instanceof DiagramModuleView)
				{
					DiagramController c = ((DiagramModuleView) getController().getCurrentModuleView()).getController();
					if(action.getFocusedObject().getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) 
					{
						ShapeGraphicalRepresentation gr = (ShapeGraphicalRepresentation) action.getFocusedObject().getGraphicalRepresentation();
						ShapeView shapeView = c.getDrawingView().shapeViewForObject(gr);
						shapeView.captureScreenshot();
						BufferedImage image = shapeView.getScreenshot();
						ShapeBorder b = gr.getBorder();
						ShadowStyle ss = gr.getShadowStyle();
						action.setScreenshot(ScreenshotGenerator.makeImage(image, b.left, b.top,
								(int) gr.getWidth() + (ss.getDrawShadow() ? ss.getShadowBlur() : 0) + 1,
								(int) gr.getHeight() + (ss.getDrawShadow() ? ss.getShadowBlur() : 0) + 1));
					}
					else if(action.getFocusedObject().getGraphicalRepresentation() instanceof DiagramGR){
						DiagramGR gr = (DiagramGR)action.getFocusedObject().getGraphicalRepresentation();
						FGELayeredView view = (FGELayeredView)c.getDrawingView().viewForObject(gr);
						view.captureScreenshot();
						BufferedImage image = view.getScreenshot();
						action.setScreenshot(ScreenshotGenerator.makeImage(image, 0, 0,
								(int) gr.getWidth(),
								(int) gr.getHeight()));
						
					}
					return action.saveAsJpeg();
				}
					
				return false;
			}
		};
	}


	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.EXPORT_ICON;
	}

}
