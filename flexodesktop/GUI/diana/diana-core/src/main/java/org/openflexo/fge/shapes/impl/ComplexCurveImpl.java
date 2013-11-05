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
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.ComplexCurve;

public abstract class ComplexCurveImpl extends ShapeSpecificationImpl implements ComplexCurve {

	private List<FGEPoint> points;
	private Closure closure = Closure.CLOSED_FILLED;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ComplexCurveImpl() {
		super();
		this.points = new ArrayList<FGEPoint>();
	}

	public ComplexCurveImpl(List<FGEPoint> points) {
		this();
		for (FGEPoint pt : points) {
			this.points.add(pt);
		}
	}

	public ComplexCurveImpl(FGEComplexCurve curve) {
		this();
		for (FGEPoint pt : curve.getPoints()) {
			points.add(pt);
		}
	}

	@Override
	public List<FGEPoint> getPoints() {
		return points;
	}

	@Override
	public void setPoints(List<FGEPoint> points) {
		if (points != null) {
			this.points = new ArrayList<FGEPoint>(points);
		} else {
			this.points = null;
		}
		notifyChange(POINTS);
	}

	@Override
	public void addToPoints(FGEPoint aPoint) {
		points.add(aPoint);
		notifyChange(POINTS);
	}

	@Override
	public void removeFromPoints(FGEPoint aPoint) {
		points.remove(aPoint);
		notifyChange(POINTS);
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.COMPLEX_CURVE;
	}

	@Override
	public Closure getClosure() {
		return closure;
	}

	@Override
	public void setClosure(Closure aClosure) {
		FGEAttributeNotification notification = requireChange(CLOSURE, aClosure);
		if (notification != null) {
			closure = aClosure;
			hasChanged(notification);
		}
	}

	@Override
	public FGEComplexCurve makeFGEShape(ShapeNode<?> node) {
		return new FGEComplexCurve(getClosure(), points);
	}

}
