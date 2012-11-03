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
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.viewpoint.action.PushToPalette;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaController;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaModuleView;

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
	public VPMController getController() {
		return (VPMController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<PushToPalette> getDefaultInitializer() {
		return new FlexoActionInitializer<PushToPalette>() {
			@Override
			public boolean run(ActionEvent e, PushToPalette action) {
				if (getController().getCurrentModuleView() instanceof CalcDrawingShemaModuleView
						&& action.getFocusedObject().getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
					CalcDrawingShemaController c = ((CalcDrawingShemaModuleView) getController().getCurrentModuleView()).getController();
					ShapeGraphicalRepresentation gr = (ShapeGraphicalRepresentation) action.getFocusedObject().getGraphicalRepresentation();
					ShapeView shapeView = c.getDrawingView().shapeViewForObject(gr);
					BufferedImage image = shapeView.getScreenshot();
					ShapeBorder b = gr.getBorder();
					ShadowStyle ss = gr.getShadowStyle();
					action.setScreenshot(ScreenshotGenerator.makeImage(image, b.left, b.top,
							(int) gr.getWidth() + (ss.getDrawShadow() ? ss.getShadowBlur() : 0) + 1,
							(int) gr.getHeight() + (ss.getDrawShadow() ? ss.getShadowBlur() : 0) + 1));
					// action.setScreenshot(ScreenshotGenerator.trimImage(image));
				}

				FIBDialog dialog = FIBDialog.instanciateAndShowDialog(CEDCst.PUSH_TO_PALETTE_DIALOG_FIB, action,
						FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
				if (dialog.getStatus() == Status.VALIDATED) {
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

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.CALC_PALETTE_ICON;
	}

}
