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

public class Polygon extends Shape {

	private FGEPolygon _polygon;

	private List<FGEPoint> points;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public Polygon() {
		this(null);
	}

	public Polygon(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		super(aGraphicalRepresentation);
	}

	public Polygon(ShapeGraphicalRepresentation aGraphicalRepresentation, List<FGEPoint> points) {
		super(aGraphicalRepresentation);
		this.points = new ArrayList<FGEPoint>(points);
		updateShape();
	}

	public Polygon(ShapeGraphicalRepresentation aGraphicalRepresentation, FGEPolygon polygon) {
		super(aGraphicalRepresentation);
		this.points = new ArrayList<FGEPoint>();
		for (FGEPoint pt : polygon.getPoints()) {
			points.add(pt);
		}
		updateShape();
	}

	public Polygon(ShapeGraphicalRepresentation aGraphicalRepresentation, FGEPoint... points) {
		super(aGraphicalRepresentation);
		this.points = new ArrayList<FGEPoint>();
		for (FGEPoint pt : points) {
			this.points.add(pt);
		}
		updateShape();
	}

	public List<FGEPoint> getPoints() {
		return points;
	}

	public void setPoints(List<FGEPoint> points) {
		if (points != null) {
			this.points = new ArrayList<FGEPoint>(points);
			updateShape();
		} else {
			this.points = null;
		}
	}

	public void addToPoints(FGEPoint aPoint) {
		points.add(aPoint);
		updateShape();
	}

	public void removeFromPoints(FGEPoint aPoint) {
		points.remove(aPoint);
		updateShape();
	}

	@Override
	public FGEPolygon getShape() {
		return _polygon;
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.CUSTOM_POLYGON;
	}

	@Override
	public void updateShape() {
		_polygon = new FGEPolygon(Filling.FILLED, points);
	}

}
