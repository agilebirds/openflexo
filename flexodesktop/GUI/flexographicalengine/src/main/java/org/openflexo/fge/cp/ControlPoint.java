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
package org.openflexo.fge.cp;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEGraphics;

public abstract class ControlPoint extends ControlArea<FGEPoint> {

	private static final Logger logger = Logger.getLogger(ControlPoint.class.getPackage().getName());

	public ControlPoint(GraphicalRepresentation<?> aGraphicalRepresentation, FGEPoint pt) {
		super(aGraphicalRepresentation, pt);
	}

	public FGEPoint getPoint() {
		return getArea();
	}

	public void setPoint(FGEPoint point) {
		setArea(point);
	}

	// @SuppressWarnings("unchecked")
	@Override
	public Rectangle paint(FGEGraphics graphics) {
		if (getGraphicalRepresentation() == null) {
			logger.warning("Unexpected null GraphicalRepresentation");
			return null;
		}
		graphics.useDefaultForegroundStyle();
		if (isEmbeddedInComponentHierarchy(graphics)) {
			AffineTransform at = GraphicalRepresentation.convertNormalizedCoordinatesAT(getGraphicalRepresentation(),
					graphics.getGraphicalRepresentation());
			return graphics.drawControlPoint(getPoint().transform(at), FGEConstants.CONTROL_POINT_SIZE);
		} else {
			return graphics.drawControlPoint(getPoint(), FGEConstants.CONTROL_POINT_SIZE);
		}

	}

	public boolean isEmbeddedInComponentHierarchy(FGEGraphics graphics) {
		return getGraphicalRepresentation().isConnectedToDrawing();
	}

}
