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
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Rectangle;
import org.openflexo.fge.geomedit.construction.RectangleWithTwoPointsConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class CreateRectangleFromPoints extends Edition {

	public CreateRectangleFromPoints(GeomEditController controller) {
		super("Create rectangle from points", controller);
		inputs.add(new ObtainPoint("Select first point", controller));
		inputs.add(new ObtainPoint("Select second point", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(0);
		ObtainPoint p2 = (ObtainPoint) inputs.get(1);

		addObject(new Rectangle(getController().getDrawing().getModel(), new RectangleWithTwoPointsConstruction(p1.getConstruction(),
				p2.getConstruction())));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(FGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		} else if (currentStep == 1) {
			// Nothing to draw

			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			FGEPoint p2 = lastMouseLocation;

			FGEPoint p = new FGEPoint();
			p.x = Math.min(p1.x, p2.x);
			p.y = Math.min(p1.y, p2.y);

			double width = Math.abs(p1.x - p2.x);
			double height = Math.abs(p1.y - p2.y);

			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			new FGERectangle(p.x, p.y, width, height).paint(graphics);
		}
	}
}
