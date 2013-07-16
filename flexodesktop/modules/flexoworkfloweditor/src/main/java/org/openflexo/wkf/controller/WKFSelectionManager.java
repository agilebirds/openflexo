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
package org.openflexo.wkf.controller;

import java.awt.Point;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.selection.PastingGraphicalContext;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Selection manager dedicated to Workflow editor This class manages a set of FlexoModelObject instances which are all at the same level
 * 
 * Note that we don't select the data, but the views representing data.
 * 
 * @author sguerin
 */
public class WKFSelectionManager extends SelectionManager {

	protected static final Logger logger = Logger.getLogger(WKFSelectionManager.class.getPackage().getName());

	private FlexoLevel level = null;

	public WKFSelectionManager(WKFController controller) {
		super(controller);
		FlexoMenuBar menuBar = controller.getMenuBar();
		_clipboard = new WKFClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem,
				menuBar.getEditMenu(controller).cutItem);
		level = null;
		_contextualMenuManager = new WKFContextualMenuManager(this, controller);
	}

	public WKFController getWKFController() {
		return (WKFController) getController();
	}

	@Override
	public boolean performSelectionSelectAll() {
		logger.info("Select all");
		/* FlexoModelObject context = pasteContextForComponent(getLastClickedContainer());
		 if (context instanceof FlexoPetriGraph) {
		     FlexoPetriGraph pg = ((FlexoPetriGraph)context);
		     Vector newSelection = pg.getAllContainedObjectsAtThisLevel();
		     setSelectedObjects(newSelection);
		     return true;
		 } else {
		     return false;
		 }*/
		return false;
	}

	@Override
	public boolean performSelectionCut() {
		_clipboard.performSelectionCut(getSelection());
		FlexoPetriGraph parent = null;
		for (FlexoObject o : new Vector<FlexoObject>(getSelection())) {
			if (o instanceof PetriGraphNode) {
				PetriGraphNode node = (PetriGraphNode) o;
				if (parent == null) {
					parent = node.getParentPetriGraph();
				} else if (node.getParentPetriGraph() != parent) {
					continue;
				}
				node.getParentPetriGraph().removeFromNodes(node);
			}
			if (o instanceof WKFArtefact) {
				WKFArtefact annotation = (WKFArtefact) o;
				if (parent == null) {
					parent = annotation.getParentPetriGraph();
				} else if (annotation.getParentPetriGraph() != parent) {
					continue;
				}
				annotation.getParentPetriGraph().removeFromArtefacts(annotation);
			}
			if (o instanceof Role) {
				Role role = (Role) o;
				role.delete();
			}
			/*if (o instanceof FlexoPort) {
				 FlexoPort port = (FlexoPort) o;
				 ((FlexoPort) o).getPortRegistery().removeFromPorts(port);
			 }*/
		}
		return true;
	}

	// ==========================================================================
	// ================ Selection Management, public A.P.I
	// ======================
	// ==========================================================================

	@Override
	protected void fireSelectionBecomesEmpty() {
		super.fireSelectionBecomesEmpty();
		level = null;
	}

	@Override
	protected void fireSelectionIsNoMoreEmpty() {
		super.fireSelectionIsNoMoreEmpty();
		if (getSelection().firstElement() instanceof LevelledObject) {
			level = ((LevelledObject) getSelection().firstElement()).getLevel();
		}
	}

	public FlexoLevel getSelectionLevel() {
		return level;
	}

	/**
	 * Returns the root object that can be currently edited
	 * 
	 * @return FlexoObservable
	 */
	@Override
	public FlexoModelObject getRootFocusedObject() {
		return getWKFController().getCurrentFlexoProcess();
	}

	@Override
	public FlexoModelObject getPasteContext() {
		if (getWKFController().getCurrentModuleView() instanceof DrawingView) {
			GraphicalRepresentation gr = ((DrawingView) getWKFController().getCurrentModuleView()).getController().getLastSelectedGR();
			if (gr != null && gr.getDrawable() instanceof FlexoModelObject) {
				return (FlexoModelObject) gr.getDrawable();
			} else {
				return (FlexoModelObject) ((DrawingView<?>) getWKFController().getCurrentModuleView()).getDrawingGraphicalRepresentation()
						.getDrawable();
			}
		}
		return null;
	}

	@Override
	public PastingGraphicalContext getPastingGraphicalContext() {
		PastingGraphicalContext pgc = new PastingGraphicalContext();
		if (getWKFController().getCurrentModuleView() instanceof DrawingView) {
			DrawingView<?> moduleView = (DrawingView<?>) getWKFController().getCurrentModuleView();
			DrawingController controller = moduleView.getController();
			GraphicalRepresentation target = controller.getLastSelectedGR();
			if (target == null) {
				pgc.targetContainer = controller.getDrawingView();
			} else {
				pgc.targetContainer = (JComponent) moduleView.viewForObject(target);
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
