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
import java.util.Vector;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public abstract class Edition {
	public int currentStep;
	public Vector<EditionInput> inputs;
	private String label;
	private GeomEditController controller;

	protected ForegroundStyle focusedForegroundStyle;
	protected BackgroundStyle focusedBackgroundStyle;

	public Edition(String aLabel, GeomEditController aController) {
		super();
		controller = aController;
		focusedForegroundStyle = aController.getFactory().makeForegroundStyle(Color.RED);
		focusedBackgroundStyle = aController.getFactory().makeTexturedBackground(TextureType.TEXTURE1, Color.RED, Color.WHITE);
		focusedBackgroundStyle.setUseTransparency(true);
		label = aLabel;
		currentStep = 0;
		inputs = new Vector<EditionInput>();
	}

	public String getLabel() {
		return label;
	}

	public GeomEditController getController() {
		return controller;
	}

	public final void addObject(GeometricObject object) {
		getController().getDrawing().getModel().addToChilds(object);
	}

	public boolean next() {
		currentStep++;
		if (currentStep >= inputs.size()) {
			performEdition();
			return true;
		}
		return false;
	}

	public abstract void performEdition();

	public final void paint(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		paintEdition(graphics, lastMouseLocation);
		inputs.get(currentStep).paint(graphics);
	}

	public abstract void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation);

	public boolean requireRepaint(FGEPoint lastMouseLocation) {
		return true;
	}

}