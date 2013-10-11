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
package org.openflexo.fge.control.actions;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;

public class RectangleSelectingAction extends MouseDragControlActionImpl<DianaInteractiveViewer<?, ?, ?>> {

	static final Logger logger = Logger.getLogger(RectangleSelectingAction.class.getPackage().getName());

	private Point rectangleSelectingOriginInDrawingView;
	private Point currentMousePositionInDrawingView;

	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {

		if (editor instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) editor;

			// logger.info("Perform mouse PRESSED on RECTANGLE_SELECTING AbstractMouseDragControlActionImpl");

			rectangleSelectingOriginInDrawingView = getPointInDrawingView(controller, context);

			currentMousePositionInDrawingView = rectangleSelectingOriginInDrawingView;
			if (controller.getDrawingView() == null) {
				return false;
			}
			controller.getDrawingView().setRectangleSelectingAction(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context,
			boolean isSignificativeDrag) {
		if (editor instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) editor;
			if (isSignificativeDrag && node instanceof ContainerNode) {
				List<DrawingTreeNode<?, ?>> newSelection = buildCurrentSelection((ContainerNode<?, ?>) node, controller);
				controller.setSelectedObjects(newSelection);
				if (controller.getDrawingView() == null) {
					return false;
				}
				controller.getDrawingView().resetRectangleSelectingAction();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
		if (editor instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) editor;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perform mouse DRAGGED on RECTANGLE_SELECTING AbstractMouseDragControlActionImpl");
			}
			currentMousePositionInDrawingView = getPointInDrawingView(controller, context);

			List<DrawingTreeNode<?, ?>> newFocusSelection;
			if (node instanceof ContainerNode) {
				newFocusSelection = buildCurrentSelection((ContainerNode<?, ?>) node, controller);
			} else {
				newFocusSelection = Collections.emptyList();
			}
			controller.setFocusedObjects(newFocusSelection);
			if (controller.getDrawingView() == null) {
				return false;
			}
			if (controller.getDelegate() != null) {
				controller.getDelegate().repaintAll();
			}

			return true;
		}
		return false;
	}

	private List<DrawingTreeNode<?, ?>> buildCurrentSelection(ContainerNode<?, ?> node, AbstractDianaEditor<?, ?, ?> controller) {
		if (getRectangleSelection() == null) {
			return null;
		}
		List<DrawingTreeNode<?, ?>> returned = new Vector<DrawingTreeNode<?, ?>>();
		for (DrawingTreeNode<?, ?> child : node.getChildNodes()) {
			if (child.getGraphicalRepresentation().getIsVisible()) {
				if (child.isContainedInSelection(getRectangleSelection(), controller.getScale())) {
					returned.add(child);
				}
				if (child instanceof ContainerNode) {
					returned.addAll(buildCurrentSelection((ContainerNode<?, ?>) child, controller));
				}

			}
		}
		return returned;
	}

	/**
	 * Return current rectangle selection
	 * 
	 * @return Rectangle object as current selection
	 */
	private Rectangle getRectangleSelection() {
		if (rectangleSelectingOriginInDrawingView != null && currentMousePositionInDrawingView != null) {
			Point origin = new Point();
			Dimension dim = new Dimension();
			if (rectangleSelectingOriginInDrawingView.x <= currentMousePositionInDrawingView.x) {
				origin.x = rectangleSelectingOriginInDrawingView.x;
				dim.width = currentMousePositionInDrawingView.x - rectangleSelectingOriginInDrawingView.x;
			} else {
				origin.x = currentMousePositionInDrawingView.x;
				dim.width = rectangleSelectingOriginInDrawingView.x - currentMousePositionInDrawingView.x;
			}
			if (rectangleSelectingOriginInDrawingView.y <= currentMousePositionInDrawingView.y) {
				origin.y = rectangleSelectingOriginInDrawingView.y;
				dim.height = currentMousePositionInDrawingView.y - rectangleSelectingOriginInDrawingView.y;
			} else {
				origin.y = currentMousePositionInDrawingView.y;
				dim.height = rectangleSelectingOriginInDrawingView.y - currentMousePositionInDrawingView.y;
			}
			return new Rectangle(origin, dim);
		} else {
			return null;
		}
	}

	public void paint(Graphics g, AbstractDianaEditor<?, ?, ?> controller) {
		Rectangle selection = getRectangleSelection();
		if (selection == null) {
			return;
		}
		g.setColor(controller.getDrawing().getRoot().getGraphicalRepresentation().getRectangleSelectingSelectionColor());
		g.drawRect(selection.x, selection.y, selection.width, selection.height);
	}

}