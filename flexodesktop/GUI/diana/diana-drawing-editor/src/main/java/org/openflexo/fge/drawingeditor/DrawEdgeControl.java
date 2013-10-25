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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseDragControlActionImpl;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.DiagramElement;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.swing.control.JMouseControlContext;
import org.openflexo.model.undo.CompoundEdit;

public class DrawEdgeControl extends MouseDragControlImpl<DianaDrawingEditor> {

	public DrawEdgeControl(DiagramFactory factory) {
		super("Draw edge", MouseButton.LEFT, new DrawEdgeAction(factory), false, true, false, false, factory); // CTRL-DRAG
	}

	protected static class DrawEdgeAction extends MouseDragControlActionImpl<DianaDrawingEditor> {

		Point currentDraggingLocationInDrawingView = null;
		boolean drawEdge = false;
		ShapeNode<Shape> fromShape = null;
		ShapeNode<Shape> toShape = null;
		private DiagramFactory factory;

		public DrawEdgeAction(DiagramFactory factory) {
			this.factory = factory;
		}

		@Override
		public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaDrawingEditor controller, MouseControlContext context) {
			if (node instanceof ShapeNode) {
				drawEdge = true;
				fromShape = (ShapeNode<Shape>) node;
				((DiagramEditorView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaDrawingEditor controller, MouseControlContext context,
				boolean isSignificativeDrag) {
			if (drawEdge) {
				if (fromShape != null && toShape != null) {
					// System.out.println("Add ConnectorSpecification contextualMenuInvoker="+contextualMenuInvoker+" point="+contextualMenuClickedPoint);
					CompoundEdit drawEdge = factory.getUndoManager().startRecording("Draw edge");
					Connector newConnector = factory.makeNewConnector(fromShape.getDrawable(), toShape.getDrawable(), controller
							.getDrawing().getModel());
					DrawingTreeNode<?, ?> fatherNode = FGEUtils.getFirstCommonAncestor(fromShape, toShape);
					((DiagramElement<?, ?>) fatherNode.getDrawable()).addToConnectors(newConnector);
					System.out.println("Add new connector !");
					factory.getUndoManager().stopRecording(drawEdge);
				}
				drawEdge = false;
				fromShape = null;
				toShape = null;
				((DiagramEditorView) controller.getDrawingView()).setDrawEdgeAction(null);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaDrawingEditor controller, MouseControlContext context) {
			if (drawEdge) {
				MouseEvent event = ((JMouseControlContext) context).getMouseEvent();
				DrawingTreeNode<?, ?> dtn = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (dtn instanceof ShapeNode && dtn != fromShape && !fromShape.getAncestors().contains(dtn)) {
					toShape = (ShapeNode<Shape>) dtn;
				} else {
					toShape = null;
				}
				currentDraggingLocationInDrawingView = getPointInDrawingView(controller, context);
				controller.getDrawingView().getPaintManager().repaint(controller.getDrawingView());
				return true;
			}
			return false;
		}

		public void paint(Graphics g, AbstractDianaEditor controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller
						.getDrawing()
						.getRoot()
						.convertRemoteNormalizedPointToLocalViewCoordinates(fromShape.getShape().getShape().getCenter(), fromShape,
								controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toShape != null) {
					to = controller
							.getDrawing()
							.getRoot()
							.convertRemoteNormalizedPointToLocalViewCoordinates(toShape.getShape().getShape().getCenter(), toShape,
									controller.getScale());
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
