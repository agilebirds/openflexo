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

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.GeometricGraphicalRepresentation;

public class FGEGeometricGraphics extends FGEGraphics {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FGEGeometricGraphics.class.getPackage().getName());

	public FGEGeometricGraphics(GeometricNode<?> geometricNode) {
		super(geometricNode);
	}

	@Override
	public GeometricGraphicalRepresentation getGraphicalRepresentation() {
		return (GeometricGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	// Geometric graphics doesn't use normalized coordinates system
	/*public Point convertNormalizedPointToViewCoordinates(double x, double y)
	{
		return new Point((int)x,(int)y);
	}*/

	// Geometric graphics doesn't use normalized coordinates system
	/*public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y)
	{
		return new FGEPoint(x,y);
	}*/

}
