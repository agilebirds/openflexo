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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentationUtils;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.graphics.FGEGraphics;

public abstract class ControlArea<A extends FGEArea> implements FGEConstants {

	private static final Logger logger = Logger.getLogger(ControlArea.class.getPackage().getName());

	private GraphicalRepresentation<?> graphicalRepresentation;

	private A _area;

	public ControlArea(GraphicalRepresentation<?> aGraphicalRepresentation, A area) {
		graphicalRepresentation = aGraphicalRepresentation;
		_area = area;
		if (graphicalRepresentation == null) {
			logger.warning("!!!!!!!!!!!! ControlArea built for null GraphicalRepresentation");
		}

	}

	public GraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public A getArea() {
		return _area;
	}

	public void setArea(A point) {
		_area = point;
	}

	// Please override when required
	public boolean isClickable() {
		return false;
	}

	public abstract boolean isDraggable();

	// Please override when required
	public Cursor getDraggingCursor() {
		return Cursor.getDefaultCursor();
	}

	private FGEArea _draggingAuthorizedArea = new FGEEmptyArea();

	public FGEArea getDraggingAuthorizedArea() {
		return _draggingAuthorizedArea;
	}

	public final void setDraggingAuthorizedArea(FGEArea area) {
		_draggingAuthorizedArea = area;
	}

	protected FGEPoint getNearestPointOnAuthorizedArea(FGEPoint point) {
		return getDraggingAuthorizedArea().getNearestPoint(point);
	}

	// Override when required
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
	}

	// Override when required
	/**
	 * Drag control area to supplied location Return a flag indicating if dragging should continue Override this method when required
	 * 
	 * @param event
	 *            TODO
	 */
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		return true;
	}

	// Override when required
	public void stopDragging(DrawingController<?> controller, GraphicalRepresentation focusedGR) {
	}

	// Override when required
	/**
	 * Clicked on control area to supplied location Return a flag indicating if click has been handled
	 */
	public boolean clickOnPoint(FGEPoint clickedPoint, int clickCount) {
		return true;
	}

	public abstract Rectangle paint(FGEGraphics graphics);

	/**
	 * Return distance between a point (normalized) and represented area, asserting that we are working on a view (not normalized), and at a
	 * given scale
	 * 
	 * @param aPoint
	 *            normalized point relative to current GraphicalRepresentation
	 * @param scale
	 * @return
	 */
	public double getDistanceToArea(FGEPoint aPoint, double scale) {
		FGEPoint nearestPoint = getArea().getNearestPoint(aPoint);
		if (nearestPoint == null) {
			logger.warning("Could not find nearest point for " + aPoint + " on " + getArea());
			return Double.POSITIVE_INFINITY;
		}
		if (getGraphicalRepresentation() == null) {
			logger.warning("Unexpected null GraphicalRepresentation !");
			return Double.POSITIVE_INFINITY;
		}
		Point pt1 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(nearestPoint, scale);
		Point pt2 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(aPoint, scale);
		return Point2D.distance(pt1.x, pt1.y, pt2.x, pt2.y);
	}

	/**
	 * Return distance between a point (normalized) and represented area, asserting that we are working on a view (not normalized), and at a
	 * given scale
	 * 
	 * @param aPoint
	 *            view point relative to current GraphicalRepresentation, at correct scale
	 * @param scale
	 * @return
	 */
	public double getDistanceToArea(Point aPoint, double scale) {
		FGEPoint normalizedPoint = graphicalRepresentation.convertViewCoordinatesToNormalizedPoint(aPoint, scale);
		FGEPoint nearestPoint = getArea().getNearestPoint(normalizedPoint);
		Point pt1 = graphicalRepresentation.convertNormalizedPointToViewCoordinates(nearestPoint, scale);
		return Point2D.distance(pt1.x, pt1.y, aPoint.x, aPoint.y);
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "," + Integer.toHexString(hashCode()) + "]";
	}
}
