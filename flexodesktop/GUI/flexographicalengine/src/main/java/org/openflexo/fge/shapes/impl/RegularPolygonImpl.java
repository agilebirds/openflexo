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
package org.openflexo.fge.shapes.impl;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERegularPolygon;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.RegularPolygon;

public class RegularPolygonImpl extends PolygonImpl implements RegularPolygon {

	private int npoints = -1;
	private int startAngle = 90;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public RegularPolygonImpl() {
		super();
		setNPoints(5);
	}

	public RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, List<FGEPoint> points) {
		this();
		setPoints(new ArrayList<FGEPoint>(points));
	}

	public RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		this();
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
	}

	/*@Deprecated
	private RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
	}

	@Deprecated
	private RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, List<FGEPoint> points) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		setPoints(new ArrayList<FGEPoint>(points));
	}

	@Deprecated
	private RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
		updateShape();
	}*/

	@Override
	public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
		if (getPoints() != null && getPoints().size() > 0) {
			return new FGEPolygon(Filling.FILLED, getPoints());
		} else if (getNPoints() > 2) {
			return new FGERegularPolygon(0, 0, 1, 1, Filling.FILLED, getNPoints(), startAngle);
		}
		return null;
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.POLYGON;
	}

	@Override
	public int getNPoints() {
		return npoints;
	}

	@Override
	public void setNPoints(int pointsNb) {
		FGENotification notification = requireChange(N_POINTS, pointsNb);
		if (notification != null) {
			npoints = pointsNb;
			hasChanged(notification);
		}
	}

	@Override
	public int getStartAngle() {
		return startAngle;
	}

	@Override
	public void setStartAngle(int anAngle) {
		FGENotification notification = requireChange(START_ANGLE, anAngle);
		if (notification != null) {
			startAngle = anAngle;
			hasChanged(notification);
		}
	}

}
