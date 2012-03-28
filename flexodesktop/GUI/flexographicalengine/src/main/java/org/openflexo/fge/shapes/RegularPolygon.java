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
package org.openflexo.fge.shapes;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERegularPolygon;

public class RegularPolygon extends Polygon {

	private FGEPolygon _polygon;

	private int npoints = 5;
	private int startAngle = 90;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public RegularPolygon() {
		this(null);
	}

	public RegularPolygon(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this(aGraphicalRepresentation, 5);
	}

	public RegularPolygon(ShapeGraphicalRepresentation aGraphicalRepresentation, List<FGEPoint> points) {
		super(aGraphicalRepresentation);
		setPoints(new ArrayList<FGEPoint>(points));
	}

	public RegularPolygon(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		super(aGraphicalRepresentation);
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
		updateShape();
	}

	@Override
	public void updateShape() {
		if (getPoints() != null) {
			_polygon = new FGEPolygon(Filling.FILLED, getPoints());
		} else {
			_polygon = new FGERegularPolygon(0, 0, 1, 1, Filling.FILLED, npoints, startAngle);
		}
		rebuildControlPoints();
		if (getGraphicalRepresentation() != null) {
			getGraphicalRepresentation().notifyShapeChanged();
		}
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.POLYGON;
	}

	public int getNPoints() {
		return npoints;
	}

	public void setNPoints(int pointsNb) {
		if (pointsNb != npoints) {
			npoints = pointsNb;
			updateShape();
		}
	}

	public int getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(int anAngle) {
		if (anAngle != startAngle) {
			startAngle = anAngle;
			updateShape();
		}
	}

	@Override
	public FGEPolygon getShape() {
		return _polygon;
	}

}
