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
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Polylin;
import org.openflexo.fge.geomedit.construction.RectPolylinWithStartAndEndAreaConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class CreateRectPolylinWithStartAndEndArea extends Edition {

	public CreateRectPolylinWithStartAndEndArea(GeomEditController controller) {
		super("Create line from points", controller);
		inputs.add(new ObtainObject("Select starting area", controller));
		inputs.add(new ObtainSimplifiedCardinalDirection("Select start orientation", SimplifiedCardinalDirection.NORTH, controller));
		inputs.add(new ObtainObject("Select end area", controller));
		inputs.add(new ObtainSimplifiedCardinalDirection("Select end orientation", SimplifiedCardinalDirection.NORTH, controller));
	}

	@Override
	public void performEdition() {
		ObtainObject o1 = (ObtainObject) inputs.get(0);
		ObtainSimplifiedCardinalDirection startOrientation = (ObtainSimplifiedCardinalDirection) inputs.get(1);
		ObtainObject o2 = (ObtainObject) inputs.get(2);
		ObtainSimplifiedCardinalDirection endOrientation = (ObtainSimplifiedCardinalDirection) inputs.get(3);

		addObject(new Polylin(getController().getDrawing().getModel(), new RectPolylinWithStartAndEndAreaConstruction(o1.getConstruction(),
				startOrientation.getInputData(), o2.getConstruction(), endOrientation.getInputData())));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(FGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		// Nothing to draw
	}
}
