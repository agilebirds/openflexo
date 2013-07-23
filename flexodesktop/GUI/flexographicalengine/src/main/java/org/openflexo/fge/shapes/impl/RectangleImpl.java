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
package org.openflexo.fge.shapes.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.Rectangle;

public class RectangleImpl extends ShapeSpecificationImpl implements Rectangle {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RectangleImpl.class.getPackage().getName());

	private boolean isRounded = false;
	private double arcSize = FGEConstants.DEFAULT_ROUNDED_RECTANGLE_ARC_SIZE;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public RectangleImpl() {
		super();
	}

	/*@Deprecated
	private RectangleImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		updateShape();
	}*/

	@Override
	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	@Override
	public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
		if (node != null && isRounded) {
			double arcwidth = arcSize / node.getWidth();
			double archeight = arcSize / node.getHeight();
			return new FGERoundRectangle(0, 0, 1, 1, arcwidth, archeight, Filling.FILLED);
		}
		return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
	}

	/**
	 * Returns arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @return
	 */
	@Override
	public double getArcSize() {
		return arcSize;
	}

	/**
	 * Sets arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @param anArcSize
	 */
	@Override
	public void setArcSize(double anArcSize) {
		FGENotification notification = requireChange(RectangleParameters.arcSize, anArcSize);
		if (notification != null) {
			arcSize = anArcSize;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsRounded() {
		return isRounded;
	}

	@Override
	public void setIsRounded(boolean aFlag) {
		FGENotification notification = requireChange(RectangleParameters.isRounded, aFlag);
		if (notification != null) {
			isRounded = aFlag;
			hasChanged(notification);
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

	/*@Override
	public void notifyObjectResized() {
		if (getIsRounded()) {
			setArcSize(arcSize);
		}
	}*/

}
