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
import java.util.logging.Logger;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfBand;


public class AdjustableIntermediateSegment extends RectPolylinAdjustableSegment
{
	static final Logger logger = Logger.getLogger(AdjustableIntermediateSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private int index;
	private FGESegment currentSegment;
	private FGESegment previousSegment;
	private FGESegment nextSegment;
	private FGESegment beforePreviousSegment;
	private FGESegment afterNextSegment;
	private SimplifiedCardinalDirection currentOrientation;
	private SimplifiedCardinalDirection previousOrientation;
	private SimplifiedCardinalDirection nextOrientation;
	private FGEArea draggingAuthorizedArea;

	private void retrieveInfos()
	{
		currentSegment = getArea();
		index = getPolylin().getSegmentIndex(currentSegment);
		if (index <= 0 || index >= getPolylin().getSegmentNb()-1) {
			RectPolylinConnector.logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector "+getGraphicalRepresentation().getText()+" index="+index+" polylin.getSegmentNb()="+getPolylin().getSegmentNb());
			return;
		}
		previousSegment = getPolylin().getSegmentAt(index-1);
		nextSegment = getPolylin().getSegmentAt(index+1);
		if (currentSegment.getApproximatedOrientation() == null
				|| previousSegment.getApproximatedOrientation() == null
				|| nextSegment.getApproximatedOrientation() == null) {
			RectPolylinConnector.logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
			return;
		}
		if (index > 1) beforePreviousSegment = getPolylin().getSegmentAt(index-2);
		if (index+2 < getPolylin().getSegmentNb()) afterNextSegment = getPolylin().getSegmentAt(index+2);
		currentOrientation = currentSegment.getApproximatedOrientation();
		previousOrientation = previousSegment.getApproximatedOrientation();
		nextOrientation = nextSegment.getApproximatedOrientation();
		
	 	if (currentOrientation.isHorizontal()) {
			if (previousOrientation == SimplifiedCardinalDirection.NORTH) {
				if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
					draggingAuthorizedArea = new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
					FGESegment limit;						
					if (previousSegment.getP1().y>nextSegment.getP2().y) {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,nextSegment.getP2().y),
								new FGEPoint(currentSegment.getP2().x,nextSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,previousSegment.getP1().y),
								new FGEPoint(currentSegment.getP2().x,previousSegment.getP1().y));
					}
					draggingAuthorizedArea = new FGEHalfBand(limit,SimplifiedCardinalDirection.NORTH);
				}
			}
			else if (previousOrientation == SimplifiedCardinalDirection.SOUTH) {
				if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
					draggingAuthorizedArea = new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
					FGESegment limit;						
					if (previousSegment.getP1().y<nextSegment.getP2().y) {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,nextSegment.getP2().y),
								new FGEPoint(currentSegment.getP2().x,nextSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,previousSegment.getP1().y),
								new FGEPoint(currentSegment.getP2().x,previousSegment.getP1().y));
					}
					draggingAuthorizedArea = new FGEHalfBand(limit,SimplifiedCardinalDirection.SOUTH);
				}
			}
		}
		if (currentOrientation.isVertical()) {
			if (previousOrientation == SimplifiedCardinalDirection.EAST) {
				if (nextOrientation == SimplifiedCardinalDirection.EAST) {
					draggingAuthorizedArea = new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.WEST) {
					FGESegment limit;						
					if (previousSegment.getP1().x<nextSegment.getP2().x) {
						limit = new FGESegment(
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP1().y),
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP1().y),
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP2().y));
					}
					draggingAuthorizedArea = new FGEHalfBand(limit,SimplifiedCardinalDirection.EAST);
				}
			}
			else if (previousOrientation == SimplifiedCardinalDirection.WEST) {
				if (nextOrientation == SimplifiedCardinalDirection.WEST) {
					draggingAuthorizedArea = new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.EAST) {
					FGESegment limit;						
					if (previousSegment.getP1().x>nextSegment.getP2().x) {
						limit = new FGESegment(
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP1().y),
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP1().y),
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP2().y));
					}
					draggingAuthorizedArea = new FGEHalfBand(limit,SimplifiedCardinalDirection.WEST);
				}
			}
		}
		
		if (draggingAuthorizedArea == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
			return;
		}
		
		consistentData = true;
	}


	public AdjustableIntermediateSegment(FGESegment segment, RectPolylinConnector connector)
	{
		super(segment, connector);
		retrieveInfos();
	}

	@Override
	public void startDragging(DrawingController controller, FGEPoint startPoint)
	{
		super.startDragging(controller, startPoint);
		retrieveInfos();
	}

	@Override
	public FGEArea getDraggingAuthorizedArea()
	{
		if (!consistentData)
			return new FGEEmptyArea();
		
		return draggingAuthorizedArea;
	/*
	 	if (currentOrientation.isHorizontal()) {
			if (previousOrientation == SimplifiedCardinalDirection.NORTH) {
				if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
					return new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
					FGESegment limit;						
					if (previousSegment.getP1().y>nextSegment.getP2().y) {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,nextSegment.getP2().y),
								new FGEPoint(currentSegment.getP2().x,nextSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,previousSegment.getP1().y),
								new FGEPoint(currentSegment.getP2().x,previousSegment.getP1().y));
					}
					return new FGEHalfBand(limit,SimplifiedCardinalDirection.NORTH);
				}
			}
			else if (previousOrientation == SimplifiedCardinalDirection.SOUTH) {
				if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
					return new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
					FGESegment limit;						
					if (previousSegment.getP1().y<nextSegment.getP2().y) {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,nextSegment.getP2().y),
								new FGEPoint(currentSegment.getP2().x,nextSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(currentSegment.getP1().x,previousSegment.getP1().y),
								new FGEPoint(currentSegment.getP2().x,previousSegment.getP1().y));
					}
					return new FGEHalfBand(limit,SimplifiedCardinalDirection.SOUTH);
				}
			}
		}
		if (currentOrientation.isVertical()) {
			if (previousOrientation == SimplifiedCardinalDirection.EAST) {
				if (nextOrientation == SimplifiedCardinalDirection.EAST) {
					return new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.WEST) {
					FGESegment limit;						
					if (previousSegment.getP1().x<nextSegment.getP2().x) {
						limit = new FGESegment(
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP1().y),
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP1().y),
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP2().y));
					}
					return new FGEHalfBand(limit,SimplifiedCardinalDirection.EAST);
				}
			}
			else if (previousOrientation == SimplifiedCardinalDirection.WEST) {
				if (nextOrientation == SimplifiedCardinalDirection.WEST) {
					return new FGERectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.EAST) {
					FGESegment limit;						
					if (previousSegment.getP1().x>nextSegment.getP2().x) {
						limit = new FGESegment(
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP1().y),
								new FGEPoint(nextSegment.getP2().x,currentSegment.getP2().y));
					}
					else {
						limit = new FGESegment(
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP1().y),
								new FGEPoint(previousSegment.getP1().x,currentSegment.getP2().y));
					}
					return new FGEHalfBand(limit,SimplifiedCardinalDirection.WEST);
				}
			}
		}
*/
		
		//logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
		//return new FGEEmptyArea();
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
	{
		FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		
		if (pt == null) {
			logger.warning("Unexpected null point while dragging AdjustableIntermediateSegment "+getDraggingAuthorizedArea()+" pt="+newRelativePoint);
			return false;
		}
		
		FGEPoint p1 = getPolylin().getPointAt(index);
		if (p1 == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
			return false;
		}
		FGEPoint p2 = getPolylin().getPointAt(index+1);
		if (p2 == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
			return false;
		}

		if (currentOrientation.isHorizontal()) {
			p1.y = pt.y;
			p2.y = pt.y;
		}	
		else if (currentOrientation.isVertical()) {
			p1.x = pt.x;
			p2.x = pt.x;
		}	
		else {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
			return false;
		}			
		
		getPolylin().updatePointAt(index,p1);
		getConnector()._getControlPoints().elementAt(index).setPoint(p1);
		
		getPolylin().updatePointAt(index+1,p2);
		getConnector()._getControlPoints().elementAt(index+1).setPoint(p2);
		
		getConnector()._connectorChanged(true);
		getGraphicalRepresentation().notifyConnectorChanged();
		
		return true;
	
		
		/*System.out.println("Index = "+index+" pour "+getGraphicalRepresentation().getText());
		System.out.println("getDraggingAuthorizedArea() = "+getDraggingAuthorizedArea());
		System.out.println("pt = "+pt);
		System.out.println("polylin = "+polylin);
		System.out.println("polylin.getSegmentNb() = "+polylin.getSegmentNb());
		System.out.println("polylin.getPointAt(index) = "+polylin.getPointAt(index));*/
		/*if (currentOrientation.isHorizontal()) {
			FGEPoint p1 = getPolylin().getPointAt(index);
			if (p1 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
				return false;
			}
			p1.y = pt.y;
			getPolylin().updatePointAt(index,p1);
			getConnector()._getControlPoints().elementAt(index).setPoint(p1);
			FGEPoint p2 = getPolylin().getPointAt(index+1);
			if (p2 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
				return false;
			}
			p2.y = pt.y;
			getPolylin().updatePointAt(index+1,p2);
			getConnector()._getControlPoints().elementAt(index+1).setPoint(p2);
			getConnector().rememberLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
			return true;
		}
		else if (currentOrientation.isVertical()) {
			FGEPoint p1 = getPolylin().getPointAt(index);
			if (p1 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
				return false;
			}
			p1.x = pt.x;
			getPolylin().updatePointAt(index,p1);
			getConnector()._getControlPoints().elementAt(index).setPoint(p1);
			FGEPoint p2 = getPolylin().getPointAt(index+1);
			if (p2 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
				return false;
			}
			p2.x = pt.x;
			getPolylin().updatePointAt(index+1,p2);
			getConnector()._getControlPoints().elementAt(index+1).setPoint(p2);
			getConnector().rememberLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
			return true;
		}
		else {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnector");
			return false;
		}			*/
	}
	
	@Override
	public void stopDragging(DrawingController controller)
	{
		if (beforePreviousSegment != null && beforePreviousSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index-1);
		}
		if (afterNextSegment != null && afterNextSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index+1);
		}
		super.stopDragging(controller);
	}
}