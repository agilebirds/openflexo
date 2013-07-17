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

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEGraphics;

/**
 * A {@link ControlArea} encodes an interactive area, attached to a DrawingTreeNode, and represented by a single point<br>
 * 
 * @author sylvain
 */
public abstract class ControlPoint extends ControlArea<FGEPoint> {

	private static final Logger logger = Logger.getLogger(ControlPoint.class.getPackage().getName());

	public ControlPoint(DrawingTreeNode<?, ?> node, FGEPoint pt) {
		super(node, pt);
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
		if (getNode() == null) {
			logger.warning("Unexpected null node");
			return null;
		}
		// logger.info("paintControlPoint " + getPoint() + "style=" + graphics.getDefaultForeground() + " for " +
		// getGraphicalRepresentation());
		graphics.useDefaultForegroundStyle();
		if (isEmbeddedInComponentHierarchy(graphics)) {
			AffineTransform at = FGEUtils.convertNormalizedCoordinatesAT(getNode(), graphics.getNode());
			return graphics.drawControlPoint(getPoint().transform(at), FGEConstants.CONTROL_POINT_SIZE);
		} else {
			return graphics.drawControlPoint(getPoint(), FGEConstants.CONTROL_POINT_SIZE);
		}

	}

	public boolean isEmbeddedInComponentHierarchy(FGEGraphics graphics) {
		return getNode().isConnectedToDrawing();
	}

}
