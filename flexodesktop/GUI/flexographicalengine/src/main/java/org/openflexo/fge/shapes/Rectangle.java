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

import org.openflexo.fge.shapes.impl.RectangleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a rectangle, which could have rounded corners
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(RectangleImpl.class)
@XMLElement(xmlTag = "RectangleShape")
public interface Rectangle extends Shape {

	// Property keys

	public static final String IS_ROUNDED = "isRounded";
	public static final String ARC_SIZE = "arcSize";

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	/**
	 * Returns arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @return
	 */
	@Getter(value = ARC_SIZE, defaultValue = "30")
	@XMLAttribute
	public double getArcSize();

	/**
	 * Sets arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @param anArcSize
	 */
	@Setter(value = ARC_SIZE)
	public void setArcSize(double anArcSize);

	@Getter(value = IS_ROUNDED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsRounded();

	@Setter(value = IS_ROUNDED)
	public void setIsRounded(boolean aFlag);

}
