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

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.shapes.Star;

public class StarImpl extends ShapeImpl implements Star {

	private int npoints = 6;
	private double ratio = 0.5;
	private int startAngle = 90;

	private FGEPolygon _polygon;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public StarImpl() {
		super();
	}

	@Deprecated
	private StarImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this(aGraphicalRepresentation, 5);
	}

	@Deprecated
	private StarImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		updateShape();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.STAR;
	}

	@Override
	public int getNPoints() {
		return npoints;
	}

	@Override
	public void setNPoints(int pointsNb) {
		if (pointsNb != npoints) {
			npoints = pointsNb;
			updateShape();
		}
	}

	@Override
	public int getStartAngle() {
		return startAngle;
	}

	@Override
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

	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public void setRatio(double aRatio) {
		if (aRatio > 0 && aRatio < 1.0 && ratio != aRatio) {
			ratio = aRatio;
			updateShape();
		}
	}

	@Override
	public void updateShape() {
		_polygon = new FGEPolygon(Filling.FILLED);
		double startA = getStartAngle() * Math.PI / 180;
		double angleInterval = Math.PI * 2 / npoints;
		for (int i = 0; i < npoints; i++) {
			double angle = i * angleInterval + startA;
			double angle1 = (i - 0.5) * angleInterval + startA;
			_polygon.addToPoints(new FGEPoint(Math.cos(angle1) * 0.5 * ratio + 0.5, Math.sin(angle1) * 0.5 * ratio + 0.5));
			_polygon.addToPoints(new FGEPoint(Math.cos(angle) * 0.5 + 0.5, Math.sin(angle) * 0.5 + 0.5));
		}
		rebuildControlPoints();
		if (getGraphicalRepresentation() != null) {
			getGraphicalRepresentation().notifyShapeChanged();
		}
	}
}
