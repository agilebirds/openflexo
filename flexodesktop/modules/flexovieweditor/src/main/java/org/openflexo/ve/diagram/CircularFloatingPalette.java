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
package org.openflexo.ve.diagram;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;

public class CircularFloatingPalette extends ControlArea<FGEArea> implements Observer {

	private static final Logger logger = Logger.getLogger(CircularFloatingPalette.class.getPackage().getName());

	private DiagramShapeGR shapeGR;
	private DiagramElement<?> target;

	private static final BackgroundStyle DEFAULT = BackgroundStyle.makeColoredBackground(Color.WHITE);
	private static final ForegroundStyle NODE_FOREGROUND = ForegroundStyle.makeStyle(Color.BLACK, 1.0f);
	private static final BackgroundStyle NODE_BACKGROUND = BackgroundStyle.makeColorGradientBackground(Color.DARK_GRAY, Color.WHITE,
			ColorGradientDirection.SOUTH_EAST_NORTH_WEST);

	static {
		DEFAULT.setUseTransparency(true);
		DEFAULT.setTransparencyLevel(0.3f);
		NODE_BACKGROUND.setUseTransparency(true);
		NODE_BACKGROUND.setTransparencyLevel(0.7f);
	}

	public CircularFloatingPalette(DiagramShapeGR shapeGR, DiagramElement<?> target, SimplifiedCardinalDirection orientation) {
		super(shapeGR, makeRoundRect(shapeGR));
		this.shapeGR = shapeGR;
		this.target = target;
		shapeGR.addObserver(this);
		updateElements(shapeGR);
	}

	@Override
	public boolean isDraggable() {
		return shapeGR.getDrawing().isEditable();
	}

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected boolean isDnd = false;
	protected DiagramShapeGR to = null;
	protected GraphicalRepresentation<?> focusedGR;
	private DiagramController controller;
	private FGEPoint normalizedStartPoint;

	private Rectangle previousRectangle;

