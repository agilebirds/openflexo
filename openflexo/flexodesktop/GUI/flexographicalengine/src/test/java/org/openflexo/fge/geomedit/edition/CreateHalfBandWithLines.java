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
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEBand;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.HalfBand;
import org.openflexo.fge.geomedit.construction.HalfBandWithLinesConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;



public class CreateHalfBandWithLines extends Edition {
	
    private static final Logger logger = Logger.getLogger(CreateHalfBandWithLines.class.getPackage().getName());

	public CreateHalfBandWithLines(GeomEditController controller) {
		super("Create half-plane with line and point",controller);
		inputs.add(new ObtainLine("Select first line",controller));
		inputs.add(new ObtainLine("Select second line",controller));
		inputs.add(new ObtainLine("Select a line delimiting half-plane",controller));
		inputs.add(new ObtainPoint("Select point inside half-plane",controller));
	}
	
	@Override
	public void performEdition()
	{		
		ObtainLine l1 = (ObtainLine)inputs.get(0);
		ObtainLine l2 = (ObtainLine)inputs.get(1);
		ObtainLine l3 = (ObtainLine)inputs.get(2);
		ObtainPoint p = (ObtainPoint)inputs.get(3);
		
		addObject (new HalfBand(
					getController().getDrawing().getModel(),
				new HalfBandWithLinesConstruction(l1.getConstruction(),l2.getConstruction(),l3.getConstruction(),p.getConstruction())));

	}
	
	private FGEArea whatToPaint = null;
	private boolean requireRepaint = true;
	
	@Override
	public void paintEdition(FGEDrawingGraphics graphics,FGEPoint lastMouseLocation)
	{
		if (currentStep == 0) {
			// Nothing to draw
			return;
		}
		else if (currentStep == 1) {
			FGELine line1 = ((ObtainLine)inputs.get(0)).getInputData();
			whatToPaint = new FGEHalfPlane(line1,lastMouseLocation);
		}
		else if (currentStep == 2) {
			FGELine line1 = ((ObtainLine)inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine)inputs.get(1)).getInputData();
			whatToPaint = new FGEBand(line1,line2);
		}
		else if (currentStep == 3) {
			FGELine line1 = ((ObtainLine)inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine)inputs.get(1)).getInputData();
			FGELine limitLine = ((ObtainLine)inputs.get(2)).getInputData();
			whatToPaint = new FGEHalfBand(line1,line2,new FGEHalfPlane(limitLine,lastMouseLocation.clone()));
		}
		graphics.setDefaultForeground(focusedForegroundStyle);
		graphics.setDefaultBackground(focusedBackgroundStyle);
		whatToPaint.paint(graphics);
	}
	
	@Override
	public boolean requireRepaint(FGEPoint lastMouseLocation)
	{
		FGEArea thingToPaint = null;
		if (currentStep == 0) {
			// Nothing to draw
			return false;
		}
		else if (currentStep == 1) {
			FGELine line1 = ((ObtainLine)inputs.get(0)).getInputData();
			thingToPaint = new FGEHalfPlane(line1,lastMouseLocation);
		}
		else if (currentStep == 2) {
			FGELine line1 = ((ObtainLine)inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine)inputs.get(1)).getInputData();
			thingToPaint = new FGEBand(line1,line2);
		}
		else if (currentStep == 3) {
			FGELine line1 = ((ObtainLine)inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine)inputs.get(1)).getInputData();
			FGELine limitLine = ((ObtainLine)inputs.get(2)).getInputData();
			thingToPaint = new FGEHalfBand(line1,line2,new FGEHalfPlane(limitLine,lastMouseLocation.clone()));
		}
		if (whatToPaint == null || !whatToPaint.equals(thingToPaint)) {
			whatToPaint = thingToPaint;
			requireRepaint = true;
		}
		else {
			requireRepaint = false;
		}
		
		//System.out.println("Require repaint = "+requireRepaint+" currentStep="+currentStep+" thingToPaint="+thingToPaint);
		
		return requireRepaint;
	}
}


