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
package org.openflexo.fge.connectors.rpc;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.fge.connectors.impl.RectPolylinConnector;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geom.area.FGEPlane;

public class AdjustableIntermediateControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger logger = Logger.getLogger(AdjustableIntermediateControlPoint.class.getPackage().getName());

	private int index;

	public AdjustableIntermediateControlPoint(FGEPoint point, int index, RectPolylinConnector connector) {
		super(point, connector);
		this.index = index;
	}

	@Override
	public FGEArea getDraggingAuthorizedArea() {
		return new FGEPlane();
	}

	@Override
	public Cursor getDraggingCursor() {
		if (getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
		return super.getDraggingCursor();
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {

		System.out.println("AdjustableIntermediateControlPoint dragged to " + newRelativePoint);

		if (getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
			getConnectorSpecification().setCrossedControlPoint(newRelativePoint);
			getConnector()._connectorChanged(true);
			getNode().notifyConnectorModified();
			return true;
		}
		FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		if (pt == null) {
			logger.warning("Cannot nearest point for point " + newRelativePoint + " and area " + getDraggingAuthorizedArea());
			return false;
		}
		// Following little hack is used here to prevent some equalities that may
		// lead to inconsistent orientations
		pt.x += FGEGeometricObject.EPSILON;
		pt.y += FGEGeometricObject.EPSILON;
		FGEPoint oldPoint = getPoint();
		setPoint(pt);
		getPolylin().updatePointAt(index, pt);
		boolean continueDragging = movedIntermediateCP(index, oldPoint, true);
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return continueDragging;
	}

	/**
	 * This method is internally called to notify that intermediate control point at supplied index has just been moved.
	 * 
	 * Previous and next point are updated accordingly
	 * 
	 * Return boolean indicating if dragging lead to major layout modification where current dragged control point was suppressed: - When
	 * true: control point still exist - When false: control point has disappeared and dragging should stop
	 * 
	 * @param index
	 */
	private boolean movedIntermediateCP(int index, FGEPoint oldCPLocation, boolean simplifyLayout) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Moved intermediate point at index: " + index);
		}

		// First, obtain location of Control Point being moved
		FGEPoint newCPLocation = getPolylin().getPointAt(index);

		// Then obtain locations of previous and following control points
		FGEPoint previousCPLocation = getPolylin().getPointAt(index - 1);
		FGEPoint previousCPOldLocation = previousCPLocation.clone();
		FGEPoint nextCPLocation = getPolylin().getPointAt(index + 1);
		FGEPoint nextCPOldLocation = nextCPLocation.clone();

		// And their orientations, that will be usefull too
		SimplifiedCardinalDirection previousSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index - 1);
		SimplifiedCardinalDirection nextSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index);
		// SimplifiedCardinalDirection startSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index-2);
		// SimplifiedCardinalDirection endSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index+1);

		FGESegment intermediateCPStartSegment = getPolylin().getSegmentAt(index - 2);
		FGESegment intermediateCPEndSegment = getPolylin().getSegmentAt(index + 1);
		// FGESegment intermediateCPFirstSegment = polylin.getSegmentAt(index-1);
		// FGESegment intermediateCPNextSegment = polylin.getSegmentAt(index);

		// Declare new locations
		FGEPoint previousCPNewLocation = new FGEPoint(previousCPLocation);
		FGEPoint nextCPNewLocation = new FGEPoint(nextCPLocation);

		// Update previous control point location according to orientation of related segment
		if (previousSegmentOrientation.isHorizontal()) {
			previousCPNewLocation.y = newCPLocation.y;
		} else if (previousSegmentOrientation.isVertical()) {
			previousCPNewLocation.x = newCPLocation.x;
		} else {
			logger.warning("Inconsistent data: segment not horizontal nor vertical");
		}

		// If we don't modify general layout of connector,
		// control point will move from location previousCPLocation to previousCPNewLocation

		if (simplifyLayout) {
			// But meanwhile, we can also decide to change general shape by deleting some points.
			// This will happen if oldCPLocation and newCPLocation are each other
			// From both side of half-plane formed by intermediateCPStartSegment
			FGEHalfPlane intermediateCPStartSegmentHalfPlane = new FGEHalfPlane(intermediateCPStartSegment, oldCPLocation);
			if (!intermediateCPStartSegmentHalfPlane.containsPoint(newCPLocation)) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Two points will be removed (pattern 1) at index=" + (index - 1));
				}
				getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index - 1, newCPLocation);
				/*
				getPolylin().removePointAtIndex(index-1);
				getPolylin().removePointAtIndex(index-1);
				getConnector()._getControlPoints().remove(index-1);
				getConnector()._getControlPoints().remove(index-1);
				if (previousSegmentOrientation.isHorizontal()) {
				getPolylin().updatePointAt(index-2, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index-2).y));
				getPolylin().updatePointAt(index-1, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index-1).y));
				}
				else if (previousSegmentOrientation.isVertical()) {
				getPolylin().updatePointAt(index-2, new FGEPoint(getPolylin().getPointAt(index-2).x,newCPLocation.y));
				getPolylin().updatePointAt(index-1, new FGEPoint(getPolylin().getPointAt(index-1).x,newCPLocation.y));
				}
				getConnector().updateWithNewPolylin(getPolylin());*/
				return false;
			}

			if (index > 2) {
				// This may also happen if previousCPOldLocation and previousCPNewLocation are each other
				// From both side of half-plane formed by intermediateCPBeforeStartSegment
				FGESegment intermediateCPBeforeStartSegment = getPolylin().getSegmentAt(index - 3);
				FGEHalfPlane intermediateCPBeforeStartSegmentHalfPlane = new FGEHalfPlane(intermediateCPBeforeStartSegment,
						previousCPOldLocation);
				if (!intermediateCPBeforeStartSegmentHalfPlane.containsPoint(previousCPNewLocation)) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Two points will be removed (pattern 2) at index=" + (index - 2));
					}
					getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index - 2, newCPLocation);
					/*
					getPolylin().removePointAtIndex(index-2);
					getPolylin().removePointAtIndex(index-2);
					getConnector()._getControlPoints().remove(index-2);
					getConnector()._getControlPoints().remove(index-2);
					if (startSegmentOrientation.isVertical()) {
					getPolylin().updatePointAt(index-2, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index-2).y));
					getPolylin().updatePointAt(index-1, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index-1).y));
					}
					else if (startSegmentOrientation.isHorizontal()) {
					getPolylin().updatePointAt(index-2, new FGEPoint(getPolylin().getPointAt(index-2).x,newCPLocation.y));
					getPolylin().updatePointAt(index-1, new FGEPoint(getPolylin().getPointAt(index-1).x,newCPLocation.y));
					}
					getConnector().updateWithNewPolylin(getPolylin());*/
					return false;
				}
			}
		}

		getPolylin().updatePointAt(index - 1, previousCPNewLocation);
		getConnector().getControlPoints().get(index - 1).setPoint(previousCPNewLocation);

		// Update next control point location according to orientation of related segment
		if (nextSegmentOrientation.isHorizontal()) {
			nextCPNewLocation.y = newCPLocation.y;
		} else if (nextSegmentOrientation.isVertical()) {
			nextCPNewLocation.x = newCPLocation.x;
		} else {
			logger.warning("Inconsistent data: segment not horizontal nor vertical");
		}

		// If we don't modify general layout of connector,
		// control point will move from location nextCPLocation to nextCPNewLocation

		if (simplifyLayout) {

			// But meanwhile, we can also decide to change general shape by deleting some points.
			// This will happen if oldCPLocation and newCPLocation are each other
			// From both side of half-plane formed by intermediateCPEndSegment
			FGEHalfPlane intermediateCPEndSegmentHalfPlane = new FGEHalfPlane(intermediateCPEndSegment, oldCPLocation);
			if (!intermediateCPEndSegmentHalfPlane.containsPoint(newCPLocation)) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Two points will be removed (pattern 3) at index=" + index);
				}
				getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index, newCPLocation);
				/*
				getPolylin().removePointAtIndex(index);
				getPolylin().removePointAtIndex(index);
				getConnector()._getControlPoints().remove(index);
				getConnector()._getControlPoints().remove(index);
				if (nextSegmentOrientation.isHorizontal()) {
				getPolylin().updatePointAt(index-1, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index-1).y));
				getPolylin().updatePointAt(index, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index).y));
				}
				else if (nextSegmentOrientation.isVertical()) {
				getPolylin().updatePointAt(index-1, new FGEPoint(getPolylin().getPointAt(index-1).x,newCPLocation.y));
				getPolylin().updatePointAt(index, new FGEPoint(getPolylin().getPointAt(index).x,newCPLocation.y));
				}
				getConnector().updateWithNewPolylin(getPolylin());*/
				return false;
			}

			if (getPolylin().getSegmentNb() > index + 2) {
				// This may also happen if nextCPOldLocation and nextCPNewLocation are each other
				// From both side of half-plane formed by intermediateCPAfterEndSegment
				FGESegment intermediateCPAfterEndSegment = getPolylin().getSegmentAt(index + 2);
				FGEHalfPlane intermediateCPAfterEndSegmentHalfPlane = new FGEHalfPlane(intermediateCPAfterEndSegment, nextCPOldLocation);
				if (!intermediateCPAfterEndSegmentHalfPlane.containsPoint(nextCPNewLocation)) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Two points will be removed (pattern 4) at index=" + (index + 1));
					}
					getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index + 1, newCPLocation);
					/*
					getPolylin().removePointAtIndex(index+1);
					getPolylin().removePointAtIndex(index+1);
					getConnector()._getControlPoints().remove(index+1);
					getConnector()._getControlPoints().remove(index+1);
					if (endSegmentOrientation.isHorizontal()) {
					getPolylin().updatePointAt(index, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index).y));
					getPolylin().updatePointAt(index+1, new FGEPoint(newCPLocation.x,getPolylin().getPointAt(index+1).y));
					}
					else if (endSegmentOrientation.isVertical()) {
					getPolylin().updatePointAt(index, new FGEPoint(getPolylin().getPointAt(index).x,newCPLocation.y));
					getPolylin().updatePointAt(index+1, new FGEPoint(getPolylin().getPointAt(index+1).x,newCPLocation.y));
					}
					getConnector().updateWithNewPolylin(getPolylin());*/
					return false;
				}
			}
		}

		getPolylin().updatePointAt(index + 1, nextCPNewLocation);
		getConnector().getControlPoints().get(index + 1).setPoint(nextCPNewLocation);

		/*if (!getPolylin().isNormalized()) {
			getConnector().updateWithNewPolylin(getPolylin(), true);
		}*/

		return true;
	}

}