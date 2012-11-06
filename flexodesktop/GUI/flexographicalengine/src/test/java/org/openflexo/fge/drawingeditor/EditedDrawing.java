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
package org.openflexo.fge.drawingeditor;

import java.util.logging.Logger;

import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.logging.FlexoLogger;

public class EditedDrawing extends DefaultDrawing<MyDrawing> {
	private static final Logger logger = FlexoLogger.getLogger(TestDrawingEditor.class.getPackage().getName());

	private MyDrawingController controller;

	public EditedDrawing(MyDrawing drawing) {
		super(drawing);
	}

	@Override
	public MyDrawingGraphicalRepresentation getDrawingGraphicalRepresentation() {
		return getModel().getGraphicalRepresentation();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable) {
		if (aDrawable == getModel()) {
			return (GraphicalRepresentation<O>) getDrawingGraphicalRepresentation();
		}
		if (aDrawable instanceof MyShape) {
			return (GraphicalRepresentation<O>) ((MyShape) aDrawable).getGraphicalRepresentation();
		}
		if (aDrawable instanceof MyConnector) {
			return (GraphicalRepresentation<O>) ((MyConnector) aDrawable).getGraphicalRepresentation();
		}
		return null;
	}

	public void init() {
		controller = new MyDrawingController(this);
	}

	public MyDrawingController getController() {
		return controller;
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
	}

}