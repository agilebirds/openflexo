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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.RectangularOctogon;

public class RectangularOctogonImpl extends PolygonImpl implements RectangularOctogon {

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public RectangularOctogonImpl() {
		super();
		addToPoints(new FGEPoint(0, 0.2));
		addToPoints(new FGEPoint(0, 0.8));
		addToPoints(new FGEPoint(0.1, 1));
		addToPoints(new FGEPoint(0.9, 1));
		addToPoints(new FGEPoint(1, 0.8));
		addToPoints(new FGEPoint(1, 0.2));
		addToPoints(new FGEPoint(0.9, 0));
		addToPoints(new FGEPoint(0.1, 0));
	}

	/*@Deprecated
	private RectangularOctogonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
	}*/

}
