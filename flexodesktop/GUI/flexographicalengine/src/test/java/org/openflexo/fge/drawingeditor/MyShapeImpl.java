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

import org.openflexo.fge.drawingeditor.MyDrawing.DrawingBuilder;

public abstract class MyShapeImpl extends MyDrawingElementImpl<MyShape, MyShapeGraphicalRepresentation> implements MyShape {
	public String name;

	// Called for LOAD
	public MyShapeImpl(DrawingBuilder builder) {
		super(builder.drawing);
		// initializeDeserialization();
	}

	// Used by PAMELA, do not use it
	public MyShapeImpl() {
		super(null);
	}

	// Called for NEW
	/*public MyShapeImpl(ShapeType shape, FGEPoint p, EditedDrawing drawing) {
		super(drawing.getModel());
		MyShapeGraphicalRepresentation gr = drawing.getModel().getFactory().makeNewShapeGR(shape, this, drawing);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setWidth(80);
			gr.setHeight(80);
		} else {
			gr.setWidth(100);
			gr.setHeight(80);
		}
		gr.setX(p.x);
		gr.setY(p.y);
		setGraphicalRepresentation(gr);
	}

	public MyShapeImpl(ShapeGraphicalRepresentation<?> aGR, FGEPoint p, EditedDrawing drawing) {
		super(drawing.getModel());
		MyShapeGraphicalRepresentation gr = drawing.getModel().getFactory().makeNewShapeGR(aGR, this, drawing);
		gr.setX(p.x);
		gr.setY(p.y);
		setGraphicalRepresentation(gr);
	}*/

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "MyShape[" + name + ":" + getGraphicalRepresentation().toString() + "]";
	}

}