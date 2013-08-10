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
package org.openflexo.fge.drawingeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.LaunchGraphDrawing.MyDrawing.MyShapeGraphicalRepresentation;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;

public class DrawEdgeControl extends MouseDragControl {

	Point currentDraggingLocationInDrawingView = null;
	boolean drawEdge = false;
	ShapeGraphicalRepresentation fromShape = null;
	ShapeGraphicalRepresentation toShape = null;
	private DrawingEditorFactory factory;

	public DrawEdgeControl(DrawingEditorFactory factory) {
		super("Draw edge", MouseButton.LEFT, false, true, false, false); // CTRL DRAG
		action = new DrawEdgeAction();
		this.factory = factory;
	}

	protected class DrawEdgeAction extends CustomDragControlAction {
		@Override
		public boolean handleMousePressed(GraphicalRepresentation graphicalRepresentation, DrawingController controller, MouseEvent event) {
			if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
				drawEdge = true;
				fromShape = (ShapeGraphicalRepresentation) graphicalRepresentation;
				((MyDrawingView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation graphicalRepresentation, DrawingController controller, MouseEvent event,
				boolean isSignificativeDrag) {
			if (drawEdge) {
				if (fromShape != null && toShape != null) {
					// System.out.println("Add ConnectorSpecification contextualMenuInvoker="+contextualMenuInvoker+" point="+contextualMenuClickedPoint);
					MyConnector newConnector = factory.makeNewConnector(fromShape, toShape, (EditedDrawing) controller.getDrawing());
					((MyDrawingController) controller).addNewConnector(newConnector);
				}
				drawEdge = false;
				fromShape = null;
				toShape = null;
				((MyDrawingView) controller.getDrawingView()).setDrawEdgeAction(null);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseDragged(GraphicalRepresentation graphicalRepresentation, DrawingController controller, MouseEvent event) {
			if (drawEdge) {
				GraphicalRepresentation gr = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (gr instanceof MyShapeGraphicalRepresentation && gr != fromShape && !fromShape.getAncestors().contains(gr.getDrawable())) {
					toShape = (MyShapeGraphicalRepresentation) gr;
				} else {
					toShape = null;
				}
				currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
						controller.getDrawingView());
				controller.getDrawingView().getPaintManager().repaint(controller.getDrawingView());
				return true;
			}
			return false;
		}

		public void paint(Graphics g, DrawingController controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
						fromShape.getShape().getShape().getCenter(), fromShape, controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toShape != null) {
					to = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
							toShape.getShape().getShape().getCenter(), toShape, controller.getScale());
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
