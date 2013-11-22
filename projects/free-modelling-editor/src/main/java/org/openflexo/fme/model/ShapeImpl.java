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
package org.openflexo.fme.model;

import org.openflexo.fge.ShapeGraphicalRepresentation;

public abstract class ShapeImpl extends DiagramElementImpl<Shape, ShapeGraphicalRepresentation> implements Shape {
	// public String name;

	private static int INDEX = 0;
	private int index = 0;

	// Called for LOAD
	/*public ShapeImpl(DrawingBuilder builder) {
		super(builder.drawing);
		// initializeDeserialization();
	}*/

	// Used by PAMELA, do not use it
	public ShapeImpl() {
		super(null);
		index = INDEX++;
	}

	// Called for NEW
	/*public ShapeImpl(ShapeType shape, FGEPoint p, DiagramDrawing drawing) {
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

	public ShapeImpl(ShapeGraphicalRepresentation aGR, FGEPoint p, DiagramDrawing drawing) {
		super(drawing.getModel());
		MyShapeGraphicalRepresentation gr = drawing.getModel().getFactory().makeNewShapeGR(aGR, this, drawing);
		gr.setX(p.x);
		gr.setY(p.y);
		setGraphicalRepresentation(gr);
	}*/

	/*@Override
	public String getName() {
		if (name == null) {
			return "unnamed" + index;
		}
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}*/

	@Override
	public String toString() {
		return "Shape[" + getName() + "]";
	}

}