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

import java.awt.image.BufferedImage;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.action.PushToPalette;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.VPMCst;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.examplediagram.ExampleDiagramController;
import org.openflexo.vpm.examplediagram.ExampleDiagramModuleView;

public class PushToPaletteInitializer extends ActionInitializer<PushToPalette, ExampleDiagramShape, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	PushToPaletteInitializer(VPMControllerActionInitializer actionInitializer) {
		super(PushToPalette.actionType, actionInitializer);
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
	protected FlexoActionInitializer<PushToPalette> getDefaultInitializer() {
		return new FlexoActionInitializer<PushToPalette>() {
			@Override
			public boolean run(EventObject e, PushToPalette action) {
				if (getController().getCurrentModuleView() instanceof ExampleDiagramModuleView
						&& action.getFocusedObject().getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
					ExampleDiagramController c = ((ExampleDiagramModuleView) getController().getCurrentModuleView()).getController();
					ShapeGraphicalRepresentation gr = action.getFocusedObject().getGraphicalRepresentation();
					ShapeView shapeView = c.getDrawingView().shapeViewForNode(gr);
					BufferedImage image = shapeView.getScreenshot();
					ShapeBorder b = gr.getBorder();
					ShadowStyle ss = gr.getShadowStyle();
					action.setScreenshot(ScreenshotGenerator.makeImage(image, b.left, b.top,
							(int) gr.getWidth() + (ss.getDrawShadow() ? ss.getShadowBlur() : 0) + 1,
							(int) gr.getHeight() + (ss.getDrawShadow() ? ss.getShadowBlur() : 0) + 1));
					// action.setScreenshot(ScreenshotGenerator.trimImage(image));
				}

				return instanciateAndShowDialog(action, VPMCst.PUSH_TO_PALETTE_DIALOG_FIB);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<PushToPalette> getDefaultFinalizer() {
		return new FlexoActionFinalizer<PushToPalette>() {
			@Override
			public boolean run(EventObject e, PushToPalette action) {
				getController().setCurrentEditedObjectAsModuleView(action.palette, getController().VIEW_POINT_PERSPECTIVE);
				getController().getSelectionManager().setSelectedObject(action.getNewPaletteElement());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.DIAGRAM_PALETTE_ICON;
	}

}
