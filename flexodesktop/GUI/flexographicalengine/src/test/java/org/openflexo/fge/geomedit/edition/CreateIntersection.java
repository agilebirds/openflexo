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
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.ObjectIntersection;
import org.openflexo.fge.geomedit.construction.IntersectionConstruction;
import org.openflexo.fge.geomedit.construction.ObjectReference;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.logging.FlexoLogger;

public class CreateIntersection extends Edition {

	private static final Logger logger = FlexoLogger.getLogger(GeomEditController.class.getPackage().getName());

	public CreateIntersection(GeomEditController controller) {
		super("Create intersection", controller);
		appendNewObjectEdition(controller, 1);
	}

	private void appendNewObjectEdition(final GeomEditController controller, int index) {
		ObtainObject newObtainObject = new ObtainObject("Select object " + index, controller, true) {
			@Override
			public void done() {
				appendNewObjectEdition(controller, currentStep + 2);
				super.done();
			}

			@Override
			public void endEdition() {
				System.out.println("End edition called");
				super.done();
			}
		};
		inputs.add(newObtainObject);
	}

	@Override
	public void performEdition() {
		Vector<ObjectReference<?>> lgc = new Vector<ObjectReference<?>>();
		for (EditionInput o : inputs) {
			ObtainObject oo = (ObtainObject) o;
			if (oo.getReferencedObject() != null) {
				oo.getReferencedObject().getGraphicalRepresentation().setIsSelected(false);
			}
			ObjectReference<?> or = ((ObtainObject) o).getConstruction();
			if (or != null) {
				lgc.add(or);
			}
		}

		addObject(new ObjectIntersection(getController().getDrawing().getModel(), new IntersectionConstruction(lgc)));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(FGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		for (EditionInput o : inputs) {
			ObtainObject oo = (ObtainObject) o;
			if (oo.getReferencedObject() != null) {
				oo.getReferencedObject().getGraphicalRepresentation().setIsSelected(true);
			}
		}
	}
}
