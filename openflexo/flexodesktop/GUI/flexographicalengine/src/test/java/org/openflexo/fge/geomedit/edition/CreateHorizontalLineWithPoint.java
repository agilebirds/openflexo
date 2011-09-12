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
package org.openflexo.fge.geomedit.edition;

import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Line;
import org.openflexo.fge.geomedit.construction.HorizontalLineWithPointConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;



public class CreateHorizontalLineWithPoint extends Edition {
	
	public CreateHorizontalLineWithPoint(GeomEditController controller) {
		super("Create horizontal line crossing point",controller);
		inputs.add(new ObtainPoint("Select point",controller));
	}
	
	@Override
	public void performEdition()
	{
		ObtainPoint p = (ObtainPoint)inputs.get(0);
		
		addObject (new Line(
				getController().getDrawing().getModel(),
				new HorizontalLineWithPointConstruction(p.getConstruction())));

	}
	
	@Override
	public void paintEdition(FGEDrawingGraphics graphics,FGEPoint lastMouseLocation)
	{
		if (currentStep == 0) {
			graphics.setDefaultForeground(focusedForegroundStyle);
			(FGELine.makeHorizontalLine(lastMouseLocation)).paint(graphics);
		}
	}
}


