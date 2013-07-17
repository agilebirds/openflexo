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

import java.util.List;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.shapes.impl.ShapeImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents a shape as a geometrical shape in a {@link ShapeGraphicalRepresentation}<br>
 * This class is a wrapper of {@link FGEShape} which is the geometrical definition of the object as defined in geometrical framework.<br>
 * A {@link Shape} has a geometrical definition inside a normalized rectangle as defined by (0.0,0.0,1.0,1.0)<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(ShapeImpl.class)
@Imports({ @Import(Arc.class), @Import(Circle.class), @Import(Losange.class), @Import(Oval.class), @Import(Polygon.class),
		@Import(Rectangle.class), @Import(RectangularOctogon.class), @Import(RegularPolygon.class), @Import(Square.class),
		@Import(Star.class), @Import(Triangle.class) })
public interface Shape extends FGEObject {

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

	public void setPaintAttributes(ShapeNode<?> node, FGEShapeGraphics g);

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	public boolean areDimensionConstrained();

	public ShapeType getShapeType();

	/**
	 * Return geometric shape of this shape, in the context of supplied node
	 * 
	 * @return
	 */
	public FGEShape<?> getShape(ShapeNode<?> node);

	/**
	 * Return outline for geometric shape of this shape, in the context of supplied node (this is the shape itself, but NOT filled)
	 * 
	 * @return
	 */
	public FGEShape<?> getOutline(ShapeNode<?> node);

	// public ShapeGraphicalRepresentation getGraphicalRepresentation();

	// public void setGraphicalRepresentation(ShapeGraphicalRepresentation aGR);

	// public List<ControlPoint> getControlPoints();

	/**
	 * Recompute all control points for supplied shape node<br>
	 * Return a newly created list of required control points
	 * 
	 * @param shapeNode
	 * @return
	 */
	public List<ControlPoint> rebuildControlPoints(ShapeNode<?> shapeNode);

	/**
	 * Return nearest point located on outline, asserting aPoint is related to shape coordinates, and normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint, ShapeNode<?> shapeNode);

	/**
	 * Return flag indicating if position represented is located inside shape, asserting aPoint is related to shape coordinates, and
	 * normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	public boolean isPointInsideShape(FGEPoint aPoint, ShapeNode<?> shapeNode);

	/**
	 * Compute point where supplied line intersects with shape outline trying to minimize distance from "from" point
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	public FGEPoint outlineIntersect(FGELine line, FGEPoint from, ShapeNode<?> shapeNode);

	/**
	 * Compute point where a line formed by current shape's center and "from" point intersects with shape outline trying to minimize
	 * distance from "from" point This implementation provide simplified computation with outer bounds (relative coordinates (0,0)-(1,1))
	 * and must be overriden when required
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	public FGEPoint outlineIntersect(FGEPoint from, ShapeNode<?> shapeNode);

	public FGEArea getAllowedHorizontalConnectorLocationFromEast();

	// public FGEArea getAllowedHorizontalConnectorLocationFromWest2();

	public FGEArea getAllowedHorizontalConnectorLocationFromWest();

	public FGEArea getAllowedVerticalConnectorLocationFromNorth();

	public FGEArea getAllowedVerticalConnectorLocationFromSouth();

	public void paintShadow(ShapeNode<?> node, FGEShapeGraphics g);

	// @Override
	public void paintShape(ShapeNode<?> node, FGEShapeGraphics g);

	// Override when required
	public void notifyObjectResized();

	public Shape clone();

	@Override
	public boolean equals(Object object);

	@Override
	public int hashCode();

}
