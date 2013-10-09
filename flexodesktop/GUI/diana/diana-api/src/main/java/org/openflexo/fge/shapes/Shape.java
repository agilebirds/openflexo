/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public interface Shape<SS extends ShapeSpecification> {

	public abstract SS getShapeSpecification();

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<ControlPoint> getControlAreas();

	/**
	 * Retrieve all control points used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<ControlPoint> getControlPoints();

	public abstract FGEShape<?> getShape();

	public abstract FGEShape<?> getOutline();

	public abstract ShapeType getShapeType();

	/**
	 * Return nearest point located on outline, asserting aPoint is related to shape coordinates, and normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	public abstract FGEPoint nearestOutlinePoint(FGEPoint aPoint);

	/**
	 * Return flag indicating if position represented is located inside shape, asserting aPoint is related to shape coordinates, and
	 * normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	public abstract boolean isPointInsideShape(FGEPoint aPoint);

	/**
	 * Compute point where supplied line intersects with shape outline trying to minimize distance from "from" point
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	public abstract FGEPoint outlineIntersect(FGELine line, FGEPoint from);

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
	public abstract FGEPoint outlineIntersect(FGEPoint from);

	public abstract FGEArea getAllowedHorizontalConnectorLocationFromEast();

	public abstract FGEArea getAllowedHorizontalConnectorLocationFromWest();

	public abstract FGEArea getAllowedVerticalConnectorLocationFromNorth();

	public abstract FGEArea getAllowedVerticalConnectorLocationFromSouth();

	public abstract void paintShadow(FGEShapeGraphics g);

	public abstract void paintShape(FGEShapeGraphics g);

}