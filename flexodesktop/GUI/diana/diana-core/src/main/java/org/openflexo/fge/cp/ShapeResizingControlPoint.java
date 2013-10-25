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
package org.openflexo.fge.cp;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.model.undo.CompoundEdit;

/**
 * A {@link LabelControlPoint} encodes an interactive control point which purpose is to resize a shape<br>
 * 
 * @author sylvain
 */
public class ShapeResizingControlPoint extends ControlPoint {

	private static final Logger logger = Logger.getLogger(ShapeResizingControlPoint.class.getPackage().getName());

	private CardinalDirection cardinalDirection;

	private FGEPoint initialShapePosition;
	private double initialWidth;
	private double initialHeight;
	private FGEDimension offset;

	// private FGERectangle initialRequiredBounds;

	public ShapeResizingControlPoint(ShapeNode<?> node, FGEPoint pt, CardinalDirection aCardinalDirection) {
		super(node, pt);

		// logger.info("***** new ShapeResizingControlPoint "+Integer.toHexString(hashCode())+" for "+graphicalRepresentation);

		if (aCardinalDirection == null) {
			cardinalDirection = FGEPoint.getOrientation(ShapeSpecification.CENTER, getPoint());
		} else {
			cardinalDirection = aCardinalDirection;
		}

		if (cardinalDirection == CardinalDirection.NORTH) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.SOUTH, ShapeSpecification.NORTH));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.EAST) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.WEST, ShapeSpecification.EAST));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.SOUTH) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.NORTH, ShapeSpecification.SOUTH));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.WEST) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.EAST, ShapeSpecification.WEST));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.NORTH_EAST) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(ShapeSpecification.SOUTH_WEST, CardinalQuadrant.NORTH_EAST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.SOUTH_WEST, ShapeSpecification.NORTH_EAST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.SOUTH, ShapeSpecification.NORTH));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.WEST, ShapeSpecification.EAST));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.NORTH_WEST) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(ShapeSpecification.SOUTH_EAST, CardinalQuadrant.NORTH_WEST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.SOUTH_EAST, ShapeSpecification.NORTH_WEST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.SOUTH, ShapeSpecification.NORTH));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.EAST, ShapeSpecification.WEST));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.SOUTH_WEST) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(ShapeSpecification.NORTH_EAST, CardinalQuadrant.SOUTH_WEST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.NORTH_EAST, ShapeSpecification.SOUTH_WEST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.NORTH, ShapeSpecification.SOUTH));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.EAST, ShapeSpecification.WEST));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else if (cardinalDirection == CardinalDirection.SOUTH_EAST) {
			if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(ShapeSpecification.NORTH_WEST, CardinalQuadrant.SOUTH_EAST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.NORTH_WEST, ShapeSpecification.SOUTH_EAST));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.NORTH, ShapeSpecification.SOUTH));
			} else if (getNode().getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED) {
				setDraggingAuthorizedArea(new FGEHalfLine(ShapeSpecification.EAST, ShapeSpecification.WEST));
			} else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		} else {
			setDraggingAuthorizedArea(new FGEEmptyArea());
		}
	}

	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}

	@Override
	public Cursor getDraggingCursor() {
		if (!isDraggable()) {
			return Cursor.getDefaultCursor();
		}
		FGEPoint center = getNode().getFGEShape().getCenter();
		return getResizingCursor(FGEPoint.getOrientation(center, getPoint()));
	}

	private static Cursor getResizingCursor(CardinalDirection direction) {
		if (direction == CardinalDirection.NORTH) {
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.SOUTH) {
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.EAST) {
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.WEST) {
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.NORTH_EAST) {
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.SOUTH_EAST) {
			return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.NORTH_WEST) {
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		} else if (direction == CardinalDirection.SOUTH_WEST) {
			return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		}

		return null;
	}

	/*public FGEArea getDraggingAuthorizedArea()
	{
		return authorizedDragArea;
	}*/

	@Override
	public boolean isDraggable() {
		return getNode().getGraphicalRepresentation().getDimensionConstraints() != DimensionConstraints.UNRESIZABLE
				&& getNode().getGraphicalRepresentation().getDimensionConstraints() != DimensionConstraints.CONTAINER;
	}

	private CompoundEdit resizeEdit = null;

	@Override
	public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
		if (!isDraggable()) {
			return;
		}

		if (getNode().getFactory().getUndoManager() != null) {
			resizeEdit = getNode().getFactory().getUndoManager().startRecording("Resizing shape");
		}

		initialWidth = getNode().getUnscaledViewWidth();
		initialHeight = getNode().getUnscaledViewHeight();
		if (initialWidth < FGEGeometricObject.EPSILON) {
			initialWidth = 1;
		}
		if (initialHeight < FGEGeometricObject.EPSILON) {
			initialHeight = 1;
		}
		offset = new FGEDimension(initialWidth - getNode().getWidth(), initialHeight - getNode().getHeight());
		initialShapePosition = getNode().getLocation();

		/*if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
			controller.getPaintManager().invalidate(getGraphicalRepresentation());
		}*/

		/*if (getNode().getGraphicalRepresentation().getAdaptBoundsToContents()) {
			initialRequiredBounds = getNode().getRequiredBoundsForContents();
		}*/

		getNode().notifyObjectWillResize();
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
		if (nearestPoint == null) {
			logger.warning("Could not find nearest point on authorized area: " + getDraggingAuthorizedArea() + " for " + getNode());
			return true;
		}
		if (cardinalDirection == CardinalDirection.NORTH) {
			FGEPoint opposite = ShapeSpecification.SOUTH;
			double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
			getNode().setSize(new FGEDimension(initialWidth - offset.width, newHeight - offset.height));
			getNode().setLocation(new FGEPoint(initialShapePosition.x, initialShapePosition.y - (newHeight - initialHeight)));
		} else if (cardinalDirection == CardinalDirection.SOUTH) {
			FGEPoint opposite = ShapeSpecification.NORTH;
			double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
			getNode().setSize(new FGEDimension(initialWidth - offset.width, newHeight - offset.height));
		} else if (cardinalDirection == CardinalDirection.WEST) {
			FGEPoint opposite = ShapeSpecification.EAST;
			double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
			getNode().setSize(new FGEDimension(newWidth - offset.width, initialHeight - offset.height));
			getNode().setLocation(new FGEPoint(initialShapePosition.x - (newWidth - initialWidth), initialShapePosition.y));
		} else if (cardinalDirection == CardinalDirection.EAST) {
			FGEPoint opposite = ShapeSpecification.WEST;
			double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
			getNode().setSize(new FGEDimension(newWidth - offset.width, initialHeight - offset.height));
		} else if (cardinalDirection == CardinalDirection.SOUTH_EAST) {
			FGEPoint opposite = ShapeSpecification.NORTH_WEST;
			double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
			double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);

			/*if (getGraphicalRepresentation().getAdaptBoundsToContents()) {
				// FGERectangle r = getGraphicalRepresentation().getRequiredBoundsForContents();
				System.out.println("Les bounds mini sont " + initialRequiredBounds);
				System.out.println("J'essaie " + new FGEDimension(newWidth - offset.width, newHeight - offset.height));
				getGraphicalRepresentation().setSize(
						new FGEDimension(Math.max(newWidth - offset.width, initialRequiredBounds.x + initialRequiredBounds.width), Math
								.max(newHeight - offset.height, initialRequiredBounds.y + initialRequiredBounds.height)));
			} else {
				getGraphicalRepresentation().setSize(new FGEDimension(newWidth - offset.width, newHeight - offset.height));
			}*/
			getNode().setSize(new FGEDimension(newWidth - offset.width, newHeight - offset.height));
		} else if (cardinalDirection == CardinalDirection.SOUTH_WEST) {
			FGEPoint opposite = ShapeSpecification.NORTH_EAST;
			double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
			double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
			getNode().setSize(new FGEDimension(newWidth - offset.width, newHeight - offset.height));
			getNode().setLocation(new FGEPoint(initialShapePosition.x - (newWidth - initialWidth), initialShapePosition.y));
		} else if (cardinalDirection == CardinalDirection.NORTH_EAST) {
			FGEPoint opposite = ShapeSpecification.SOUTH_WEST;
			double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
			double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
			getNode().setSize(new FGEDimension(newWidth - offset.width, newHeight - offset.height));
			getNode().setLocation(new FGEPoint(initialShapePosition.x, initialShapePosition.y - (newHeight - initialHeight)));
		} else if (cardinalDirection == CardinalDirection.NORTH_WEST) {
			FGEPoint opposite = ShapeSpecification.SOUTH_EAST;
			double newWidth = initialWidth * (opposite.x - nearestPoint.x) / (opposite.x - initialPoint.x);
			double newHeight = initialHeight * (opposite.y - nearestPoint.y) / (opposite.y - initialPoint.y);
			getNode().setSize(new FGEDimension(newWidth - offset.width, newHeight - offset.height));
			getNode().setLocation(
					new FGEPoint(initialShapePosition.x - (newWidth - initialWidth), initialShapePosition.y - (newHeight - initialHeight)));
		}
		/*if (getGraphicalRepresentation().getAdaptBoundsToContents()) {
			System.out.println("c'etait a " + getGraphicalRepresentation().getSize());
			getGraphicalRepresentation().extendBoundsToHostContents();
			System.out.println("je remets a " + getGraphicalRepresentation().getSize());
		}*/
		return true;
	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		if (!isDraggable()) {
			return;
		}

		initialWidth = 0;
		initialHeight = 0;

		/*if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().resetTemporaryObjects();
			controller.getPaintManager().invalidate(getGraphicalRepresentation());
			controller.getPaintManager().repaint(controller.getDrawingView());
		}*/

		getNode().notifyObjectHasResized();

		if (getNode().getFactory().getUndoManager() != null) {
			getNode().getFactory().getUndoManager().stopRecording(resizeEdit);
		}

	}
}
