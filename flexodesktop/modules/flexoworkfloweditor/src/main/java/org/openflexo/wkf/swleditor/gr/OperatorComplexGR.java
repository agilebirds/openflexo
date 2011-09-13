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
package org.openflexo.wkf.swleditor.gr;

import java.awt.Color;

import javax.swing.ImageIcon;

import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.graphics.ForegroundStyle.CapStyle;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;


public class OperatorComplexGR extends OperatorGR<ComplexOperator> {

	private ForegroundStyle painterForeground;

	public OperatorComplexGR(ComplexOperator operatorNode, SwimmingLaneRepresentation aDrawing,boolean isInPalet) 
	{
		super(operatorNode, aDrawing,isInPalet);
		painterForeground = ForegroundStyle.makeStyle(Color.BLACK);
		painterForeground.setLineWidth(4.0);
		painterForeground.setCapStyle(CapStyle.CAP_ROUND);
		setShapePainter(new ShapePainter() {

		@Override
		public void paintShape(FGEShapeGraphics g)
		{
			g.useForegroundStyle(painterForeground);
			g.drawLine(0.35, 0.35, 0.65, 0.65);
			g.drawLine(0.65, 0.35, 0.35, 0.65);
			
			g.drawLine(0.5, 0.25, 0.5, 0.75);
			g.drawLine(0.25, 0.5, 0.75, 0.5);
		}
		});
	}
	
	@Override
	public ImageIcon getImageIcon() 
	{
		return null;
	}
}
