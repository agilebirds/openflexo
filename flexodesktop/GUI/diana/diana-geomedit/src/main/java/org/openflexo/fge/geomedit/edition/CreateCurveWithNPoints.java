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

import java.util.Vector;

import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.ComplexCurve;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.ComplexCurveWithNPointsConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphicsImpl;

public class CreateCurveWithNPoints extends Edition {

	public CreateCurveWithNPoints(GeomEditController controller) {
		super("Create curve from a list of points", controller);
		appendNewPointEdition(controller, 1);
	}

	private void appendNewPointEdition(final GeomEditController controller, int index) {
		ObtainPoint newObtainPoint = new ObtainPoint("Select point " + index, controller, true) {
			@Override
			public void done() {
				appendNewPointEdition(controller, currentStep + 2);
				super.done();
			}

			@Override
			public void endEdition() {
				System.out.println("End edition called");
				super.done();
			}
		};
		inputs.add(newObtainPoint);
	}

	@Override
	public void performEdition() {
		Vector<PointConstruction> pc = new Vector<PointConstruction>();
		for (EditionInput o : inputs) {
			PointConstruction pp = ((ObtainPoint) o).getConstruction();
			if (pp != null) {
				pc.add(pp);
			}
		}
		addObject(new ComplexCurve(getController().getDrawing().getModel(), new ComplexCurveWithNPointsConstruction(pc)));
	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(FGEDrawingGraphicsImpl graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		}
		if (currentStep == 1) {
			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			new FGESegment(p1, lastMouseLocation).paint(graphics);
		} else {
			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			graphics.setDefaultForeground(focusedForegroundStyle);
			for (int i = 0; i < currentStep; i++) {
				FGEPoint p = ((ObtainPoint) inputs.get(i)).getInputData();
				p.paint(graphics);
				pts.add(p);
			}
			pts.add(lastMouseLocation);
			new FGEComplexCurve(Closure.OPEN_NOT_FILLED, pts).paint(graphics);

		}
	}
}
