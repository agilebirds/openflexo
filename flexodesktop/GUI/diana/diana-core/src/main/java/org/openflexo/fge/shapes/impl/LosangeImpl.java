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

import org.openflexo.fge.shapes.Losange;

public abstract class LosangeImpl extends RegularPolygonImpl implements Losange {

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public LosangeImpl() {
		super();
		super.setNPoints(4);
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.LOSANGE;
	}

	@Override
	public int getNPoints() {
		return 4;
	}

	@Override
	public void setNPoints(int pointsNb) {
	}

	@Override
	public int getStartAngle() {
		return 90;
	}

	@Override
	public void setStartAngle(int anAngle) {
	}

}
