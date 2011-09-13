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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.Band;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.BandWithTwoLinesConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;



public class CreateBandFromLines extends Edition {
	
	public CreateBandFromLines(GeomEditController controller) {
		super("Create band from lines",controller);
		inputs.add(new ObtainLine("Select first line",controller));
		inputs.add(new ObtainLine("Select second line",controller));
	}
	
	@Override
	public void performEdition()
	{
		ObtainLine l1 = (ObtainLine)inputs.get(0);
		ObtainLine l2 = (ObtainLine)inputs.get(1);
		
		addObject (new Band(
				getController().getDrawing().getModel(),
				new BandWithTwoLinesConstruction(l1.getConstruction(),l2.getConstruction())));

	}
	
	@Override
	public void paintEdition(FGEDrawingGraphics graphics,FGEPoint lastMouseLocation)
	{
		if (currentStep == 0) {
			// Nothing to draw
		}
		else if (currentStep == 1) {
			// Nothing to draw
		}
	}
}


