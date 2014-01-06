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
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Point;
import org.openflexo.fge.geomedit.construction.PointMiddleOfTwoPointsConstruction;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreatePointMiddleOfPoints extends Edition {

	public CreatePointMiddleOfPoints(GeomEditController controller) {
		super("Create point as middle of points", controller);
		inputs.add(new ObtainPoint("Select first point", controller));
		inputs.add(new ObtainPoint("Select second point", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(0);
		ObtainPoint p2 = (ObtainPoint) inputs.get(1);

		addObject(new Point(getController().getDrawing().getModel(), new PointMiddleOfTwoPointsConstruction(p1.getConstruction(),
				p2.getConstruction())));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		// Nothing to draw
	}
}
