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

import java.util.logging.Logger;

import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.HalfPlane;
import org.openflexo.fge.geomedit.construction.HalfPlaneWithLineAndPointConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;



public class CreateHalfPlaneWithLineAndPoint extends Edition {
	
    private static final Logger logger = Logger.getLogger(CreateHalfPlaneWithLineAndPoint.class.getPackage().getName());

	public CreateHalfPlaneWithLineAndPoint(GeomEditController controller) {
		super("Create half-plane with line and point",controller);
		inputs.add(new ObtainLine("Select a line delimiting half-plane",controller));
		inputs.add(new ObtainPoint("Select point inside half-plane",controller));
	}
	
	@Override
	public void performEdition()
	{		
		ObtainLine l = (ObtainLine)inputs.get(0);
		ObtainPoint p = (ObtainPoint)inputs.get(1);
		
		addObject (new HalfPlane(
					getController().getDrawing().getModel(),
				new HalfPlaneWithLineAndPointConstruction(l.getConstruction(),p.getConstruction())));

	}
	
	private FGEHalfPlane hpToPaint = null;
	private boolean requireRepaint = true;
	
	@Override
	public void paintEdition(FGEDrawingGraphics graphics,FGEPoint lastMouseLocation)
	{
		if (currentStep == 0) {
			// Nothing to draw
		}
		else if (currentStep == 1) {
			FGELine line = ((ObtainLine)inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			graphics.setDefaultBackground(focusedBackgroundStyle);
			FGEHalfPlane hp = new FGEHalfPlane(line,lastMouseLocation);
			hp.paint(graphics);
		}
	}
	
	@Override
	public boolean requireRepaint(FGEPoint lastMouseLocation)
	{
		if (currentStep == 1) {
			FGELine line = ((ObtainLine)inputs.get(0)).getInputData();
			FGEHalfPlane hp = new FGEHalfPlane(line,lastMouseLocation.clone());
			if (hpToPaint == null || !hpToPaint.equals(hp)) {
				hpToPaint = hp;
				requireRepaint = true;
			}
			else {
				requireRepaint = false;
			}
		}
		return requireRepaint;
	}
}


