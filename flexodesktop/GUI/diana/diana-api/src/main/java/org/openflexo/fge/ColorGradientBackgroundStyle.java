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
package org.openflexo.fge;

import java.awt.Color;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a background colored with a linear gradient between two colors
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ColorGradientBackgroundStyle")
public interface ColorGradientBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR1_KEY = "color1";
	@PropertyIdentifier(type = Color.class)
	public static final String COLOR2_KEY = "color2";
	@PropertyIdentifier(type = ColorGradientDirection.class)
	public static final String DIRECTION_KEY = "direction";

	public static GRParameter<Color> COLOR1 = GRParameter.getGRParameter(ColorGradientBackgroundStyle.class, COLOR1_KEY, Color.class);
	public static GRParameter<Color> COLOR2 = GRParameter.getGRParameter(ColorGradientBackgroundStyle.class, COLOR2_KEY, Color.class);
	public static GRParameter<ColorGradientDirection> DIRECTION = GRParameter.getGRParameter(ColorGradientBackgroundStyle.class,
			DIRECTION_KEY, ColorGradientDirection.class);

	public static enum ColorGradientDirection {
		NORTH_SOUTH, WEST_EAST, SOUTH_EAST_NORTH_WEST, SOUTH_WEST_NORTH_EAST
	}

	@Getter(value = COLOR1_KEY)
	@XMLAttribute
	public java.awt.Color getColor1();

	@Setter(value = COLOR1_KEY)
	public void setColor1(java.awt.Color aColor);

	@Getter(value = COLOR2_KEY)
	@XMLAttribute
	public java.awt.Color getColor2();

	@Setter(value = COLOR2_KEY)
	public void setColor2(java.awt.Color aColor);

	@Getter(value = DIRECTION_KEY)
	@XMLAttribute
	public ColorGradientDirection getDirection();

	@Setter(value = DIRECTION_KEY)
	public void setDirection(ColorGradientDirection aDirection);

}