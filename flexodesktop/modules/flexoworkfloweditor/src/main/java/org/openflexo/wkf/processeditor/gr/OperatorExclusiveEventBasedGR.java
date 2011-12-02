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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;

import javax.swing.ImageIcon;

import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class OperatorExclusiveEventBasedGR extends OperatorGR<ExclusiveEventBasedOperator> {

	private ForegroundStyle painterForeground;
	private BackgroundStyle painterBackground;

	private FGEPolygon _polygon;
	private FGECircle _externalCircle;
	private FGECircle _internalCircle;

	public OperatorExclusiveEventBasedGR(ExclusiveEventBasedOperator operatorNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(operatorNode, aDrawing, isInPalet);
		painterForeground = ForegroundStyle.makeStyle(Color.BLACK);
		painterBackground = BackgroundStyle.makeColoredBackground(Color.BLACK);
		painterForeground.setLineWidth(1.0);
		_polygon = new FGEPolygon(Filling.FILLED);
		double startA = -Math.PI / 2;
		double scaling = 0.35d;
		double angleInterval = Math.PI * 2 / 5;
		// double ratio = 0.5d;
		for (int i = 0; i < 5; i++) {
			double angle = i * angleInterval + startA;
			// double angle1 = (i-0.5)*angleInterval+startA;
			// _polygon.addToPoints(new FGEPoint(Math.cos(angle1)*0.5*ratio*scaling+0.5,Math.sin(angle1)*0.5*ratio*scaling+0.5));
			_polygon.addToPoints(new FGEPoint(Math.cos(angle) * 0.5 * scaling + 0.5, Math.sin(angle) * 0.5 * scaling + 0.5));
		}

		_externalCircle = new FGECircle(new FGEPoint(0.5, 0.5), 0.26, Filling.NOT_FILLED);
		_internalCircle = new FGECircle(new FGEPoint(0.5, 0.5), 0.32, Filling.NOT_FILLED);
		setShapePainter(new ShapePainter() {

			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useForegroundStyle(painterForeground);
				g.useBackgroundStyle(painterBackground);
				g.drawCircle(_externalCircle.getX(), _externalCircle.getY(), _externalCircle.getWidth(), _externalCircle.getHeight());
				g.drawCircle(_internalCircle.getX(), _internalCircle.getY(), _internalCircle.getWidth(), _internalCircle.getHeight());
				g.drawPolygon(_polygon);
			}
		});
	}

	@Override
	public ImageIcon getImageIcon() {
		return null;
		// return IconLibrary.EXCLUSIVE_EVENT_BASED_OPERATOR_ICON;
	}
}
