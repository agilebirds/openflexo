package org.openflexo.wkf.swleditor.gr;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ShapeResizingControlPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.shapes.ShapeSpecification;

public class SWLContainerResizeAreas {

	private static class LineControlArea extends ControlArea<FGESegment> {
		private final SimplifiedCardinalDirection cardinalDirection;

		public LineControlArea(ShapeGraphicalRepresentation aGraphicalRepresentation, SimplifiedCardinalDirection cardinalDirection) {
			super(aGraphicalRepresentation, getSegment(cardinalDirection));
			this.cardinalDirection = cardinalDirection;
		}

		private static FGESegment getSegment(SimplifiedCardinalDirection cardinalDirection) {
			switch (cardinalDirection) {
			case EAST:
				return new FGESegment(1, 0, 1, 1);
			case NORTH:
				return new FGESegment(0, 0, 1, 0);
			case SOUTH:
				return new FGESegment(0, 1, 1, 1);
			case WEST:
				return new FGESegment(0, 0, 0, 1);

			}
			return null;
		}

		@Override
		public ShapeGraphicalRepresentation getGraphicalRepresentation() {
			return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
		}

		@Override
		public Rectangle paint(FGEGraphics graphics) {
			return new Rectangle();
		}

		private FGEPoint initialShapePosition;
		private double initialWidth;
		private double initialHeight;
		private FGEDimension offset;

		private FGERectangle initialRequiredBounds;

		@Override
		public void startDragging(DrawingController controller, FGEPoint startPoint) {
			if (!isDraggable()) {
				return;
			}
			initialWidth = getGraphicalRepresentation().getUnscaledViewWidth();
			initialHeight = getGraphicalRepresentation().getUnscaledViewHeight();
			if (initialWidth < FGEGeometricObject.EPSILON) {
				initialWidth = 1;
			}
			if (initialHeight < FGEGeometricObject.EPSILON) {
				initialHeight = 1;
			}
			offset = new FGEDimension(initialWidth - getGraphicalRepresentation().getWidth(), initialHeight
					- getGraphicalRepresentation().getHeight());
			initialShapePosition = getGraphicalRepresentation().getLocation();

			/*if (controller.getPaintManager().isPaintingCacheEnabled()) {
				controller.getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
				controller.getPaintManager().invalidate(getGraphicalRepresentation());
			}*/

			if (getGraphicalRepresentation().getAdaptBoundsToContents()) {
				initialRequiredBounds = getGraphicalRepresentation().getRequiredBoundsForContents();
			}
			setDraggingAuthorizedArea(getArea().getOrthogonalLine(startPoint));
			getGraphicalRepresentation().notifyObjectWillResize();
		}

		@Override
		public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
				FGEPoint initialPoint, MouseEvent event) {
			if (!isDraggable()) {
				return true;
			}

			// System.out.println("pointRelativeToInitialConfiguration="+pointRelativeToInitialConfiguration);
			FGEPoint nearestPoint = getNearestPointOnAuthorizedArea(pointRelativeToInitialConfiguration);
			// System.out.println("nearestPoint="+nearestPoint);
			if (cardinalDirection == SimplifiedCardinalDirection.NORTH) {
				FGEPoint opposite = ShapeSpecification.SOUTH;
				double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
				getGraphicalRepresentation().setSize(new FGEDimension(initialWidth - offset.width, newHeight - offset.height));
				getGraphicalRepresentation().setLocation(
						new FGEPoint(initialShapePosition.x, initialShapePosition.y - (newHeight - initialHeight)));
			} else if (cardinalDirection == SimplifiedCardinalDirection.SOUTH) {
				FGEPoint opposite = ShapeSpecification.NORTH;
				double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
				getGraphicalRepresentation().setSize(new FGEDimension(initialWidth - offset.width, newHeight - offset.height));
			} else if (cardinalDirection == SimplifiedCardinalDirection.WEST) {
				FGEPoint opposite = ShapeSpecification.EAST;
				double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
				getGraphicalRepresentation().setSize(new FGEDimension(newWidth - offset.width, initialHeight - offset.height));
				getGraphicalRepresentation().setLocation(
						new FGEPoint(initialShapePosition.x - (newWidth - initialWidth), initialShapePosition.y));
			} else if (cardinalDirection == SimplifiedCardinalDirection.EAST) {
				FGEPoint opposite = ShapeSpecification.WEST;
				double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
				getGraphicalRepresentation().setSize(new FGEDimension(newWidth - offset.width, initialHeight - offset.height));
			}
			return true;
		}

		@Override
		public void stopDragging(DrawingController controller, GraphicalRepresentation focusedGR) {
			if (!isDraggable()) {
				return;
			}

			initialWidth = 0;
			initialHeight = 0;
			setDraggingAuthorizedArea(null);
			getGraphicalRepresentation().notifyObjectHasResized();
		}

		@Override
		public Cursor getDraggingCursor() {
			if (!isDraggable()) {
				return Cursor.getDefaultCursor();
			}
			return getResizingCursor(cardinalDirection);
		}

		@Override
		public boolean isDraggable() {
			return getGraphicalRepresentation().getDimensionConstraints() != DimensionConstraints.UNRESIZABLE
					&& getGraphicalRepresentation().getDimensionConstraints() != DimensionConstraints.CONTAINER;
		}

		private static Cursor getResizingCursor(SimplifiedCardinalDirection direction) {
			if (direction == SimplifiedCardinalDirection.NORTH) {
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			} else if (direction == SimplifiedCardinalDirection.SOUTH) {
				return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			} else if (direction == SimplifiedCardinalDirection.EAST) {
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			} else if (direction == SimplifiedCardinalDirection.WEST) {
				return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			}
			return null;
		}

	}

	private List<ControlArea<?>> controlAreas;

	public SWLContainerResizeAreas(ShapeGraphicalRepresentation gr) {
		super();
		controlAreas = new ArrayList<ControlArea<?>>();
		controlAreas.add(new ShapeResizingControlPoint(gr, new FGEPoint(1, 1), null));
		controlAreas.add(new LineControlArea(gr, SimplifiedCardinalDirection.SOUTH));
		controlAreas.add(new LineControlArea(gr, SimplifiedCardinalDirection.EAST));
	}

	public List<ControlArea<?>> getControlAreas() {
		return controlAreas;
	}

}
