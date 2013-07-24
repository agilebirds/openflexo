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

import org.openflexo.fge.connectors.impl.RectPolylinConnector;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;

public class AdjustableUniqueSegment extends RectPolylinAdjustableSegment {
	static final Logger logger = Logger.getLogger(AdjustableUniqueSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private FGESegment currentSegment;
	private SimplifiedCardinalDirection currentOrientation;
	private FGEArea startArea;
	private FGEArea endArea;
	private FGEArea draggingAuthorizedArea;

	public AdjustableUniqueSegment(FGESegment segment, RectPolylinConnector connector) {
		super(segment, connector);
		retrieveInfos();
	}

	private void retrieveInfos() {
		currentSegment = getArea();
		if (currentSegment.getApproximatedOrientation() == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}
		currentOrientation = currentSegment.getApproximatedOrientation();

		startArea = getConnector().retrieveAllowedStartArea(false);
		endArea = getConnector().retrieveAllowedEndArea(false);

		/*
		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getStartObject(), getGraphicalRepresentation());
		startArea = getConnector().getStartObject().getShape().getOutline().transform(at1);
		FGEArea startOrthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(currentOrientation);

		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getEndObject(), getGraphicalRepresentation());
		endArea = getConnector().getEndObject().getShape().getOutline().transform(at2);
		FGEArea endOrthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(currentOrientation.getOpposite());
		*/

		FGEArea startOrthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(currentOrientation);
		FGEArea endOrthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(currentOrientation.getOpposite());

		draggingAuthorizedArea = startOrthogonalPerspectiveArea.intersect(endOrthogonalPerspectiveArea);

		consistentData = true;
	}

	@Override
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
		retrieveInfos();
		logger.info("start cpts=" + getConnector().getControlAreas());
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

		FGEPoint p1 = getPolylin().getPointAt(0);
		if (p1 == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}
		FGEPoint p2 = getPolylin().getPointAt(1);
		if (p2 == null) {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		// System.out.println("draggingAuthorizedArea="+draggingAuthorizedArea);
		// System.out.println("pt="+pt);

		p1 = startArea.nearestPointFrom(pt, currentOrientation.getOpposite());
		p2 = endArea.nearestPointFrom(pt, currentOrientation);

		/*if (currentOrientation.isHorizontal()) {
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
		}*/

		getPolylin().updatePointAt(0, p1);
		getConnector().getControlPoints().get(0).setPoint(p1);

		getPolylin().updatePointAt(1, p2);
		getConnector().getControlPoints().get(1).setPoint(p2);

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		// logger.info("drag cpts="+getConnector().getControlAreas());
		return true;
	}

}