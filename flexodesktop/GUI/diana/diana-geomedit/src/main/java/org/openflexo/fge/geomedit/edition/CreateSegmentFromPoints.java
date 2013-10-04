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
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Segment;
import org.openflexo.fge.geomedit.construction.SegmentWithTwoPointsConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphicsImpl;

public class CreateSegmentFromPoints extends Edition {

	public CreateSegmentFromPoints(GeomEditController controller) {
		super("Create segment from points", controller);
		inputs.add(new ObtainPoint("Select first point", controller));
		inputs.add(new ObtainPoint("Select second point", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(0);
		ObtainPoint p2 = (ObtainPoint) inputs.get(1);

		addObject(new Segment(getController().getDrawing().getModel(), new SegmentWithTwoPointsConstruction(p1.getConstruction(),
				p2.getConstruction())));

	}

	@Override
	public void paintEdition(FGEDrawingGraphicsImpl graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		} else if (currentStep == 1) {
			// Nothing to draw
			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			new FGESegment(p1, lastMouseLocation).paint(graphics);
		}
	}
}
