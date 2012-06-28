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

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;

public class RectangularOctogon extends Polygon {

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public RectangularOctogon() {
		this(null);
	}

	public RectangularOctogon(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		super(aGraphicalRepresentation, new FGEPoint(0, 0.2), new FGEPoint(0, 0.8), new FGEPoint(0.1, 1), new FGEPoint(0.9, 1),
				new FGEPoint(1, 0.8), new FGEPoint(1, 0.2), new FGEPoint(0.9, 0), new FGEPoint(0.1, 0));
	}

}
