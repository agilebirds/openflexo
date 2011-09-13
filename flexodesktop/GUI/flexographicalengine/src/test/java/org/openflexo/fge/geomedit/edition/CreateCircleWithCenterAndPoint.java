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

import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geomedit.Circle;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.CircleWithCenterAndPointConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;



public class CreateCircleWithCenterAndPoint extends Edition {
	
	public CreateCircleWithCenterAndPoint(GeomEditController controller) {
		super("Create circle from center and point",controller);
		inputs.add(new ObtainPoint("Select center",controller));
		inputs.add(new ObtainPoint("Select a point",controller));
	}
	
	@Override
	public void performEdition()
	{
		ObtainPoint p1 = (ObtainPoint)inputs.get(0);
		ObtainPoint p2 = (ObtainPoint)inputs.get(1);
		
		addObject (new Circle(
				getController().getDrawing().getModel(),
				new CircleWithCenterAndPointConstruction(p1.getConstruction(),p2.getConstruction())));

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
			// Nothing to draw
			
			FGEPoint center = ((ObtainPoint)inputs.get(0)).getInputData();
			FGEPoint p = lastMouseLocation;

			double diameter = FGESegment.getLength(center,p)*2;

			graphics.setDefaultForeground(focusedForegroundStyle);
			center.paint(graphics);
			(new FGEEllips(center,new FGEDimension(diameter,diameter),Filling.NOT_FILLED)).paint(graphics);
		}
	}
}


