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

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.geom.area.FGESubstractionArea;


public class AdjustableFirstControlPoint extends RectPolylinAdjustableControlPoint
{
	static final Logger logger = Logger.getLogger(AdjustableFirstControlPoint.class.getPackage().getName());

	private SimplifiedCardinalDirection currentStartOrientation = null;

	public AdjustableFirstControlPoint(FGEPoint point, RectPolylinConnector connector)
	{
		super(point,connector);
	}

	@Override
	public FGEArea getDraggingAuthorizedArea()
	{
		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getGraphicalRepresentation().getStartObject(), getGraphicalRepresentation());
		FGEArea startArea = getGraphicalRepresentation().getStartObject().getShape().getShape().transform(at1);
		return new FGESubstractionArea(new FGEPlane(),startArea,false);
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
	{
		FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		if (pt == null) {
			logger.warning("Cannot nearest point for point "+newRelativePoint+" and area "+getDraggingAuthorizedArea());
			return false;
		}
		// Following little hack is used here to prevent some equalities that may
		// lead to inconsistent orientations
		//pt.x += FGEPoint.EPSILON;
		//pt.y += FGEPoint.EPSILON;
		setPoint(pt);
		getPolylin().updatePointAt(1,pt);
		movedFirstCP();				
		getConnector()._connectorChanged(true);
		getGraphicalRepresentation().notifyConnectorChanged();
		return true;
	}

	/**
	 * This method is internally called when first control point has been detected to be moved.
	 *
	 */
	private void movedFirstCP()
	{
		FGEArea startArea = getConnector().retrieveAllowedStartArea(false);
		
		if (getConnector().getIsStartingLocationFixed() 
				&& !getConnector().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = getConnector().retrieveStartArea();
		}
						
		FGEPoint newFirstCPLocation = getPoint(); //polylin.getPointAt(1);
		FGEPoint nextCPLocation = initialPolylin.getPointAt(2);
		SimplifiedCardinalDirection initialStartOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
		SimplifiedCardinalDirection initialFirstOrientation = initialPolylin.getApproximatedOrientationOfSegment(1);
		SimplifiedCardinalDirection initialNextOrientation = (initialPolylin.getSegmentNb() > 2 ? initialPolylin.getApproximatedOrientationOfSegment(2) : null);
		
		if (startArea.getOrthogonalPerspectiveArea(initialStartOrientation).containsPoint(newFirstCPLocation)) {
			
			// OK, the new location will not modify general structure of connector
			FGEPoint newStartPoint = startArea.nearestPointFrom(newFirstCPLocation, initialStartOrientation.getOpposite());
			if (newStartPoint == null) {
				logger.warning("Could not find nearest point from "+newFirstCPLocation+" on "+startArea+" following orientation "+initialStartOrientation.getOpposite());
				newStartPoint = startArea.getNearestPoint(newFirstCPLocation);
			}
			getPolylin().updatePointAt(0, newStartPoint);
			getConnector().getStartControlPoint().setPoint(newStartPoint);
			if (getConnector().getIsStartingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedStartLocation(
						GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), newStartPoint, getGraphicalRepresentation().getStartObject()));
			}

			if (initialPolylin.getSegmentNb() > 3) {
				FGESegment oppositeSegment = initialPolylin.getSegmentAt(2);
				FGERectPolylin appendingPath1 = new FGERectPolylin(
						newFirstCPLocation,initialFirstOrientation,
						oppositeSegment.getP2(),initialPolylin.getApproximatedOrientationOfSegment(2).getOpposite(),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath2 = new FGERectPolylin(
						newFirstCPLocation,initialFirstOrientation,
						oppositeSegment.getP2(),initialPolylin.getApproximatedOrientationOfSegment(2),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath = (appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ?
						appendingPath1 : appendingPath2);

				//debugPolylin = appendingPath;

				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1, appendingPath.getPointsNb()-1);

				mergedPolylin = getConnector().mergePolylins(
						mergedPolylin, 0, mergedPolylin.getPointsNb()-1, 
						initialPolylin, 4, initialPolylin.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin,true);
			}		

			else { // We go directely to end point, we have to preserve direction
				FGERectPolylin appendingPath = new FGERectPolylin(
						newFirstCPLocation,initialFirstOrientation,
						initialPolylin.getSegmentAt(2).getP2(),initialNextOrientation.getOpposite(),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1, appendingPath.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin,true);
			}

			currentStartOrientation = initialStartOrientation;

		}

