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
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfPlane;

public class AdjustableLastSegment extends RectPolylinAdjustableSegment {
	static final Logger logger = Logger.getLogger(AdjustableLastSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private int segmentsNb;
	private FGESegment currentSegment;
	private FGESegment previousSegment;
	private FGESegment beforePreviousSegment;
	private SimplifiedCardinalDirection currentOrientation;
	private SimplifiedCardinalDirection previousOrientation;
	private FGEArea endArea;
	private FGEArea draggingAuthorizedArea;

	public AdjustableLastSegment(FGESegment segment, RectPolylinConnector connector) {
		super(segment, connector);
		retrieveInfos();
	}

	private void retrieveInfos() {
		currentSegment = getArea();
		segmentsNb = getPolylin().getSegmentNb();
		previousSegment = getPolylin().getSegmentAt(segmentsNb - 2);
		if (currentSegment.getApproximatedOrientation() == null || previousSegment.getApproximatedOrientation() == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}
		if (getPolylin().getSegmentNb() > 2) {
			beforePreviousSegment = getPolylin().getSegmentAt(segmentsNb - 3);
		}
		currentOrientation = currentSegment.getApproximatedOrientation();
		previousOrientation = previousSegment.getApproximatedOrientation();

		AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(getNode().getEndNode(), getNode());
		endArea = getNode().getEndNode().getFGEShapeOutline().transform(at2);
		FGEArea orthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(currentOrientation.getOpposite());
		if (!previousSegment.containsPoint(currentSegment.getP2())) {
			FGEHalfPlane hp = new FGEHalfPlane(previousSegment, currentSegment.getP2());
			draggingAuthorizedArea = orthogonalPerspectiveArea.intersect(hp);
		} else {
			draggingAuthorizedArea = orthogonalPerspectiveArea;
		}

		consistentData = true;
	}

	@Override
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
		retrieveInfos();
	}

	@Override
	public FGEArea getDraggingAuthorizedArea() {
		if (!consistentData) {
			return new FGEEmptyArea();
		}

		return draggingAuthorizedArea;

	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);

		FGEPoint p1 = getPolylin().getPointAt(segmentsNb - 1);
		if (p1 == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}
		FGEPoint p2 = getPolylin().getPointAt(segmentsNb);
		if (p2 == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		p2 = endArea.nearestPointFrom(pt, currentOrientation);
		if (currentOrientation.isHorizontal()) {
			p1.y = pt.y;
		} else if (currentOrientation.isVertical()) {
			p1.x = pt.x;
		} else {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		/*
		if (currentOrientation.isHorizontal()) {
			p1.y = pt.y;
			p2.y = pt.y;
		}	
		else if (currentOrientation.isVertical()) {
			p1.x = pt.x;
			p2.x = pt.x;
		}	
		else {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}			
		
		// Now we must ensure that p2 is located on shape outline
		// To do so, use outlineIntersect by projecting p2 along horizontal or vertical line
		
		FGEPoint outlineIntersect = endArea.nearestPointFrom(p2, currentOrientation);
		if (outlineIntersect != null) p2 = outlineIntersect;
		else {
			logger.warning("Could not compute outlineIntersect() from "+p2);
		}		*/

		/*if (endArea instanceof FGEShape) {
			FGEPoint outlineIntersect = null;
			if (currentOrientation.isHorizontal()) {
				outlineIntersect = ((FGEShape)endArea).outlineIntersect(FGELine.makeHorizontalLine(p2), p2);
			}	
			else if (currentOrientation.isVertical()) {
				outlineIntersect = ((FGEShape)endArea).outlineIntersect(FGELine.makeVerticalLine(p2), p2);
			}	
			if (outlineIntersect != null) p2 = outlineIntersect;
			else {
				logger.warning("Could not compute outlineIntersect() from "+p2);
			}		
		}*/

		getPolylin().updatePointAt(segmentsNb - 1, p1);
		getConnector().getControlPoints().get(segmentsNb - 1).setPoint(p1);

		getPolylin().updatePointAt(segmentsNb, p2);
		getConnector().getControlPoints().get(segmentsNb).setPoint(p2);

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		return true;

	}

	/*@Override
	public void stopDragging(DrawingController controller)
	{
		if (beforePreviousSegment != null && beforePreviousSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(segmentsNb-2);
		}
		super.stopDragging(controller);
	}*/
}