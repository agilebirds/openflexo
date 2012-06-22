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
package org.openflexo.wkf.controller.action;

import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.IconLibrary;
import org.openflexo.print.FlexoPrintableComponent;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;
import org.openflexo.wkf.view.print.PrintProcessAction;
import org.openflexo.wkf.view.print.PrintProcessPreviewDialog;
import org.openflexo.wkf.view.print.PrintableProcessView;
import org.openflexo.wkf.view.print.PrintableSwimimingLaneView;

public class PrintProcessInitializer extends ActionInitializer<PrintProcessAction, FlexoProcess, WKFObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	PrintProcessInitializer(WKFControllerActionInitializer actionInitializer) {
		super(PrintProcessAction.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<PrintProcessAction> getDefaultInitializer() {
		return new FlexoActionInitializer<PrintProcessAction>() {
			@Override
			public boolean run(EventObject e, PrintProcessAction anAction) {
				if (anAction.getFocusedObject() == null) {
					anAction.setFocusedObject(getController().getCurrentFlexoProcess());
				}
				return anAction.getFocusedObject() != null;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<PrintProcessAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<PrintProcessAction>() {
			@Override
			public boolean run(EventObject e, final PrintProcessAction anAction) {

				FlexoPrintableComponent printableView = null;
				if (getController().getCurrentPerspective() == getController().SWIMMING_LANE_PERSPECTIVE) {
					SwimmingLaneEditorController printController = new SwimmingLaneEditorController(getController(),
							anAction.getFocusedObject()) {
						@Override
						public DrawingView<SwimmingLaneRepresentation> makeDrawingView(SwimmingLaneRepresentation drawing) {
							return new PrintableSwimimingLaneView(drawing, this, getWKFController());
						}
					};
					printController.getDrawingView().getPaintManager().disablePaintingCache();
					printController.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
					printableView = (FlexoPrintableComponent) printController.getDrawingView();
				} else {
					ProcessEditorController printController = new ProcessEditorController(anAction.getFocusedObject(), null, null) {
						@Override
						public DrawingView<ProcessRepresentation> makeDrawingView(ProcessRepresentation drawing) {
							return new PrintableProcessView(drawing, this, getWKFController());
						}
					};
					printController.getDrawingView().getPaintManager().disablePaintingCache();
					printController.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
					printableView = (FlexoPrintableComponent) printController.getDrawingView();
				}
				PrintProcessPreviewDialog dialog = new PrintProcessPreviewDialog(getController(), printableView);
				return dialog.getStatus() == PrintProcessPreviewDialog.ReturnedStatus.CONTINUE_PRINTING;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.PRINT_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_P, FlexoCst.META_MASK);
	}

}
