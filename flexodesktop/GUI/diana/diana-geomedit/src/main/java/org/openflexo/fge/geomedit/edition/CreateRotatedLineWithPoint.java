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
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.RotatedLineWithPointConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphicsImpl;

public class CreateRotatedLineWithPoint extends Edition {

	public CreateRotatedLineWithPoint(GeomEditController controller) {
		super("Create horizontal line crossing point", controller);
		inputs.add(new ObtainLine("Select line", controller));
		inputs.add(new ObtainDouble("Select rotation angle (degree)", 45, controller));
		inputs.add(new ObtainPoint("Select point", controller));
	}

	@Override
	public void performEdition() {
		if (((ObtainLine) inputs.get(0)).getReferencedLine() != null) {
			((ObtainLine) inputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(false);
		}

		LineConstruction line = ((ObtainLine) inputs.get(0)).getConstruction();
		double angle = ((ObtainDouble) inputs.get(1)).getInputData();
		PointConstruction point = ((ObtainPoint) inputs.get(2)).getConstruction();

		addObject(new Line(getController().getDrawing().getModel(), new RotatedLineWithPointConstruction(line, point, angle)));

	}

	@Override
	public void paintEdition(FGEDrawingGraphicsImpl graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		} else if (currentStep == 1) {
			if (((ObtainLine) inputs.get(0)).getReferencedLine() != null) {
				((ObtainLine) inputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(true);
			}
		} else if (currentStep == 2) {
			graphics.setDefaultForeground(focusedForegroundStyle);
			FGELine line = ((ObtainLine) inputs.get(0)).getConstruction().getData();
			double angle = ((ObtainDouble) inputs.get(1)).getInputData();
			FGELine.getRotatedLine(line, angle, lastMouseLocation).paint(graphics);
		}
	}
}
