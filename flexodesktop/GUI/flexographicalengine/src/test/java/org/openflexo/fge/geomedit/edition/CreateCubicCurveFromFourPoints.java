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

import java.awt.Color;

import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.CubicCurve;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.CubicCurveWithFourPointsConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class CreateCubicCurveFromFourPoints extends Edition {

	public CreateCubicCurveFromFourPoints(GeomEditController controller) {
		super("Create quadratic curve from three points", controller);
		inputs.add(new ObtainPoint("Select start point", controller));
		inputs.add(new ObtainPoint("Select end point", controller));
		inputs.add(new ObtainPoint("Select control point 1", controller));
		inputs.add(new ObtainPoint("Select control point 2", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(0);
		ObtainPoint p2 = (ObtainPoint) inputs.get(1);
		ObtainPoint p3 = (ObtainPoint) inputs.get(2);
		ObtainPoint p4 = (ObtainPoint) inputs.get(3);

		addObject(new CubicCurve(getController().getDrawing().getModel(), new CubicCurveWithFourPointsConstruction(p1.getConstruction(),
				p3.getConstruction(), p4.getConstruction(), p2.getConstruction())));

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
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			(new FGESegment(p1, lastMouseLocation)).paint(graphics);
		} else if (currentStep == 2) {
			// Draw construction
			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			FGEPoint p2 = ((ObtainPoint) inputs.get(1)).getInputData();

			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.MEDIUM_DASHES));
			FGESegment line1 = new FGESegment(lastMouseLocation, p1);
			line1.paint(graphics);

			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 1));
			p1.paint(graphics);
			p2.paint(graphics);
			lastMouseLocation.paint(graphics);

			(new FGEQuadCurve(p1, lastMouseLocation, p2)).paint(graphics);
		} else if (currentStep == 3) {
			// Draw construction
			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			FGEPoint p2 = ((ObtainPoint) inputs.get(1)).getInputData();
			FGEPoint cp1 = ((ObtainPoint) inputs.get(2)).getInputData();

			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.MEDIUM_DASHES));
			FGESegment line1 = new FGESegment(p1, cp1);
			FGESegment line2 = new FGESegment(p2, lastMouseLocation);
			line1.paint(graphics);
			line2.paint(graphics);

			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 1));
			p1.paint(graphics);
			p2.paint(graphics);
			cp1.paint(graphics);
			lastMouseLocation.paint(graphics);

			(new FGECubicCurve(p1, cp1, lastMouseLocation, p2)).paint(graphics);
		}
	}
}
