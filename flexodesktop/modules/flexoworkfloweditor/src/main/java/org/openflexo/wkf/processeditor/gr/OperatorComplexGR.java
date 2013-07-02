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
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class OperatorComplexGR extends OperatorGR<ComplexOperator> {

	private static final FGERectangle VERTICAL_RECTANGLE = new FGERectangle(0.45, 0.25, 0.1, 0.5);
	private static final FGERectangle HORIZONTAL_RECTANGLE = new FGERectangle(0.25, 0.45, 0.5, 0.1);
	private static final FGEPolygon DIAGONAL1 = (FGEPolygon) VERTICAL_RECTANGLE
			.transform(AffineTransform.getTranslateInstance(-VERTICAL_RECTANGLE.getCenterX(), -VERTICAL_RECTANGLE.getCenterY()))
			.transform(AffineTransform.getRotateInstance(Math.toRadians(45)))
			.transform(AffineTransform.getTranslateInstance(VERTICAL_RECTANGLE.getCenterX(), VERTICAL_RECTANGLE.getCenterY()));
	private static final FGEPolygon DIAGONAL2 = (FGEPolygon) VERTICAL_RECTANGLE
			.transform(AffineTransform.getTranslateInstance(-VERTICAL_RECTANGLE.getCenterX(), -VERTICAL_RECTANGLE.getCenterY()))
			.transform(AffineTransform.getRotateInstance(Math.toRadians(-45)))
			.transform(AffineTransform.getTranslateInstance(VERTICAL_RECTANGLE.getCenterX(), VERTICAL_RECTANGLE.getCenterY()));

	public OperatorComplexGR(ComplexOperator operatorNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(operatorNode, aDrawing, isInPalet);
		setShapePainter(new ShapePainter() {

			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useBackgroundStyle(BackgroundStyle.makeColoredBackground(Color.BLACK));
				g.fillRect(VERTICAL_RECTANGLE.x, VERTICAL_RECTANGLE.y, VERTICAL_RECTANGLE.width, VERTICAL_RECTANGLE.height);
				g.fillRect(HORIZONTAL_RECTANGLE.x, HORIZONTAL_RECTANGLE.y, HORIZONTAL_RECTANGLE.width, HORIZONTAL_RECTANGLE.height);
				g.fillPolygon(DIAGONAL1);
				g.fillPolygon(DIAGONAL2);
			}
		});
	}

	@Override
	public ImageIcon getImageIcon() {
		return null;
	}
}
