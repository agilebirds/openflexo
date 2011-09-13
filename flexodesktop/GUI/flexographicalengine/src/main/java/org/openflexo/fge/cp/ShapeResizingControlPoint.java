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

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.fge.shapes.Shape;


public class ShapeResizingControlPoint extends ControlPoint {

	private static final Logger logger = Logger.getLogger(ShapeResizingControlPoint.class.getPackage().getName());

	private CardinalDirection cardinalDirection;
	//private FGEArea authorizedDragArea;

	public ShapeResizingControlPoint(ShapeGraphicalRepresentation<?> graphicalRepresentation, FGEPoint pt, CardinalDirection aCardinalDirection)
	{
		super(graphicalRepresentation,pt);

		//logger.info("***** new ShapeResizingControlPoint "+Integer.toHexString(hashCode())+" for "+graphicalRepresentation);

		if (aCardinalDirection == null) {
			cardinalDirection = FGEPoint.getOrientation(Shape.CENTER, getPoint());
		}
		else {
			cardinalDirection = aCardinalDirection;
		}

		if (cardinalDirection == CardinalDirection.NORTH) {
			if ((graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE)
					|| (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED)
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.SOUTH,Shape.NORTH));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.EAST) {
			if ((graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE)
					|| (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED)
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.WEST,Shape.EAST));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.SOUTH) {
			if ((graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE)
					|| (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED)
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.NORTH,Shape.SOUTH));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.WEST) {
			if ((graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE)
					|| (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED)
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.EAST,Shape.WEST));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.NORTH_EAST) {
			if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(Shape.SOUTH_WEST, CardinalQuadrant.NORTH_EAST));
			}
			else if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.SOUTH_WEST,Shape.NORTH_EAST));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.NORTH_WEST) {
			if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(Shape.SOUTH_EAST, CardinalQuadrant.NORTH_WEST));
			}
			else if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.SOUTH_EAST,Shape.NORTH_WEST));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.SOUTH_WEST) {
			if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(Shape.NORTH_EAST, CardinalQuadrant.SOUTH_WEST));
			}
			else if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.NORTH_EAST,Shape.SOUTH_WEST));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else if (cardinalDirection == CardinalDirection.SOUTH_EAST) {
			if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.FREELY_RESIZABLE
					|| (graphicalRepresentation.getDimensionConstraints()==DimensionConstraints.STEP_CONSTRAINED)) {
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(Shape.NORTH_WEST, CardinalQuadrant.SOUTH_EAST));
			}
			else if (graphicalRepresentation.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				setDraggingAuthorizedArea(new FGEHalfLine(Shape.NORTH_WEST,Shape.SOUTH_EAST));
			}
			else {
				setDraggingAuthorizedArea(new FGEEmptyArea());
			}
		}
		else {
			setDraggingAuthorizedArea(new FGEEmptyArea());
		}
	}

	@Override
	public ShapeGraphicalRepresentation<?> getGraphicalRepresentation()
	{
		return (ShapeGraphicalRepresentation<?>)super.getGraphicalRepresentation();
	}

	@Override
	public Cursor getDraggingCursor()
	{
		if (!isDraggable()) return Cursor.getDefaultCursor();
		FGEPoint center = getGraphicalRepresentation().getShape().getShape().getCenter();
		return getResizingCursor(FGEPoint.getOrientation(center, getPoint()));
	}

	private static Cursor getResizingCursor(CardinalDirection direction)
	{
		if (direction == CardinalDirection.NORTH) {
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.SOUTH) {
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.EAST) {
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.WEST) {
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.NORTH_EAST) {
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.SOUTH_EAST) {
			return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.NORTH_WEST) {
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		}
		else if (direction == CardinalDirection.SOUTH_WEST) {
			return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		}

		return null;
	}

	/*public FGEArea getDraggingAuthorizedArea()
	{
		return authorizedDragArea;
	}*/

	@Override
	public boolean isDraggable()
	{
		return (getGraphicalRepresentation().getDimensionConstraints() != DimensionConstraints.UNRESIZABLE
				&& getGraphicalRepresentation().getDimensionConstraints() != DimensionConstraints.CONTAINER);
	}

	private FGEPoint initialShapePosition;
	private double initialWidth;
	private double initialHeight;
	private FGEDimension offset;

	@Override
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint)
	{
		if (!isDraggable()) return;
		initialWidth = getGraphicalRepresentation().getUnscaledViewWidth();
		initialHeight = getGraphicalRepresentation().getUnscaledViewHeight();
		if (initialWidth<FGEGeometricObject.EPSILON)
			initialWidth = 1;
		if (initialHeight<FGEGeometricObject.EPSILON)
			initialHeight = 1;
		offset = new FGEDimension(initialWidth-getGraphicalRepresentation().getWidth(),initialHeight-getGraphicalRepresentation().getHeight());
		initialShapePosition = getGraphicalRepresentation().getLocation();

		/*if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
			controller.getPaintManager().invalidate(getGraphicalRepresentation());
		}*/

		getGraphicalRepresentation().notifyObjectWillResize();
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
	{
		if (!isDraggable()) return true;


		//System.out.println("pointRelativeToInitialConfiguration="+pointRelativeToInitialConfiguration);
		FGEPoint nearestPoint = getNearestPointOnAuthorizedArea(pointRelativeToInitialConfiguration);
		//System.out.println("nearestPoint="+nearestPoint);
		if (nearestPoint == null) {
			logger.warning("Could not find nearest point on authorized area: "+getDraggingAuthorizedArea()+" for "+getGraphicalRepresentation());
			return true;
		}
		if (cardinalDirection == CardinalDirection.NORTH) {
			FGEPoint opposite = Shape.SOUTH;
			double newHeight = initialHeight*(opposite.y-nearestPoint.y)/(opposite.y-initialPoint.y);
			getGraphicalRepresentation().setSize(new FGEDimension(initialWidth-offset.width,newHeight-offset.height));
			getGraphicalRepresentation().setLocation(
					new FGEPoint(initialShapePosition.x,initialShapePosition.y-(newHeight-initialHeight)));
		}
		else if (cardinalDirection == CardinalDirection.SOUTH) {
			FGEPoint opposite = Shape.NORTH;
			double newHeight = initialHeight*(opposite.y-nearestPoint.y)/(opposite.y-initialPoint.y);
			getGraphicalRepresentation().setSize(new FGEDimension(initialWidth-offset.width,newHeight-offset.height));
		}
		else if (cardinalDirection == CardinalDirection.WEST) {
			FGEPoint opposite = Shape.EAST;
			double newWidth = initialWidth*(opposite.x-nearestPoint.x)/(opposite.x-initialPoint.x);
			getGraphicalRepresentation().setSize(new FGEDimension(newWidth-offset.width,initialHeight-offset.height));
			getGraphicalRepresentation().setLocation(
					new FGEPoint( initialShapePosition.x-(newWidth-initialWidth),initialShapePosition.y));
		}
		else if (cardinalDirection == CardinalDirection.EAST) {
			FGEPoint opposite = Shape.WEST;
			double newWidth = initialWidth*(opposite.x-nearestPoint.x)/(opposite.x-initialPoint.x);
			getGraphicalRepresentation().setSize(new FGEDimension(newWidth-offset.width,initialHeight-offset.height));
		}
		else if (cardinalDirection == CardinalDirection.SOUTH_EAST) {
			FGEPoint opposite = Shape.NORTH_WEST;

			double newWidth = initialWidth*(opposite.x-nearestPoint.x)/(opposite.x-initialPoint.x);
			double newHeight = initialHeight*(opposite.y-nearestPoint.y)/(opposite.y-initialPoint.y);
			getGraphicalRepresentation().setSize(new FGEDimension(newWidth-offset.width,newHeight-offset.height));
		}
		else if (cardinalDirection == CardinalDirection.SOUTH_WEST) {
			FGEPoint opposite = Shape.NORTH_EAST;
			double newWidth = initialWidth*(opposite.x-nearestPoint.x)/(opposite.x-initialPoint.x);
			double newHeight = initialHeight*(opposite.y-nearestPoint.y)/(opposite.y-initialPoint.y);
			getGraphicalRepresentation().setSize(new FGEDimension(newWidth-offset.width,newHeight-offset.height));
			getGraphicalRepresentation().setLocation(
					new FGEPoint(
							initialShapePosition.x-(newWidth-initialWidth),
							initialShapePosition.y));
		}
		else if (cardinalDirection == CardinalDirection.NORTH_EAST) {
			FGEPoint opposite = Shape.SOUTH_WEST;
			double newWidth = initialWidth*(opposite.x-nearestPoint.x)/(opposite.x-initialPoint.x);
			double newHeight = initialHeight*(opposite.y-nearestPoint.y)/(opposite.y-initialPoint.y);
			getGraphicalRepresentation().setSize(new FGEDimension(newWidth-offset.width,newHeight-offset.height));
			getGraphicalRepresentation().setLocation(
					new FGEPoint(
							initialShapePosition.x,
							initialShapePosition.y-(newHeight-initialHeight)));
		}
		else if (cardinalDirection == CardinalDirection.NORTH_WEST) {
			FGEPoint opposite = Shape.SOUTH_EAST;
			double newWidth = initialWidth*(opposite.x-nearestPoint.x)/(opposite.x-initialPoint.x);
			double newHeight = initialHeight*(opposite.y-nearestPoint.y)/(opposite.y-initialPoint.y);
			getGraphicalRepresentation().setSize(new FGEDimension(newWidth-offset.width,newHeight-offset.height));
			getGraphicalRepresentation().setLocation(
					new FGEPoint(
							initialShapePosition.x-(newWidth-initialWidth),
							initialShapePosition.y-(newHeight-initialHeight)));
		}
		return true;
	}

	@Override
	public void stopDragging(DrawingController controller)
	{
		if (!isDraggable()) return;

		initialWidth = 0;
		initialHeight = 0;

		/*if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().resetTemporaryObjects();
			controller.getPaintManager().invalidate(getGraphicalRepresentation());
			controller.getPaintManager().repaint(controller.getDrawingView());
		}*/

		getGraphicalRepresentation().notifyObjectHasResized();
	}
}
