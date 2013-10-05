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
package org.openflexo.fge.control.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;

public class DianaInspectors {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaInspectors.class.getPackage().getName());

	private FIBDialog<ForegroundStyle> foregroundInspector;

	private DianaInteractiveViewer<?, ?, ?> controller;

	private List<ShapeNode<?>> selectedShapes = new ArrayList<ShapeNode<?>>();
	private List<ConnectorNode<?>> selectedConnectors = new ArrayList<ConnectorNode<?>>();
	private List<DrawingTreeNode<?, ?>> selection = new ArrayList<DrawingTreeNode<?, ?>>();

	public DianaInspectors(DianaInteractiveViewer<?, ?, ?> controller) {
		super();
		this.controller = controller;

	}

	public FIBDialog<ForegroundStyle> getForegroundInspector() {
		if (foregroundInspector == null) {
			foregroundInspector = FIBDialog.instanciateAndShowDialog(
					FIBLibrary.instance().retrieveFIBComponent(FIBForegroundStyleSelector.FIB_FILE), null, null, false);
		}
		return foregroundInspector;
	}

	public void delete() {

		selectedShapes.clear();
		selectedConnectors.clear();
		selection.clear();
		controller = null;// Don't delete, we did not create it
	}

	public DianaInteractiveViewer<?, ?, ?> getController() {
		return controller;
	}

	public void update() {
		selectedShapes.clear();
		selectedConnectors.clear();
		selection.clear();
		for (DrawingTreeNode<?, ?> node : controller.getSelectedObjects()) {
			if (node instanceof ShapeNode) {
				selection.add(node);
				selectedShapes.add((ShapeNode<?>) node);
			}
			if (node instanceof ConnectorNode) {
				selection.add(node);
				selectedConnectors.add((ConnectorNode<?>) node);
			}
		}
		if (selection.size() > 0) {
			if (selectedShapes.size() > 0) {
				if (foregroundInspector != null) {
					foregroundInspector.setData(selectedShapes.get(0).getGraphicalRepresentation().getForeground());
				}
			} else if (selectedConnectors.size() > 0) {
				if (foregroundInspector != null) {
					foregroundInspector.setData(selectedShapes.get(0).getGraphicalRepresentation().getForeground());
				}
			}
		} else {
			if (foregroundInspector != null) {
				foregroundInspector.setData(null);
			}
		}
	}

	public List<ShapeNode<?>> getSelectedShapes() {
		return selectedShapes;
	}

	public List<DrawingTreeNode<?, ?>> getSelection() {
		return selection;
	}
}
