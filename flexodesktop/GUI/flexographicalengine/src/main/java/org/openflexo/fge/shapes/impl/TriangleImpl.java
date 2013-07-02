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
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.fge.shapes.Shape.ShapeType;

public class TriangleImpl extends RegularPolygonImpl implements Triangle {

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public TriangleImpl() {
		super();
		super.setNPoints(3);
	}

	@Deprecated
	private TriangleImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		updateShape();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.TRIANGLE;
	}

	@Override
	public int getNPoints() {
		return 3;
	}

	@Override
	public void setNPoints(int pointsNb) {
	}

}
