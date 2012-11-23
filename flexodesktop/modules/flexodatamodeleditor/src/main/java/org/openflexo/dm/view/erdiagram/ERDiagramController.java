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
package org.openflexo.dm.view.erdiagram;

import org.openflexo.dm.view.controller.DMController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.selection.SelectionManagingDrawingController;

public class ERDiagramController extends SelectionManagingDrawingController<ERDiagramRepresentation> {

	private DMController controller;
	private DiagramPalette palette;

	public ERDiagramController(DMController controller, ERDiagram diagram) {
		super(new ERDiagramRepresentation(diagram), controller.getSelectionManager());
		this.controller = controller;
		palette = new DiagramPalette();
		registerPalette(palette);
		activatePalette(palette);
	}

	@Override
	public void delete() {
		if (controller != null) {
			if (getDrawingView() != null) {
				controller.removeModuleView(getDrawingView());
			}
		}
		super.delete();
		getDrawing().delete();
	}

	@Override
	public DrawingView<ERDiagramRepresentation> makeDrawingView(ERDiagramRepresentation drawing) {
		return new DiagramView(drawing, this);
	}

	public DMController getDMController() {
		return controller;
	}

	@Override
	public DiagramView getDrawingView() {
		return (DiagramView) super.getDrawingView();
	}

	public DiagramPalette getPalette() {
		return palette;
	}

}