		else {

			
			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			//SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : getConnector().getPrimitiveAllowedStartOrientations()) {
				if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(newFirstCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.
				// I don't want to hide the thuth: this is NOT good...

				// We keep here initial start orientation
				if (currentStartOrientation == null) currentStartOrientation = initialStartOrientation;
				orientation = currentStartOrientation;

				if (!getConnector().getAllowedStartOrientations().contains(orientation) 
						&& getConnector().getAllowedStartOrientations().size() > 0) {
					orientation = getConnector().getAllowedStartOrientations().firstElement();
				}
				
				/*if (startArea.containsPoint(newFirstCPLocation)) {
					orientation = currentStartOrientation;
					alternativeOrientation = currentStartOrientation;
				}
				else {
					CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),newFirstCPLocation);
					orientation = quadrant.getHorizonalComponent();
					alternativeOrientation = quadrant.getVerticalComponent();
				}*/
				
				//CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),newFirstCPLocation);
				//orientation = currentStartOrientation;
				/*if (quadrant.getHorizonalComponent() == currentStartOrientation) {
					alternativeOrientation = quadrant.getVerticalComponent();
				}
				else {
					alternativeOrientation = quadrant.getHorizonalComponent();
				}*/
				

				//System.out.println("orientation="+orientation+" alternativeOrientation="+alternativeOrientation);

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of start area regarding orientation
				FGEPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(newFirstCPLocation);

				// Compute path to append
				FGERectPolylin appendingPath;
				/*FGERectPolylin appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
						newStartPosition, nextCPLocation, newFirstCPLocation, 
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation),
						SimplifiedCardinalDirection.allDirectionsExcept(initialNextOrientation.getOpposite()));*/
				if (initialPolylin.getSegmentNb() > 3) {
					FGESegment oppositeSegment = initialPolylin.getSegmentAt(2);
					 appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
								newStartPosition, oppositeSegment.getP2(), newFirstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());				
				}
				else {
					 appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
							newStartPosition, nextCPLocation, newFirstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				}

				// Merge polylin
				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-1, initialPolylin, 3, initialPolylin.getPointsNb()-1);

