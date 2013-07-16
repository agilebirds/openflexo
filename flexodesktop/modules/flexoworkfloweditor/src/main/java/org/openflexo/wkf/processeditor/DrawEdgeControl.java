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
package org.openflexo.wkf.processeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.wkf.action.CreateAssociation;
import org.openflexo.foundation.wkf.action.CreateEdge;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.processeditor.gr.AbstractNodeGR;
import org.openflexo.wkf.processeditor.gr.PreConditionGR;
import org.openflexo.wkf.processeditor.gr.WKFNodeGR;

public class DrawEdgeControl extends MouseDragControl {

	private static final Logger logger = Logger.getLogger(CreateEdge.class.getPackage().getName());
	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected WKFNodeGR<?> from = null;
	protected WKFNodeGR<?> to = null;

	public DrawEdgeControl() {
		super("Draw edge", MouseButton.LEFT, false, true, false, false); // CTRL DRAG
		action = new DrawEdgeAction();
	}

	protected class DrawEdgeAction extends CustomDragControlAction {
		@Override
		public boolean handleMousePressed(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event) {
			if (graphicalRepresentation instanceof WKFNodeGR) {
				drawEdge = true;
				from = (WKFNodeGR<?>) graphicalRepresentation;
				((ProcessView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event, boolean isSignificativeDrag) {
			if (drawEdge) {
				try {
					if (from != null && to != null) {
						if (from instanceof AbstractNodeGR && to instanceof AbstractNodeGR) {
							CreateEdge createEdgeAction = CreateEdge.actionType.makeNewAction(((AbstractNodeGR<?>) from).getDrawable(),
									null, ((ProcessEditorController) controller).getEditor());
							createEdgeAction.setStartingNode(((AbstractNodeGR<?>) from).getNode());
							if (to instanceof PreConditionGR) {
								createEdgeAction.setEndNodePreCondition(((PreConditionGR) to).getFlexoPreCondition());
								createEdgeAction.setEndNode(((PreConditionGR) to).getFlexoPreCondition().getNode());
							} else if (to instanceof AbstractNodeGR) {
								createEdgeAction.setEndNode(((AbstractNodeGR<?>) to).getNode());
							}
							createEdgeAction.doAction();
						} else {
							CreateAssociation createEdgeAction = CreateAssociation.actionType.makeNewAction(from.getDrawable(), null,
									((ProcessEditorController) controller).getEditor());
							createEdgeAction.setStartingNode(from.getNode());
							createEdgeAction.setEndNode(to.getNode());
							createEdgeAction.doAction();
						}
					}
				} finally {
					drawEdge = false;
					from = null;
					to = null;
					((ProcessView) controller.getDrawingView()).setDrawEdgeAction(null);
					DrawingView drawingView = controller.getDrawingView();
					FGEPaintManager paintManager = drawingView.getPaintManager();
					paintManager.invalidate(drawingView.getDrawingGraphicalRepresentation());
					paintManager.repaint(drawingView.getDrawingGraphicalRepresentation());
				}
				return true;
			}
			return false;
		}

		// Attempt to repaint relevant zone only
		private Rectangle getBoundsToRepaint(DrawingView<?> drawingView) {
			ShapeView<?> fromView = drawingView.shapeViewForObject(from);
			Rectangle fromViewBounds = SwingUtilities.convertRectangle(fromView, fromView.getBounds(), drawingView);
			Rectangle boundsToRepaint = fromViewBounds;

			if (to != null) {
				ShapeView<?> toView = drawingView.shapeViewForObject(to);
				Rectangle toViewBounds = SwingUtilities.convertRectangle(toView, toView.getBounds(), drawingView);
				boundsToRepaint = fromViewBounds.union(toViewBounds);
			}

			if (currentDraggingLocationInDrawingView != null) {
				Rectangle lastLocationBounds = new Rectangle(currentDraggingLocationInDrawingView);
				boundsToRepaint = fromViewBounds.union(lastLocationBounds);
			}

			// logger.fine("boundsToRepaint="+boundsToRepaint);

			return boundsToRepaint;
		}

		@Override
		public boolean handleMouseDragged(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event) {
			if (drawEdge) {

				DrawingView<?> drawingView = controller.getDrawingView();
				FGEPaintManager paintManager = drawingView.getPaintManager();

				// Attempt to repaint relevant zone only
				Rectangle oldBounds = previousRectangle;
				if (oldBounds != null) {
					oldBounds.x -= 1;
					oldBounds.y -= 1;
					oldBounds.width += 2;
					oldBounds.height += 2;
				}

				GraphicalRepresentation gr = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (gr instanceof WKFNodeGR && gr != from
				/*&& !(from.getAncestors().contains(gr.getDrawable()))*/) {
					to = (WKFNodeGR<?>) gr;
				} else {
					to = null;
				}

				currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
						controller.getDrawingView());

				// Attempt to repaint relevant zone only
				Rectangle newBounds = getBoundsToRepaint(drawingView);
				Rectangle boundsToRepaint;
				if (oldBounds != null) {
					boundsToRepaint = oldBounds.union(newBounds);
				} else {
					boundsToRepaint = newBounds;
				}
				paintManager.repaint(drawingView, boundsToRepaint);

				// Alternative @brutal zone
				// paintManager.repaint(drawingView);

				return true;
			}
			return false;
		}

		private Rectangle previousRectangle;

		public void paint(Graphics g, DrawingController controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point fromPoint = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
						from.getShape().getShape().getCenter(), from, controller.getScale());
				Point toPoint = currentDraggingLocationInDrawingView;
				if (to != null) {
					/*toPoint = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
							to.getShape().getShape().getCenter(), to, controller.getScale());*/
					g.setColor(WKFCst.OK);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
				int x, y, w, h;
				if (fromPoint.x >= toPoint.x) {
					x = toPoint.x;
					w = fromPoint.x - toPoint.x;
				} else {
					x = fromPoint.x;
					w = toPoint.x - fromPoint.x;
				}
				if (fromPoint.y >= toPoint.y) {
					y = toPoint.y;
					h = fromPoint.y - toPoint.y;
				} else {
					y = fromPoint.y;
					h = toPoint.y - fromPoint.y;
				}
				previousRectangle = new Rectangle(x, y, w, h);
			}
		}
	}

}
