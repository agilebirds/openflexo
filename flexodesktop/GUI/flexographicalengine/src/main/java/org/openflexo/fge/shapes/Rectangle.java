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

import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;

public class Rectangle extends Shape {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(Rectangle.class.getPackage().getName());

	private FGEShape<?> _rectangle;

	private boolean isRounded = false;
	private double arcSize = FGEConstants.DEFAULT_ROUNDED_RECTANGLE_ARC_SIZE;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public Rectangle() {
		this(null);
	}

	public Rectangle(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		super(aGraphicalRepresentation);
		updateShape();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	@Override
	public void updateShape() {
		if (getGraphicalRepresentation() != null) {
			if (isRounded) {
				double arcwidth = arcSize / getGraphicalRepresentation().getWidth();
				double archeight = arcSize / getGraphicalRepresentation().getHeight();
				_rectangle = new FGERoundRectangle(0, 0, 1, 1, arcwidth, archeight, Filling.FILLED);
			} else {
				_rectangle = new FGERectangle(0, 0, 1, 1, Filling.FILLED);
			}
		}
		rebuildControlPoints();
		if (getGraphicalRepresentation() != null)
			getGraphicalRepresentation().notifyShapeChanged();
	}

	@Override
	public FGEShape<?> getShape() {
		return _rectangle;
	}

	/**
	 * Returns arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @return
	 */
	public double getArcSize() {
		return arcSize;
	}

	/**
	 * Sets arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @param anArcSize
	 */
	public void setArcSize(double anArcSize) {
		if (arcSize != anArcSize) {
			arcSize = anArcSize;
			updateShape();
		}
	}

	public boolean getIsRounded() {
		return isRounded;
	}

	public void setIsRounded(boolean aFlag) {
		if (isRounded != aFlag) {
			isRounded = aFlag;
			updateShape();
		}
	}

	/*@Override
	public FGEShape getOutline()
	{
		if (getIsRounded()) {
			double arcwidth = arcSize/getGraphicalRepresentation().getWidth();
			double archeight = arcSize/getGraphicalRepresentation().getHeight();
			return new FGERoundRectangle(0,0,1,1,arcwidth,archeight,Filling.NOT_FILLED);
		}
		else {
			return new FGERectangle(0,0,1,1,Filling.NOT_FILLED);
		}
	}*/

	@Override
	public void notifyObjectResized() {
		if (getIsRounded())
			setArcSize(arcSize);
	}

}
