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

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;

public class FGEShapeDecorationGraphicsImpl extends FGEGraphicsImpl implements FGEShapeDecorationGraphics {

	private static final Logger logger = Logger.getLogger(FGEShapeDecorationGraphicsImpl.class.getPackage().getName());

	public FGEShapeDecorationGraphicsImpl(ShapeNode<?> node) {
		super(node);
	}

	@Override
	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	public double getWidth() {
		return getGraphicalRepresentation().getWidth();
	}

	public double getHeight() {
		return getGraphicalRepresentation().getHeight();
	}

	// Decoration graphics doesn't use normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) (x * getScale()), (int) (y * getScale()));
	}

	// Decoration graphics doesn't use normalized coordinates system
	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new FGEPoint(x / getScale(), y / getScale());
	}

}
