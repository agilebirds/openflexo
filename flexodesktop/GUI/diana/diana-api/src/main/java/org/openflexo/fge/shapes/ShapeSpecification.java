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
package org.openflexo.fge.shapes;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

/**
 * This is the specification of a Shape<br>
 * Contains all the properties required to manage a Shape as a geometrical shape in a {@link ShapeNode}<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(Arc.class), @Import(Circle.class), @Import(Losange.class), @Import(Oval.class), @Import(Polygon.class),
		@Import(Rectangle.class), @Import(RectangularOctogon.class), @Import(RegularPolygon.class), @Import(Square.class),
		@Import(Star.class), @Import(Triangle.class) })
public interface ShapeSpecification extends FGEObject {

	public static enum ShapeType {
		RECTANGLE, SQUARE, RECTANGULAROCTOGON, POLYGON, TRIANGLE, LOSANGE, OVAL, CIRCLE, STAR, ARC, CUSTOM_POLYGON
	}

	public static final FGEPoint CENTER = new FGEPoint(0.5, 0.5);
	public static final FGEPoint NORTH_EAST = new FGEPoint(1, 0);
	public static final FGEPoint SOUTH_EAST = new FGEPoint(1, 1);
	public static final FGEPoint SOUTH_WEST = new FGEPoint(0, 1);
	public static final FGEPoint NORTH_WEST = new FGEPoint(0, 0);
	public static final FGEPoint NORTH = new FGEPoint(0.5, 0);
	public static final FGEPoint EAST = new FGEPoint(1, 0.5);
	public static final FGEPoint SOUTH = new FGEPoint(0.5, 1);
	public static final FGEPoint WEST = new FGEPoint(0, 0.5);

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	public boolean areDimensionConstrained();

	public ShapeType getShapeType();

	public ShapeSpecification clone();

	public abstract Shape<?> makeShape(ShapeNode<?> node);

	public FGEShape<?> makeFGEShape(ShapeNode<?> node);

}
