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
package org.openflexo.fge.geomedit.gr;

import java.awt.Color;
import java.awt.Graphics;

import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.CubicCurve;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.graphics.FGEGeometricGraphicsImpl;
import org.openflexo.xmlcode.XMLSerializable;

public class CubicCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGECubicCurve, CubicCurve> implements
		XMLSerializable {
	// Called for LOAD
	public CubicCurveGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public CubicCurveGraphicalRepresentation(CubicCurve curve, GeometricDrawing aDrawing) {
		super(curve, aDrawing);
	}

	@Override
	public void paint(Graphics g, AbstractDianaEditor controller) {
		// TODO: un petit @brutal pour avancer, il faudrait faire les choses plus proprement
		rebuildControlPoints();
		super.paint(g, controller);
	}

	@Override
	public void paintGeometricObject(FGEGeometricGraphicsImpl graphics) {
		getGeometricObject().paint(graphics);

		if (getIsSelected() || getIsFocused()) {
			// Draw construction
			FGEPoint p1 = getGeometricObject().getP1();
			FGEPoint p2 = getGeometricObject().getP2();
			FGEPoint cp1 = getGeometricObject().getCtrlP1();
			FGEPoint cp2 = getGeometricObject().getCtrlP2();

			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.PLAIN_STROKE));

			FGESegment line1 = new FGESegment(p1, cp1);
			FGESegment line2 = new FGESegment(p2, cp2);
			line1.paint(graphics);
			line2.paint(graphics);

		}
	}

}