	public void paint(Graphics g, DiagramController controller) {
		if (drawEdge && currentDraggingLocationInDrawingView != null) {
			FGEShape<?> fgeShape = shapeGR.getShapeSpecification().getOutline();
			DrawingGraphicalRepresentation<?> drawingGR = controller.getDrawingGraphicalRepresentation();
			double scale = controller.getScale();
			FGEPoint nearestOnOutline = fgeShape.getNearestPoint(drawingGR.convertLocalViewCoordinatesToRemoteNormalizedPoint(
					currentDraggingLocationInDrawingView, shapeGR, scale));
			/*nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint, controller.getDrawingGraphicalRepresentation(), controller.getScale())*/
			Point fromPoint = shapeGR.convertLocalNormalizedPointToRemoteViewCoordinates(nearestOnOutline, drawingGR, scale);
			Point toPoint = currentDraggingLocationInDrawingView;

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

	@Override
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {

	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
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
			focusedGR = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
			if (focusedGR instanceof DiagramShapeGR && focusedGR != shapeGR) {
				to = (DiagramShapeGR) focusedGR;
			} else {
				to = null;
			}

			currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					controller.getDrawingView());
			if (!isDnd) {
				isDnd = shapeGR.convertLocalNormalizedPointToRemoteViewCoordinates(normalizedStartPoint,
						controller.getDrawingGraphicalRepresentation(), controller.getScale()).distance(
						currentDraggingLocationInDrawingView) > 5;
			}

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

	// Attempt to repaint relevant zone only
	private Rectangle getBoundsToRepaint(DrawingView<?> drawingView) {
		ShapeView<?> fromView = drawingView.shapeViewForObject(shapeGR);
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
	public void stopDragging(DrawingController<?> controller, GraphicalRepresentation<?> focusedGR) {
		if (drawEdge && currentDraggingLocationInDrawingView != null && isDnd) {
			try {
				GraphicalRepresentation<?> targetGR = controller.getGraphicalRepresentation(target);
				if (targetGR == null) {
					targetGR = controller.getDrawingGraphicalRepresentation();
				}
				if (focusedGR == null) {
					focusedGR = controller.getDrawingGraphicalRepresentation();
				}
				SimplifiedCardinalDirection direction = FGEPoint.getSimplifiedOrientation(
						new FGEPoint(shapeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint,
								controller.getDrawingGraphicalRepresentation(), controller.getScale())), new FGEPoint(
								currentDraggingLocationInDrawingView));
				Point dropPoint = currentDraggingLocationInDrawingView;
				if (dropPoint.x < 0) {
					dropPoint.x = 0;
				}
				if (dropPoint.y < 0) {
					dropPoint.y = 0;
				}

				Point p = GraphicalRepresentation.convertPoint(controller.getDrawingGraphicalRepresentation(), dropPoint, focusedGR,
						controller.getScale());
				FGEPoint dropLocation = new FGEPoint(p.x / controller.getScale(), p.y / controller.getScale());
				DiagramShape to = null;

			} finally {
				((DiagramView) controller.getDrawingView()).resetFloatingPalette();
				DrawingView<?> drawingView = controller.getDrawingView();
				FGEPaintManager paintManager = drawingView.getPaintManager();
				paintManager.invalidate(drawingView.getDrawingGraphicalRepresentation());
				paintManager.repaint(drawingView.getDrawingGraphicalRepresentation());
			}
		} else {
		}
		super.stopDragging(controller, focusedGR);
	}

	private DiagramShape createNewShape(FGEPoint dropLocation, DiagramElement<?> container, DropScheme dropScheme) {

		DropSchemeAction dropSchemeAction = DropSchemeAction.actionType.makeNewAction(container, null, controller.getVEController()
				.getEditor());
		dropSchemeAction.setDropScheme(dropScheme);
		dropSchemeAction.escapeParameterRetrievingWhenValid = true;
		dropSchemeAction.doAction();

		if (dropSchemeAction.getPrimaryShape() != null) {

			GraphicalRepresentation<?> targetGR = controller.getDrawing().getGraphicalRepresentation(target);

			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) controller.getGraphicalRepresentation(dropSchemeAction
					.getPrimaryShape());

			double xOffset = 0;
			double yOffset = 0;
			if (gr != null) {
				if (gr.getBorder() != null) {
					xOffset -= gr.getBorder().left;
					yOffset -= gr.getBorder().top;
				}
				xOffset -= gr.getWidth() / 2;
				yOffset -= gr.getHeight() / 2;
				gr.setX(dropLocation.x + xOffset);
				gr.setY(dropLocation.y + yOffset);
			}

		}
		return dropSchemeAction.getPrimaryShape();
	}

	@Override
	public Rectangle paint(FGEGraphics drawingGraphics) {
		// System.out.println("Focused:"+nodeGR.getIsFocused());
		if (shapeGR.getIsSelected() && !shapeGR.getIsFocused()) {
			return null;
		}
		if (/*nodeGR.getIsSelected() ||*/shapeGR.isResizing() || shapeGR.isMoving()) {
			return null;
		}
		if (!shapeGR.getDrawing().isEditable()) {
			return null;
		}
		AffineTransform at = GraphicalRepresentation.convertNormalizedCoordinatesAT(shapeGR, drawingGraphics.getGraphicalRepresentation());

		Graphics2D oldGraphics = drawingGraphics.cloneGraphics();

		FGERoundRectangle paletteRect = (FGERoundRectangle) getArea().transform(at);
		FGEArea nodeRect = makeBaseRoundedArc(shapeGR, 1, 5);

		// paletteRect.paint(drawingGraphics);

		// 1. Node
		drawingGraphics.setDefaultForeground(NODE_FOREGROUND);
		drawingGraphics.setDefaultBackground(NODE_BACKGROUND);
		nodeRect.paint(drawingGraphics);

		drawingGraphics.releaseClonedGraphics(oldGraphics);
		return drawingGraphics.getGraphicalRepresentation().convertNormalizedRectangleToViewCoordinates(nodeRect.getEmbeddingBounds(),
				drawingGraphics.getScale());

	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == shapeGR) {
			if (arg instanceof ObjectResized) {
				updateElements(shapeGR);
			}
		}
	}

	private void updateElements(DiagramShapeGR shapeGR) {
		setArea(makeRoundRect(shapeGR));
		FGEArea roleRect = makeBaseRoundedArc(shapeGR, 1, 5);
	}

	private static FGEArea makeBaseRoundedArc(DiagramShapeGR shapeGR, double id, double arcNumber) {

		FGECircle centerShape = new FGECircle(new FGEPoint(shapeGR.getX() + 50, shapeGR.getY() + 50), shapeGR.getWidth() / 3,
				Filling.FILLED);
		FGEArc arc = new FGEArc(centerShape.getCenter(), new FGEDimension(centerShape.getHeight(), centerShape.getWidth()), id,
				360 / arcNumber - 5, ArcType.PIE);
		FGEArea area = arc.substract(centerShape, true);
		return area;
	}

	private static FGERoundRectangle makeRoundRect(DiagramShapeGR shapeGR) {

		return new FGERoundRectangle(shapeGR.getX(), shapeGR.getY(), shapeGR.getWidth() + 50, shapeGR.getWidth() + 50, 13.0, 13.0,
				Filling.FILLED);

	}
}
