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
import org.openflexo.fge.geomedit.construction.ParallelLineWithPointConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;



public class CreateParallelLineWithPoint extends Edition {
	
	public CreateParallelLineWithPoint(GeomEditController controller) {
		super("Create parallel line with point",controller);
		inputs.add(new ObtainLine("Select line",controller));
		inputs.add(new ObtainPoint("Select point",controller));
	}
	
	@Override
	public void performEdition()
	{		
		ObtainLine l = (ObtainLine)inputs.get(0);
		ObtainPoint p = (ObtainPoint)inputs.get(1);
		
		addObject (new Line(
					getController().getDrawing().getModel(),
				new ParallelLineWithPointConstruction(l.getConstruction(),p.getConstruction())));

	}
	
	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/
	
	@Override
	public void paintEdition(FGEDrawingGraphics graphics,FGEPoint lastMouseLocation)
	{
		if (currentStep == 0) {
			// Nothing to draw
		}
		else if (currentStep == 1) {
			FGELine line = ((ObtainLine)inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			FGELine l = line.getParallelLine(lastMouseLocation);
			l.paint(graphics);
		}
	}
}