				//System.out.println("mergedPolylin="+mergedPolylin);
				//System.out.println("L'orientation est: "+mergedPolylin.getSegmentAt(0).getApproximatedOrientation());
				
				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin,true);

			}
			else {
				// OK, we have found a cardinal direction in which it is possible to 
				// project dragged control point

				// Compute new start position by projecting dragged control point
				// related to orientation
				
				FGEPoint newStartPosition = startArea.nearestPointFrom(newFirstCPLocation, orientation.getOpposite());
				if (newStartPosition == null) {
					logger.warning("Could not find nearest point from "+newFirstCPLocation+" on "+startArea+" following orientation "+initialStartOrientation.getOpposite());
					newStartPosition = startArea.getNearestPoint(newFirstCPLocation);
				}
				getPolylin().updatePointAt(0, newStartPosition);
				getConnector().getStartControlPoint().setPoint(newStartPosition);
				if (getConnector().getIsStartingLocationFixed()) { // Don't forget this !!!
					getConnector().setFixedStartLocation(
							GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), newStartPosition, getGraphicalRepresentation().getStartObject()));
				}

				
				//FGEPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(newFirstCPLocation);

				// Compute path to append
				FGERectPolylin appendingPath ;

				if (initialPolylin.getSegmentNb() > 3) {
					FGESegment oppositeSegment = initialPolylin.getSegmentAt(2);
					 appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
								newStartPosition, oppositeSegment.getP2(), newFirstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());				
				}
				else {
					 appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
							newStartPosition, nextCPLocation, newFirstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				}
				
				//getConnector().debugPolylin = appendingPath;
				
				// Merge polylin
				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-1, initialPolylin, 3, initialPolylin.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin,true);

				currentStartOrientation = orientation;

			}
		}
	}

	/**
	 * This method is internally called when first control point has been detected to be moved.
	 *
	 */
	/*private void movedFirstCP()
	{

		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getStartObject(), getGraphicalRepresentation());
		FGEArea startArea = getConnector().getStartObject().getShape().getOutline().transform(at1);

		FGEPoint firstCPLocation = getPoint(); //polylin.getPointAt(1);
		FGEPoint nextCPLocation = initialPolylin.getPointAt(2);
		SimplifiedCardinalDirection initialStartOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
		SimplifiedCardinalDirection initialFirstOrientation = initialPolylin.getApproximatedOrientationOfSegment(1);
		SimplifiedCardinalDirection initialNextOrientation = (initialPolylin.getSegmentNb() > 2 ? initialPolylin.getApproximatedOrientationOfSegment(2) : null);


		if (startArea.getOrthogonalPerspectiveArea(initialStartOrientation).containsPoint(firstCPLocation)) {
			// OK, the new location will not modify general structure of connector
			FGEPoint newPoint = new FGEPoint(getPolylin().getPointAt(0));
			if (initialStartOrientation.isHorizontal()) {
				newPoint.setY(firstCPLocation.y);
			}
			else if (initialStartOrientation.isVertical()) {
				newPoint.setX(firstCPLocation.x);
			}
			getPolylin().updatePointAt(0, newPoint);
			getConnector().getStartControlPoint().setPoint(newPoint);


			if (initialPolylin.getSegmentNb() > 3) {
				FGESegment oppositeSegment = initialPolylin.getSegmentAt(2);
				FGERectPolylin appendingPath1 = new FGERectPolylin(
						firstCPLocation,initialFirstOrientation,
						oppositeSegment.getP2(),initialPolylin.getApproximatedOrientationOfSegment(2).getOpposite(),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath2 = new FGERectPolylin(
						firstCPLocation,initialFirstOrientation,
						oppositeSegment.getP2(),initialPolylin.getApproximatedOrientationOfSegment(2),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				FGERectPolylin appendingPath = (appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ?
						appendingPath1 : appendingPath2);

				//debugPolylin = appendingPath;

				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1, appendingPath.getPointsNb()-1);

				mergedPolylin = getConnector().mergePolylins(
						mergedPolylin, 0, mergedPolylin.getPointsNb()-1, 
						initialPolylin, 4, initialPolylin.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);
			}		

			else { // We go directely to end point, we have to preserve direction
				FGERectPolylin appendingPath = new FGERectPolylin(
						firstCPLocation,initialFirstOrientation,
						initialPolylin.getSegmentAt(2).getP2(),initialNextOrientation.getOpposite(),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1, appendingPath.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);
			}

			currentStartOrientation = initialStartOrientation;

		}

		else {

			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
				if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(firstCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.


				// We keep here initial start orientation
				if (currentStartOrientation == null) currentStartOrientation = initialStartOrientation;
				orientation = currentStartOrientation;

				if (startArea.containsPoint(firstCPLocation)) {
					orientation = currentStartOrientation;
					alternativeOrientation = currentStartOrientation;
				}
				else {
					CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),firstCPLocation);
					orientation = quadrant.getHorizonalComponent();
					alternativeOrientation = quadrant.getVerticalComponent();
				}

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of start area regarding orientation
				FGEPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(firstCPLocation);

				// Compute path to append
				FGERectPolylin appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
						newStartPosition, nextCPLocation, firstCPLocation, 
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation,alternativeOrientation),
						SimplifiedCardinalDirection.allDirectionsExcept(initialNextOrientation.getOpposite()));

				// Merge polylin
				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-1, initialPolylin, 3, initialPolylin.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

			}
			else {
				// OK, we have found a cardinal direction in which it is possible to 
				// project dragged control point

				// Compute new start position by projecting dragged control point
				// related to orientation
				FGEPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(firstCPLocation);

				// Compute path to append
				FGERectPolylin appendingPath = FGERectPolylin.makeRectPolylinCrossingPoint(
						newStartPosition, nextCPLocation, firstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				// Merge polylin
				FGERectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-1, initialPolylin, 3, initialPolylin.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

				currentStartOrientation = orientation;

			}
		}
	}*/


}
