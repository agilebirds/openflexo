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
package org.openflexo.ve.controller;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.selection.PastingGraphicalContext;
import org.openflexo.selection.SelectionManager;
import org.openflexo.ve.diagram.DiagramModuleView;
import org.openflexo.ve.diagram.DiagramView;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * This is the selection manager responsible for selection in ViewEditor module
 * 
 * @author sylvain
 */
public class VESelectionManager extends SelectionManager {

	protected static final Logger logger = Logger.getLogger(VESelectionManager.class.getPackage().getName());

	public VESelectionManager(VEController controller) {
		super(controller);
		FlexoMenuBar menuBar = controller.getMenuBar();
		_clipboard = new VEClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem,
				menuBar.getEditMenu(controller).cutItem);
		_contextualMenuManager = new VEContextualMenuManager(this, controller);
	}

	public VEController getVEController() {
		return (VEController) getController();
	}

	@Override
	public boolean performSelectionSelectAll() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("'Select All' not implemented yet in this module");
		}
		return false;
	}

	// ==========================================================================
	// ============================= Deletion
	// ===================================
	// ==========================================================================

	/**
	 * Returns the root object that can be currently edited
	 * 
	 * @return FlexoModelObject
	 */
	@Override
	public FlexoObject getRootFocusedObject() {
		return getVEController().getCurrentDisplayedObjectAsModuleView();
	}

	@Override
	public FlexoModelObject getPasteContext() {
		if (getVEController().getCurrentModuleView() instanceof DiagramModuleView) {
			DiagramView v = ((DiagramModuleView) getVEController().getCurrentModuleView()).getController().getDrawingView();
			GraphicalRepresentation gr = v.getController().getLastSelectedGR();
			if (gr != null && gr.getDrawable() instanceof FlexoModelObject) {
				return (FlexoModelObject) gr.getDrawable();
			} else {
				return (FlexoModelObject) ((DrawingView<?>) getVEController().getCurrentModuleView()).getDrawingGraphicalRepresentation()
						.getDrawable();
			}
		}
		return null;
	}

	@Override
	public PastingGraphicalContext getPastingGraphicalContext() {
		PastingGraphicalContext pgc = new PastingGraphicalContext();
		if (getVEController().getCurrentModuleView() instanceof DiagramModuleView) {
			DiagramView v = ((DiagramModuleView) getVEController().getCurrentModuleView()).getController().getDrawingView();
			DrawingController controller = v.getController();
			GraphicalRepresentation target = controller.getLastSelectedGR();
			if (target == null) {
				pgc.targetContainer = controller.getDrawingView();
			} else {
				pgc.targetContainer = (JComponent) v.viewForObject(target);
			}
			if (controller.getLastClickedPoint() != null) {
				pgc.precisePastingLocation = controller.getLastClickedPoint();
				pgc.pastingLocation = new Point((int) pgc.precisePastingLocation.getX(), (int) pgc.precisePastingLocation.getY());
			} else {
				pgc.precisePastingLocation = new FGEPoint(0, 0);
				pgc.pastingLocation = new Point(0, 0);
			}
		}
		return pgc;
	}

}
