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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentationUtils;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;

public class AdjustableMiddleControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger logger = Logger.getLogger(AdjustableMiddleControlPoint.class.getPackage().getName());

	public AdjustableMiddleControlPoint(FGEPoint point, RectPolylinConnector connector) {
		super(point, connector);
	}

	@Override
	public FGEArea getDraggingAuthorizedArea() {
		return new FGEPlane();
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
		pt.x += FGEGeometricObject.EPSILON;
		pt.y += FGEGeometricObject.EPSILON;
		setPoint(pt);
		getPolylin().updatePointAt(1, pt);
		movedMiddleCP();
		getConnector()._connectorChanged(true);
		getGraphicalRepresentation().notifyConnectorChanged();
		return true;
	}

	private void movedMiddleCP() {
		FGEArea startArea = getConnector().retrieveAllowedStartArea(false);

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getConnector().getPrimitiveAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getConnector().getPrimitiveAllowedEndOrientations();

		if (getConnector().getIsStartingLocationFixed() && !getConnector().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = getConnector().retrieveStartArea();
			allowedStartOrientations = getConnector().getAllowedStartOrientations();
		}

		FGEArea endArea = getConnector().retrieveAllowedEndArea(false);

		if (getConnector().getIsEndingLocationFixed() && !getConnector().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = getConnector().retrieveEndArea();
			allowedEndOrientations = getConnector().getAllowedEndOrientations();
		}

		/*System.out.println("startArea="+startArea);
		System.out.println("endArea="+endArea);
		System.out.println("allowedStartOrientations="+allowedStartOrientations);
		System.out.println("allowedEndOrientations="+allowedEndOrientations);*/

		FGERectPolylin newPolylin;

		FGEPoint middleCPLocation = getPoint();

		newPolylin = FGERectPolylin.makeRectPolylinCrossingPoint(startArea, endArea, middleCPLocation, true, getConnector()
				.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(), SimplifiedCardinalDirection
				.allDirectionsExcept(allowedStartOrientations), SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));

		if (newPolylin == null) {
			logger.warning("Obtained null polylin allowedStartOrientations=" + allowedStartOrientations);
			return;
		}

		if (getConnector().getIsStartingLocationFixed()) { // Don't forget this !!!
			getConnector().setFixedStartLocation(
					GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(), newPolylin.getFirstPoint(),
							getGraphicalRepresentation().getStartObject()));
		}
		if (getConnector().getIsEndingLocationFixed()) { // Don't forget this !!!
			getConnector().setFixedEndLocation(
					GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(), newPolylin.getLastPoint(),
							getGraphicalRepresentation().getEndObject()));
		}

		if (newPolylin.isNormalized()) {
			getConnector().updateWithNewPolylin(newPolylin, true, false);
		} else {
			logger.warning("Computed layout returned a non-normalized polylin. Please investigate");
			getConnector().updateWithNewPolylin(newPolylin, false, false);
		}

	}

	/**
	 * This method is internally called when first control point has been detected to be moved.
	 * 
	 */
	/*private void movedMiddleCP()
	{

		FGEArea startArea = getConnector().retrieveAllowedStartArea(false);
		
		if (getConnector().getIsStartingLocationFixed() 
				&& !getConnector().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = getConnector().retrieveStartArea();
		}
						
		FGEArea endArea = getConnector().retrieveAllowedEndArea(false);
		
		if (getConnector().getIsEndingLocationFixed() 
				&& !getConnector().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = getConnector().retrieveEndArea();
		}
		
		FGEPoint middleCPLocation = getPoint();
		//SimplifiedCardinalDirection initialStartOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
		//SimplifiedCardinalDirection initialEndOrientation = initialPolylin.getApproximatedOrientationOfSegment(1).getOpposite();

		// Try to find a cardinal direction in which it is possible to project
		// dragged control point
		SimplifiedCardinalDirection startOrientation = null;
		SimplifiedCardinalDirection altStartOrientation = null;
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(middleCPLocation)) {
				startOrientation = o;
			}
		}
		if (startOrientation == null) {
			CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),middleCPLocation);			
			startOrientation = quadrant.getHorizonalComponent();
			altStartOrientation = quadrant.getVerticalComponent();
		}

		// Try to find a cardinal direction in which it is possible to project
		// dragged control point
		SimplifiedCardinalDirection endOrientation = null;
		SimplifiedCardinalDirection altEndOrientation = null;
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(middleCPLocation)) {
				endOrientation = o;
			}
		}
		if (endOrientation == null) {
			CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(middleCPLocation, getPolylin().getLastPoint());			
			endOrientation = quadrant.getHorizonalComponent().getOpposite();
			altEndOrientation = quadrant.getVerticalComponent().getOpposite();
		}



		if (startArea.getOrthogonalPerspectiveArea(startOrientation).containsPoint(middleCPLocation)) {
			// OK, the new location will not modify general structure of connector
			FGEPoint newPoint = new FGEPoint(getPolylin().getFirstPoint());
			if (startOrientation.isHorizontal()) {
				newPoint.setY(middleCPLocation.y);
			}
			else if (startOrientation.isVertical()) {
				newPoint.setX(middleCPLocation.x);
			}
			getPolylin().updatePointAt(0, newPoint);
			getConnector().getStartControlPoint().setPoint(newPoint);
			if (getConnector().getIsStartingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedStartLocation(
						GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(), newPoint, getGraphicalRepresentation().getStartObject()));
			}
		}

		if (endArea.getOrthogonalPerspectiveArea(endOrientation).containsPoint(middleCPLocation)) {
			// OK, the new location will not modify general structure of connector
			FGEPoint newPoint = new FGEPoint(getPolylin().getLastPoint());
			if (endOrientation.isHorizontal()) {
				newPoint.setY(middleCPLocation.y);
			}
			else if (endOrientation.isVertical()) {
				newPoint.setX(middleCPLocation.x);
			}
			getPolylin().updatePointAt(getPolylin().getPointsNb()-1, newPoint);
			getConnector().getEndControlPoint().setPoint(newPoint);
			if (getConnector().getIsEndingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedEndLocation(
						GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(), newPoint, getGraphicalRepresentation().getEndObject()));
			}
		}

		System.out.println("Tiens, je sais pas quoi faire...");
		
		int alternatives = 1;
		if (altStartOrientation != null) alternatives=alternatives*2;
		if (altEndOrientation != null) alternatives=alternatives*2;
		FGERectPolylin[] newPolylins = new FGERectPolylin[alternatives]; 

		int n=0;
		newPolylins[n++] 
		            = FGERectPolylin.makeRectPolylinCrossingPoint(
		            		getPolylin().getFirstPoint(), 
		            		getPolylin().getLastPoint(), 
		            		middleCPLocation, 
		            		startOrientation,
		            		endOrientation,
		            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

		if (altStartOrientation != null) {
			newPolylins[n++] 
			            = FGERectPolylin.makeRectPolylinCrossingPoint(
			            		getPolylin().getFirstPoint(), 
			            		getPolylin().getLastPoint(), 
			            		middleCPLocation, 
			            		altStartOrientation,
			            		endOrientation,
			            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
			if (altEndOrientation != null) {
				newPolylins[n++] 
				            = FGERectPolylin.makeRectPolylinCrossingPoint(
				            		getPolylin().getFirstPoint(), 
				            		getPolylin().getLastPoint(), 
				            		middleCPLocation, 
				            		altStartOrientation,
				            		altEndOrientation,
				            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
			}
		}
		if (altEndOrientation != null) {
			newPolylins[n++] 
			            = FGERectPolylin.makeRectPolylinCrossingPoint(
			            		getPolylin().getFirstPoint(), 
			            		getPolylin().getLastPoint(), 
			            		middleCPLocation, 
			            		startOrientation,
			            		altEndOrientation,
			            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
		}

		FGERectPolylin newPolylin = null;

		for (int i=0; i<alternatives; i++) {
			if (!newPolylins[i].crossedItSelf() && newPolylin == null) 
				newPolylin = newPolylins[i];
		}
		if (newPolylin == null) newPolylin = newPolylins[0];


		getConnector().updateWithNewPolylin(newPolylin,true);

	}
	*/

}