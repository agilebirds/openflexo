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

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class OperatorInclusiveGR extends OperatorGR<InclusiveOperator> {

	private ForegroundStyle painterForeground;

	public OperatorInclusiveGR(InclusiveOperator operatorNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(operatorNode, aDrawing, isInPalet);
		painterForeground = ForegroundStyleImpl.makeStyle(Color.BLACK);
		painterForeground.setLineWidth(3.0);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useForegroundStyle(painterForeground);
				g.drawCircle(0.215, 0.2, 0.57, 0.58);
			}
		});
	}

	@Override
	public ImageIcon getImageIcon() {
		return null;
	}
}
