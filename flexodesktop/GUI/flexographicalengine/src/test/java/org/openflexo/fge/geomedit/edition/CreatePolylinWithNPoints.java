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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Polylin;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.PolylinWithNPointsConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class CreatePolylinWithNPoints extends Edition {

	public CreatePolylinWithNPoints(GeomEditController controller) {
		super("Create polylin from a list of points", controller);
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
		addObject(new Polylin(getController().getDrawing().getModel(), new PolylinWithNPointsConstruction(pc)));
	}

	@Override
	public void paintEdition(FGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		}
		if (currentStep == 1) {
			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			(new FGESegment(p1, lastMouseLocation)).paint(graphics);
		} else {
			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			graphics.setDefaultForeground(focusedForegroundStyle);
			for (int i = 0; i < currentStep; i++) {
				FGEPoint p = ((ObtainPoint) inputs.get(i)).getInputData();
				p.paint(graphics);
				pts.add(p);
			}
			pts.add(lastMouseLocation);
			(new FGEPolylin(pts)).paint(graphics);

		}
	}
}
