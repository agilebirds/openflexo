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
import java.awt.Font;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent text properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "TextStyle")
public interface TextStyle extends FGEStyle {

	// Property keys

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR_KEY = "color";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_KEY = "backgroundColor";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = Integer.class)
	public static final String ORIENTATION_KEY = "orientation";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_BACKGROUND_COLORED_KEY = "isBackgroundColored";

	public static GRParameter<Color> COLOR = GRParameter.getGRParameter(TextStyle.class, COLOR_KEY, Color.class);
	public static GRParameter<Color> BACKGROUND_COLOR = GRParameter.getGRParameter(TextStyle.class, BACKGROUND_COLOR_KEY, Color.class);
	public static GRParameter<Font> FONT = GRParameter.getGRParameter(TextStyle.class, FONT_KEY, Font.class);
	public static GRParameter<Integer> ORIENTATION = GRParameter.getGRParameter(TextStyle.class, ORIENTATION_KEY, Integer.class);
	public static GRParameter<Boolean> IS_BACKGROUND_COLORED = GRParameter.getGRParameter(TextStyle.class, IS_BACKGROUND_COLORED_KEY,
			Boolean.class);

	/*public static enum Parameters implements GRParameter {
		color, backgroundColor, font, orientation, backgroundColored
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public Color getColor();

	@Setter(value = COLOR_KEY)
	public void setColor(Color aColor);

	@Getter(value = BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundColor();

	@Setter(value = BACKGROUND_COLOR_KEY)
	public void setBackgroundColor(Color aColor);

	@Getter(value = FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(value = FONT_KEY)
	public void setFont(Font aFont);

	@Getter(value = ORIENTATION_KEY, defaultValue = "0")
	@XMLAttribute
	public int getOrientation();

	@Setter(value = ORIENTATION_KEY)
	public void setOrientation(int anOrientation);

	@Getter(value = IS_BACKGROUND_COLORED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsBackgroundColored();

	@Setter(value = IS_BACKGROUND_COLORED_KEY)
	public void setIsBackgroundColored(boolean aFlag);

	public TextStyle clone();

}
