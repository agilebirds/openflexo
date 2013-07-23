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

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.impl.RectPolylinConnector;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.geom.area.FGESubstractionArea;

public class AdjustableLastControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger logger = Logger.getLogger(AdjustableLastControlPoint.class.getPackage().getName());

	private SimplifiedCardinalDirection currentEndOrientation = null;

	public AdjustableLastControlPoint(FGEPoint point, RectPolylinConnector connector) {
		super(point, connector);
	}

	@Override
	public FGEArea getDraggingAuthorizedArea() {
		AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(getNode().getEndNode(), getNode());
		FGEArea endArea = getNode().getEndNode().getGraphicalRepresentation().getShape().getShape(getNode().getEndNode()).transform(at2);
		return new FGESubstractionArea(new FGEPlane(), endArea, false);
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		if (pt == null) {
			logger.warning("Cannot nearest point for point " + newRelativePoint + " and area " + getDraggingAuthorizedArea());
			return false;
		}
		// Following little hack is used here to prevent some equalities that may
		// lead to inconsistent orientations
		// pt.x += FGEPoint.EPSILON;
		// pt.y += FGEPoint.EPSILON;
		setPoint(pt);
		getPolylin().updatePointAt(getPolylin().getPointsNb() - 2, pt);
		movedLastCP();
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return true;
	}

	/**
	 * This method is internally called when last control point has been detected to be moved.
	 * 
	 */
	private void movedLastCP() {
		FGEArea endArea = getConnector().retrieveAllowedEndArea(false);

		if (getConnectorSpecification().getIsEndingLocationFixed() && !getConnectorSpecification().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = getConnector().retrieveEndArea();
		}

		FGEPoint newLastCPLocation = getPoint();
		FGEPoint previousCPLocation = initialPolylin.getPointAt(initialPolylin.getPointsNb() - 3);
		SimplifiedCardinalDirection initialEndOrientation = initialPolylin.getApproximatedOrientationOfSegment(
				initialPolylin.getSegmentNb() - 1).getOpposite();
		SimplifiedCardinalDirection initialLastOrientation = initialPolylin.getApproximatedOrientationOfSegment(
				initialPolylin.getSegmentNb() - 2).getOpposite();
		SimplifiedCardinalDirection initialPreviousOrientation = initialPolylin.getSegmentNb() > 2 ? initialPolylin
				.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb() - 3).getOpposite() : null;

		if (endArea.getOrthogonalPerspectiveArea(initialEndOrientation).containsPoint(newLastCPLocation)) {
			// OK, the new location will not modify general structure of connector
			FGEPoint newEndPoint = endArea.nearestPointFrom(newLastCPLocation, initialEndOrientation.getOpposite());
			if (newEndPoint == null) {
				logger.warning("Could not find nearest point from " + newLastCPLocation + " on " + endArea + " following orientation "
						+ initialEndOrientation.getOpposite());
				newEndPoint = endArea.getNearestPoint(newLastCPLocation);
			}
			getPolylin().updatePointAt(getPolylin().getPointsNb() - 1, newEndPoint);
			getConnector().getEndControlPoint().setPoint(newEndPoint);
			if (getConnectorSpecification().getIsEndingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedEndLocation(FGEUtils.convertNormalizedPoint(getNode(), newEndPoint, getNode().getEndNode()));
			}

			if (initialPolylin.getSegmentNb() > 3) {
				FGESegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3);
				FGERectPolylin appendingPath1 = new FGERectPolylin(oppositeSegment.getP1(), initialPolylin
						.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb() - 3).getOpposite(), newLastCPLocation,
						initialLastOrientation, true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector()
								.getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath2 = new FGERectPolylin(oppositeSegment.getP1(),
						initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb() - 3), newLastCPLocation,
						initialLastOrientation, true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector()
								.getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath = appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ? appendingPath1
						: appendingPath2;

				FGERectPolylin mergedPolylin = getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2,
						getPolylin(), getPolylin().getPointsNb() - 2, getPolylin().getPointsNb() - 1);

				mergedPolylin = getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 5, mergedPolylin, 0,
						mergedPolylin.getPointsNb() - 1);

				getConnector().updateWithNewPolylin(mergedPolylin, true);
			}

			else { // We go directely to end point, we have to preserve direction
				FGERectPolylin appendingPath = new FGERectPolylin(initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3).getP1(),
						initialPreviousOrientation.getOpposite(), newLastCPLocation, initialLastOrientation, true, getConnector()
								.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				FGERectPolylin mergedPolylin = getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2,
						getPolylin(), getPolylin().getPointsNb() - 2, getPolylin().getPointsNb() - 1);

				getConnector().updateWithNewPolylin(mergedPolylin, true);

			}

			currentEndOrientation = initialEndOrientation;

		}

		else {
			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			// SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : getConnector().getPrimitiveAllowedEndOrientations()) {
				if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(newLastCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.
				// I don't want to hide the thuth: this is NOT good...

				// We keep here initial start orientation
				if (currentEndOrientation == null) {
					currentEndOrientation = initialEndOrientation;
				}
				orientation = currentEndOrientation;

				if (!getConnector().getAllowedEndOrientations().contains(orientation)
						&& getConnector().getAllowedEndOrientations().size() > 0) {
					orientation = getConnector().getAllowedEndOrientations().firstElement();
				}

				/*if (endArea.containsPoint(newLastCPLocation)) {
						orientation = currentEndOrientation;
						alternativeOrientation = currentEndOrientation;
					}
					else {
						CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(getPolylin().getLastPoint(),newLastCPLocation);
						orientation = quadrant.getHorizonalComponent();
						alternativeOrientation = quadrant.getVerticalComponent();
					}*/

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of end area regarding orientation
				FGEPoint newEndPosition = endArea.getAnchorAreaFrom(orientation).getNearestPoint(newLastCPLocation);

				// Compute path to append
				/*FGERectPolylin appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
						previousCPLocation, newEndPosition, newLastCPLocation,
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(initialPreviousOrientation.getOpposite()),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation));*/

				FGERectPolylin appendingPath;

				if (initialPolylin.getSegmentNb() > 3) {
					FGESegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3);
					appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(oppositeSegment.getP1(), newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				} else {
					appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(previousCPLocation, newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				}
				// debugPolylin = appendingPath;

				// Merge polylin
				FGERectPolylin mergedPolylin = getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 4,
						appendingPath, 0, appendingPath.getPointsNb() - 1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin, true);

			} else {
				// OK, we have found a cardinal direction in which it is possible to
				// project dragged control point

				// Compute new end position by projecting dragged control point
				// related to orientation
				FGEPoint newEndPosition = endArea.nearestPointFrom(newLastCPLocation, orientation.getOpposite());
				if (newEndPosition == null) {
					logger.warning("Could not find nearest point from " + newLastCPLocation + " on " + endArea + " following orientation "
							+ initialEndOrientation.getOpposite());
					newEndPosition = endArea.getNearestPoint(newLastCPLocation);
				}
				getPolylin().updatePointAt(getPolylin().getPointsNb() - 1, newEndPosition);
				getConnector().getEndControlPoint().setPoint(newEndPosition);
				if (getConnectorSpecification().getIsEndingLocationFixed()) { // Don't forget this !!!
					getConnectorSpecification().setFixedEndLocation(
							FGEUtils.convertNormalizedPoint(getNode(), newEndPosition, getNode().getEndNode()));
				}

				// Compute path to append
				FGERectPolylin appendingPath;

				if (initialPolylin.getSegmentNb() > 3) {
					FGESegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3);
					appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(oppositeSegment.getP1(), newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				} else {
					appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(previousCPLocation, newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				}

				// getConnector().debugPolylin = appendingPath;

				// Merge polylin
				FGERectPolylin mergedPolylin = getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 4,
						appendingPath, 0, appendingPath.getPointsNb() - 1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin, true);

				currentEndOrientation = orientation;
			}
		}
	}

	/**
	 * This method is internally called when last control point has been detected to be moved.
	 * 
	 */
	/*private void movedLastCP()
	{

		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getEndObject(), getGraphicalRepresentation());
		FGEArea endArea = getConnector().getEndObject().getShape().getOutline().transform(at2);

		FGEPoint lastCPLocation = getPoint();
		FGEPoint previousCPLocation = initialPolylin.getPointAt(initialPolylin.getPointsNb()-3);
		SimplifiedCardinalDirection initialEndOrientation = initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-1).getOpposite();
		SimplifiedCardinalDirection initialLastOrientation = initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-2).getOpposite();
		SimplifiedCardinalDirection initialPreviousOrientation = (initialPolylin.getSegmentNb() > 2 ? initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-3).getOpposite() : null);


		if (endArea.getOrthogonalPerspectiveArea(initialEndOrientation).containsPoint(lastCPLocation)) {
			// OK, the new location will not modify general structure of connector
			FGEPoint newPoint = new FGEPoint(getPolylin().getLastPoint());
			if (initialEndOrientation.isHorizontal()) {
				newPoint.setY(lastCPLocation.y);
			}
			else if (initialEndOrientation.isVertical()) {
				newPoint.setX(lastCPLocation.x);
			}
			getPolylin().updatePointAt(getPolylin().getPointsNb()-1, newPoint);
			getConnector().getEndControlPoint().setPoint(newPoint);

			if (initialPolylin.getSegmentNb() > 3) {
				FGESegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb()-3);
				FGERectPolylin appendingPath1 = new FGERectPolylin(
						oppositeSegment.getP1(),initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-3).getOpposite(),
						lastCPLocation,initialLastOrientation,
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath2 = new FGERectPolylin(
						oppositeSegment.getP1(),initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-3),
						lastCPLocation,initialLastOrientation,
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath = (appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ?
						appendingPath1 : appendingPath2);


				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-2, getPolylin(), getPolylin().getPointsNb()-2, getPolylin().getPointsNb()-1 );

				mergedPolylin = getConnector().mergePolylins(
						initialPolylin, 0, initialPolylin.getPointsNb()-5,
						mergedPolylin, 0, mergedPolylin.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);
			}		

			else { // We go directely to end point, we have to preserve direction
				FGERectPolylin appendingPath = new FGERectPolylin(
						initialPolylin.getSegmentAt(initialPolylin.getSegmentNb()-3).getP1(),initialPreviousOrientation.getOpposite(),
						lastCPLocation,initialLastOrientation,
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-2, getPolylin(), getPolylin().getPointsNb()-2,getPolylin().getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);

			}

			currentEndOrientation = initialEndOrientation;

		}

		else {
			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
				if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(lastCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.

				// We keep here initial start orientation
				if (currentEndOrientation == null) currentEndOrientation = initialEndOrientation;
				orientation = currentEndOrientation;

				if (endArea.containsPoint(lastCPLocation)) {
					orientation = currentEndOrientation;
					alternativeOrientation = currentEndOrientation;
				}
				else {
					CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(getPolylin().getLastPoint(),lastCPLocation);
					orientation = quadrant.getHorizonalComponent();
					alternativeOrientation = quadrant.getVerticalComponent();
				}

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of end area regarding orientation
				FGEPoint newEndPosition = endArea.getAnchorAreaFrom(orientation).getNearestPoint(lastCPLocation);

				// Compute path to append
				FGERectPolylin appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
						previousCPLocation, newEndPosition, lastCPLocation,
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(initialPreviousOrientation.getOpposite()),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation,alternativeOrientation));

				//debugPolylin = appendingPath;

				// Merge polylin
				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb()-4,appendingPath, 0, appendingPath.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

			}
			else {
				// OK, we have found a cardinal direction in which it is possible to 
				// project dragged control point

				// Compute new end position by projecting dragged control point
				// related to orientation
				FGEPoint newEndPosition = endArea.getAnchorAreaFrom(orientation).getNearestPoint(lastCPLocation);

				// Compute path to append
				FGERectPolylin appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
						previousCPLocation, newEndPosition, lastCPLocation, initialPreviousOrientation.getOpposite(), orientation, true, 
						getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				//debugPolylin = appendingPath;

				// Merge polylin
				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb()-4,appendingPath, 0, appendingPath.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

				currentEndOrientation = orientation;
			}
		}
	}*/

}
