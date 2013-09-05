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

import org.openflexo.fge.GRParameter;
import org.openflexo.fge.shapes.impl.RectangleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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
public interface Rectangle extends ShapeSpecification {

	// Property keys

	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ROUNDED_KEY = "isRounded";
	@PropertyIdentifier(type = Double.class)
	public static final String ARC_SIZE_KEY = "arcSize";

	public static GRParameter<Boolean> IS_ROUNDED = GRParameter.getGRParameter(Rectangle.class, IS_ROUNDED_KEY, Boolean.class);
	public static GRParameter<Double> ARC_SIZE = GRParameter.getGRParameter(Rectangle.class, ARC_SIZE_KEY, Double.class);

	/*public static enum RectangleParameters implements GRParameter {
		isRounded, arcSize;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	/**
	 * Returns arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @return
	 */
	@Getter(value = ARC_SIZE_KEY, defaultValue = "30")
	@XMLAttribute
	public double getArcSize();

	/**
	 * Sets arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @param anArcSize
	 */
	@Setter(value = ARC_SIZE_KEY)
	public void setArcSize(double anArcSize);

	@Getter(value = IS_ROUNDED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsRounded();

	@Setter(value = IS_ROUNDED_KEY)
	public void setIsRounded(boolean aFlag);

}
