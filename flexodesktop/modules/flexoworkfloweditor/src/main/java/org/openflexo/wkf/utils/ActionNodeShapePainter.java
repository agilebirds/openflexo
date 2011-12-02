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
package org.openflexo.wkf.utils;

import java.awt.Color;
import java.util.Vector;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERegularPolygon;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActionType;

public class ActionNodeShapePainter implements ShapePainter {

	private static final double DIST = 0.3;
	private static final FGEPoint[] TRIANGLE = new FGERegularPolygon(DIST / 2, DIST / 2, 1 - DIST, 1 - DIST, Filling.FILLED, 3, 0)
			.getPoints().toArray(new FGEPoint[0]);

	private static final double GEAR_DIST1 = 0.3;
	private static final double GEAR_DIST2 = 0.55;
	private static final int NUMBER_OF_TEETH = 10;

	private static FGEPoint[] GEAR;
	static {
		Vector<FGEPoint> points = new Vector<FGEPoint>();
		for (int i = 0; i < NUMBER_OF_TEETH; i++) {
			FGEArc arc = new FGEArc(GEAR_DIST1 / 2 + 1 / 30, GEAR_DIST1 / 2 + 1 / 30, 1 - GEAR_DIST1, 1 - GEAR_DIST1, -180
					/ (2 * NUMBER_OF_TEETH) + (i * 360 / NUMBER_OF_TEETH), 180 / NUMBER_OF_TEETH);
			points.add(arc.getStartPoint());
			points.add(arc.getEndPoint());
			double offset = 180 / (1.5 * NUMBER_OF_TEETH);
			FGEArc arc2 = new FGEArc(GEAR_DIST2 / 2 + 1 / 30, GEAR_DIST2 / 2 + 1 / 30, 1 - GEAR_DIST2, 1 - GEAR_DIST2, offset
					+ (i * 360 / NUMBER_OF_TEETH), offset);
			points.add(arc2.getStartPoint());
			points.add(arc2.getEndPoint());
		}
		GEAR = points.toArray(new FGEPoint[0]);
	}

	private static final ForegroundStyle FOREGROUND = ForegroundStyle.makeStyle(Color.BLACK);
	private ShapeGraphicalRepresentation<ActionNode> shape;

	public ActionNodeShapePainter(ShapeGraphicalRepresentation<ActionNode> shape) {
		this.shape = shape;
	}

	private ActionNode getActionNode() {
		return this.shape.getDrawable();
	}

	@Override
	public void paintShape(FGEShapeGraphics g) {
		g.useForegroundStyle(FOREGROUND);
		if (getActionNode().getActionType() == ActionType.DISPLAY_ACTION) {
			g.drawPolygon(TRIANGLE);
		} else if (getActionNode().getActionType() == ActionType.FLEXO_ACTION) {
			g.drawPolygon(GEAR);
		}
	}

}
