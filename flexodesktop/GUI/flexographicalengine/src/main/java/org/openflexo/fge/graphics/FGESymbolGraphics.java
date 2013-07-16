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
package org.openflexo.fge.graphics;

import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;

//import org.openflexo.fge.ShapeGraphicalRepresentation;

public class FGESymbolGraphics extends FGEGraphics {

	private static final Logger logger = Logger.getLogger(FGESymbolGraphics.class.getPackage().getName());

	public FGESymbolGraphics(ConnectorGraphicalRepresentation aGraphicalRepresentation) {
		super(aGraphicalRepresentation);
	}

	// sometimes this method throw a class cast exception with a TokenEdgeGR instead of a
	// ShapeGraphicalRepresentation.
	// don't know the source cause, but commenting this code seems to be a workaround.

	// The real solution could be to use Generic Typing to find the root cause of this issue
	// ... but it's a lot of complex work.

	// public ShapeGraphicalRepresentation getGraphicalRepresentation()
	// {
	// return (ShapeGraphicalRepresentation)super.getGraphicalRepresentation();
	// }

	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) x, (int) y);
	}

	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new FGEPoint(x, y);
	}

}
